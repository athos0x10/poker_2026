package main.java.com.projet.poker.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {
    private List<Card> cards;
    private Random rand;

    public Deck() {
        this.cards = createStandardDeck();
        this.rand = new Random();
    }

    private List<Card> createStandardDeck() {
        List<Card> deck = new ArrayList<>();

        for (Value v : Value.values()) {
            for (Color c : Color.values()) {
                deck.add(new Card(v, c));
            }
        }

        return deck;
    }

    public void shuffle() {
        int n = cards.size();
        int j;
        
        for (int i = n - 1; i >= 0; i--) {
            j = rand.nextInt(i + 1);
            Card tmp = cards.get(i);
            cards.set(i, cards.get(j));
            cards.set(j, tmp);
        }
    }

    public Card popCard() {
        return cards.removeFirst();
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> crds) {
        this.cards = crds;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("[");
        
        for (Card c : cards) {
            s.append("-" + c.toString() + "\n");
        }
        
        return s.toString();
    }
}

