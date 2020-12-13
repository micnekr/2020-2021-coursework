package com.example.cardgame;

import java.util.concurrent.ThreadLocalRandom;

public class Deck {
    private Card[] cards;

    private CardTypes[] cardTypes;
    private int cardOfTypeNum;

    private int lastCard;

    public Deck(CardTypes[] _cardTypes, int _cardOfTypeNum){
        cardTypes = _cardTypes.clone();
        cardOfTypeNum = _cardOfTypeNum;

        cards = new Card[_cardTypes.length * _cardOfTypeNum];
        for(int i = 0; i < _cardOfTypeNum; i++){
            for(int j = 0; j < _cardTypes.length; j++){
                cards[j * _cardOfTypeNum + i] = new Card(_cardTypes[j], i + 1);
            }
        }

        lastCard = cards.length - 1;
    }

    Deck(Card[] _cards, CardTypes[] _cardTypes, int _cardOfTypeNum){
        cardTypes = _cardTypes.clone();
        cardOfTypeNum = _cardOfTypeNum;

        cards = _cards.clone();

        lastCard = cards.length - 1;
    }

    public String toString(){
        String out = "";
        for(int i = 0; i < cards.length; i++){
            out +="A card with a type " + cards[i].getType() + " numbered " + cards[i].getNum() + "\n";
        }
        return out;
    }

    public Deck shuffle(){
        Card[] newCards = cards.clone();

        //https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle
        for(int i = newCards.length - 1; i > 1; i--){
            int j = ThreadLocalRandom.current().nextInt(0, i + 1);
            //swap
            Card cardA = newCards[i];
            Card cardB = newCards[j];

            newCards[j] = cardA;
            newCards[i] = cardB;
        }

        return new Deck(newCards, cardTypes, cardOfTypeNum);
    }

    public Card dealCard(){
        if(lastCard < 0) return null;
        Card newCard = cards[lastCard];
        lastCard--;
        return newCard;
    }
}
