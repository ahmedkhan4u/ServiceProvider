package com.khan.serviceprovider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.khan.serviceprovider.Models.DryCleanModel;

import java.util.Calendar;

public class DryCleanActivity extends AppCompatActivity {

    private String [] spinnerValues = {"Pickup","Yes","No"};
    private String selectType;
    Toolbar toolbar;
    Spinner mSpinner;
    String date_time = "";
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    String current_userId;
    RelativeLayout mCalender;
    private TextView dateAndTime,txtDryCleanCost,txtDryCleanPickup,txtDryCleanTotal;
    String completeDateTime;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dry_clean);

        toolbar = findViewById(R.id.toolbar_dryclean);
        mSpinner = findViewById(R.id.spinner_dryclean);
        mCalender = findViewById(R.id.dryClean_calendar);
        dateAndTime = findViewById(R.id.carwash_datAndTime);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        current_userId = mAuth.getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("Reservations").child("Dry Clean");

        txtDryCleanCost = findViewById(R.id.txt_DryCleanCost);
        txtDryCleanPickup = findViewById(R.id.txt_DryCleanPickup);
        txtDryCleanTotal = findViewById(R.id.txt_DryCleanTotal);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Dry Clean");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fillSpinnerValues();

        mCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

    }


    private void fillSpinnerValues() {
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(DryCleanActivity.this,android.R.layout.simple_spinner_item,spinnerValues);
        mSpinner.setAdapter(arrayAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = mSpinner.getSelectedItemPosition();
                selectType = spinnerValues[index];
                if (selectType.equalsIgnoreCase("yes")){

                    int cleanCost=40, picupCost=10, total;
                    total = cleanCost+picupCost;

                    txtDryCleanCost.setText("$40");
                    txtDryCleanPickup.setText("$10");
                    txtDryCleanTotal.setText("$"+total);
                }
                else if (selectType.equalsIgnoreCase("no")){
                    txtDryCleanCost.setText("$40");
                    txtDryCleanPickup.setText("$0");
                    txtDryCleanTotal.setText("$40");
                }
                else {
                    txtDryCleanCost.setText("$0");
                    txtDryCleanPickup.setText("$0");
                    txtDryCleanTotal.setText("$0");
                }
                Toast.makeText(DryCleanActivity.this, spinnerValues[index], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void datePicker(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(DryCleanActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year+",";
                        //*************Call Time Picker Here ********************
                        Toast.makeText(DryCleanActivity.this, date_time, Toast.LENGTH_SHORT).show();
                        tiemPicker();

                        c.set(mYear,mMonth,mDay);

                    }
                }, mYear, mMonth, mDay);


        datePickerDialog.show();
    }

    private void tiemPicker(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(DryCleanActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        completeDateTime = date_time+hourOfDay+":"+minute;
                        dateAndTime.setText(completeDateTime);
                        Toast.makeText(DryCleanActivity
                                        .this, completeDateTime ,
                                Toast.LENGTH_SHORT).show();
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }


    public void Button_OrderDryClean(View view) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(DryCleanActivity.this);
        builder.setTitle("Confirm Service Schedule Car Wash");
        View mView = LayoutInflater.from(DryCleanActivity.this)
                .inflate(R.layout.custom_dialog_accept_decline,null,false);

        Button mBtnAccept = mView.findViewById(R.id.btn_acceptDialog);
        Button mBtnDecline = mView.findViewById(R.id.btn_declineDialog);
        builder.setView(mView);
        final Dialog dialog = builder.create();
        mBtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Please wait...");
                progressDialog.setMessage("Savig Request in Progress");
                progressDialog.setCancelable(false);
                progressDialog.show();
                saveRequstToFirebaseDatabase();
                dialog.dismiss();

            }
        });

        mBtnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Snackbar.make(findViewById(R.id.dryClean),"Request Cancled",Snackbar.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    private void saveRequstToFirebaseDatabase() {

        if (selectType.equalsIgnoreCase("Pickup")){
            Snackbar.make(findViewById(R.id.dryClean),"Please Select Pickup Type",Snackbar.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }
        if (dateAndTime.getText().equals("")){
            Snackbar.make(findViewById(R.id.dryClean),"Please Choose Date And Time",Snackbar.LENGTH_SHORT).show();
            progressDialog.dismiss();
            return;
        }

        String drycleanPriceTotal = txtDryCleanTotal.getText().toString();

        DryCleanModel dryCleanModel = new DryCleanModel(completeDateTime,drycleanPriceTotal,current_userId);

        mRef.push().child(current_userId).setValue(dryCleanModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    cleatAllFields();
                    Snackbar.make(findViewById(R.id.dryClean),"Dry Clean Request Saved Successfully",Snackbar.LENGTH_SHORT).show();
                }else{
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(R.id.dryClean),task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void cleatAllFields() {
        mSpinner.setSelection(0);
        txtDryCleanCost.setText("$0");
        txtDryCleanPickup.setText("$0");
        txtDryCleanTotal.setText("$0");
        dateAndTime.setText("");
    }
}
