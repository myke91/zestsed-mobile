package com.zestsed.mobile.ui;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

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
import com.zestsed.mobile.adapter.ContributionListAdapter;
import com.zestsed.mobile.data.Client;
import com.zestsed.mobile.data.Constants;
import com.zestsed.mobile.data.Contribution;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.zestsed.mobile.R.id.dateOfBirth;

public class ContributeFragment extends Fragment implements View.OnClickListener {

    RequestQueue mRequestQueue;
    String url = Constants.BACKEND_BASE_URL + "/contributions";
    SharedPreferences pref;
    private static final String TAG = "CONTRIBUTION_FRAGMENT";
    private FloatingActionButton add;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE);
        mRequestQueue = Volley.newRequestQueue(getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_contribute, container, false);

        add = (FloatingActionButton) layout.findViewById(R.id.add_contribution);
        ListView lv = (ListView) layout.findViewById(R.id.contributionList);
        ContributionListAdapter adapter = new ContributionListAdapter(getActivity(), getContributions());
        lv.setAdapter(adapter);

        add.setOnClickListener(this);


        return layout;
    }


    private List<Contribution> getContributions() {
        final List<Contribution> list = new ArrayList<>();
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

    public void showAddContributionDialog() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_add_contribution, (ViewGroup) getActivity().findViewById(R.id.activity_contribute));

        Spinner spnModeOfPayment = (Spinner) layout.findViewById(R.id.mode_of_payment);
        TextInputEditText txtSourceOfPayment = (TextInputEditText) layout.findViewById(R.id.source_of_payment);
        TextInputEditText txtVendorName = (TextInputEditText) layout.findViewById(R.id.vendor_name);
        final TextInputEditText txtDateOfContribution = (TextInputEditText) layout.findViewById(R.id.date_of_contribution);
        TextInputEditText txtContributionAmount = (TextInputEditText) layout.findViewById(R.id.contribution_amount);

        final CharSequence[] MODE_PAYMENT_OPTION = {"- MODE OF PAYMENT -", "Mobile Money", "Bank Transfer", "Cash", "Cheque", "Bank Draft"};
        ArrayAdapter<CharSequence> genderAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, MODE_PAYMENT_OPTION);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnModeOfPayment.setAdapter(genderAdapter);

        final SimpleDateFormat dateFormatter;

        txtDateOfContribution.setOnClickListener(this);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        txtDateOfContribution.setInputType(InputType.TYPE_NULL);
        txtDateOfContribution.setFocusable(false);
        txtDateOfContribution.setClickable(true);
        txtDateOfContribution.requestFocus();

        Calendar newCalendar = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                txtDateOfContribution.setText(dateFormatter.format(newDate.getTime()));
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        txtDateOfContribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(layout);
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Saving contribution...");
                progressDialog.show();

                Contribution contribution = Contribution.load();
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, contribution, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("DATA SAVED SUCCESSFUL AND PENDING APPROVAL");
                        builder.setTitle(R.string.app_name);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        if (error instanceof com.android.volley.ServerError) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("SERVER ERROR");
                            builder.setTitle(R.string.app_name);
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        } else {
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
                mRequestQueue.add(jsonRequest);

            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(TAG);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == add) {
            showAddContributionDialog();
        }
    }
}
