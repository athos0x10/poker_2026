package main.java.com.projet.poker.engine;

public enum HandType {
    QUINTE_FLUSH(9),
    CARRE(8),
    FULL(7),
    FLUSH(6),
    QUINTE(5),
    BRELAN(4),
    DOUBLE_PAIRE(3),
    PAIRE(2),
    CARTE_HAUTE(1);

    private final int score;
    
    private HandType(int score) {
        this.score = score;
    }

    public static HandType valueOfValue(int score) {
        for (HandType h : values()) {
            if (java.util.Objects.equals(h.score, score)) {
                return h;
            }
        }

        return null;
    }

    public int getScore() {
        return score;
    }
}
