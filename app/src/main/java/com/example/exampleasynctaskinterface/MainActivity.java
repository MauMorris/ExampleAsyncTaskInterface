package com.example.exampleasynctaskinterface;

import android.content.Context;
import android.os.Bundle;

import com.example.exampleasynctaskinterface.databinding.ActivityMainBinding;
import com.example.exampleasynctaskinterface.datasource.GithubQueryTask;
import com.example.exampleasynctaskinterface.datasource.utilities.NetworkUtils;
import com.example.exampleasynctaskinterface.vm.MainActivityViewModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static final String SEARCH_QUERY_EXTRA = "query";
    public static final int SEARCH_QUERY_EXTRA_URL = 0;
    public static final int SEARCH_QUERY_EXTRA_RESULT = 1;

    //campos para ingresar el param de busqueda, mostrar la URL y la respuesta del request
    private ActivityMainBinding mDataBinding;
    private MainActivityViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(mDataBinding.toolbar);

        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        setClickListeners(mDataBinding);

        if(savedInstanceState != null) {
            if (savedInstanceState.containsKey(SEARCH_QUERY_EXTRA)) {
                ArrayList <String> queryUrl = savedInstanceState.getStringArrayList(SEARCH_QUERY_EXTRA);
                if (queryUrl != null && !queryUrl.isEmpty()){
                        mDataBinding.secondaryLayout.urlDisplayTextView.setText(queryUrl.get(SEARCH_QUERY_EXTRA_URL));
                        mDataBinding.secondaryLayout.githubSearchResultsJsonTextView.setText(queryUrl.get(SEARCH_QUERY_EXTRA_RESULT));
                }
            }
        }

        subscribeUI(mViewModel);
    }

    private void setClickListeners(ActivityMainBinding mDataBinding) {
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

    private void subscribeUI(MainActivityViewModel mViewModel) {
        mViewModel.getDataFromQuery().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String result) {
                mDataBinding.secondaryLayout.loadingIndicatorProgressBar.setVisibility(View.INVISIBLE);

                if(result != null && !TextUtils.isEmpty(result)){
                    mDataBinding.secondaryLayout.githubSearchResultsJsonTextView.setText(result);
                    showJsonDataView();
                } else {
                    showErrorMessage();
                }
            }
        });
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
     * This method retrieves the search text from the EditText, constructs the URL
     * (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our (not yet created) {@link GithubQueryTask}
     */
    private void makeGithubSearchQuery() {
        String githubQueryFromEditText = mDataBinding.secondaryLayout.searchBoxEditText.getText().toString();

        if(TextUtils.isEmpty(githubQueryFromEditText)){
            mDataBinding.secondaryLayout.urlDisplayTextView.setText(getString(R.string.error_no_query));
            return;
        }

        String githubSearchUrl = mViewModel.getUrl(githubQueryFromEditText);
        mDataBinding.secondaryLayout.urlDisplayTextView.setText(githubSearchUrl);

        mDataBinding.secondaryLayout.loadingIndicatorProgressBar.setVisibility(View.VISIBLE);
        showNoView();

        mViewModel.initSearch(githubSearchUrl);
    }

    /**
     * hides everything for the user and only shows the activity indicator
     */
    private void showNoView() {
        mDataBinding.secondaryLayout.errorMessageDisplayTextView.setVisibility(View.INVISIBLE);
        mDataBinding.secondaryLayout.githubSearchResultsJsonTextView.setVisibility(View.INVISIBLE);
    }
}