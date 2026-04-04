package main.java.com.projet.poker.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Deck {
    /* Le deck est l'ensemble des cartes utilisables dans le jeu */

    private List<Card> cards;
    private Random rand;

    public Deck() {
        this.cards = createStandardDeck();
        this.rand = new Random();
    }

    /*
     * Crée un deck standard de 52 cartes
     */
    private List<Card> createStandardDeck() {
        List<Card> deck = new ArrayList<>();

        for (CardValue v : CardValue.values()) {
            for (CardColor c : CardColor.values()) {
                deck.add(new Card(v, c));
            }
        }

        return deck;
    }

    /*
     * Mélange les cartes du deck
     * Algorithme de Fisher-Yates
     */
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

    /*
     * Retire et retourne la première carte du deck
     */
    public Card drawCard() {
        return cards.remove(0);
    }

    /* Retire et retourne les n premières cartes du deck */
    public List<Card> drawCards(int n) {
        List<Card> cardsToDraw = new ArrayList<>(cards.subList(0, n));
        cards.removeAll(cardsToDraw);
        return cardsToDraw;
    }

    /*
     * Retourne la liste des cartes du deck
     */
    public List<Card> getCards() {
        return cards;
    }

    /*
     * Définit les cartes du deck
     */
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

