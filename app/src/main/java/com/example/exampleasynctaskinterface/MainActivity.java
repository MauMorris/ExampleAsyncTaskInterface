package com.example.exampleasynctaskinterface;

import android.content.Context;
import android.os.Bundle;

import com.example.exampleasynctaskinterface.databinding.ActivityMainBinding;
import com.example.exampleasynctaskinterface.utilities.GithubQueryTask;
import com.example.exampleasynctaskinterface.utilities.NetworkUtils;
import com.example.exampleasynctaskinterface.utilities.TaskInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskInterface {

    public static final String SEARCH_QUERY_EXTRA = "query";
    public static final int SEARCH_QUERY_EXTRA_URL = 0;
    public static final int SEARCH_QUERY_EXTRA_RESULT = 1;

    //campos para ingresar el param de busqueda, mostrar la URL y la respuesta del request
    private ActivityMainBinding mDataBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mDataBinding.secondaryLayout.searchBoxEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        if(savedInstanceState != null) {
            if (savedInstanceState.containsKey(SEARCH_QUERY_EXTRA)) {
                ArrayList <String> queryUrl = savedInstanceState.getStringArrayList(SEARCH_QUERY_EXTRA);
                if (queryUrl != null) {
                    if(!queryUrl.isEmpty()){
                        mDataBinding.secondaryLayout.urlDisplayTextView.setText(queryUrl.get(SEARCH_QUERY_EXTRA_URL));
                        mDataBinding.secondaryLayout.githubSearchResultsJsonTextView.setText(queryUrl.get(SEARCH_QUERY_EXTRA_RESULT));
                    }
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        String queryUrl = mDataBinding.secondaryLayout.urlDisplayTextView.getText().toString();
        String resultQuery = mDataBinding.secondaryLayout.githubSearchResultsJsonTextView.getText().toString();

        ArrayList <String> savedData = new ArrayList<>();
        savedData.add(SEARCH_QUERY_EXTRA_URL, queryUrl);
        savedData.add(SEARCH_QUERY_EXTRA_RESULT, resultQuery);

        outState.putStringArrayList(SEARCH_QUERY_EXTRA, savedData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menu con el boton de research
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // maneja el clic del menu
        int itemThatWasClickedId = item.getItemId();

        if (itemThatWasClickedId == R.id.action_search) {
            hideKeyboard();
            makeGithubSearchQuery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is part of an interface that communicates AsyncTask onPostExecute method
     * with the Activity that are executing the Thread
     *
     * @param result is the value that onPostExecute method returns
     */
    @Override
    public void myResultPostExecute(String result) {
        mDataBinding.secondaryLayout.loadingIndicatorProgressBar.setVisibility(View.INVISIBLE);

        if(result != null && !TextUtils.isEmpty(result)){
            mDataBinding.secondaryLayout.githubSearchResultsJsonTextView.setText(result);
            showJsonDataView();
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void myResultPreExecute() {
        mDataBinding.secondaryLayout.loadingIndicatorProgressBar.setVisibility(View.VISIBLE);
        showNoView();
    }

    /**
     * This method retrieves the search text from the EditText, constructs the URL
     * (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our (not yet created) {@link GithubQueryTask}
     */
    private void makeGithubSearchQuery() {
        String githubQuery = mDataBinding.secondaryLayout.githubSearchResultsJsonTextView.getText().toString();

        if(TextUtils.isEmpty(githubQuery)){
            mDataBinding.secondaryLayout.urlDisplayTextView.setText(getString(R.string.error_no_query));
            return;
        }
        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);

        mDataBinding.secondaryLayout.urlDisplayTextView.setText(githubSearchUrl.toString());

        GithubQueryTask mTask = new GithubQueryTask(this);
        mTask.execute(githubSearchUrl);
    }

    /**
     * method that controls the views that will show to the user
     * makes visible the results from query and hides error message
     */
    private void showJsonDataView() {
        mDataBinding.secondaryLayout.errorMessageDisplayTextView.setVisibility(View.INVISIBLE);
        mDataBinding.secondaryLayout.githubSearchResultsJsonTextView.setVisibility(View.VISIBLE);
    }

    /**
     * makes visible error message and hides query result
     */
    private void showErrorMessage() {
        mDataBinding.secondaryLayout.errorMessageDisplayTextView.setVisibility(View.VISIBLE);
        mDataBinding.secondaryLayout.githubSearchResultsJsonTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * hides everything for the user and only shows the activity indicator
     */
    private void showNoView() {
        mDataBinding.secondaryLayout.errorMessageDisplayTextView.setVisibility(View.INVISIBLE);
        mDataBinding.secondaryLayout.githubSearchResultsJsonTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * This method hides the keyboard on the actual activity
     */
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputmKeyboard = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputmKeyboard != null) {
                inputmKeyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}