package com.egneese.sellers.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.egneese.sellers.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by nazianoorani on 27/01/16.
 */
public class SearchShopsActivity extends ActionBarActivity implements android.widget.SearchView.OnQueryTextListener {
    @InjectView(R.id.searchView)
    SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_shops);
        populate();
    }

    private void populate() {
        ButterKnife.inject(this);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (!query.isEmpty()) {
                doMySearch(query);
            }
        }
    }



    private void doMySearch(String query) {
        Toast.makeText(SearchShopsActivity.this, query, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        doMySearch(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        doMySearch(newText);
        return false;
    }
}

