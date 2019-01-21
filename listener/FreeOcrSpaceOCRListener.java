package com.example.vmann.mapbox.listener;

import com.example.vmann.mapbox.asyncTask.asyncTaskResult.FreeOcrSpaceOCRResult;

public interface FreeOcrSpaceOCRListener extends SearchingListener {
    void onSuccess(FreeOcrSpaceOCRResult result);
}