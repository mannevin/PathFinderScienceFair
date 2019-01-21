package com.example.vmann.mapbox.listener;

public interface UploadingListener {
    void onStart();
    void onProgressUpdate(int progress);
    void onFinish(String url);
    void onFailure(Throwable error);
}