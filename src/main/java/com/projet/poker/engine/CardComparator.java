package com.projet.poker.engine;

import java.util.Comparator;

public class CardComparator implements Comparator<Card> {
    public int compare(Card a, Card b) {
        int aScore = a.getCardValue().getValue();
        int bScore = b.getCardValue().getValue();
        if (aScore < bScore) return 1;
        if (aScore > bScore) return -1;
        return 0;
    }
}
