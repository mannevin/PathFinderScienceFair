package com.example.vmann.mapbox.listener;

public interface GoogleReverseImageSearchListener extends SearchingListener {
    void onSuccess(IGoogleImageSearchResult result);
}