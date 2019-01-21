package com.example.vmann.mapbox.listener;

public interface AzureOcrSearchListener extends SearchingListener {
    void onSuccess(IAzureOcrResult result);
}