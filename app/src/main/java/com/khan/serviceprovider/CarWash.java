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
import com.khan.serviceprovider.Models.CarWashModel;

import java.util.Calendar;

public class CarWash extends AppCompatActivity {
    private Spinner mSpinner;
    private String [] spinnerValues = {"Select Type","Waterless Wash","Handwash","Automatic"};

    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    String selectedWash,currentUserId;
    String date_time = "";
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    private TextView txtCarWashPrice;
    String completeDateTime="";
    TextView mDateAndTime;
    RelativeLayout carwashCalendar;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_wash);

        toolbar = findViewById(R.id.toolbar_carWash);
        mSpinner = findViewById(R.id.spinner_carwash);
        mDateAndTime = findViewById(R.id.carwash_datAndTime);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("Reservations").child("Car Wash");
        carwashCalendar = findViewById(R.id.carwash_calendar);
        txtCarWashPrice = findViewById(R.id.txt_DryCleanCost);
        progressDialog = new ProgressDialog(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Car Wash");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fillSpinnerValues();

        carwashCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });
    }

    private void fillSpinnerValues() {
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(CarWash.this,android.R.layout.simple_spinner_item,spinnerValues);
        mSpinner.setAdapter(arrayAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = mSpinner.getSelectedItemPosition();
                selectedWash = spinnerValues[index];
                setCarWashPrice(selectedWash);
                Toast.makeText(CarWash.this, spinnerValues[index], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setCarWashPrice(String selectedWash) {
        if (selectedWash.equals("Automatic")){
            txtCarWashPrice.setText("$50");
        }else if (selectedWash.equals("Waterless Wash")){
            txtCarWashPrice.setText("$30");
        }
        else if (selectedWash.equals("Handwash")){
            txtCarWashPrice.setText("$40");
        }
        else {
            txtCarWashPrice.setText("$0");
        }
    }

    private void datePicker(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(CarWash.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year+",";
                        //*************Call Time Picker Here ********************
                        Toast.makeText(CarWash.this, date_time, Toast.LENGTH_SHORT).show();
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(CarWash.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        completeDateTime = date_time+hourOfDay+":"+minute;
                        mDateAndTime.setText(completeDateTime);
                        Toast.makeText(CarWash
                                        .this, completeDateTime ,
                                Toast.LENGTH_SHORT).show();
                        Toast.makeText(CarWash.this, completeDateTime, Toast.LENGTH_SHORT).show();

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    public void Button_OrderCarwash(View view) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(CarWash.this);
        builder.setTitle("Confirm Service Schedule Car Wash");
        View mView = LayoutInflater.from(CarWash.this)
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

                saveOrderDataToFirebaseDatabase();
                dialog.dismiss();

            }
        });

        mBtnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Snackbar.make(findViewById(R.id.carWashLayout),"Order Canceled",Snackbar.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    private void saveOrderDataToFirebaseDatabase() {
        String mPrice = txtCarWashPrice.getText().toString().trim();
        if (mPrice.equalsIgnoreCase("$0")){
            Snackbar.make(findViewById(R.id.carWashLayout),"Please Choose Wash Type",Snackbar.LENGTH_SHORT).show();

            progressDialog.dismiss();
            return;
        }
        CarWashModel carWashModel = new CarWashModel(currentUserId,selectedWash,mPrice,completeDateTime);
        mRef.push().child(currentUserId).setValue(carWashModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    clearAllFields();
                    Snackbar.make(findViewById(R.id.carWashLayout),"Order Posted Successfully",Snackbar.LENGTH_SHORT).show();
                }else {
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(R.id.carWashLayout),task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearAllFields() {
        mSpinner.setSelection(0);
        mDateAndTime.setText("");
    }
}
