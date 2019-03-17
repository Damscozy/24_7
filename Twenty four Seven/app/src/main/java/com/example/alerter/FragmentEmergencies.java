package com.example.alerter;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.alerter.Commom.Common;
import com.example.alerter.Model.Emergencies;
import com.example.alerter.ViewHolder.EmergenciesViewHolder;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class FragmentEmergencies extends Fragment {
    private static final String TAG = "Fragment Emergencies";

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;


    SwipeRefreshLayout swipeRefreshLayout;

    DatabaseReference emergencylist;
    FirebaseDatabase db;

    String emergencyTitle="";

    FirebaseRecyclerAdapter<Emergencies, EmergenciesViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.emergencies_fragment, container, false);

        //populate emergency view
        recyclerView = view.findViewById(R.id.listEmergencies);
        //recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        db = FirebaseDatabase.getInstance();
        emergencylist = db.getReference("Emergencies");

        //refersh
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.emergency_list_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_red_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_bright);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (swipeRefreshLayout.isRefreshing()) {
                    //swipeRefreshLayout.setRefreshing(false);
                if (Common.isConnectedToInternet(getActivity().getBaseContext()))
                    swipeRefreshLayout.setRefreshing(false);
                loadEmergencyList(emergencyTitle);
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity().getBaseContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });

        //First time deafult load
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getActivity().getBaseContext()))
                    loadEmergencyList(emergencyTitle);
                else {
                    Toast.makeText(getActivity().getBaseContext(), "No internet connection", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        return view;
    }


    private void loadEmergencyList(String emergencyId) {

        FirebaseRecyclerOptions<Emergencies> options = new FirebaseRecyclerOptions.Builder<Emergencies>()
                .setQuery(emergencylist, Emergencies.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Emergencies, EmergenciesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EmergenciesViewHolder holder, final int position, @NonNull final Emergencies model) {
                holder.emergency_title.setText(model.getEmergencyTitle());
                holder.emergency_description.setText(model.getDescription());
                holder.emergency_location.setText(model.getLocation());
                holder.postedtime.setText(model.getPostedTime());
                holder.date.setText(model.getDate());



            }

            @NonNull
            @Override
            public EmergenciesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.emergency_layout, parent, false);
                return new EmergenciesViewHolder(view);
            }
        };

        adapter.notifyDataSetChanged();
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

}
