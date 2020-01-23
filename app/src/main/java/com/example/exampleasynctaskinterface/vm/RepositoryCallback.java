package com.example.exampleasynctaskinterface.vm;

import java.net.URL;

public interface RepositoryCallback {
    void getDataFromGithub(URL url, ReturnDataFromTask returnDataFromTask);
}