package com.example.vmann.mapbox.listener;

public interface WatsonOCRListener extends SearchingListener {
    void onSuccess(IIBMOcrResult result);
}