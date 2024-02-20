package com.michielo;

public enum Language {

    EN("EN"),
    NL("NL"),
    ES("ES"),
    FR("FR");

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

    public static String getAllAbbreviationsFormatted() {
        StringBuilder result = new StringBuilder();

        for (Language language : Language.values()) {
            result.append(language.name()).append(", ");
        }

        // Remove the trailing ", " if there are elements in the list
        if (result.length() > 0) {
            result.setLength(result.length() - 2);
        }

        return result.toString();
    }
}
