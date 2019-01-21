package com.example.vmann.mapbox.translation;

import com.augugrumi.ghioca.translation.ApiKeys;
import com.augugrumi.ghioca.translation.YandexTranslatorAPI;
import com.augugrumi.ghioca.translation.language.Language;
import com.augugrumi.ghioca.translation.translate.Translate;

import java.net.URL;
import java.net.URLEncoder;

/**
 * Provides an interface to the Yandex Translator Detect service method
 */
public final class Detect extends YandexTranslatorAPI {
    private static final String SERVICE_URL = "https://translate.yandex.net/api/v1.5/tr.json/detect?";
    private static final String DETECTION_LABEL = "lang";
    static {
        Translate.setKey(ApiKeys.YANDEX_API_KEY);
    }
    // prevent instantiation
    private Detect(){};

    /**
     * Detects the language of a supplied String.
     *
     * @param text The String to detect the language of.
     * @return A String containing the language
     * @throws Exception on error.
     */
    public static Language execute(final String text) throws Exception {
        validateServiceState(text);
        final String params =
                PARAM_API_KEY + URLEncoder.encode(apiKey,ENCODING)
                        + PARAM_TEXT + URLEncoder.encode(text,ENCODING);
        final URL url = new URL(SERVICE_URL + params);
        return Language.fromString(retrievePropString(url, DETECTION_LABEL));
    }

    private static void validateServiceState(final String text) throws Exception {
        final int byteLength = text.getBytes(ENCODING).length;
        if(byteLength>10240) { // TODO What is the maximum text length allowable for Yandex?
            throw new RuntimeException("TEXT_TOO_LARGE - Yandex Translator (Detect) can handle up to 10,240 bytes per request");
        }
    }
}