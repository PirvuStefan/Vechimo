package org.example.vechimo;

public enum PlaceHolders {
    CNP("ɞ"),
    contractNumber("ě"),
    contractDate("Ĝ"),
    name("ɛ"),
    currentJob("Ĺ"),
    currentSalary("ĺ");

    private final String symbol;

    PlaceHolders(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }
}
