package com.hieunguyen.podcastai.enums;

public enum SupportedCountry {
    ARGENTINA("ar", "Argentina"),
    AUSTRALIA("au", "Australia"),
    AUSTRIA("at", "Austria"),
    BANGLADESH("bd", "Bangladesh"),
    BELGIUM("be", "Belgium"),
    BRAZIL("br", "Brazil"),
    BULGARIA("bg", "Bulgaria"),
    CANADA("ca", "Canada"),
    CHILE("cl", "Chile"),
    CHINA("cn", "China"),
    COLOMBIA("co", "Colombia"),
    CUBA("cu", "Cuba"),
    CZECH_REPUBLIC("cz", "Czech Republic"),
    EGYPT("eg", "Egypt"),
    ESTONIA("ee", "Estonia"),
    ETHIOPIA("et", "Ethiopia"),
    FINLAND("fi", "Finland"),
    FRANCE("fr", "France"),
    GERMANY("de", "Germany"),
    GHANA("gh", "Ghana"),
    GREECE("gr", "Greece"),
    HONG_KONG("hk", "Hong Kong"),
    HUNGARY("hu", "Hungary"),
    INDIA("in", "India"),
    INDONESIA("id", "Indonesia"),
    IRELAND("ie", "Ireland"),
    ISRAEL("il", "Israel"),
    ITALY("it", "Italy"),
    JAPAN("jp", "Japan"),
    KENYA("ke", "Kenya"),
    LEBANON("lb", "Lebanon"),
    LITHUANIA("lt", "Lithuania"),
    LATVIA("lv", "Latvia"),
    MALAYSIA("my", "Malaysia"),
    MEXICO("mx", "Mexico"),
    MOROCCO("ma", "Morocco"),
    NAMIBIA("na", "Namibia"),
    NETHERLANDS("nl", "Netherlands"),
    NEW_ZEALAND("nz", "New Zealand"),
    NIGERIA("ng", "Nigeria"),
    NORWAY("no", "Norway"),
    PAKISTAN("pk", "Pakistan"),
    PERU("pe", "Peru"),
    PHILIPPINES("ph", "Philippines"),
    POLAND("pl", "Poland"),
    PORTUGAL("pt", "Portugal"),
    ROMANIA("ro", "Romania"),
    RUSSIA("ru", "Russia"),
    SAUDI_ARABIA("sa", "Saudi Arabia"),
    SENEGAL("sn", "Senegal"),
    SERBIA("si", "Serbia"),
    SINGAPORE("sg", "Singapore"),
    SLOVAKIA("sk", "Slovakia"),
    SOUTH_AFRICA("za", "South Africa"),
    SOUTH_KOREA("kr", "South Korea"),
    SPAIN("es", "Spain"),
    SWEDEN("se", "Sweden"),
    SWITZERLAND("ch", "Switzerland"),
    TAIWAN("tw", "Taiwan"),
    TANZANIA("tz", "Tanzania"),
    THAILAND("th", "Thailand"),
    TURKEY("tr", "Turkey"),
    UGANDA("ug", "Uganda"),
    UKRAINE("ua", "Ukraine"),
    UNITED_ARAB_EMIRATES("ae", "United Arab Emirates"),
    UNITED_KINGDOM("gb", "United Kingdom"),
    UNITED_STATES("us", "United States"),
    VENEZUELA("ve", "Venezuela"),
    VIETNAM("vn", "Vietnam"),
    ZIMBABWE("zw", "Zimbabwe");

    private final String code;
    private final String displayName;

    SupportedCountry(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SupportedCountry fromCode(String code) {
        for (SupportedCountry country : values()) {
            if (country.code.equalsIgnoreCase(code)) {
                return country;
            }
        }
        throw new IllegalArgumentException("Unsupported country code: " + code);
    }
}

