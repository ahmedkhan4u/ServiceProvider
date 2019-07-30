package com.khan.serviceprovider;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.khan.serviceprovider.Models.UserDataModel;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class signup_personal_information extends Fragment {

    View mView;
    private EditText mEmail, mName, mPassword, mPhoneNo;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private ProgressDialog dialog;
    private TextView btnCreateAccount;
    Button btnRegister;
    CircleImageView profileImage;
    Uri imageUri;
    String downloadUrl;
    StorageReference mStorage;
    String email,name,password,phone,uId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_signup_personal_information, container, false);


        mEmail = mView.findViewById(R.id.register_Email);
        mName = mView.findViewById(R.id.register_Name);
        mPassword = mView.findViewById(R.id.register_password);
        mPhoneNo = mView.findViewById(R.id.register_PhoneNumber);
        mStorage = FirebaseStorage.getInstance().getReference().child("Profile Images");
        btnCreateAccount = mView.findViewById(R.id.btnCreateAccount);
        btnRegister = mView.findViewById(R.id.btnRegister);
        profileImage = mView.findViewById(R.id.profileImage);

        dialog = new ProgressDialog(getContext());

        mAuth = FirebaseAuth.getInstance();//Firebase Auth

        mRef = FirebaseDatabase.getInstance().getReference().child("Users");//Db Reference

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEmail.getText().toString().trim();
                name = mName.getText().toString().trim();
                password = mPassword.getText().toString().trim();
                phone = mPhoneNo.getText().toString().trim();

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
                if (imageUri == null){
                    Toast.makeText(getContext(), "Please Choose an image", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.setTitle("Please wait....");
                dialog.setMessage("Saving data in progress");
                dialog.setCancelable(false);
                dialog.show();

                RegisterUserInFirebaseAuth(name,email,phone,password);
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallary();
            }
        });

        return mView;
    }

    private void RegisterUserInFirebaseAuth(final String name, final String email, final String phone, String password) {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "User Registration Successful", Toast.LENGTH_SHORT).show();
                            uId = mAuth.getCurrentUser().getUid();
                            saveImageToFirebaseDb();

                        }else {
                            dialog.dismiss();
                            Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void SaveDataToFirebaseDatabase(String name, String email, String phone, String userId) {

        UserDataModel userDataModel = new UserDataModel(name,email,phone,userId,"User",downloadUrl);

        mRef.child(userId).setValue(userDataModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                }else {
                    dialog.dismiss();
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearEditFields() {
        mPassword.setText(null);
        mName.setText(null);
        mPhoneNo.setText(null);
        mEmail.setText(null);
        imageUri = null;
        profileImage.setImageURI(null);
    }

    private void getImageFromGallary() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }

    private void saveImageToFirebaseDb(){

        final StorageReference reference = mStorage.child(uId+".jpg");
        Task uploadTask = reference.putFile(imageUri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    //dialog.dismiss();
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadUrl = task.getResult().toString();
                    Toast.makeText(getContext(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    SaveDataToFirebaseDatabase(name,email,password,uId);
                    Picasso.with(getContext()).load(downloadUrl).placeholder(R.drawable.profile_image).into(profileImage);
                    clearEditFields();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}
