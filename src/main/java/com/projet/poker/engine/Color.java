package main.java.com.projet.poker.engine;

public enum Color {
    PIQUE("♠"),
    TREFLE("♣"),
    COEUR("♥"),
    CARREAU("♦");

    private final String symbol;
    
    private Color(String symbol) {
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
