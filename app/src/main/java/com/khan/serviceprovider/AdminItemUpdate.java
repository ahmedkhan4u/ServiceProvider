package com.khan.serviceprovider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.khan.serviceprovider.Models.ItemsModel;

public class AdminItemUpdate extends AppCompatActivity {

    ImageView mItemImage;
    TextView mItemText;
    LinearLayout linearLayout;
    Uri imageUri;
    String downloadUrl,currentUser;
    StorageReference mStorage;
    DatabaseReference mRef;
    FirebaseAuth mAuth;

    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_item_update);

        mItemImage = findViewById(R.id.itemImage);
        mItemText = findViewById(R.id.itemText);
        linearLayout = findViewById(R.id.linearLayout);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        mStorage = FirebaseStorage.getInstance().getReference().child("Item Images");
        mRef = FirebaseDatabase.getInstance().getReference();

        dialog = new ProgressDialog(this);

        mItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallary();
            }
        });

    }

    private void getImageFromGallary() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            mItemImage.setImageURI(imageUri);
        }
    }

    public void SaveItem(View view) {

        String itemName = mItemText.getText().toString().trim();

        if (itemName.isEmpty()){
            mItemText.setError("Required");
            return;
        }
        if (imageUri==null){
            Snackbar snackbar = Snackbar
                    .make(linearLayout, "Please choose an image", Snackbar.LENGTH_LONG);
            snackbar.show();
            return;
        }

        dialog.setTitle("Please Wait");
        dialog.setMessage("Data Saving In Progress");
        dialog.setCancelable(false);
        dialog.show();
        saveItemDataToFirebaseDb(itemName,imageUri);
    }

    private void saveItemDataToFirebaseDb(final String itemName, final Uri imageUri) {

        final StorageReference reference = mStorage.child(imageUri.getLastPathSegment()+".jpg");
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
                    //Toast.makeText(AddPost.this, downloadUrl, Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    saveItemDataToFirebaseDatabase(itemName,downloadUrl);
                } else {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    private void saveItemDataToFirebaseDatabase(String itemName, String downloadUrl) {

        ItemsModel itemsModel = new ItemsModel(itemName,downloadUrl);

        mRef.child("Items Data").push().setValue(itemsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Data Stored Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    dialog.dismiss();
                    Toast.makeText(AdminItemUpdate.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
