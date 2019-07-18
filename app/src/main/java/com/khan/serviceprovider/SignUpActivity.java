package com.khan.serviceprovider;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private EditText mEmail,mName,mPassword,mPhoneNo;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmail = findViewById(R.id.register_Email);
        mName = findViewById(R.id.register_Name);
        mPassword = findViewById(R.id.register_password);
        mPhoneNo = findViewById(R.id.register_PhoneNumber);

        mAuth = FirebaseAuth.getInstance();//Firebase Auth

    }

    public void GoToLogin(View view) {
    }

    public void Register(View view) {

        final String email = mEmail.getText().toString().trim();
        final String name = mName.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();
        final String phone = mPhoneNo.getText().toString().trim();

        if (email.isEmpty()){
            mEmail.setError("Required");
            return;
        }
        if (name.isEmpty()) {
            mName.setError("Required");
            return;
        }
        if (phone.isEmpty()){
            mPhoneNo.setError("Required");
            return;
        }
        if (password.isEmpty()){
            mPassword.setError("Required");
            return;
        }

        RegisterUserInFirebaseAuth(name,email,phone,password);

    }

    private void RegisterUserInFirebaseAuth(String name, String email, String phone, String password) {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this, "User Registration Successful", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
