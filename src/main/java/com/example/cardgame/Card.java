package com.example.cardgame;

public class Card {
    public CardTypes getCardType() {
        return cardType;
    }

    public void setCardType(CardTypes cardType) {
        this.cardType = cardType;
    }

    public void setNum(int num) {
        this.num = num;
    }

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

    @Override
    public String toString() {
        return "Card " +
                cardType +
                " of value " + num;
    }
}
