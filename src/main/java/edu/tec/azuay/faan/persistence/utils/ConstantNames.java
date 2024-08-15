package edu.tec.azuay.faan.persistence.utils;

import java.util.HashMap;
import java.util.Map;

public class ConstantNames {

    public static final String IMAGES = "uploads/images";
    public static final Map<String, String> FOLDER_MAPPING = new HashMap<>();

    static {
        FOLDER_MAPPING.put("images", IMAGES);
    }
}
