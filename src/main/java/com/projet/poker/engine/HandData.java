package com.projet.poker.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HandData {
    /* Données d'une carte utiles pour trouver les combinaison
     * et évaluer les mains.
    */
    
     // Type de combinaison (ex: Quinte FLush, ...)
    private HandType type;

    // Nombre d'occurences en Valeur de chaque carte (ex: Il y a 2 Rois dans la main)
    private HashMap<CardValue, Integer> countCardValues; 

    // Nombre d'occurences en Couleur de chaque carte (ex: Il y a 2 Piques dans la main)
    private HashMap<CardColor, Integer> countCardColors;

    // Cartes triées par valeur décroissante: AS, ROI, DAME, ..., 2
    private List<Card> sortedCards;

    // Combinaison complète (ex: ROI, ROI , ROI, 10, 10, 2)
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
