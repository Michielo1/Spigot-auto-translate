package com.michielo.translation;

public enum Language {

    EN("ENG"),
    NL("NLD"),
    ES("SPA"),
    FR("FRA");

    private final String abbreviation;

    // Constructor
    private Language(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static String getAbbreviation(String input) {
        for (Language language : Language.values()) {
            if (language.name().equals(input) || language.getAbbreviation().equals(input)) {
                return language.getAbbreviation();
            }
        }
        return null; // Handle invalid input
    }
}
