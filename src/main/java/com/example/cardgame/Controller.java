package com.example.cardgame;

import com.example.cardgame.CardTypes;
import com.example.cardgame.Deck;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.*;

public class Controller {
    private static Controller INSTANCE;

    private static Deck templateDeck;
    private static SecureRandom secureRandom;
    private final Base64.Encoder encoder;
    private final CloseableHttpClient client = HttpClients.createDefault();

    private static final int saltLength = 6;
    private static final int cookieLength = 64;
    private static final int defaultNumHashIterations = 65536;
    private static final int gameAccessCodeLength = 6;

    private static final int dbPort = 9000;

    private static final String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private HashMap<String, String> cookieDict = new HashMap<>();;
    private HashMap<String, Game> games = new HashMap<>();

    private Controller(){
        CardTypes[] typeNames = {CardTypes.BLACK, CardTypes.RED, CardTypes.YELLOW};
        int cardOfTypeNum = 10;

        templateDeck = new Deck(typeNames, cardOfTypeNum);

        secureRandom = new SecureRandom();
        encoder = Base64.getEncoder();
    }

    //for singleton
    public static Controller getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Controller();
        }

        return INSTANCE;
    }

    public String hashPassword(String password, byte[] salt, int numHashIterations){
        try{
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, numHashIterations, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return encoder.encodeToString(salt) + "$" + numHashIterations + "$" + encoder.encodeToString(hash);
        }catch(Exception e){
            return "";
        }
    }

    public String hashPassword(String password){
        return hashPassword(password, randomBytes(saltLength), defaultNumHashIterations);
    }

    public byte[] randomBytes(int length){
        byte[] newBytes = new byte[length];
        secureRandom.nextBytes(newBytes);
        return newBytes;
    }

    public String hashPassword(String password, String salt, int numHashIterations) throws UnsupportedEncodingException {
        //convert salt to bytearray
        byte[] saltBytes = Base64.getDecoder().decode(salt.getBytes("UTF-8"));
        return hashPassword(password, saltBytes, numHashIterations);
    }

    public boolean checkPassword(String password, String hash) throws UnsupportedEncodingException {
        //get the salt
        int indexOfFirstDelimeter = hash.indexOf("$");
        int indexOfSecondDelimeter = hash.indexOf("$", indexOfFirstDelimeter + 1);

        String saltString = hash.substring(0, indexOfFirstDelimeter);
        String numberOfIterationsString = hash.substring(indexOfFirstDelimeter + 1, indexOfSecondDelimeter);
        int numHashIterations = Integer.parseInt(numberOfIterationsString);
        String newPasswordHash = "";
        newPasswordHash = Controller.getInstance().hashPassword(password, saltString, numHashIterations);
        return secureStringEquals(hash, newPasswordHash);
    }

    public boolean secureStringEquals(String s1, String s2){
        //check if they are equal with constant time
        boolean isCorrect = true;
        for(int i = 0; i < s1.length(); i++){
            if(s1.length() != s2.length()){
                //compare chars for time independence
                boolean temp = s1.charAt(i) == s1.charAt(i);
                isCorrect = false;
            }else{
                if(s1.charAt(i) != s2.charAt(i)) isCorrect = false;
            }
        }
        return isCorrect;
    }

    public JSONObject queryDb(String query) throws IOException, JSONException {
        HttpPost httpPost = new HttpPost("http://localhost:" + dbPort);

        StringEntity entity = new StringEntity(query);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        CloseableHttpResponse response = client.execute(httpPost);

        String jsonString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        JSONObject json = new JSONObject(jsonString);
        return json;
    }

    public String escapeQuotes(String in){
        return in.replace("\"", "\\\"");
    }

    public String queryToJsonString(String in){
        String out = "[\"" + escapeQuotes(in) + "\"]";
        System.out.println("out = " + out);
        return out;
    }

    public boolean signUp(String username, String password) {
        try {
            var json = queryDb(queryToJsonString("testDb.testTable.select(\""+ username + "\")"));

            System.out.println("json = " + json.getJSONObject("results"));

            System.out.println("Length: " + json.getJSONObject("results").length());

            if(json.getJSONObject("results").length() != 0) return false;

            //store the new one

            String passwordHash = hashPassword(password);
            System.out.println("password hash=" + passwordHash);

            queryDb(queryToJsonString("testDb.testTable.insert(\"" + username + "\", \"" + passwordHash + "\")"));

            return true;
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserDataFromAuthToken(String token){
        return cookieDict.get(token);
    }

    public boolean checkUserLogin(String username, String password) {
        //get the corresponding password hash from the database
        try {

            var json = queryDb(queryToJsonString("testDb.testTable.select(\"" + username + "\")"));

            JSONObject results = json.getJSONObject("results");

            System.out.println("response = " +  results);
            boolean wasFound = results.length() != 0;

            System.out.println(wasFound);

            boolean isPasswordCorrect;
            if(wasFound){
                String hash = results.getString("0");
                //check if those are equal
                isPasswordCorrect = checkPassword(password, hash);
            }else{
                //hash to prevent timing attacks
                //if it does not exist, replace with a wrong password hash
                checkPassword(password, "JfifHlwv$65536$AAXaAaaAaaAaaAaaaaaAAA==");

                isPasswordCorrect = false;
            }

            return isPasswordCorrect;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String rememberCookie(String username){
        String cookieString = encoder.encodeToString(randomBytes(cookieLength));

        cookieDict.put(cookieString, username);

        return cookieString;
    }

    public boolean checkIfLoggedIn(String cookie){
        return cookieDict.containsKey(cookie);
    }

    public String createGame(){
        String out = "";
        do{
            StringBuilder sb = new StringBuilder(gameAccessCodeLength);
            for(int i = 0; i < gameAccessCodeLength; i++)
                sb.append(alphabet.charAt(secureRandom.nextInt(alphabet.length())));
            out = sb.toString();
        }while(games.containsKey(out));

        Deck shuffledDeck = templateDeck.shuffle();

        Game newGame = new Game(shuffledDeck);



        games.put(out, newGame);

        return out;
    }

    public HashMap<String, Game> getGames() {
        return games;
    }
}
