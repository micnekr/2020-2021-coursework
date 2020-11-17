package com.example.cardgame;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    @Test
    void testHashingAndPasswordChecks() {
        String[] stringsToHash = {"test", "hi", "hello"};
        String[] resultHashes = {"O6eawxQS$65536$BohJVfCqzE8us7dMiKIX7w==", "LmDDxZdl$65536$prm+axAhZ+zlqYHEeZh9cQ==", "02JklH2w$65536$lsnTTWAy4rVP2J8O3FRnZg=="};

        for(int i = 0; i < stringsToHash.length; i++){
            try {
                assert (Controller.getInstance().checkPassword(stringsToHash[i], resultHashes[i]));
            }catch(Exception e){
                fail("Exception on password check " + e.getMessage());
            }
        }

        String[] wrongStringsToHash = {"test", "hi", "hello"};
        String[] wrongResultHashes = {"O6eawxQS$65536$BohJVfCqzE8us7dMiKIX8w==", "LmDDxZdl$65537$prm+axAhZ+zlqYHEeZh9cQ==", "02kklH2w$65536$lsnTTWAy4rVP2J8O3FRnZg=="};

        for(int i = 0; i < stringsToHash.length; i++){
            try {
                assertFalse(Controller.getInstance().checkPassword(wrongStringsToHash[i], wrongResultHashes[i]));
            }catch(Exception e){
                fail("Exception on password check " + e.getMessage());
            }
        }

    }

    @Test
    void testSecureEquals() {
        String[] controlStrings = {"abc", "test", "Hello, Mr. Scott"};
        String[] correctStrings = {"abc", "test", "Hello, Mr. Scott"};
        String[] incorrectStrings = {"abd", "a", "Goodbye, Mr. Scott, your password is wrong"};
        for(int i = 0; i < controlStrings.length; i++) {
            String controlString = controlStrings[i];
            String incorrectString = incorrectStrings[i];
            String correctString = correctStrings[i];
            assert(Controller.getInstance().secureStringEquals(controlString, correctString));
            assertFalse(Controller.getInstance().secureStringEquals(controlString, incorrectString));
        }
    }

}