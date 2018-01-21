package jc.edu.corsage.dawae;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import jc.edu.corsage.dawae.adapters.SearchAdapter;
import jc.edu.corsage.dawae.mapquest.MapQuest;
import jc.edu.corsage.dawae.mapquest.models.Result;
import jc.edu.corsage.dawae.mapquest.models.Search;
import jc.edu.corsage.dawae.utils.Utils;

public class LocationActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener {
    private String TAG = "LocationActivity";

    private SearchView searchView;
    private Button button;

    private CompositeDisposable mCompositeDisposable;

    private MapQuest mapQuest;

    String[] columns = {BaseColumns._ID, "name" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Request permissions here.
        Utils.permissionsRequest(this);

        mCompositeDisposable = new CompositeDisposable();
        mapQuest = new MapQuest();

        searchView = findViewById(R.id.locationSearch);
        button = findViewById(R.id.mainButtonStart);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), MainActivity.class);
                i.putExtra("TO_LOCATION", searchView.getQuery().toString());

                view.getContext().startActivity(i);
            }
        });

        searchView.setQueryHint(getString(R.string.activity_location_query_hint));
        searchView.setOnQueryTextListener(this);

        MatrixCursor cursor = new MatrixCursor(columns);
        searchView.setSuggestionsAdapter(new SearchAdapter(this, cursor));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }

    /* OnSuggestionListener */

    @Override
    public boolean onSuggestionClick(int pos) {
        MatrixCursor cursor = (MatrixCursor) searchView.getSuggestionsAdapter().getCursor();
        Log.d(TAG, cursor.getString(1));
        //nt indexColumnSuggestion = cursor.getColumnIndex( SuggestionsDatabase.FIELD_SUGGESTION);

        //searchView.setQuery(cursor.getString(indexColumnSuggestion), false);

        return true;
    }

    @Override
    public boolean onSuggestionSelect(int pos) {
        Log.d(TAG, "Suggestion " + pos + " selected.");
        return false;
    }

    /* OnQueryTextListener */

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() >= 3) {
            search(newText);
        } else {
            searchView.getSuggestionsAdapter().changeCursor(null);
        }
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        search(query);
        return false;
    }

    /* Search Private Functions */

    private void handleResponse(Search searches) {
        Log.d(TAG, searches.results.get(0).displayString);

        MatrixCursor cursor = new MatrixCursor(columns);

        // Parse the search terms into the MatrixCursor.
        for (int index = 0; index < searches.results.size(); index++) {
            String term = searches.results.get(index).displayString;

            Object[] row = new Object[] { index, term };
            cursor.addRow(row);
        }

        searchView.getSuggestionsAdapter().swapCursor(cursor);
    }

    private void handleError(Throwable error) {
        Log.d(TAG, "Error: " + error.getMessage());
    }

    private void search(String query) {
        mCompositeDisposable.add(mapQuest.getSearchResults(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, this::handleError)
        );
    }
}
