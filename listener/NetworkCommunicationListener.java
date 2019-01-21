package com.example.vmann.mapbox.listener;

public interface NetworkCommunicationListener extends Listener {
    void onStart();
    void onFailure(Exception e);
}