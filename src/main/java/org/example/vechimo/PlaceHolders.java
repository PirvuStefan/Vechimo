package org.example.vechimo;

import java.util.HashMap;
import java.util.Map;

public enum PlaceHolders {
    CNP("ɞ"),
    today("ſ"),
    contractNumber("ě"),
    contractDate("Ĝ"),
    name("ɛ"),
    currentJob("Ĺ"),
    currentSalary("ĺ"),
    intervention("Ŕ"),
    date("ŕ"),
    act("ś"),
    job("ū"),
    salary("ų");

    private final String format = "dd.MM.yyyy";
    private final String symbol;



    PlaceHolders(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }

}