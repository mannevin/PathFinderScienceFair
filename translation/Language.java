package com.example.vmann.mapbox.translation;

import java.util.ArrayList;

/**
 * Language - an enum of language codes supported by the Yandex API
 */
public enum Language {
    ALBANIAN("sq"),
    ARMENIAN("hy"),
    AZERBAIJANI("az"),
    BELARUSIAN("be"),
    BULGARIAN("bg"),
    CATALAN("ca"),
    CROATIAN("hr"),
    CZECH("cs"),
    DANISH("da"),
    DUTCH("nl"),
    ENGLISH("en"),
    ESTONIAN("et"),
    FINNISH("fi"),
    FRENCH("fr"),
    GERMAN("de"),
    GEORGIAN("ka"),
    GREEK("el"),
    HUNGARIAN("hu"),
    ITALIAN("it"),
    LATVIAN("lv"),
    LITHUANIAN("lt"),
    MACEDONIAN("mk"),
    NORWEGIAN("no"),
    POLISH("pl"),
    PORTUGUESE("pt"),
    ROMANIAN("ro"),
    RUSSIAN("ru"),
    SERBIAN("sr"),
    SLOVAK("sk"),
    SLOVENIAN("sl"),
    SPANISH("es"),
    SWEDISH("sv"),
    TURKISH("tr"),
    UKRAINIAN("uk");

    /**
     * String representation of this language.
     */
    private final String language;

    /**
     * Enum constructor.
     * @param pLanguage The language identifier.
     */
    private Language(final String pLanguage) {
        language = pLanguage;
    }

    public static Language fromString(final String pLanguage) {
        for (Language l : values()) {
            if (l.toString().equalsIgnoreCase(pLanguage)) {
                return l;
            }
        }
        return null;
    }

    /**
     * Returns the String representation of this language.
     * @return The String representation of this language.
     */
    @Override
    public String toString() {
        return language;
    }

    private static final ArrayList<String> languages = new ArrayList<>();

    static {
        languages.add("sq");
        languages.add("hy");
        languages.add("az");
        languages.add("be");
        languages.add("bg");
        languages.add("ca");
        languages.add("hr");
        languages.add("cs");
        languages.add("da");
        languages.add("nl");
        languages.add("en");
        languages.add("et");
        languages.add("fi");
        languages.add("fr");
        languages.add("de");
        languages.add("ka");
        languages.add("el");
        languages.add("hu");
        languages.add("it");
        languages.add("lv");
        languages.add("lt");
        languages.add("mk");
        languages.add("no");
        languages.add("pl");
        languages.add("pt");
        languages.add("ro");
        languages.add("ru");
        languages.add("sr");
        languages.add("sk");
        languages.add("sl");
        languages.add("es");
        languages.add("sv");
        languages.add("tr");
        languages.add("uk");
    }

    public static ArrayList<String> getAllSupportedLangagesCode() {
        return languages;
    }
}