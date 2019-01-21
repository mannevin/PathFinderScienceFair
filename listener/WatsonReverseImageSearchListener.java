package com.example.vmann.mapbox.listener;

public interface WatsonReverseImageSearchListener extends SearchingListener {
    void onSuccess(IIBMImageSearchResult result);
}