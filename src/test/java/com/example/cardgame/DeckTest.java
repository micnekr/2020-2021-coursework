package com.example.cardgame;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void testToStringAndShuffle() {
        CardTypes[] typeNames = {CardTypes.BLACK, CardTypes.RED, CardTypes.YELLOW};
        Deck deck = new Deck(typeNames, 10);

        try{
            String previousString = deck.toString();
            Deck newDeck = deck.shuffle();

            //the first deck should not have changed
            assert(previousString.equals(deck.toString()));
            //the deck should have been shuffled
            assertFalse(newDeck.toString().equals(previousString));
        }catch(Exception e){
            fail("Exception at toString or shuffle");
        }
    }
}