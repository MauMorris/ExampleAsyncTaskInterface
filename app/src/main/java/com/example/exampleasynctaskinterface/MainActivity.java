package com.example.exampleasynctaskinterface;

import android.content.Context;
import android.os.Bundle;

import com.example.exampleasynctaskinterface.utilities.GithubQueryTask;
import com.example.exampleasynctaskinterface.utilities.NetworkUtils;
import com.example.exampleasynctaskinterface.utilities.TaskInterface;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements TaskInterface {

    //campoos para ingresar el param de busqueda, mostrar la URL y la respuesta del request
    private EditText mSearchBoxEditText;
    private TextView mUrlDisplayTextView;
    private TextView mSearchResultsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSearchBoxEditText = findViewById(R.id.et_search_box);
        mUrlDisplayTextView = findViewById(R.id.tv_url_display);
        mSearchResultsTextView = findViewById(R.id.tv_github_search_results_json);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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
     * @param result is the value that onPostExecute method returns
     */
    @Override
    public void myResult(String result) {
        mSearchResultsTextView.setText(result);
    }

    /**
     * This method retrieves the search text from the EditText, constructs the URL
     * (using {@link NetworkUtils}) for the github repository you'd like to find, displays
     * that URL in a TextView, and finally fires off an AsyncTask to perform the GET request using
     * our (not yet created) {@link GithubQueryTask}
     */
    private void makeGithubSearchQuery() {
        String githubQuery = mSearchBoxEditText.getText().toString();
        URL githubSearchUrl = NetworkUtils.buildUrl(githubQuery);

        mUrlDisplayTextView.setText(githubSearchUrl.toString());

        GithubQueryTask mTask = new GithubQueryTask(this);
        mTask.execute(githubSearchUrl);
    }

    /**
     This method hides the keyboard on the actual activity
     */
    public void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputmKeyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputmKeyboard != null) {
                inputmKeyboard.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}