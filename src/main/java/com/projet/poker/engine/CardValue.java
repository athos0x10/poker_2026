package main.java.com.projet.poker.engine;

public enum CardValue {
    DEUX(2),
    TROIS(3),
    QUATRE(4),
    CINQ(5),
    SIX(6),
    SEPT(7),
    HUIT(8),
    NEUF(9),
    DIX(10),
    VALET(11),
    DAME(12),
    ROI(13),
    AS(14);

    private final int value;
    
    private CardValue(int value) {
        this.value = value;
    }

    public static CardValue valueOfValue(int value) {
        for (CardValue v : values()) {
            if (java.util.Objects.equals(v.value, value)) {
                return v;
            }
        }

        return null;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (value <= 10) {
            return Integer.toString(value);
        } else {
            switch (value) {
                case 11:
                    return "J";
                case 12:
                    return "Q";
                case 13:
                    return "K";
                case 14:
                    return "AS";
                default:
                    break;
            }
        }

        return "";
    }
}

