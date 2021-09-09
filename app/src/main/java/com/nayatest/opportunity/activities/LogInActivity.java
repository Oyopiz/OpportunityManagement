package com.nayatest.opportunity.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.nayatest.opportunity.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;


public class LogInActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private EditText mLoginEmail;
    private EditText mLoginPassword;

    private Button mLogin_btn,mSign_up;
    private Context mContext;

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;
    AlertDialog dialog_verifying,profile_dialog;
    private ProgressDialog mRegProgress;
    private DatabaseReference mUserDatabase;
    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null) {
            //Start HomeActivity
            finish();
            startActivity(new Intent(LogInActivity.this, HomeActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mAuth = FirebaseAuth.getInstance();

        mLogin_btn=(Button)findViewById(R.id.lg_login);
        mSign_up=(Button)findViewById(R.id.lg_signup);

        mLoginEmail=(EditText)findViewById(R.id.lg_email);
        mRegProgress = new ProgressDialog(LogInActivity.this);
        mLoginPassword=(EditText)findViewById(R.id.lg_pass);

        mContext = this;


        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=mLoginEmail.getText().toString();
                String password=mLoginPassword.getText().toString();


//                LayoutInflater inflater = getLayoutInflater();
//                View alertLayout= inflater.inflate(R.layout.profile_create_dialog,null);
//                AlertDialog.Builder show = new AlertDialog.Builder(mContext);
//                show.setView(alertLayout);
//                show.setCancelable(false);
//                dialog_verifying = show.create();
//                dialog_verifying.show();



                if (email.isEmpty() || password.isEmpty()){
                    Toast.makeText(mContext, "You can't leave fields empty", Toast.LENGTH_SHORT).show();
                }
                else {

                    mRegProgress.setTitle("Logging in");
                    mRegProgress.setMessage("Please wait while we create your account !");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();


                    loginUser(email,password);
                }



            }
        });

        mSign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this,SignUpActivity.class));

            }
        });





        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    //Log.e("rg", "onComplete: Failed=" + Objects.requireNonNull(task.getException()).getMessage());

                    final String current_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                    FirebaseUser currentUser = mAuth.getCurrentUser();

                    currentUser.getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                mUserDatabase.child(current_user_id).child("device_token").setValue(Objects.requireNonNull(task.getResult()).getToken()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        // dialog_verifying.cancel();
                                        //dialog_verifying = null;
                                        mRegProgress.dismiss();
                                        Intent mainIntent = new Intent(LogInActivity.this, HomeActivity.class);
                                        mainIntent.putExtra("id", current_user_id);
                                        startActivity(mainIntent);
                                        finish();

                                    }
                                });

                            } else {
                                Log.e("TAG", "exception=" + Objects.requireNonNull(task.getException()).toString());
                                Toast.makeText(LogInActivity.this, "Error - " +task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }
        });
    }
    @Override
    protected void onResume()
    {
        super.onResume();

        FirebaseDatabase.getInstance().goOnline();

    }
}