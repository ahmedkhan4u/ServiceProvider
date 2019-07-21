package com.khan.serviceprovider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView forgetpassword,createaccount;

    EditText email, password;
    Button loginbutton;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        forgetpassword = (TextView) findViewById(R.id.forgetpassword);
        createaccount = (TextView) findViewById(R.id.createaccount);
        email = (EditText) findViewById(R.id.loginemail);
        password = (EditText) findViewById(R.id.passwordemail);
        loginbutton = (Button) findViewById(R.id.buttlogin);

        progressDialog = new ProgressDialog(MainActivity.this);


        createaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String userstring = email.getText().toString().trim();
                String passwordstring =password.getText().toString().trim();

                if(TextUtils.isEmpty(userstring))
                {
                    email.setError("Email Is Required");
                    return;
                }
                if(TextUtils.isEmpty(passwordstring))
                {
                    password.setError("Password Is Required");
                    return;
                }
                progressDialog.setMessage("Connecting to Servers...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(userstring,passwordstring)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if(task.isSuccessful())
                                {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(getApplicationContext(), Home.class));
                                    finish();
                                }else {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mUser != null){
            startActivity(new Intent(getApplicationContext(),Home.class));
            finish();
        }

    }
}





