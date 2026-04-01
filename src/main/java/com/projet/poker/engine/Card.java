package main.java.com.projet.poker.engine;

public class Card {
    /*
     * Représentation d'une carte (valeur + couleur)
     */

    private CardValue value;
    private CardColor color;

    public Card(CardValue v, CardColor c) {
        this.value = v;
        this.color = c;
    }

    public CardValue getCardValue() {
        return value;
    }

    public CardColor getCardColor() {
        return color;
    }

    public void setColor(CardColor color) {
        this.color = color;
    }

    public void setValue(CardValue value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString() + color.toString();
    }
}
