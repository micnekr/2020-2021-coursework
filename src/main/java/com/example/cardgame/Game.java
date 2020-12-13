package com.example.cardgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game {

    private List<String> playerNames = new ArrayList<>();

    private List<List<Card>> plays = new ArrayList<>();
    private List<String> winners = new ArrayList<>();

    private int p1Score = 0;
    private int p2Score = 0;

    public Game(Deck deck) {
        //play the game
        Card p1Card, p2Card;

        while (true){
            p1Card = deck.dealCard();
            p2Card = deck.dealCard();

            if(p1Card == null) break;

            boolean firstWon;

            //determine who won
            if(p1Card.getCardType() == p2Card.getCardType()) firstWon = p1Card.getNum() > p2Card.getNum();
            else {firstWon = (p1Card.getType().ordinal() - p2Card.getCardType().ordinal() + 3) % 3 == 2;
            }

            if (firstWon) p1Score++;
            else p2Score++;

            plays.add(Arrays.asList(p1Card, p2Card));
            winners.add(firstWon ? "First won": "Second won");
        }
    }

    public boolean isPlayerInGame(String name){
        return playerNames.contains(name);
    }

    public void addPlayer(String username){
        playerNames.add(username);
    }

    public boolean isFull(){
        return playerNames.size() >= 2;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        for (String entry :
                toEntries()) {
            out.append(entry).append("\n");
        }
        return out.toString();
    }

    public List<String> toEntries(){
        List<String> out = new ArrayList<>();
        for (int i = 0; i < plays.size(); i++){
            StringBuilder entry = new StringBuilder();
            entry.append("player 1: ").append(plays.get(i).get(0)).append(" player 2: ").append(plays.get(i).get(1));
            entry.append("   ").append(winners.get(i)).append("  won\n");
            out.add(entry.toString());
        }
        out.add((new StringBuilder()).append("First score: ").append(p1Score).append(" second score: ").append(p2Score).toString());
        out.add(p1Score > p2Score? "First player won!" : "Second player won!");
        return out;
    }
}
