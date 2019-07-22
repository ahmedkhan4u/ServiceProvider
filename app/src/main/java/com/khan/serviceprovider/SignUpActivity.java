package com.khan.serviceprovider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khan.serviceprovider.Models.UserDataModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private EditText mEmail,mName,mPassword,mPhoneNo;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private ProgressDialog dialog;
    CircleImageView profileImage;

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fragment = new signup_personal_information();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment1,fragment);

        HorizontalStepView setpview5 = (HorizontalStepView) findViewById(R.id.step_view);
        List<StepBean> stepsBeanList = new ArrayList<>();
        StepBean stepBean0 = new StepBean("Step-1",1);
        StepBean stepBean1 = new StepBean("Step-2",1);
        StepBean stepBean2 = new StepBean("Step-3",1);
        stepsBeanList.add(stepBean0);
        stepsBeanList.add(stepBean1);
        stepsBeanList.add(stepBean2);

        setpview5
                .setStepViewTexts(stepsBeanList)
                .setTextSize(12)
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(SignUpActivity.this, android.R.color.holo_red_dark))
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(SignUpActivity.this, R.color.uncompleted_text_color))
                .setStepViewComplectedTextColor(ContextCompat.getColor(SignUpActivity.this, android.R.color.holo_red_dark))
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(SignUpActivity.this, R.color.uncompleted_text_color))
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.complted))
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.default_icon))
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(SignUpActivity.this, R.drawable.attention));

        mEmail = findViewById(R.id.register_Email);
        mName = findViewById(R.id.register_Name);
        mPassword = findViewById(R.id.register_password);
        mPhoneNo = findViewById(R.id.register_PhoneNumber);

        dialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();//Firebase Auth
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");//Db Reference

    }

    public void GoToLogin(View view) {

        Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
        startActivity(intent);
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

        dialog.setTitle("Please wait....");
        dialog.setMessage("Saving data in progress");
        dialog.setCancelable(false);
        dialog.show();

        RegisterUserInFirebaseAuth(name,email,phone,password);

    }

    private void RegisterUserInFirebaseAuth(final String name, final String email, final String phone, String password) {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SignUpActivity.this, "User Registration Successful", Toast.LENGTH_SHORT).show();
                    String userId = mAuth.getCurrentUser().getUid();
                    SaveDataToFirebaseDatabase(name,email,phone,userId);

                }else {
                    dialog.dismiss();
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SaveDataToFirebaseDatabase(String name, String email, String phone, String userId) {

        UserDataModel userDataModel = new UserDataModel(name,email,phone,userId,"User","");

        mRef.child(userId).setValue(userDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                    clearEditFields();
                }else {
                    dialog.dismiss();
                    Toast.makeText(SignUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearEditFields() {
        mPassword.setText(null);
        mName.setText(null);
        mPhoneNo.setText(null);
        mEmail.setText(null);
    }
}
