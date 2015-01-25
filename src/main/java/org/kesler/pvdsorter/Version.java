package org.kesler.pvdsorter;

/**
 * Класс для хранения версии приложения
 */
public abstract class Version {

    private static String version = "1.1.0.0";
    private static String releaseDate = "25.01.2015";

    public static String getVersion() {
        return version;
    }

    public static String getReleaseDate() {
        return releaseDate;
    }
}
