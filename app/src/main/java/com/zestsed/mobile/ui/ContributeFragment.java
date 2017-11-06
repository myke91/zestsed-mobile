package com.zestsed.mobile.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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
import com.zestsed.mobile.adapter.ContributionListAdapter;
import com.zestsed.mobile.data.Contribution;

import java.util.ArrayList;
import java.util.List;

public class ContributeFragment extends Fragment {
    DatabaseReference dbReference;
    FirebaseAuth auth;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.fragment_contribute, container, false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        dbReference = database.getReference();

        auth = FirebaseAuth.getInstance();

        ListView lv = (ListView) layout.findViewById(R.id.contributionList);
        ContributionListAdapter adapter = new ContributionListAdapter(getActivity(), getContributions());
        lv.setAdapter(adapter);

        return layout;
    }


    private List<Contribution> getContributions() {
        final List<Contribution> list = new ArrayList<>();

        dbReference.child("clients").orderByChild("email").equalTo(auth.getCurrentUser().getEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String clientKey = snapshot.getKey();
                    Log.d("ZestSed","Found client key " + clientKey);

                    dbReference.child("contributions").orderByChild("clientId").equalTo(clientKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot contribDataSnapshot) {
                            for (DataSnapshot dataDetailSnapshot : contribDataSnapshot.getChildren()) {
                                Double amount = dataDetailSnapshot.child("amount").getValue(Double.class);
                                String date = dataDetailSnapshot.child("date").getValue(String.class);
                                Log.d("ZestSed","contribution for user " + amount);
                                Contribution contribution = new Contribution(date, amount);
                                list.add(contribution);
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
                Log.d("ZestSed","The read failed " + databaseError.getMessage());
            }
        });

        return list;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
