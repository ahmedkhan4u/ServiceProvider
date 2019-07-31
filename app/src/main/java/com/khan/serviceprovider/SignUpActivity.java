package com.khan.serviceprovider;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khan.serviceprovider.Models.UserDataModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {

    private EditText mEmail,mName,mPassword,mPhoneNo;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private ProgressDialog dialog;
    CircleImageView profileImage;

    int counter=0;
    Button nextFragmentButton;


    RadioButton step1,step2,step3;
    View view1,view2;

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signup_personal_information signupFragment = new signup_personal_information();
        final addresses_fragment addresses_fragment = new addresses_fragment();
        final payment_methods payment_methods = new payment_methods();

        replaceFragment(signupFragment);

        step1 = (RadioButton) findViewById(R.id.personal_info_radio);
        step2 = (RadioButton) findViewById(R.id.addresses_radio);
        step3 = (RadioButton) findViewById(R.id.payment_radio);
        view1 = (View) findViewById(R.id.view1);
        view2 = (View) findViewById(R.id.view2);

        step1.setChecked(true);


        mEmail = findViewById(R.id.register_Email);
        mName = findViewById(R.id.register_Name);
        mPassword = findViewById(R.id.register_password);
        mPhoneNo = findViewById(R.id.register_PhoneNumber);
        nextFragmentButton = (Button) findViewById(R.id.nextbutton);
        linearLayout = findViewById(R.id.linearLayout);
        nextFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;
                Toast.makeText(SignUpActivity.this,"clicked",Toast.LENGTH_LONG).show();
                if(counter==1)
                {
                    replaceFragment(addresses_fragment);
                    Toast.makeText(SignUpActivity.this,"condition 1",Toast.LENGTH_LONG).show();
                    step2.setChecked(true);
                    view1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                }
               else if(counter == 2)
                {
                    replaceFragment(payment_methods);
                    Toast.makeText(SignUpActivity.this,"condition 2",Toast.LENGTH_LONG).show();
                    step3.setChecked(true);
                    view2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }


            }
        });

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
    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment1,fragment);
        fragmentTransaction.commit();

    }
}
