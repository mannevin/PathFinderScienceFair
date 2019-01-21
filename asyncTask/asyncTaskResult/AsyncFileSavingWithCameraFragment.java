package com.example.vmann.mapbox.asyncTask.asyncTaskResult;

import android.os.AsyncTask;

import com.augugrumi.ghioca.MyApplication;
import com.augugrumi.ghioca.listener.SavingListener;

import java.io.File;



public class AsyncFileSavingWithCameraFragment extends AsyncTask<Void, Void, Void> {

    private SavingListener listener;
    private String filename;

    public AsyncFileSavingWithCameraFragment(SavingListener listener, String filename) {
        this.listener = listener;
        this.filename = filename;
    }

    @Override
    protected Void doInBackground(Void... params) {
        boolean b = false;
        File f;
        while (!b) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                listener.onFailure(e);
            }
            f = new File(MyApplication.appFolderPath, filename);
            b = f.exists();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onSuccess();
    }
}