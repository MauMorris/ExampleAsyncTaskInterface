package com.example.exampleasynctaskinterface.repository;

public interface TaskInterface {
    void sucessResultPostExecute(String result);
    void errorResultPostExecute(String error);
}