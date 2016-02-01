package com.egneese.sellers.dashboardfragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.egneese.sellers.R;
import com.egneese.sellers.activities.SearchShopsActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by adityaagrawal on 17/01/16.
 */
public class ShopsFragment extends Fragment implements View.OnClickListener {
    private View rootView;
    @InjectView(R.id.imgSearch)
    ImageView imgSearch;
    @InjectView(R.id.listShops)
    RecyclerView listShops;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_shops, container, false);
        ButterKnife.inject(this, rootView);
        populate();
        return rootView;
    }

    private void populate(){
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listShops.setLayoutManager(layoutManager);
        imgSearch.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imgSearch : Intent intent = new Intent(getActivity(),SearchShopsActivity.class);
                getActivity().startActivity(intent);
                break;

        }

    }
}
