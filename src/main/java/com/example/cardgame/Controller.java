package com.example.cardgame;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;


public class Controller {
    private static Controller INSTANCE;

    private static Deck templateDeck;
    private static SecureRandom secureRandom;
    private Base64.Encoder encoder;

    private static int saltLength = 6;
    private static int defaultNumHashIterations = 65536;

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
        byte[] salt = new byte[saltLength];
        secureRandom.nextBytes(salt);
        return hashPassword(password, salt, defaultNumHashIterations);
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
}
