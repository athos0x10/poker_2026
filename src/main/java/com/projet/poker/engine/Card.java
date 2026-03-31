package main.java.com.projet.poker.engine;

public class Card {

    private Value value;
    private Color color;

    public Card(Value v, Color c) {
        this.value = v;
        this.color = c;
    }

    public Value getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString() + color.toString();
    }
}
