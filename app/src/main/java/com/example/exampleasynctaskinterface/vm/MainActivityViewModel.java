package com.example.exampleasynctaskinterface.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.exampleasynctaskinterface.datasource.utilities.NetworkUtils;
import com.example.exampleasynctaskinterface.repository.GithubQueryRepository;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivityViewModel extends AndroidViewModel {
    private MutableLiveData<String> mDataFromQuery;
    private RepositoryCallback mRepoCallback;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        if (mDataFromQuery == null) {
            mDataFromQuery = new MutableLiveData<>();
            mRepoCallback = GithubQueryRepository.getInstance();
        }
    }

    public MutableLiveData<String> getDataFromQuery() {
        return mDataFromQuery;
    }

    public String getUrl(String githubQuery) {
        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);
        return githubSearchUrl.toString();
    }

    public void initSearch(String query) {
        try {
            URL url = new URL(query);

            mRepoCallback.getDataFromGithub(url, new ReturnDataFromTask() {
                @Override
                public void returnData(String data) {
                    getDataFromQuery().setValue(data);
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}