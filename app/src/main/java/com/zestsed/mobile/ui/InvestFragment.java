package com.zestsed.mobile.ui;

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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InvestFragment extends Fragment {
    RequestQueue mRequestQueue;
    String url = Constants.BACKEND_BASE_URL + "/contributions";
    SharedPreferences pref;
    private static final String TAG = "INVESTMENT_FRAGMENT";

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


        ListView lv = (ListView) layout.findViewById(R.id.investmentsList);
        InvestmentListAdapter adapter = new InvestmentListAdapter(getActivity(), getInvestments());
        lv.setAdapter(adapter);

        return layout;
    }

    private List<Investment> getInvestments() {
        final List<Investment> list = new ArrayList();

        JSONObject json = new JSONObject();
        try {
            json.put("email", pref.getString("email", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, json, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse != null && error.networkResponse.data != null) {
                    VolleyError volleyError = new VolleyError(new String(error.networkResponse.data));
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(volleyError.getMessage());
                    builder.setTitle(R.string.app_name);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            }
        });
        jsonRequest.setTag(TAG);
//        mRequestQueue.add(jsonRequest);
        return list;

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
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }
}
