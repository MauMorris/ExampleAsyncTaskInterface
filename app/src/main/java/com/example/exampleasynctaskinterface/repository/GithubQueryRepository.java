package com.example.exampleasynctaskinterface.repository;

import com.example.exampleasynctaskinterface.datasource.GithubQueryTask;
import com.example.exampleasynctaskinterface.vm.RepositoryCallback;
import com.example.exampleasynctaskinterface.vm.ReturnDataFromTask;

import java.net.URL;

public class GithubQueryRepository implements RepositoryCallback {
    private static GithubQueryRepository sInstance;

    private GithubQueryRepository(){}

    public static GithubQueryRepository getInstance(){
        if(sInstance == null){
            sInstance = new GithubQueryRepository();
        }
        return sInstance;
    }

    @Override
    public void getDataFromGithub(URL url, final ReturnDataFromTask returnDataFromTask) {

        GithubQueryTask mTask = new GithubQueryTask(new TaskInterface() {
            @Override
            public void sucessResultPostExecute(String result) {
                returnDataFromTask.returnData(result);
            }

            @Override
            public void errorResultPostExecute(String error) {

            }
        });
        mTask.execute(url);
    }
}