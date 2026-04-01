package main.java.com.projet.poker.engine;

import java.util.HashMap;
import java.util.List;

public class HandData {
    
    private HandType type;
    private HashMap<CardValue, Integer> cardValues;
    private HashMap<CardColor, Integer> cardColors;
    private List<Card> sortedCards;

    public HandData(HashMap<CardValue, Integer> cardValues,
                HashMap<CardColor, Integer> cardColors,
                List<Card> sortedCards) {
        this.cardValues = cardValues;
        this.cardColors = cardColors;
        this.sortedCards = sortedCards;
        type = HandType.CARTE_HAUTE;
    }

    public HandType getType() {
        return type;
    }

    public HashMap<CardColor, Integer> getCardColors() {
        return cardColors;
    }

    public HashMap<CardValue, Integer> getCardValues() {
        return cardValues;
    }

    public List<Card> getSortedCards() {
        return sortedCards;
    }

    public void setType(HandType type) {
        this.type = type;
    }
}
