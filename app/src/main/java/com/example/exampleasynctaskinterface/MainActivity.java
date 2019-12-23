package com.example.exampleasynctaskinterface;

import android.content.Context;
import android.os.Bundle;

import com.example.exampleasynctaskinterface.utilities.GithubQueryTask;
import com.example.exampleasynctaskinterface.utilities.NetworkUtils;
import com.example.exampleasynctaskinterface.utilities.TaskInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TaskInterface {

    public static final String SEARCH_QUERY_EXTRA = "query";
    public static final int SEARCH_QUERY_EXTRA_URL = 0;
    public static final int SEARCH_QUERY_EXTRA_RESULT = 1;

    //campos para ingresar el param de busqueda, mostrar la URL y la respuesta del request
    private EditText mSearchBoxEditText;
    private TextView mUrlDisplayTextView;

    private TextView mSearchResultsTextView;
    private TextView mErrorMessageTextView;
    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSearchBoxEditText = findViewById(R.id.et_search_box);
        mUrlDisplayTextView = findViewById(R.id.tv_url_display);

        mSearchResultsTextView = findViewById(R.id.tv_github_search_results_json);
        mErrorMessageTextView = findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        mSearchBoxEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
                        mUrlDisplayTextView.setText(queryUrl.get(SEARCH_QUERY_EXTRA_URL));
                        mSearchResultsTextView.setText(queryUrl.get(SEARCH_QUERY_EXTRA_RESULT));
                    }
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        String queryUrl = mUrlDisplayTextView.getText().toString();
        String resultQuery = mSearchResultsTextView.getText().toString();

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
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if(result != null && !TextUtils.isEmpty(result)){
            mSearchResultsTextView.setText(result);
            showJsonDataView();
        } else {
            showErrorMessage();
        }
    }

    @Override
    public void myResultPreExecute() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        showNoView();
    }

    /**
     * This method retrieves the search text from the EditText, constructs the URL
     * (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our (not yet created) {@link GithubQueryTask}
     */
    private void makeGithubSearchQuery() {
        String githubQuery = mSearchBoxEditText.getText().toString();

        if(TextUtils.isEmpty(githubQuery)){
            mUrlDisplayTextView.setText(getString(R.string.error_no_query));
            return;
        }
        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);

        mUrlDisplayTextView.setText(githubSearchUrl.toString());

        GithubQueryTask mTask = new GithubQueryTask(this);
        mTask.execute(githubSearchUrl);
    }

    /**
     * method that controls the views that will show to the user
     * makes visible the results from query and hides error message
     */
    private void showJsonDataView() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mSearchResultsTextView.setVisibility(View.VISIBLE);
    }

    /**
     * makes visible error message and hides query result
     */
    private void showErrorMessage() {
        mErrorMessageTextView.setVisibility(View.VISIBLE);
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * hides everything for the user and only shows the activity indicator
     */
    private void showNoView() {
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mSearchResultsTextView.setVisibility(View.INVISIBLE);
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