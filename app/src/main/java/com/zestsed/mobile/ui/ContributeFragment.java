package com.zestsed.mobile.ui;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestsed.mobile.R;
import com.zestsed.mobile.adapter.ContributionListAdapter;
import com.zestsed.mobile.data.Constants;
import com.zestsed.mobile.data.Contribution;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ContributeFragment extends Fragment implements View.OnClickListener {

    RequestQueue mRequestQueue;

    SharedPreferences pref;
    private static final String TAG = "CONTRIBUTION_FRAGMENT";
    private FloatingActionButton add;
    ProgressDialog progressDialog;
    ListView lv;

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
        progressDialog = ProgressDialog.show(getActivity(), "", "Loading data...");
        add = (FloatingActionButton) layout.findViewById(R.id.add_contribution);
        lv = (ListView) layout.findViewById(R.id.contributionList);

        getContributions();
        add.setOnClickListener(this);


        return layout;
    }


    private List<Contribution> getContributions() {
        progressDialog.show();
        final List<Contribution> list = new ArrayList<>();
        final ObjectMapper mapper = new ObjectMapper();
        String email = pref.getString("email", "");

        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, Constants.BACKEND_BASE_URL + "/mobile/getContributions?email=" + email, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                progressDialog.hide();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject json = response.getJSONObject(i);
                        Contribution contribution = mapper.readValue(json.toString(), Contribution.class);
                        list.add(contribution);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                ContributionListAdapter adapter = new ContributionListAdapter(getActivity(), list);
                lv.setAdapter(adapter);

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
        return list;

    }

    @Override
    public void onDestroy() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

    public void showAddContributionDialog() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_add_contribution, (ViewGroup) getActivity().findViewById(R.id.activity_contribute));

        final Spinner spnModeOfPayment = (Spinner) layout.findViewById(R.id.mode_of_payment);
        final TextInputEditText txtSourceOfPayment = (TextInputEditText) layout.findViewById(R.id.source_of_payment);
        final TextInputEditText txtVendorName = (TextInputEditText) layout.findViewById(R.id.vendor_name);
        final TextInputEditText txtDateOfContribution = (TextInputEditText) layout.findViewById(R.id.date_of_contribution);
        final TextInputEditText txtContributionAmount = (TextInputEditText) layout.findViewById(R.id.contribution_amount);

        final CharSequence[] MODE_PAYMENT_OPTION = {
                "- MODE OF PAYMENT -", "Mobile Money", "Bank Transfer", "Cash", "Cheque", "Bank Draft"
        };
        ArrayAdapter<CharSequence> genderAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, MODE_PAYMENT_OPTION);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnModeOfPayment.setAdapter(genderAdapter);

        final SimpleDateFormat dateFormatter;

        txtDateOfContribution.setOnClickListener(this);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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
                boolean error = false;

                if (spnModeOfPayment.getSelectedItem().toString().equalsIgnoreCase("- MODE OF PAYMENT -")) {
                    TextView v = (TextView) spnModeOfPayment.getSelectedView();
                    v.setError("Select a mode of payment");
                    error = true;
                }
                if (TextUtils.isEmpty(txtSourceOfPayment.getText().toString())) {
                    txtSourceOfPayment.setError("Enter Source of Payment");
                    error = true;
                }


                if (TextUtils.isEmpty(txtDateOfContribution.getText().toString())) {
                    txtDateOfContribution.setError("Enter Date of Contribution");
                    error = true;
                }
                if (TextUtils.isEmpty(txtContributionAmount.getText().toString())) {
                    txtContributionAmount.setError("Enter Quota Amount");
                    error = true;
                }


                if (error) {
                    Toast.makeText(getActivity(), "Invalid form entryg", Toast.LENGTH_LONG).show();
                } else {
                    dialog.dismiss();

                    final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "Saving quota...");
                    progressDialog.show();
                    String modeOfPayment = spnModeOfPayment.getSelectedItem().toString();
                    String sourceOfPayment = txtSourceOfPayment.getText().toString();
                    String vendorName = txtVendorName.getText().toString();
                    String dateOfContribution = txtDateOfContribution.getText().toString();
                    String contributionAmount = txtContributionAmount.getText().toString();
                    String email = pref.getString("email", "");

                    if (email.equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Cannot Save Quota. \n Possible Invalid Session. \nRe login and try again ");
                        builder.setTitle(R.string.app_name);
                        AlertDialog errorDialog = builder.create();
                        errorDialog.show();
                        return;
                    }
                    Contribution contribution = Contribution.load(modeOfPayment, sourceOfPayment, vendorName, dateOfContribution, contributionAmount, email);

                    JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, Constants.BACKEND_BASE_URL + "/mobile/addContribution", contribution, new Response.Listener<JSONObject>() {
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
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE);
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
