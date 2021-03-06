package com.example.vmann.mapbox;

import org.apache.commons.lang3.text.WordUtils;

import java.util.ArrayList;



public class ShareFragmentImageRecognition extends ShareFragment {
    @Override
    protected String shareContent() {
        ArrayList<String> results = ((ReverseImageSearchResultActivity)getActivity()).getResults();
        String description = ((ReverseImageSearchResultActivity)getActivity()).getDescription();

        StringBuilder toShare = new StringBuilder(description);
        toShare.append("\n");
        for (String res : results) {
            toShare.append("#");
            toShare.append(WordUtils.uncapitalize((WordUtils.capitalize(res)).replaceAll(" ", "")));
            toShare.append(" ");
        }

        toShare.append("#GhioCa");

        return toShare.toString();
    }
}