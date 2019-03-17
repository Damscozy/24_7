package com.example.alerter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.alerter.Commom.Common;
import com.example.alerter.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.SinchError;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements SinchService.StartFailedListener {

    private static final String TAG = "MainActivity";
    //private static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY = "myKey";


    Button btnSignUp;
    Button btnlogin;
    //firebase auth object
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


       // getSinchServiceInterface().startClient(clientno);


        btnSignUp = (Button) findViewById(R.id.btnsignup);
        btnlogin = (Button) findViewById(R.id.btnlogin);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signup = new Intent(MainActivity.this, SignUp.class);
                startActivity(signup);
            }
        });

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    if ((getSinchServiceInterface() == null) || (!getSinchServiceInterface().isStarted())) {
                        SharedPreferences sharedPreferences = getApplication().getSharedPreferences(SignUp.SHARED_PREFS, MODE_PRIVATE);
                        String welcomeno = sharedPreferences.getString(SignUp.KEY, "");
                        Log.d(TAG, "the number you saved= " + welcomeno);
                        Toast.makeText(getApplication(), "Welcome User "+ welcomeno, Toast.LENGTH_SHORT).show();
                        getSinchServiceInterface().startClient(welcomeno);
                        Intent intent = new Intent(MainActivity.this, Drawer.class);
                        startActivity(intent);
                    }
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        });



    }
    @Override
    protected void onServiceConnected() {
        btnlogin.setEnabled(true);
        onStart();
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onPause() {
        /*if (mSpinner != null) {
            mSpinner.dismiss();
        }*/
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("ERROR", "set online false OnResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d("ERROR", "set online false OnDestroy");
        super.onDestroy();
    }

    @Override
    public void onStarted() {

    }




    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        /*if (mSpinner != null) {
            mSpinner.dismiss();
        }*/
    }

   /* @Override
    protected void onStart() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // User is signed in
            if ((getSinchServiceInterface() == null) || (!getSinchServiceInterface().isStarted())) {
                SharedPreferences sharedPreferences = getApplication().getSharedPreferences(SignUp.SHARED_PREFS, MODE_PRIVATE);
                String welcomeno = sharedPreferences.getString(SignUp.KEY, "");
                Log.d(TAG, "the number you saved= " + welcomeno);
                Toast.makeText(getApplication(), "Welcome User "+ welcomeno, Toast.LENGTH_SHORT).show();
                getSinchServiceInterface().startClient(welcomeno);
                Intent intent = new Intent(MainActivity.this, Drawer.class);
                startActivity(intent);
            }
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
        super.onStart();
    }*/

}
