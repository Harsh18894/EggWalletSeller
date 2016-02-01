package com.egneese.sellers.dashboardfragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.egneese.sellers.R;
import com.egneese.sellers.adapters.TransactionListAdapter;
import com.egneese.sellers.constants.EggWallet;
import com.egneese.sellers.constants.NetworkConstants;
import com.egneese.sellers.dto.MessageCustomDialogDTO;
import com.egneese.sellers.dto.RequiredDTO;
import com.egneese.sellers.dto.TransactionDTO;
import com.egneese.sellers.dto.TransactionRequestDTO;
import com.egneese.sellers.global.GlobalData;
import com.egneese.sellers.ui.SnackBar;
import com.egneese.sellers.util.RequiredDTOFactory;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by adityaagrawal on 17/01/16.
 */
public class TransactionFragment extends Fragment implements AbsListView.OnScrollListener, View.OnClickListener, NetworkConstants {
    private View rootView;
    @InjectView(R.id.listTransactions)
    ListView listTransactions;
    @InjectView(R.id.progress_wheel)
    ProgressBar progressBar;
    @InjectView(R.id.imgError)
    ImageView imgError;
    @InjectView(R.id.txtErrorMessage)
    com.neopixl.pixlui.components.textview.TextView txtErrorMessage;
    private boolean isDataLoaded = false;
    private View footerView;
    private int preLast, skip = 0;
    private List<TransactionDTO> transactions = new ArrayList<>();
    private String url = GET_NETWORK_IP + LOAD_TRANSACTION_URL;
    private TransactionListAdapter transactionListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_transaction, container, false);
        ButterKnife.inject(this, rootView);
     //   setRetainInstance(true);
       // populate();
        return rootView;
    }

    private void populate(){
        listTransactions.setOnScrollListener(this);
        footerView =  ((LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.progress_dialog, null, false);
        listTransactions.addFooterView(footerView);
        imgError.setOnClickListener(this);
        txtErrorMessage.setOnClickListener(this);
        listTransactions.addFooterView(footerView);
        transactionListAdapter = new TransactionListAdapter(getActivity(), transactions);
        /*TransactionDTO transactionDTO = new TransactionDTO();
        transactions.add(transactionDTO);


*/
        populateData();
    }

    private void populateData(){
        txtErrorMessage.setVisibility(View.GONE);
        imgError.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        loadData();
    }


    private void loadData(){
        TransactionRequestDTO transactionRequestDTO = new TransactionRequestDTO();
        RequiredDTO requiredDTO = RequiredDTOFactory.getObject(getActivity());
        transactionRequestDTO.setId(requiredDTO.getId());
        transactionRequestDTO.setAccessToken(requiredDTO.getAccessToken());
        transactionRequestDTO.setMobile(requiredDTO.getMobile());
        transactionRequestDTO.setLimit(EggWallet.LIMIT);
        transactionRequestDTO.setSkip(skip);
        String url = this.url;
        try {
            url = url.replaceFirst("QUERY", URLEncoder.encode(new Gson().toJson(transactionRequestDTO), "UTF-8"));
        }catch (Exception e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                listTransactions.setVisibility(View.VISIBLE);
                if (response != null) {
                    if(!isDataLoaded){
                        listTransactions.setAdapter(transactionListAdapter);
                        parseJSONFeed(response);
                    }else{
                        parseJSONFeed(response);
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if(!isDataLoaded){
                    error.printStackTrace();
                    txtErrorMessage.setVisibility(View.VISIBLE);
                    imgError.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                    messageCustomDialogDTO.setTitle("Network Error");
                    messageCustomDialogDTO.setButton("OK");
                    messageCustomDialogDTO.setMessage(error.toString());
                    messageCustomDialogDTO.setContext(getActivity());
                    SnackBar.show(getActivity(), messageCustomDialogDTO);
                }else{
                    loadData();
                    progressBar.setVisibility(View.GONE);
                }
            }
        }
        ){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        GlobalData.getInstance().addToRequestQueue(jsonReq);
    }



    private void parseJSONFeed(JSONObject response){
        try {
                Gson gson = new Gson();
                JSONObject jsonObject = new JSONObject(response.getString("response"));
                if(jsonObject.getInt("count") == 0){
                    listTransactions.removeFooterView(footerView);
                    transactionListAdapter.notifyDataSetChanged();

                    if(!isDataLoaded){
                        MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                        messageCustomDialogDTO.setTitle("Network Error");
                        messageCustomDialogDTO.setButton("OK");
                        messageCustomDialogDTO.setMessage(getActivity().getResources().getString(R.string.no_trans));
                        messageCustomDialogDTO.setContext(getActivity());
                        SnackBar.show(getActivity(), messageCustomDialogDTO);

                        listTransactions.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }else{
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("transactions"));
                    JSONObject jsonObject1 = null;
                    TransactionDTO transactionDTO = null;
                    for (int i = 0 ; i < jsonArray.length() ; i++){
                        jsonObject1 = jsonArray.getJSONObject(i);
                        transactionDTO = gson.fromJson(jsonObject1.toString(), TransactionDTO.class);
                        transactions.add(transactionDTO);
                    }

                    /*transactionDTOs = gson.fromJson(jsonObject.getString("transactions"), List.class);
                    transactions.addAll(transactionDTOs);
                    Toast.makeText(getActivity(), transactions.toString(), Toast.LENGTH_SHORT).show();*/

                    transactionListAdapter.notifyDataSetChanged();
                    listTransactions.requestLayout();
                    skip = skip + jsonObject.getInt("count");
                }


                progressBar.setVisibility(View.GONE);
                isDataLoaded = true;

        } catch (Exception e) {
            if(!isDataLoaded){

                MessageCustomDialogDTO messageCustomDialogDTO = new MessageCustomDialogDTO();
                messageCustomDialogDTO.setTitle("Network Error");
                messageCustomDialogDTO.setButton("OK");
                messageCustomDialogDTO.setMessage(getActivity().getResources().getString(R.string.server_error));
                messageCustomDialogDTO.setContext(getActivity());
                SnackBar.show(getActivity(), messageCustomDialogDTO);

                imgError.setVisibility(View.VISIBLE);
                txtErrorMessage.setVisibility(View.VISIBLE);
                listTransactions.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtErrorMessage:
            case R.id.imgError:
                populateData();
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        final int lastItem = firstVisibleItem + visibleItemCount;
        if(lastItem == totalItemCount) {
            if(preLast!=lastItem){
                loadData();
                preLast = lastItem;
            }
        }
    }


}
