package com.example.vmann.mapbox.asyncTask.asyncTaskResult;

import android.os.AsyncTask;
import android.util.Log;

import com.augugrumi.ghioca.MyApplication;
import com.augugrumi.ghioca.R;
import com.augugrumi.ghioca.listener.AzureReverseImageSearchListener;

import it.polpetta.libris.image.ReverseImageSearch;
import it.polpetta.libris.image.azure.contract.IAzureImageSearchResult;

import java.net.URL;


public class AsyncAzureReverseImageSearch extends AsyncTask<Void, Void, Void> {

    //todo manage azure key
    private static String azureKey = MyApplication.getAppContext().getString(R.string.AZURE_KEY);

    private AzureReverseImageSearchListener listener;
    private IAzureImageSearchResult result;
    private boolean error;
    private String url;
    private Exception e;

    public AsyncAzureReverseImageSearch (String url, AzureReverseImageSearchListener listener) {
        this.listener = listener;
        this.url = url;
        error = false;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        listener.onStart();
    }


    @Override
    protected Void doInBackground(Void... params) {
        try {
            result = ReverseImageSearch
                    .getAzureServices(azureKey)
                    .imageSearchBuildQuery()
                    .setImage(new URL(url))
                    .build()
                    .search();
            Log.i("SEARCH_RESULT", result.toJSONString());
        } catch (Exception error) {
            e = error;
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        // TODO think if it could be the right thing to do
        if (error)
            listener.onFailure(e);
        else
            listener.onSuccess(result);
    }
}
