package com.example.alerter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SignUp extends AppCompatActivity {
    private EditText editTextMobile;
    private static final String TAG = "Sign Up";
    //firebase auth object
    private FirebaseAuth mAuth;
    ImageView back;
    public String mobile;
    public static final String SHARED_PREFS = "sharedPrefs";
    private  String TEXT;
   public static final String KEY = "mykey";
   // public String KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        editTextMobile = findViewById(R.id.editTextMobile);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mobile = editTextMobile.getText().toString().trim();
        //KEY = mobile;



        findViewById(R.id.btnstart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mobile = editTextMobile.getText().toString().trim();
                TEXT = mobile;



                if(mobile.isEmpty() || mobile.length() < 11){
                    editTextMobile.setError("Enter a valid phone number");
                    editTextMobile.requestFocus();
                    return;
                }
                Intent i = new Intent(SignUp.this, PhoneVerification.class);
                i.putExtra("UserNo", mobile);
                String TEXT= mobile;
                SharedPreferences sharedPreferences = getApplication().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY, TEXT);
                editor.apply();
                Fragment mFragment = null;
                mFragment = new PhoneVerification();
                FragmentManager fragmentManager = getSupportFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("mobile", mobile);
                mFragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, mFragment)
                        .commit();
                String test = sharedPreferences.getString(KEY, "");
                String hameed = i.getStringExtra("UserNo");
                Toast.makeText(getApplication(), "Test No Is" + hameed, Toast.LENGTH_LONG).show();
                Log.d(TAG, "the number you saved= " + hameed);
            }
        });
    }


}
