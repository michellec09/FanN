package edu.tec.azuay.faan.persistence.utils;

import org.apache.commons.lang3.StringUtils;

public class Converter {

    public static String normalizeFileName(String fileName) {
        return StringUtils.stripAccents(fileName)
                .replaceAll("[^a-zA-Z0-9._-]", "");
    }
}
