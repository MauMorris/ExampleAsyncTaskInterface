package com.example.exampleasynctaskinterface.utilities;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;

public class GithubQueryTask extends AsyncTask<URL, Void, String> {
    private TaskInterface myInterface;

    public GithubQueryTask(TaskInterface myInterface) {
        this.myInterface = myInterface;
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL searchUrl = urls[0];
        String githubSearchResults;

        try {
            githubSearchResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            return  githubSearchResults;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null && !s.equals(""))
            myInterface.myResult(s);
    }
}