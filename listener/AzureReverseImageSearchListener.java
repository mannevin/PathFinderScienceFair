package com.example.vmann.mapbox.listener;

import it.polpetta.libris.image.azure.contract.IAzureImageSearchResult;

public interface AzureReverseImageSearchListener extends SearchingListener {
    void onSuccess(IAzureImageSearchResult result);
}