package com.example.vmann.mapbox.utility;

import android.os.AsyncTask;

import com.example.vmann.mapbox.asyncTask.asyncTaskResult.AsyncTranslate;
import com.example.vmann.mapbox.listener.TranslateListener;
import com.example.vmann.mapbox.translation.Language;


public class TranslateUtility {

    public static void translateWithYandex(final String text,
                                           final Language to,
                                           final TranslateListener listener) {
        new AsyncTranslate(text, to,listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null, null, null);

    }
}
