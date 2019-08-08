package com.khan.serviceprovider;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class CarGasRefuelActivity extends AppCompatActivity {

    Spinner mSpinnerFuelType,mSpinnerFuelGrade;
    String selectedFuelType,selectdFuelGrade,currentUserId;
    DatabaseReference mRef;
    TextView txtCarFuelCost;
    FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    String spinnerFuelTypeValues [] = {"Fuel Type","Gasoline","Gas","Petrol"};
    String spinnerFuelGradeValues [] = {"Fuel Grade","1 litre","2 litre","3 litre","4 litre","5 litre","8 litre","10 litre","12 litre","15 litre","20 litre"};

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_gas_refuel);

        mSpinnerFuelType = findViewById(R.id.spinner_carFuelType);
        mSpinnerFuelGrade = findViewById(R.id.spinner_carFuelGrade);
        mRef = FirebaseDatabase.getInstance().getReference().child("Reservations").child("CarGasRefuel");
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);
        txtCarFuelCost = findViewById(R.id.txt_carRefuelCost);
        toolbar = findViewById(R.id.toolbar_carRefuel);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Car Gas Refuel");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fillFuelTypeSpinnerValues();
        fillFuelGradeSpinnerValues();
    }

    private void fillFuelTypeSpinnerValues() {
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(CarGasRefuelActivity.this,android.R.layout.simple_spinner_item,spinnerFuelTypeValues);
        mSpinnerFuelType.setAdapter(arrayAdapter);

        mSpinnerFuelType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = mSpinnerFuelType.getSelectedItemPosition();
                selectedFuelType = spinnerFuelTypeValues[index];
                Toast.makeText(CarGasRefuelActivity.this, selectedFuelType, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void fillFuelGradeSpinnerValues() {
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(CarGasRefuelActivity.this,android.R.layout.simple_spinner_item,spinnerFuelGradeValues);
        mSpinnerFuelGrade.setAdapter(arrayAdapter);

        mSpinnerFuelGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = mSpinnerFuelGrade.getSelectedItemPosition();
                selectdFuelGrade = spinnerFuelGradeValues[index];
                Toast.makeText(CarGasRefuelActivity.this, selectdFuelGrade, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void Button_OrderRefuel(View view) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(CarGasRefuelActivity.this);
        builder.setTitle("Confirm Service\nOrder Deskside Manicures");
        View mView = LayoutInflater.from(CarGasRefuelActivity.this)
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
                savaDataToFirebaseDatabase();
                dialog.dismiss();

            }
        });

        mBtnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Snackbar.make(findViewById(R.id.carGasRefuel),"Request Canceled",Snackbar.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    private void savaDataToFirebaseDatabase() {

        if (selectedFuelType.equalsIgnoreCase("fuel type")){
            Snackbar.make(findViewById(R.id.carGasRefuel),"Select Fuel Type",Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (selectedFuelType.equalsIgnoreCase("fuel grade")){
            Snackbar.make(findViewById(R.id.carGasRefuel),"Select Fuel Grade",Snackbar.LENGTH_SHORT).show();
            return;
        }

        Map map = new HashMap<String,String>();

        map.put("fuelType",selectedFuelType);
        map.put("fuelGrade",selectdFuelGrade);
        map.put("uId",currentUserId);
        map.put("price",txtCarFuelCost.getText().toString());


        mRef.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Snackbar.make(findViewById(R.id.carGasRefuel),"Data Saved Successfully",Snackbar.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }else {
                    Snackbar.make(findViewById(R.id.carGasRefuel),task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });


    }
}
