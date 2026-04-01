package main.java.com.projet.poker.engine;

public enum CardColor {
    /*
     * Les couleurs des cartes
     */

    PIQUE("♠"),
    TREFLE("♣"),
    COEUR("♥"),
    CARREAU("♦");

    private final String symbol;
    
    private CardColor(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
