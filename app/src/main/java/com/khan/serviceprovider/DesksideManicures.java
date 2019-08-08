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
import android.widget.EditText;
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
import com.khan.serviceprovider.Models.DesksideManicuresModel;

import java.util.Calendar;

public class DesksideManicures extends AppCompatActivity {

    private Spinner mSpinner;
    private String [] spinnerValues = {"Manicure Service Type","Gel Manicure","Regular Manicure","Acrylic Manicure"};
    Toolbar toolbar;
    ProgressDialog progressDialog;
    String selectedService,currentUserId,dayTiming,price;
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    String date_time = "";
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;

    String completeDateTime="";
    TextView mDateAndTime,mPrice;
    RelativeLayout desksideManicuresCalendar;
    EditText edtDayTiming;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deskside_manicures);

        mSpinner = findViewById(R.id.spinner_DesksideManicures);
        desksideManicuresCalendar = findViewById(R.id.desksideManicures_calendar);
        mDateAndTime = findViewById(R.id.desksideManicures_datAndTime);
        edtDayTiming = findViewById(R.id.desksideManicures_dayTiming);
        mPrice = findViewById(R.id.txt_desksideManicureTotalPrice);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("Reservations").child("Deskside Manicures");

        toolbar = findViewById(R.id.toolbar_deskSideManicures);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Deskside Manicures");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        desksideManicuresCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        fillSpinnerValues();

    }

    public void ButtonDeskManicuresReqSer(View view) {
        dayTiming = edtDayTiming.getText().toString().trim();
        price = mPrice.getText().toString();
        if (mPrice.getText().equals("$0")){
            Snackbar.make(findViewById(R.id.desksideManicures),"Please Choose Service Type",Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (completeDateTime.equals(""))
        {
            Snackbar.make(findViewById(R.id.desksideManicures),"Please Choose Date And Time",Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (!dayTiming.equalsIgnoreCase("Morning") && !dayTiming.equalsIgnoreCase("Evening"))
        {
            Snackbar.make(findViewById(R.id.desksideManicures),"Write Day Timing \"Morning\" or \"Evening\"",Snackbar.LENGTH_SHORT).show();
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(DesksideManicures.this);
        builder.setTitle("Confirm Service\nOrder Deskside Manicures");
        View mView = LayoutInflater.from(DesksideManicures.this)
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
                Snackbar.make(findViewById(R.id.desksideManicures),"Request Canceled",Snackbar.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    private void saveOrderDataToFirebaseDatabase() {
        DesksideManicuresModel desksideManicuresModel = new DesksideManicuresModel(currentUserId,completeDateTime,dayTiming,price,selectedService);

        mRef.push().child(currentUserId).setValue(desksideManicuresModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(R.id.desksideManicures),"Service Request Successful",Snackbar.LENGTH_SHORT).show();
                }else {
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(R.id.desksideManicures),task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void fillSpinnerValues() {
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(DesksideManicures.this,android.R.layout.simple_spinner_item,spinnerValues);
        mSpinner.setAdapter(arrayAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = mSpinner.getSelectedItemPosition();
                selectedService = spinnerValues[index];
                selectedServiceType(selectedService);
                Toast.makeText(DesksideManicures.this, spinnerValues[index], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void selectedServiceType(String selectedService) {
        if (selectedService.equalsIgnoreCase("Gel Manicure")){
            mPrice.setText("$40");
        }
        else if (selectedService.equalsIgnoreCase("Regular Manicure")){
            mPrice.setText("$20");
        }
        else if (selectedService.equalsIgnoreCase("Acrylic Manicure")){
            mPrice.setText("$35");
        }else {
            mPrice.setText("$0");
        }
    }

    private void datePicker(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(DesksideManicures.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year+",";
                        //*************Call Time Picker Here ********************
                        Toast.makeText(DesksideManicures.this, date_time, Toast.LENGTH_SHORT).show();
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(DesksideManicures.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        completeDateTime = date_time+hourOfDay+":"+minute;
                        mDateAndTime.setText(completeDateTime);
                        Toast.makeText(DesksideManicures
                                        .this, completeDateTime ,
                                Toast.LENGTH_SHORT).show();

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
}
