package main.java.com.projet.poker.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HandData {
    
    private HandType type;
    private HashMap<CardValue, Integer> countCardValues;
    private HashMap<CardColor, Integer> countCardColors;
    private List<Card> sortedCards;
    private List<Card> bestFiveCards;

    public HandData(HashMap<CardValue, Integer> cardValues,
                HashMap<CardColor, Integer> cardColors,
                List<Card> sortedCards) {
        this.countCardValues = cardValues;
        this.countCardColors = cardColors;
        this.sortedCards = sortedCards;
        bestFiveCards = new ArrayList<>();
        type = HandType.CARTE_HAUTE;
    }

    public HandType getType() {
        return type;
    }

    public HashMap<CardColor, Integer> getCountCardColors() {
        return countCardColors;
    }

    public HashMap<CardValue, Integer> getCountCardValues() {
        return countCardValues;
    }

    public List<Card> getSortedCards() {
        return sortedCards;
    }

    public void setType(HandType type) {
        this.type = type;
    }

    public List<Card> getBestFiveCards() {
        return bestFiveCards;
    }

    public void setBestFiveCards(List<Card> bestFiveCards) {
        this.bestFiveCards = bestFiveCards;
    }
}
