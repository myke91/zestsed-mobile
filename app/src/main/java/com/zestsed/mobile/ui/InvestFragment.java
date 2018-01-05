package com.zestsed.mobile.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zestsed.mobile.R;
import com.zestsed.mobile.adapter.InvestmentListAdapter;
import com.zestsed.mobile.data.Constants;
import com.zestsed.mobile.data.Investment;
import com.zestsed.mobile.data.InvestmentDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InvestFragment extends Fragment {
    RequestQueue mRequestQueue;
    SharedPreferences pref;
    private static final String TAG = "INVESTMENT_FRAGMENT";
    ProgressDialog progressDialog;
    ListView lv;
    TextView txtOpeningBalance;
    TextView txtTotalContributions;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);
        mRequestQueue = Volley.newRequestQueue(getActivity());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_invest, container, false);
        progressDialog = ProgressDialog.show(getActivity(), "", "Loading data...");
        lv = (ListView) layout.findViewById(R.id.investmentsList);
        txtOpeningBalance = (TextView) layout.findViewById(R.id.txtOpeningBalance);
        txtTotalContributions = (TextView) layout.findViewById(R.id.txtTotalContributions);
        getInvestments();
        getHeaderSummary();
        return layout;
    }

    private void getHeaderSummary() {

        String email = pref.getString("email", "");
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, Constants.BACKEND_BASE_URL + "/mobile/getHeaderSummary?email=" + email, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    Double openingBalance = response.getDouble("openingBalance");
                    Double totalContributions = response.getDouble("totalContributions");

                    txtOpeningBalance.setText("Opening Balance \n GH₵ " + openingBalance);
                    txtTotalContributions.setText("Total Contributions \n GH₵ " + totalContributions);


                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    VolleyError volleyError = new VolleyError(new String(error.networkResponse.data));
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(volleyError.getMessage());
                    builder.setTitle(R.string.app_name);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(error.getMessage());
                    builder.setTitle(R.string.app_name);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });
        jsonRequest.setTag(TAG);
        mRequestQueue.add(jsonRequest);
    }

    private void getInvestments() {
        final List<InvestmentDetails> list = new ArrayList();

        final ObjectMapper mapper = new ObjectMapper();

        String email = pref.getString("email", "");
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, Constants.BACKEND_BASE_URL + "/mobile/getInvestments?email=" + email, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject json = response.getJSONObject(i);
                                    InvestmentDetails details = mapper.readValue(json.toString(), InvestmentDetails.class);
                                    list.add(details);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            InvestmentListAdapter adapter = new InvestmentListAdapter(getActivity(), list);
                            lv.setAdapter(adapter);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        progressDialog.hide();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    VolleyError volleyError = new VolleyError(new String(error.networkResponse.data));
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(volleyError.getMessage());
                    builder.setTitle(R.string.app_name);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(error.getMessage());
                    builder.setTitle(R.string.app_name);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });
        jsonRequest.setTag(TAG);
        mRequestQueue.add(jsonRequest);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    @Override
    public void onPause() {
        super.onPause();
        progressDialog.dismiss();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }
}
