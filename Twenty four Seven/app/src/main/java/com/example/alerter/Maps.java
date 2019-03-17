package com.example.alerter;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alerter.Commom.Common;
import com.example.alerter.Googlemaps.DirectionFinder;
import com.example.alerter.Googlemaps.DirectionFinderListener;
import com.example.alerter.Googlemaps.GetNearbyPlacesData;
import com.example.alerter.Googlemaps.Route;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class Maps extends Fragment implements OnMapReadyCallback, DirectionFinderListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerClickListener {
    public static final int RequestPermissionCode = 1;


    protected LatLng start;
    protected LatLng end;
    private GoogleMap mMap;
    private Button btnFindPath;
    private EditText edtstartlocation;
    private EditText edtendlocation;
    private ImageView currentLocation;
    private ImageView locationend;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private static final int PLACE_PICKER_REQUEST = 3;
    private String lat;
    private String lon;
    private BottomNavigationView mBottomNav;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);


            mBottomNav = (BottomNavigationView) view.findViewById(R.id.navigationView);

            mBottomNav = (BottomNavigationView) view.findViewById(R.id.navigationView);
            mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    Object dataTransfer[] = new Object[2];
                    GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

                    if (Common.isConnectedToInternet(getActivity().getBaseContext()))
                    // init corresponding fragment
                    switch (menuItem.getItemId()) {
                        case R.id.hospitals:
                            mMap.clear();
                            String hospital = "hospital";
                            String url = getNearbyPlacesUrl(latitude, longitude, hospital);
                            dataTransfer[0] = mMap;
                            dataTransfer[1] = url;

                            getNearbyPlacesData.execute(dataTransfer);
                            Toast.makeText(getContext(), "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
                            break;


                        case R.id.policestation:
                            mMap.clear();
                            String policestation = "policestation";
                            url = getNearbyPlacesUrl(latitude, longitude, policestation);
                            dataTransfer[0] = mMap;
                            dataTransfer[1] = url;

                            getNearbyPlacesData.execute(dataTransfer);
                            Toast.makeText(getContext(), "Showing Nearby Police Station", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.firehouse:
                            mMap.clear();
                            String firehouse = "firehouse";
                            url = getNearbyPlacesUrl(latitude, longitude, firehouse);
                            dataTransfer[0] = mMap;
                            dataTransfer[1] = url;

                            getNearbyPlacesData.execute(dataTransfer);
                            Toast.makeText(getContext(), "Showing Nearby FireHouse", Toast.LENGTH_SHORT).show();
                            break;

                    }

                    else {
                        Toast.makeText(getActivity().getBaseContext(), "No internet connection", Toast.LENGTH_LONG).show();
                    }
                    return false;
                }

            });


        Bundle extra = this.getArguments();
        if (extra != null) {
            lat = extra.getString("lat");
            lon = extra.getString("lon");
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // checking location permissions
        checkLocationPermission();

        btnFindPath = (Button) view.findViewById(R.id.btnFindPath);
        edtstartlocation = (EditText) view.findViewById(R.id.edtstartlocation);
        edtendlocation = (EditText) view.findViewById(R.id.edtendlocation);
        currentLocation = (ImageView) view.findViewById(R.id.currentLocation);
        locationend = (ImageView) view.findViewById(R.id.locationend);

        if (lat != null && lon != null) {
            useChatLocation(lat, lon);
        }
        // find route from start to end location
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getActivity().getBaseContext()))
                    sendRequest();
                else {
                    Toast.makeText(getActivity().getBaseContext(), "No internet connection", Toast.LENGTH_LONG).show();
                    return;
                }

            }
        });


        locationend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPlacePicker();
            }
        });
        currentLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useCurrentLocation();
            }
        });

        edtstartlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPlace();
            }
        });

        edtendlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findPlace2();
            }
        });
        return view;
    }


        @Override
        public void onStart() {
            super.onStart();
            mGoogleApiClient.connect();
        }

        @Override
        public void onStop() {
            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }
            super.onStop();
        }


        // requesting for routing information
        private void sendRequest() {
            String origin = edtstartlocation.getText().toString();
            String destination = edtendlocation.getText().toString();
            if (origin.isEmpty()) {
                Toast.makeText(getContext(), "Invalid origin address!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (destination.isEmpty()) {
                Toast.makeText(getContext(), "Invalid destination address!", Toast.LENGTH_SHORT).show();
                return;
            }

            // if the origin and destination address are both valid, find the route details
            try {
                new DirectionFinder(this, start, end).execute();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera. In this case,
         * we just add a marker to the present location
         * If Google Play services is not installed on the device, the user will be prompted to install
         * it inside the SupportMapFragment. This method will only be triggered once the user has
         * installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            mMap = googleMap;
            LatLng currentlocation = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentlocation, 11));


            if (ActivityCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission
                    (getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
                return;
            }
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        // if a new route is requested, remove all previous markers/polylines
        @Override
        public void onDirectionFinderStart() {
            progressDialog = ProgressDialog.show(getContext(), "Please wait...",
                    "Finding direction...!", true);


            if (polylinePaths != null) {
                for (Polyline polyline : polylinePaths) {
                    polyline.remove();
                }
            }

            if (originMarkers != null) {
                for (Marker marker : originMarkers) {
                    marker.remove();
                }
            }

            if (destinationMarkers != null) {
                for (Marker marker : destinationMarkers) {
                    marker.remove();
                }
            }

        }

        // once route is found, draw the route out and the origin and destination markers
        @Override
        public void onDirectionFinderSuccess(List< Route > routes) {
            progressDialog.dismiss();
            polylinePaths = new ArrayList<>();
            originMarkers = new ArrayList<>();
            destinationMarkers = new ArrayList<>();

            for (Route route : routes) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
                ((TextView) getView().findViewById(R.id.tvDuration)).setText(route.duration.text);
                ((TextView) getView().findViewById(R.id.tvDistance)).setText(route.distance.text);

                originMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                        .title(route.startAddress)
                        .position(route.startLocation)));
                destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                        .title(route.endAddress)
                        .position(route.endLocation)));

                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.BLUE).
                        width(10);

                for (int i = 0; i < route.points.size(); i++)
                    polylineOptions.add(route.points.get(i));

                polylinePaths.add(mMap.addPolyline(polylineOptions));


            }
        }

        private void loadPlacePicker() {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            try {
                startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }


        private void findPlace2() {
            try {
                Intent intent = new PlaceAutocomplete
                        .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                        .build(getActivity());
                startActivityForResult(intent, 2);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
            }
        }

        public void useCurrentLocation() {
            try {
                Geocoder geo = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addresses = geo.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
                if (addresses.isEmpty()) {
                    ((EditText) getView().findViewById(R.id.edtstartlocation)).setText("Waiting for Location");
                } else {
                    if (addresses.size() > 0) {
                        ((EditText) getView().findViewById(R.id.edtstartlocation)).setText(addresses.get(0).getFeatureName()
                                + ", " + addresses.get(0).getLocality() + ", " +
                                addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());

                        start = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // getFromLocation() may sometimes fail
            }
        }

        private void useChatLocation(String lat, String lon) {
            try {
                Geocoder geo = new Geocoder(getContext().getApplicationContext(), Locale.getDefault());
                List<Address> addresses = geo.getFromLocation(Double.valueOf(lat), Double.valueOf(lon), 1);
                if (addresses.isEmpty()) {
                    ((EditText) getView().findViewById(R.id.edtendlocation)).setText("Waiting for Location");
                } else {
                    if (addresses.size() > 0) {
                        ((EditText) getView().findViewById(R.id.edtendlocation)).setText(addresses.get(0).getFeatureName()
                                + ", " + addresses.get(0).getLocality() + ", " +
                                addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());

                        end = new LatLng(Double.valueOf(lat), Double.valueOf(lon));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); // getFromLocation() may sometimes fail
            }
        }

        private void findPlace() {
            try {
                Intent intent = new PlaceAutocomplete
                        .IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                        .build(getActivity());
                startActivityForResult(intent, 1);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
            }
        }

        // A place has been received; use requestCode to track the request.
        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                    // retrive the data by using getPlace() method.
                    Place place = PlaceAutocomplete.getPlace(getContext(), data);
                    Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());

                    ((EditText) getView().findViewById(R.id.edtstartlocation))
                            .setText(place.getName());

                    start = place.getLatLng();

                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(getContext(), data);
                    // TODO: Handle the error.
                    Log.e("Tag", status.getStatusMessage());

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }

            if (requestCode == PLACE_PICKER_REQUEST) {
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlacePicker.getPlace(getContext(), data);
                    ((EditText) getView().findViewById(R.id.edtendlocation))
                            .setText(place.getName() + "," + place.getAddress());

                    end = place.getLatLng();

                }
            } else if (requestCode == 2) {
                if (resultCode == Activity.RESULT_OK) {
                    // retrive the data by using getPlace() method.
                    Place place = PlaceAutocomplete.getPlace(getContext(), data);
                    Log.e("Tag", "Place: " + place.getAddress() + place.getPhoneNumber());

                    ((EditText) getView().findViewById(R.id.edtendlocation))
                            .setText(place.getName());

                    end = place.getLatLng();

                } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                    Status status = PlaceAutocomplete.getStatus(getContext(), data);
                    // TODO: Handle the error.
                    Log.e("Tag", status.getStatusMessage());

                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                }
            }
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(1000);
            mLocationRequest.setFastestInterval(1000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            if (ContextCompat.checkSelfPermission(getContext(),
                    ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                LocationServices.getFusedLocationProviderClient(getContext())
                        .requestLocationUpdates(mLocationRequest, new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        // do work here
                                        onLocationChanged(locationResult.getLastLocation());
                                    }
                                },
                                Looper.myLooper());
            }


            // make click event on marker
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    ((EditText) getView().findViewById(R.id.edtendlocation))
                            .setText(marker.getTitle());

                    end = marker.getPosition();

                    return false;

                }
            });

        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Toast.makeText(getContext(), "connection failed", Toast.LENGTH_SHORT).show();
        }

        public static final int MY_PERMISSIONS_REQUEST_LOCATION = 69;

        public boolean checkLocationPermission() {
            if (ContextCompat.checkSelfPermission(getContext(),
                    ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Asking user if explanation is needed
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        ACCESS_FINE_LOCATION)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                    //Prompt the user once explanation has been shown
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);


                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
                return false;
            } else {
                return true;
            }
        }

        @Override
        public void onLocationChanged(Location location) {

            mLastLocation = location;
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }

            //Place current location marker
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("Current Position");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            mCurrLocationMarker = mMap.addMarker(markerOptions);


        }

        protected synchronized void buildGoogleApiClient() {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }


        @Override
        public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_LOCATION: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        // permission was granted. Do the
                        // contacts-related task you need to do.
                        if (ContextCompat.checkSelfPermission(getContext(),
                                ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {

                            if (mGoogleApiClient == null) {
                                buildGoogleApiClient();
                            }
                            mMap.setMyLocationEnabled(true);
                        }

                    } else {

                        // Permission denied, Disable the functionality that depends on this permission.
                        Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                    }
                    return;
                }

                // other 'case' lines to check for other permissions this app might request.
                // You can add here other case statements according to your requirement.
            }
        }


        int PROXIMITY_RADIUS = 10000;

        private String getNearbyPlacesUrl(double latitude, double longitude, String nearbyPlace) {

            StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
            googlePlaceUrl.append("location=" + latitude + "," + longitude);
            googlePlaceUrl.append("&radius=" + PROXIMITY_RADIUS);
            googlePlaceUrl.append("&type=" + nearbyPlace);
            googlePlaceUrl.append("&sensor=true");
            googlePlaceUrl.append("&key=" + "AIzaSyB4EFH3YQyAD9QvKIj8k2-rXaFSCQlzGTY");
            Log.d("MapsActivity", "url = " + googlePlaceUrl.toString());

            return googlePlaceUrl.toString();
        }
        double latitude, longitude;


        private void selectplace(MenuItem v) {
            Object dataTransfer[] = new Object[2];
            GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();

            // init corresponding fragment
            switch (v.getItemId()) {
                case R.id.hospitals:
                    mMap.clear();
                    String hospital = "hospital";
                    String url = getNearbyPlacesUrl(latitude, longitude, hospital);
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;

                    getNearbyPlacesData.execute(dataTransfer);
                    Toast.makeText(getContext(), "Showing Nearby Hospitals", Toast.LENGTH_SHORT).show();
                    break;


                case R.id.policestation:
                    mMap.clear();
                    String police_station = "police station";
                    url = getNearbyPlacesUrl(latitude, longitude, police_station);
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;

                    getNearbyPlacesData.execute(dataTransfer);
                    Toast.makeText(getContext(), "Showing Nearby Police Stations", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.firehouse:
                    mMap.clear();
                    String fire_station = "fire station";
                    url = getNearbyPlacesUrl(latitude, longitude, fire_station);
                    dataTransfer[0] = mMap;
                    dataTransfer[1] = url;

                    getNearbyPlacesData.execute(dataTransfer);
                    Toast.makeText(getContext(), "Showing Nearby FireHouse", Toast.LENGTH_SHORT).show();
                    break;

            }


        }
        @Override
        public boolean onMarkerClick(Marker marker) {
            return false;
        }
    }
