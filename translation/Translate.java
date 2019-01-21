package com.example.vmann.mapbox.translation;

import com.augugrumi.ghioca.MyApplication;
import com.augugrumi.ghioca.R;
import com.augugrumi.ghioca.translation.YandexTranslatorAPI;
import com.augugrumi.ghioca.translation.language.Language;
import android.util.Log;

import java.net.URL;
import java.net.URLEncoder;

/**
 * Makes calls to the Yandex machine translation web service API
 */
public final class Translate extends YandexTranslatorAPI {

    private static final String SERVICE_URL = "https://translate.yandex.net/api/v1.5/tr.json/translate?";
    private static final String TRANSLATION_LABEL = "text";

    //prevent instantiation
    private Translate(){};

    /**
     * Translates text from a given Language to another given Language using Yandex.
     *
     * @param text The String to translate.
     * @param from The language code to translate from.
     * @param to The language code to translate to.
     * @return The translated String.
     * @throws Exception on error.
     */
    public static String execute(final String text, final Language from, final Language to) throws Exception {
        validateServiceState(text);

        if (to == null) {

            // if from is null, error message. This save us a NPE
            // FIXME please change me :(
            return "Cannot translate in this language, sorry";
        }

    /*
    boolean print = to == null;
    Log.i("ISNULL", "Result: " + print);
    */

        final String params =
                PARAM_API_KEY + URLEncoder.encode(MyApplication.getAppContext().getString(R.string.YANDEX_KEY),ENCODING)
                        + PARAM_LANG_PAIR + URLEncoder.encode(from.toString(),ENCODING) + URLEncoder.encode("-",ENCODING) + URLEncoder.encode(to.toString(),ENCODING)
                        + PARAM_TEXT + URLEncoder.encode(text,ENCODING);
        final URL url = new URL(SERVICE_URL + params);
        return retrievePropArrString(url, TRANSLATION_LABEL).trim();
    }

    private static void validateServiceState(final String text) throws Exception {
        final int byteLength = text.getBytes(ENCODING).length;
        if(byteLength>10240) { // TODO What is the maximum text length allowable for Yandex?
            throw new RuntimeException("TEXT_TOO_LARGE");
        }
    }
}
