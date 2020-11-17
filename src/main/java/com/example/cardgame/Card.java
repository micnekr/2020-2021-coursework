package com.example.cardgame;

enum CardTypes {
    YELLOW,
    RED,
    BLACK
}

public class Card {
    private CardTypes cardType;
    private int num;

    Card(CardTypes _cardType, int _num){
        cardType = _cardType;
        num = _num;
    }

    public CardTypes getType(){
        return cardType;
    }

    public int getNum(){
        return num;
    }

    public boolean isBetter(Card other){
        return false;
    }
}
