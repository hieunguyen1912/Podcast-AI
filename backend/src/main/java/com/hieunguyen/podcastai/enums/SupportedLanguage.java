package com.hieunguyen.podcastai.enums;

public enum SupportedLanguage {
    ARABIC("ar", "Arabic"),
    BENGALI("bn", "Bengali"),
    BULGARIAN("bg", "Bulgarian"),
    CATALAN("ca", "Catalan"),
    CHINESE("zh", "Chinese"),
    CZECH("cs", "Czech"),
    DUTCH("nl", "Dutch"),
    ENGLISH("en", "English"),
    ESTONIAN("et", "Estonian"),
    FINNISH("fi", "Finnish"),
    FRENCH("fr", "French"),
    GERMAN("de", "German"),
    GREEK("el", "Greek"),
    GUJARATI("gu", "Gujarati"),
    HEBREW("he", "Hebrew"),
    HINDI("hi", "Hindi"),
    HUNGARIAN("hu", "Hungarian"),
    INDONESIAN("id", "Indonesian"),
    ITALIAN("it", "Italian"),
    JAPANESE("ja", "Japanese"),
    KOREAN("ko", "Korean"),
    LATVIAN("lv", "Latvian"),
    LITHUANIAN("lt", "Lithuanian"),
    MALAYALAM("ml", "Malayalam"),
    MARATHI("mr", "Marathi"),
    NORWEGIAN("no", "Norwegian"),
    POLISH("pl", "Polish"),
    PORTUGUESE("pt", "Portuguese"),
    PUNJABI("pa", "Punjabi"),
    ROMANIAN("ro", "Romanian"),
    RUSSIAN("ru", "Russian"),
    SERBIAN("sl", "Serbian"),
    SLOVAK("sk", "Slovak"),
    SPANISH("es", "Spanish"),
    SWEDISH("sv", "Swedish"),
    TAMIL("ta", "Tamil"),
    TELUGU("te", "Telugu"),
    THAI("th", "Thai"),
    TURKISH("tr", "Turkish"),
    UKRAINIAN("uk", "Ukrainian"),
    VIETNAMESE("vi", "Vietnamese");

    private final String code;
    private final String displayName;

    SupportedLanguage(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SupportedLanguage fromCode(String code) {
        for (SupportedLanguage lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) {
                return lang;
            }
        }
        throw new IllegalArgumentException("Unsupported language code: " + code);
    }
}

