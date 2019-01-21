package com.example.vmann.mapbox;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * Handwritten digit detector.
 * <p/>
 * Created by miyoshi on 16/01/17.
 */
public class DigitDetector {
    static {
        System.loadLibrary("tensorflow_mnist");
    }

    private native int init(AssetManager assetManager, String model);

    /**
     * draw pixels
     */
    public native int detectDigit(int[] pixels);

    public boolean setup(Context context) {
        AssetManager assetManager = context.getAssets();

        // model from beginner tutorial
        //int ret = init(assetManager, "file:///android_asset/beginner-graph.pb");

        // model from expert tutorial
        int ret = init(assetManager, "file:///android_asset/expert-graph.pb");

        return ret >= 0;
    }
}
