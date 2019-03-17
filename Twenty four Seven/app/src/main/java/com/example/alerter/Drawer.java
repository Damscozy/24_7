package com.example.alerter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alerter.Model.Emergencies;
import com.example.alerter.textchat.MsgAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Drawer extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "TabsActivated";

    private SectionsPageAdapter mSectionsPageAdapter;

    private FirebaseAuth mAuth;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Intent intentThatCalled;
    public double latitude;
    public double longitude;
    public LocationManager locationManager;
    private LocationListener locationListener;
    public Criteria criteria;
    public String bestProvider;
    private String TestName;
    //public Map<String, String> headers;
    public String callinglocation;
    private TextView present_location;


    private double Lon;
    private double Lat;
    static private HashMap<String, MsgAdapter> contacts = new HashMap<String, MsgAdapter>();

    private FragmentTransaction ft;

    private FloatingActionButton fab, fab2;

    FirebaseDatabase database;
    DatabaseReference table_contacts;


    private ViewPager mViewPager;

    EditText edttitle, edtlocation, edtdescription;

    Emergencies newEmergencies;



    TabLayout tabLayout;

    public static Toolbar mToolbar;

   /* private int[] tabIcons = {
            R.drawable.ic_dashboard_black_24dp,
            R.drawable.ic_report_problem_black_24dp,
            R.drawable.ic_contact_phone_black_24dp,
            R.drawable.ic_location_on_black_24dp
    };*/

    /*private static final int[] ATTRS = new int[] {
            android.R.attr.textAppearance,
            android.R.attr.textSize,
            android.R.attr.textColor,
            android.R.attr.gravity
    };*/


    private static final String POLICE_NO = "+46000000000";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Log.d(TAG, "onCreate: Starting.");

        // ...
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();
        table_contacts = database.getReference("Contacts");


        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.containerAc);
        setupViewPager(mViewPager);


        tabLayout = findViewById(R.id.tabsAc);
        tabLayout.setupWithViewPager(mViewPager);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Lat = location.getLatitude();
                Lon = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };



        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                //make sure that you pass the appropriate arguments if you have an args constructor
                //callButtonClicked();
                callButtonClicked();
            }
        });

        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                stopButtonClicked();
                Intent testchat = new Intent(Drawer.this, ChatActivity.class);
                startActivity(testchat);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.title_name));

        navigationView.bringToFront();
        drawer.requestLayout();

        //headers = new HashMap<String, String>();


    }

    public static boolean isLocationEnabled(Context context)
    {
        //...............
        return true;
    }

    @Override
    public void onServiceConnected() {
//        TextView userName = (TextView) findViewById(R.id.loggedInName);
//        userName.setText(getSinchServiceInterface().getUserName());
        //getSinchServiceInterface().addCallClientListener(this);
        //test();
        fab.setEnabled(true);
    }

    @Override
    public void onDestroy() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        super.onDestroy();
    }

    private void stopButtonClicked() {
        if (getSinchServiceInterface() != null) {
            getSinchServiceInterface().stopClient();
        }
        finish();
    }


    public void callButtonClicked() {


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //Set Address
        try {
            Geocoder geocoder = new Geocoder(getApplication(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(Lat, Lon, 1);
            if (addresses != null && addresses.size() > 0) {

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                //Map<String, String> headers = new HashMap<String, String>();
               // headers.put("location", address + city + state + country + postalCode + knownName);
                //headers.put("location", "hello");

                callinglocation = "hello word";
            }
        } catch (Exception e) {
            e.printStackTrace(); // getFromLocation() may sometimes fail
        }
        String userName = ("08065643337");
        String secondName = ("08033045423");
        String thirdcontact = ("09032641112");
        String fourth =       ("08160082813");
        String branco = ("08170550725");
        String dorcas = ("07062428262");
        String justice = ("08037697632");
        String samuel = ("08032627917");
        String blessing = ("08092435382");
        if (userName.isEmpty()) {
            Toast.makeText(this, "You don't have any Emergency contacts to call", Toast.LENGTH_LONG).show();
            return;
        }

        Call call = getSinchServiceInterface().callUser(userName);
        Call second =getSinchServiceInterface().callUser(secondName);
        Call third =getSinchServiceInterface().callUser(thirdcontact);
        Call fourth2 = getSinchServiceInterface().callUser(fourth);
        Call branco2 = getSinchServiceInterface().callUser(branco);
        Call dorcas2 = getSinchServiceInterface().callUser(dorcas);
        Call justice2 = getSinchServiceInterface().callUser(justice);
        Call samuel2 = getSinchServiceInterface().callUser(samuel);
        Call blessing2 = getSinchServiceInterface().callUser(blessing);
        String callId = call.getCallId();
        String callId2 = second.getCallId();
        String callId3 = third.getCallId();
        String callId4 = fourth2.getCallId();
        String callId5 = branco2.getCallId();
        String callId6 = dorcas2.getCallId();
        String callId7 = justice2.getCallId();
        String callId8 = samuel2.getCallId();
        String callId9 = blessing2.getCallId();

        Intent callScreen = new Intent(this, PanicScreenActivity.class);
        //Intent test = new Intent(this, PanicScreenActivity.class);
        callScreen.putExtra(SinchService.CALL_ID, callId);
        callScreen.putExtra(SinchService.CALL_ID, callId2);
        callScreen.putExtra(SinchService.CALL_ID, callId3);
        callScreen.putExtra(SinchService.CALL_ID, callId4);
        callScreen.putExtra(SinchService.CALL_ID, callId5);
        callScreen.putExtra(SinchService.CALL_ID, callId6);
        callScreen.putExtra(SinchService.CALL_ID, callId7);
        callScreen.putExtra(SinchService.CALL_ID, callId8);
        callScreen.putExtra(SinchService.CALL_ID, callId9);
        Toast.makeText(this, "the location is " + callinglocation, Toast.LENGTH_LONG).show();
        startActivity(callScreen);
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new FragmentHome(), "Home");
        adapter.addFragment(new FragmentEmergencies(), "Emergenc..");
        adapter.addFragment(new FragmentContacts(), "Contacts");
        adapter.addFragment(new Maps(), "Locations");
        viewPager.setAdapter(adapter);
    }

    /*private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment fragment = null;
        FragmentTransaction ft;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            //show home tabs
            mViewPager.setCurrentItem(0);
        } else if (id == R.id.nav_emergencies) {
            //show emergencies tabs
            mViewPager.setCurrentItem(1);
        } else if (id == R.id.nav_contacts) {
            //show contacts tab
            mViewPager.setCurrentItem(2);
        } else if (id == R.id.nav_locations) {
            mViewPager.setCurrentItem(3);
        } else if (id == R.id.nav_logout) {

        }
        /*if (fragment != null) {
            ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.containerAc, fragment);
            ft.commit();}*/

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;

    }

    public void call_phone_no(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+POLICE_NO));
        startActivity(callIntent);

    }

    private void createMsgAdapter(ArrayList<String> contact) {
        Log.d(TAG, "createMsgAdapter:work");
        for (String ContactName : contact) {
            Log.d(TAG, "forloop work");
            database = FirebaseDatabase.getInstance();
            table_contacts.child("Contacts").child(ContactName);
            ValueEventListener userEventListener = new ValueEventListener() {
                @Override
                // THE DATA SNAPSHOT IS AT THE CHILD!! NOT THE ROOT NODE!!!!
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onDataChange:work ");
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Log.d(TAG, "work");
                        if (child.getKey().equals("ContactName")) {
                            contacts.put(child.getValue(String.class), new MsgAdapter(getApplicationContext()));
                            Log.d(TAG, child.getValue(String.class));
                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) { }

            };
            table_contacts.addValueEventListener(userEventListener);
        }
    }

    public static MsgAdapter getAdapter(String key) {
        return contacts.get(key);
    }
}
