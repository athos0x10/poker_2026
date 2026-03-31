package main.java.com.projet.poker.engine;

public enum Value {
    DEUX("2"),
    TROIS("3"),
    QUATRE("4"),
    CINQ("5"),
    SIX("6"),
    SEPT("7"),
    HUIT("8"),
    NEUF("9"),
    DIX("10"),
    VALET("J"),
    DAME("Q"),
    ROI("K"),
    AS("AS");

    private final String name;
    
    private Value(String name) {
        this.name = name;
    }

    public static Value valueOfName(String name) {
        for (Value v : values()) {
            if (java.util.Objects.equals(v.name, name)) {
                return v;
            }
        }

        return null;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

