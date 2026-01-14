package org.example.vechimo;

public enum PlaceHolders {
    CNP("ε"),
    contractNumber("№"),
    contractDate("⏤"),
    name("unnamed"),
    job("unassigned");

    private final String symbol;

    PlaceHolders(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }
}
