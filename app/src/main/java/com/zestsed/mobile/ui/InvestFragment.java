package com.zestsed.mobile.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zestsed.mobile.R;
import com.zestsed.mobile.adapter.InvestmentListAdapter;
import com.zestsed.mobile.data.Investment;

import java.util.ArrayList;
import java.util.List;

public class InvestFragment extends Fragment {
    DatabaseReference dbReference;
    FirebaseAuth auth;

    public InvestFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_invest, container, false);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbReference = database.getReference();

        auth = FirebaseAuth.getInstance();

        ListView lv = (ListView) layout.findViewById(R.id.investmentsList);
         InvestmentListAdapter adapter = new InvestmentListAdapter(getActivity(), getInvestments());
        lv.setAdapter(adapter);

        return layout;
    }

    private List<Investment> getInvestments() {
        final List<Investment> list = new ArrayList();

        dbReference.child("clients").orderByChild("email").equalTo(auth.getCurrentUser().getEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String clientKey = snapshot.getKey();
                    System.out.println("found key for client at investment view" + clientKey);
                    dbReference.child("investments").orderByChild("clientId").equalTo(clientKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot contribDataSnapshot) {
                            for (DataSnapshot dataDetailSnapshot : contribDataSnapshot.getChildren()) {
                                Double amount = dataDetailSnapshot.child("totalAmount").getValue(Double.class);
                                Double rate = dataDetailSnapshot.child("rate").getValue(Double.class);
                                System.out.println("invest for user " + amount);
                                Investment investment = new Investment(amount, rate);
                                list.add(investment);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed " + databaseError.getMessage());
            }
        });
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

    // this listener will be called when there is change in firebase user session
    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(getContext(), LoginActivity.class));

            }
        }
    };
}
