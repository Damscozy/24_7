package com.example.alerter;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.alerter.Commom.Common;
import com.example.alerter.Model.Emergencies;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class FragmentHome extends Fragment {
    private static final String TAG = "Home";

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    // static private HashMap<String, MsgAdapter> contacts = new HashMap<String, MsgAdapter>();
    private ProgressDialog mSpinner;
    private String email = "null";
    // private static final String TAG = "MainActivity";
    private TextView temp_view, city_view, description_view, date_view, present_location;
    private Button submit;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private FirebaseAuth auth;
    //private FirebaseDatabase database;
    Location mLastLocation;
    private Location location;
    private double Lat;
    protected LatLng start;
    private double Lon;
    EditText edttitle, edtlocation, edtdescription;
    Button btnsubmit;
    String lattitude,longitude;
    String adress,locality,adminarea,countryname;

    private String phoneNo = null;

    //Firebase
    FirebaseDatabase database;
    DatabaseReference emergencies;


    Emergencies newEmergencies;


    CardView report_emergencies;

   /* private static final int[] ATTRS = new int[] {
            android.R.attr.textAppearance,
            android.R.attr.textSize,
            android.R.attr.textColor,
            android.R.attr.gravity
    };
*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment, container, false);

        //Init Firebase
        database = FirebaseDatabase.getInstance();
        emergencies = database.getReference("Emergencies");
        //storage = FirebaseStorage.getInstance();
        //storageReference = storage.getReference();

        //report_emergencies = view.findViewById(R.id.reportemergency);

        auth = FirebaseAuth.getInstance();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Lat = location.getLatitude();
                Lon = location.getLongitude();
                find_weather(Lat, Lon);
                getAddress(Lat, Lon);
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

        temp_view = (TextView) view.findViewById(R.id.temp_text);
        city_view = (TextView) view.findViewById(R.id.city_text);
        description_view = (TextView) view.findViewById(R.id.descrip_text);
        date_view = (TextView) view.findViewById(R.id.date_text);
        present_location = (TextView) view.findViewById(R.id.presentlocation);
        //submit = (Button) view.findViewById(R.id.submit);

        Button btn = view.findViewById(R.id.btnreportemergency);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(); //Show Dialog for Emergency
            }
        });

        try {
            locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);
        } catch (SecurityException e) {
            Toast.makeText(getContext(),
                    "Location request failed.", Toast.LENGTH_LONG).show();
        }

        location = beginLocation();
        if (location != null) {
            Lat = location.getLatitude();
            Lon = location.getLongitude();
            find_weather(Lat, Lon);
            getAddress(Lat, Lon);
            Log.d(TAG, "onCreate:" + Lat);
            Log.d(TAG, "onCreate:" + Lon);
        }


        return view;
    }


    private void find_weather(double lat, double lon) {
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon + "&appid=8e1ae1fe7537dee7531c41d924c24bed&units=Imperial";

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main_object = response.getJSONObject("main");
                    JSONArray array = response.getJSONArray("weather");
                    JSONObject object = array.getJSONObject(0);
                    String temp = String.valueOf(main_object.getDouble("temp"));
                    String description = object.getString("description");
                    String city = response.getString("name");

                    city_view.setText(city);
                    description_view.setText(description);
                    ImageView image = (ImageView) getView().findViewById(R.id.weather_imageView);
                    changeImage(description, image);

                    Calendar calendar = Calendar.getInstance();
                    DateFormat sdf = SimpleDateFormat.getDateInstance();
                    String formatted_date = sdf.format(calendar.getTime());

                    date_view.setText(formatted_date);

                    double temp_int = Double.parseDouble(temp);
                    double centi = (temp_int - 32) / 1.8000;
                    centi = Math.round(centi);
                    int i = (int) centi;
                    temp_view.setText(String.valueOf(i));



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(jor);


    }


    private void changeImage(String s, ImageView image) {
        switch (s) {
            case "broken clouds":
            case "overcast clouds":
                image.setImageResource(R.drawable.icon_brokenclouds);
                break;
            case "sky is clear":
                image.setImageResource(R.drawable.icon_clearsky);
                break;
            case "light rain":
            case "moderate rain":
            case "heavy intensity rain":
                image.setImageResource(R.drawable.icon_rain);
                break;
            case "snow":
            case "light snow":
            case "moderate snow":
                image.setImageResource(R.drawable.icon_snow);
                break;
            case "mist":
                image.setImageResource(R.drawable.icon_mist);
                break;
            case "few clouds":
                image.setImageResource(R.drawable.icon_fewclouds);
                break;
            case "thunder storm":
                image.setImageResource(R.drawable.icon_thunderstorm);
                break;
            case "shower rain":
                image.setImageResource(R.drawable.icon_showerrain);
                break;
            default:
                image.setImageResource(R.drawable.icon_clearsky);
                break;
        }
    }

    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if (prodiverlist.contains(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;
        } else if (prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        } else {
            Toast.makeText(getContext(), "Location provider does not exist", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public Location beginLocation() {
        Log.d(TAG, "beginLocation:used ");
        //access lication service
        //if we have location provider
        if (judgeProvider(locationManager) != null) {
            //to avoid getLastKnownLocation  warning
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            return locationManager.getLastKnownLocation(judgeProvider(locationManager));
        } else {
            Toast.makeText(getContext(), "Location provider does not exist", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

       // public static void getAddress(Context context, double LATITUDE, double LONGITUDE) {
       private void getAddress(double lat, double lon) {

            //Set Address
            try {
                Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && addresses.size() > 0) {




                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                    present_location.setText(address
                            + ", " + city + ", " +
                            state + ", " + country + "," + postalCode + "," + knownName);

                    /*Log.d(TAG, "getAddress:  address" + address);
                    Log.d(TAG, "getAddress:  city" + city);
                    Log.d(TAG, "getAddress:  state" + state);
                    Log.d(TAG, "getAddress:  postalCode" + postalCode);
                    Log.d(TAG, "getAddress:  knownName" + knownName);
                    Log.d(TAG, "getAddress:  knownName" + country);*/






                }
            } catch (Exception e) {
                e.printStackTrace(); // getFromLocation() may sometimes fail
            }
            return;
        }


    //Display the Add Category Dialog
    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("\n" +
                "Report An Emergency");
        alertDialog.setMessage("\n" +
                "Please fill in all information!");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.report_emergencies, null);

        edttitle = add_menu_layout.findViewById(R.id.edttitle);
        edtlocation = add_menu_layout.findViewById(R.id.edtlocation);
        edtdescription = add_menu_layout.findViewById(R.id.edtdescription);
        btnsubmit = add_menu_layout.findViewById(R.id.submit);

        Long tsLong = System.currentTimeMillis()/1000;
        final String ts = tsLong.toString();

        //get current time
        Calendar cal = Calendar. getInstance();
        Date time=cal. getTime();
        Date date=cal.getTime();
        DateFormat timeFormat = new SimpleDateFormat("h:mm a");
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, ''yy");
        final String formattedTime=timeFormat. format(time);
        final String formattedDate = dateFormat.format(date);


        //Event for Button
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pushImagw(; //Allows users to select images from the gallery and save the url of the image
                newEmergencies = new Emergencies();
                newEmergencies.setDescription(edtdescription.getText().toString());
                newEmergencies.setLocation(edtlocation.getText().toString());
                newEmergencies.setEmergencyTitle(edttitle.getText().toString());
                newEmergencies.setPostedTime(formattedTime);
                newEmergencies.setDate(formattedDate);
                newEmergencies.setTime(ts);
                Toast.makeText(getContext(), "Emergency Report Acknowledged, Please press submit, to Report your Emergency", Toast.LENGTH_SHORT).show();

            }
        });


        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.emmergency);

        //Set button
        alertDialog.setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                //Buat saja category baru
                if (Common.isConnectedToInternet(getActivity().getBaseContext()))
                    if (newEmergencies != null){
                   /* newEmergencies = new Emergencies();
                    newEmergencies.setDescription(edtdescription.getText().toString());
                    newEmergencies.setLocation(edtlocation.getText().toString());
                    newEmergencies.setEmergencyTitle(edttitle.getText().toString());
                    newEmergencies.setPostedTime(formattedTime);
                    newEmergencies.setDate(formattedDate);
                    newEmergencies.setTime(ts);*/
                    final FirebaseUser currentUser = auth.getCurrentUser();
                    emergencies.child(currentUser.getUid()).setValue(newEmergencies);
                    //emergencies.child(currentUser.getUid());

                }
                else {
                    Toast.makeText(getActivity().getBaseContext(), "No internet connection", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(getContext(), "Emergency Successfully Reported", Toast.LENGTH_SHORT).show();
                return;

            }
        });        alertDialog.setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void uploadEmergencies(final Emergencies item) {


    }
}
