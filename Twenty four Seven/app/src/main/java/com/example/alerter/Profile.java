package com.example.alerter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.annotations.NotNull;
import com.sinch.android.rtc.SinchError;

//public class Profile extends BaseActivity implements SinchService.StartFailedListener {
public class Profile extends BaseActivity implements SinchService.StartFailedListener {

    EditText edtPhone, edtName, edtPassword;
    Button btnSignUp;

    FirebaseDatabase database;
    FirebaseUser current_user;
    DatabaseReference table_use;
    private FirebaseAuth mAuth;

    private static final String TAG = "Sign Up";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        current_user = mAuth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        table_use = database.getReference("User");

        edtPhone = (EditText)findViewById(R.id.edtPhone);
        edtName = (EditText)findViewById(R.id.edtName);
        edtPassword =(EditText)findViewById(R.id.edtPassword);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToInternet(getBaseContext())) {

                    /*final ProgressDialog mDialog = new ProgressDialog(getApplication());
                    mDialog.setMessage("Please waiting...");
                    mDialog.show();
*/
                    table_use.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //check if already
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                /*mDialog.dismiss();*/
                                // Toast.makeText(Profile.this, "Phone number already register", Toast.LENGTH_SHORT).show();
                            } else {
                                /*mDialog.dismiss();*/
                                User user = new User(edtName.getText().toString(), edtPassword.getText().toString());
                                table_use.child(edtPhone.getText().toString()).setValue(user);
                                if ((getSinchServiceInterface() == null) || (!getSinchServiceInterface().isStarted())) {
                                    SharedPreferences sharedPreferences = getApplication().getSharedPreferences(SignUp.SHARED_PREFS, MODE_PRIVATE);
                                    String clientno = sharedPreferences.getString(SignUp.KEY, "");
                                    Log.d(TAG, "the number you saved= " + clientno);
                                    Toast.makeText(getApplication(), "THE NO U SAVED IS " + clientno, Toast.LENGTH_SHORT).show();
                                    Toast.makeText(getApplication(), "your sinch user ID is " + clientno, Toast.LENGTH_LONG).show();
                                    getSinchServiceInterface().startClient(clientno);
                                    Intent Home = new Intent(Profile.this, Drawer.class);
                                    startActivity(Home);
                                } else {
                                    //Toast.makeText(getApplication(), "Sinch started", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(Profile.this, "Sign up successfully!!!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else {
                    Toast.makeText(Profile.this, "No internet connection", Toast.LENGTH_LONG).show();
                    return;
                }
            }

        });
    }

    @Override
    protected void onServiceConnected() {
        btnSignUp.setEnabled(true);
        //loginClicked();
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
    @NotNull
    private void loginClicked() {

        //String userName = (current_user.toString());

        Intent i = getIntent();
        String Number = i.getStringExtra("UserNo");

        /*String userName;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null) {
            userName = bundle.getString("UserNo");*/
            if ((getSinchServiceInterface() == null) || (!getSinchServiceInterface().isStarted())) {
                getSinchServiceInterface().startClient(Number);
                Toast.makeText(this, "your sinch user ID is "+ Number, Toast.LENGTH_LONG);
            } else {
                Toast.makeText(this, "Sinch started", Toast.LENGTH_SHORT).show();
                //showSpinner();
        }
        //openContactListActivity();
    }

    private void tesshi(){


    }

}
