package com.example.vmann.mapbox.listener;

public interface SavingListener extends Listener {
    void onSuccess();
    void onFailure(Throwable error);
}