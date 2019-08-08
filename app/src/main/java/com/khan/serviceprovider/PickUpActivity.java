package com.khan.serviceprovider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickUpActivity extends AppCompatActivity {

    Toolbar toolbar;
    ProgressDialog progressDialog;
    DatabaseReference mRef;
    FirebaseAuth mAuth;
    Spinner mSpinnerStoreName,mSpinnerStoreLocation;
    List storeNameList,storeAddressList;
    String selectedStoreName,selectedStoreLocation,downloadUrl,currentUserId;
    Uri imageUri;
    TextView mPrice;
    StorageReference mStorage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up);

        mSpinnerStoreName = findViewById(R.id.spinnerStoreName);
        mSpinnerStoreLocation = findViewById(R.id.spinnerStoreLocation);
        mStorage = FirebaseStorage.getInstance().getReference().child("PickupImages");
        mPrice = findViewById(R.id.txt_storePickupPrice);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("Reservations").child("ShoppingPickup");
        progressDialog = new ProgressDialog(this);
        storeNameList = new ArrayList<String>();
        storeAddressList = new ArrayList<String>();

        getSpinnerValuesFromFirebaseDatabase();

        toolbar = findViewById(R.id.toolbar_shoppingPickup);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Shopping Pickup");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void getSpinnerValuesFromFirebaseDatabase() {

        mRef.child("StoreData").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        storeNameList.add(snapshot.child("storeName").getValue());
                        storeAddressList.add(snapshot.child("storeLocation").getValue());

                        }

                    fillNameSpinnerValues();
                    fillLocationSpinnerValues();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void Button_shoppingPickupOrder(View view) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(PickUpActivity.this);
        builder.setTitle("Confirm Service\nAgent will call you shortly");
        View mView = LayoutInflater.from(PickUpActivity.this)
                .inflate(R.layout.custom_dialog_accept_decline,null,false);

        Button mBtnAccept = mView.findViewById(R.id.btn_acceptDialog);
        Button mBtnDecline = mView.findViewById(R.id.btn_declineDialog);
        builder.setView(mView);
        final Dialog dialog = builder.create();
        mBtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Please wait...");
                progressDialog.setMessage("Savig Order in Progress");
                progressDialog.setCancelable(false);
                progressDialog.show();
                savePickupInformationToFirebaseDatabase();
                dialog.dismiss();

            }
        });

        mBtnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Snackbar.make(findViewById(R.id.ShoppinPickup),"Request Canceled",Snackbar.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    private void savePickupInformationToFirebaseDatabase() {

        if (imageUri == null){
            progressDialog.dismiss();
            Snackbar.make(findViewById(R.id.ShoppinPickup),"Please Choose Image",Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (selectedStoreName.equalsIgnoreCase("Store Address")){
            progressDialog.dismiss();
            Snackbar.make(findViewById(R.id.ShoppinPickup),"Please Choose Store Address",Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (selectedStoreName.equalsIgnoreCase("Store Name")){
            progressDialog.dismiss();
            Snackbar.make(findViewById(R.id.ShoppinPickup),"Please Choose Store Name",Snackbar.LENGTH_SHORT).show();
            return;
        }

        saveReceiptImage();

    }

    private void saveReceiptImage() {

        final StorageReference reference = mStorage.child(imageUri.getLastPathSegment()+".jpg");
        UploadTask uploadTask = reference.putFile(imageUri);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    progressDialog.dismiss();
                    throw task.getException();
                }
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    downloadUrl = task.getResult().toString();
                    Snackbar.make(findViewById(R.id.ShoppinPickup),"Image Uploaded",Snackbar.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    savingOrderInformation();
                }else {
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(R.id.ShoppinPickup),task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void savingOrderInformation() {

        Map map = new HashMap<>();
        map.put("storeName",selectedStoreName);
        map.put("storeLocation",selectedStoreLocation);
        map.put("price",mPrice.getText().toString());
        map.put("currentUserId",currentUserId);
        map.put("imageUrl",downloadUrl);

        mRef.child("PickupData").push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Snackbar.make(findViewById(R.id.ShoppinPickup), "Pickup Data Saved Successfully", Snackbar.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else {
                    Snackbar.make(findViewById(R.id.ShoppinPickup), task.getException().getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void ButtonAddStore(View view) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(PickUpActivity.this);
        builder.setTitle("Add New Store");
        View mView = LayoutInflater.from(PickUpActivity.this)
                .inflate(R.layout.add_new_store,null,false);

        final EditText mStoreName = mView.findViewById(R.id.edt_storeName);
        final EditText mStoreLocation = mView.findViewById(R.id.edt_storeLocation);
        Button mBtnAddStore = mView.findViewById(R.id.btn_addStore);
        Button mBtnCancelDialog = mView.findViewById(R.id.btnCancelStoreDialog);

        builder.setView(mView);
        final Dialog dialog = builder.create();
        mBtnAddStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String storeName = mStoreName.getText().toString().trim();
                String storeLocation = mStoreLocation.getText().toString().trim();
                if (storeName.isEmpty()) {
                    mStoreName.setError("Required");
                    return;
                }
                if (storeLocation.isEmpty()){
                    mStoreLocation.setError("Required");
                    return;
                }
                progressDialog.setTitle("Wait a While");
                progressDialog.setMessage("Adding Store Data");
                progressDialog.setCancelable(false);
                progressDialog.show();
                addStoreDataToFirebaseDatabase(storeName,storeLocation);

                dialog.dismiss();

            }
        });

        mBtnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    private void fillNameSpinnerValues() {
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(PickUpActivity.this,android.R.layout.simple_spinner_item,storeNameList);
        mSpinnerStoreName.setAdapter(arrayAdapter);

        mSpinnerStoreName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = mSpinnerStoreName.getSelectedItemPosition();
                selectedStoreName = storeNameList.get(index).toString();
                Toast.makeText(PickUpActivity.this, selectedStoreName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fillLocationSpinnerValues() {
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(PickUpActivity.this,android.R.layout.simple_spinner_item,storeAddressList);
        mSpinnerStoreLocation.setAdapter(arrayAdapter);

        mSpinnerStoreLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = mSpinnerStoreLocation.getSelectedItemPosition();
                selectedStoreLocation = storeAddressList.get(index).toString();
                Toast.makeText(PickUpActivity.this, selectedStoreLocation, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addStoreDataToFirebaseDatabase(String storeName, String storeLocation) {

        Map map = new HashMap<String,String>();
        map.put("storeName",storeName);
        map.put("storeLocation",storeLocation);

        mRef.child("StoreData").push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(R.id.ShoppinPickup),"Data Stored Successfully",Snackbar.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),PickUpActivity.class));
                    finish();
                }else {
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(R.id.ShoppinPickup),task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void ButtonReceiptImage(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
