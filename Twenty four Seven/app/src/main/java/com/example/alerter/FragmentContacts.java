package com.example.alerter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alerter.Commom.Common;
import com.example.alerter.Model.Contacts;
import com.example.alerter.Model.Emergencies;
import com.example.alerter.ViewHolder.ContactsViewHolder;
import com.example.alerter.ViewHolder.EmergenciesViewHolder;
import com.example.alerter.textchat.MsgAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class FragmentContacts extends Fragment {
    private static final String TAG = "Contact";

    ImageView policecall;
    Call call;

    private static final String USER_ID = "current-user-id";
    static private HashMap<String, MsgAdapter> contacts = new HashMap<String, MsgAdapter>();

    private static final String APP_KEY = "d4df55d5-15ee-40ce-9ce6-081bce13d04c";
    private static final String APP_SECRET = "OlNnghwnjE2pKX+f0YRJDQ==";
    private static final String ENVIRONMENT = "clientapi.sinch.com";

    private static final String FIRE_NO = "+2348163671085";
    private static final String POLICE_NO = "+46000000000";
    private static final String MEDICAL_NO= "+46000000000";


    TextView callState;

    private Button btnTEST;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;


    SwipeRefreshLayout swipeRefreshLayout;

    DatabaseReference contactlist;
    FirebaseDatabase db;

    String contactTitle="";

    FirebaseRecyclerAdapter<Contacts, ContactsViewHolder> adapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.contacts_fragment, container, false);

        //populate contact view
        recyclerView = view.findViewById(R.id.listContacts);
        //recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        db = FirebaseDatabase.getInstance();
        contactlist = db.getReference("Contacts");

        //refersh
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.contacts_list_layout);
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
                     loadContactlist(contactTitle);
                }
                else {
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getActivity().getBaseContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });



        policecall = (ImageView) view.findViewById(R.id.callpolice);

        //asking for permissions here
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE,},100);
        }



        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE},
                    1);
        }


        final SinchClient sinchClient = Sinch.getSinchClientBuilder()
                .context(getContext())
                .userId(USER_ID)
                .applicationKey(APP_KEY)
                .applicationSecret(APP_SECRET)
                .environmentHost(ENVIRONMENT)
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.start();

        callState = (TextView) view.findViewById(R.id.callState);
        policecall = (ImageView) view.findViewById(R.id.callpolice);


        /*policecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (call == null) {
                    call = sinchClient.getCallClient().callPhoneNumber(POLICE_NO);
                    ((Call) call).addCallListener(new SinchCallListener());
                    //callButton.setText("Hang Up");
                } else {
                    call.hangup();
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+POLICE_NO));
                startActivity(callIntent);
            }
        });*/

       /* medicalcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (call == null) {
                    call = sinchClient.getCallClient().callPhoneNumber(MEDICAL_NO);
                    ((Call) call).addCallListener(new SinchCallListener());
                    //callButton.setText("Hang Up");
                } else {
                    call.hangup();
                }
            }
        });

        firecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (call == null) {
                    call = sinchClient.getCallClient().callPhoneNumber(FIRE_NO);
                    ((Call) call).addCallListener(new SinchCallListener());
                    //callButton.setText("Hang Up");
                } else {
                    call.hangup();
                }
            }
        });
*/

        //First time deafult load
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getActivity().getBaseContext()))
                    //loadEmergencyList(emergencyTitle);
                    loadContactlist(contactTitle);
                else {
                    Toast.makeText(getActivity().getBaseContext(), "No internet connection", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });
        return view;
    }

    private void loadContactlist(String contactId) {

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactlist, Contacts.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactsViewHolder holder, final int position, @NonNull final Contacts model) {
                holder.contact_name.setText(model.getContactName());
                holder.contact_phone.setText(model.getContactNo());
                Picasso.get().load(model.getContactImage()).into(holder.contact_image);



            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.contact_layout, parent, false);
                return new ContactsViewHolder(view);
            }
        };

        adapter.notifyDataSetChanged();
        adapter.startListening();
        recyclerView.setAdapter(adapter);

    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            //callButton.setText("Call");
            callState.setText("");
            getActivity().setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            callState.setText("connected");
            getActivity().setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            callState.setText("ringing");
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {
        }
    }

    private void createMsgAdapter(ArrayList<String> contact) {
        Log.d(TAG, "createMsgAdapter:work");
        for (String people : contact) {
            Log.d(TAG, "forloop work");
            //database = FirebaseDatabase.getInstance();
            DatabaseReference user = db.getReference().child("Users").child(people);
            ValueEventListener userEventListener = new ValueEventListener() {
                @Override
                // THE DATA SNAPSHOT IS AT THE CHILD!! NOT THE ROOT NODE!!!!
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange:work ");
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Log.d(TAG, "work");
                        if (child.getKey().equals("ContactNo")) {
                            contacts.put(child.getValue(String.class), new MsgAdapter(getContext()));
                            Log.d(TAG, child.getValue(String.class));
                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) { }

            };
            user.addValueEventListener(userEventListener);
        }
    }

    public static MsgAdapter getAdapter(String key) {
        return contacts.get(key);
    }

    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if(prodiverlist.contains(LocationManager.NETWORK_PROVIDER)){
            return LocationManager.NETWORK_PROVIDER;
        }else if(prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        }else{
            Toast.makeText(getContext(),"Location provider does not exist",Toast.LENGTH_SHORT).show();
        }
        return null;
    }



}
