package com.example.cardgame;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class WebController {

    @GetMapping("/login")
    public String loginGet(Model model) {
        return "login";
    }

    @GetMapping("/signup")
    public ModelAndView signupGet(Map<String, Object> model) {
        model.put("unsuccessfulMessage", "");
        System.out.println(model);
        return new ModelAndView("signup", model);
    }

    @RequestMapping(value = "/selectGame", method = RequestMethod.GET)
    public ModelAndView selectGameGet(@CookieValue(value = "auth-token", defaultValue = "not found") String authToken) {
        //check if logged in
        if (!com.example.cardgame.Controller.getInstance().checkIfLoggedIn(authToken)) return new ModelAndView("login");
        System.out.println("Logged in");
        return new ModelAndView("selectGame");
    }

    @RequestMapping(value = "/loginForm", method = RequestMethod.POST)
    public RedirectView loginPost(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password, HttpServletResponse response) {
        com.example.cardgame.Controller controller = com.example.cardgame.Controller.getInstance();

        boolean isCorrectLogin = controller.checkUserLogin(username, password);

        System.out.println("isCorrectLogin = " + isCorrectLogin);
        ;

        if (!isCorrectLogin) return new RedirectView("login");
        else {
            //set the cookie
            Cookie cookie = new Cookie("auth-token", controller.rememberCookie(username));
            response.addCookie(cookie);
            return new RedirectView("selectGame");
        }
    }

    @RequestMapping(value = "/signupForm", method = RequestMethod.POST)
    public ModelAndView signupPost(@RequestParam(name = "username") String username, @RequestParam(name = "password") String password, Map<String, Object> model) {
        com.example.cardgame.Controller controller = com.example.cardgame.Controller.getInstance();

        boolean wasSuccessful = controller.signUp(username, password);

        System.out.println("wasSuccessful = " + wasSuccessful);

        if (wasSuccessful) return new ModelAndView("login");
        else {
            model.put("unsuccessfulMessage", "This username is taken.");
            return new ModelAndView("signup", model);
        }
    }

    @RequestMapping(value = "/isGameAvaliable", method = RequestMethod.GET)
    @ResponseBody
    public Boolean checkIfCodeIsValid(@RequestParam(name = "gameId") String gameId, @CookieValue(value = "auth-token", defaultValue = "not found") String authToken, HttpServletResponse response) {
        if (!com.example.cardgame.Controller.getInstance().checkIfLoggedIn(authToken)) {
            response.setStatus(401);
            return false;
        }
        com.example.cardgame.Controller controller = com.example.cardgame.Controller.getInstance();
        System.out.println(gameId);

        Game currentGame = controller.getGames().get(gameId);
        if(currentGame == null) return false;
        return !currentGame.isFull();
    }

    @RequestMapping(value = "/createGame", method = RequestMethod.POST)
    @ResponseBody
    public String createGamePost(@CookieValue(value = "auth-token", defaultValue = "not found") String authToken, HttpServletResponse response) {
        if (!com.example.cardgame.Controller.getInstance().checkIfLoggedIn(authToken)) {
            response.setStatus(401);
            return "";
        }
        com.example.cardgame.Controller controller = com.example.cardgame.Controller.getInstance();
        System.out.println("Creating a game");
        return controller.createGame();
    }

    @RequestMapping(value = "/game", method = RequestMethod.GET)
    public ModelAndView joinGameGet(@CookieValue(value = "auth-token", defaultValue = "not found") String authToken, @RequestParam(name = "gameId", defaultValue = "") String gameId, Map<String, Object> model) {
        com.example.cardgame.Controller controller = com.example.cardgame.Controller.getInstance();

        System.out.println("Tries to join");

        //check auth
        if (!com.example.cardgame.Controller.getInstance().checkIfLoggedIn(authToken)) return new ModelAndView("login");

        //get username
        String username = controller.getUserDataFromAuthToken(authToken);

        Game currentGame = controller.getGames().get(gameId);

        //if the game does not exist or it is full, return
        if(currentGame == null || currentGame.isFull()) {
            model.put("errorMessage", "The game code is incorrect. The game does not exist or it is full.");
            return new ModelAndView("selectGame", model);
        }

        model.put("gameId", gameId);
        model.put("results", currentGame.toEntries());

        if(!currentGame.isPlayerInGame(username)) currentGame.addPlayer(username);

        System.out.println("joined the game");

        return new ModelAndView("game", model);
    }
}
