package com.khan.serviceprovider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khan.serviceprovider.Models.ConferenceRoomDateModel;

import java.util.Calendar;

public class ConferenceRoom extends AppCompatActivity {
    private Toolbar mToolbar;
    LinearLayout linearLayout;
    private Spinner mSpinner;
    private RelativeLayout mCalender;
    private DatabaseReference mRef,postRef;
    private FirebaseAuth mAuth;
    private String currentUser;
    private String [] spinnerValues = {"Select Room","Room1","Room2","Room3","Room4"};
    private TextView mDateAndTime;
    String date_time = "";
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    String completeDateTime;
    String selectedRoom;
    private Button mBtnAccept,mBtnDecline,mBtnAvailableDates;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conference_room);

        progressDialog = new ProgressDialog(this);
        mToolbar = findViewById(R.id.toolbarConferenceRoom);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Conference Room");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRef = FirebaseDatabase.getInstance().getReference().child("Reservations");
        postRef = FirebaseDatabase.getInstance().getReference().child("Reservations").child("Conference Room");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        linearLayout = findViewById(R.id.linearLayout);
        mSpinner = findViewById(R.id.spinner_ConferenceRoom);
        mCalender = findViewById(R.id.confereceRoomCalender);
        mDateAndTime = findViewById(R.id.confr_dateAndTime);
        mBtnAvailableDates = findViewById(R.id.btn_chkAvailableRoom);
        mCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            datePicker();
            }
        });
        mBtnAvailableDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AvailableRooms.class));
            }
        });
        fillSpinnerValues();

    }
    private void datePicker(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(ConferenceRoom.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year+",";
                        //*************Call Time Picker Here ********************
                        Toast.makeText(ConferenceRoom.this, date_time, Toast.LENGTH_SHORT).show();
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(ConferenceRoom.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        completeDateTime = date_time+hourOfDay+":"+minute+":1";
                        mDateAndTime.setText(completeDateTime);
                        Toast.makeText(ConferenceRoom
                                .this, completeDateTime ,
                                Toast.LENGTH_SHORT).show();
                        Toast.makeText(ConferenceRoom.this, completeDateTime, Toast.LENGTH_SHORT).show();

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void fillSpinnerValues() {
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(ConferenceRoom.this,android.R.layout.simple_spinner_item,spinnerValues);
        mSpinner.setAdapter(arrayAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = mSpinner.getSelectedItemPosition();
                selectedRoom = spinnerValues[index];
                Toast.makeText(ConferenceRoom.this, spinnerValues[index], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void ReserveRoom(View view) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ConferenceRoom.this);
        builder.setTitle("Schedule Room Reserve");

        View mView = LayoutInflater.from(ConferenceRoom.this)
                .inflate(R.layout.custom_dialog_accept_decline,null,false);

        Button mBtnAccept = mView.findViewById(R.id.btn_acceptDialog);
        Button mBtnDecline = mView.findViewById(R.id.btn_declineDialog);
        builder.setView(mView);
        final Dialog dialog = builder.create();
        mBtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Please wait...");
                progressDialog.setMessage("Checking for room reservations");
                progressDialog.setCancelable(false);
                progressDialog.show();
                reserveRoom();
                dialog.dismiss();
            }
        });

        mBtnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                Snackbar.make(findViewById(R.id.linearLayout),"Reservation Declined",Snackbar.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
    }

    private void reserveRoom() {
        Snackbar snackbar = Snackbar.
                make(findViewById(R.id.linearLayout), "Accept", Snackbar.LENGTH_LONG);
        snackbar.show();
        if (mDateAndTime.getText().equals("")){
            Snackbar.make(findViewById(R.id.linearLayout),
                    "Please select a date",Snackbar.LENGTH_LONG).show();
            progressDialog.dismiss();
         return;
        }
        if (selectedRoom.equalsIgnoreCase("select room")){
            Snackbar.make(findViewById(R.id.linearLayout),
                    "Please choose a room",Snackbar.LENGTH_LONG).show();
            progressDialog.dismiss();
            return;
        }

            postRef.child(completeDateTime).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        if (dataSnapshot.child(selectedRoom).exists()){
                                Snackbar.make(findViewById(R.id.linearLayout),
                                        "There is already a reservation on selected date with selected room",Snackbar.LENGTH_LONG).show();
                                progressDialog.dismiss();
                                return;
                        }else {
                            saveReservationToFirebase();
                        }
                    }
                    else {
                        saveReservationToFirebase();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(ConferenceRoom.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
    }

    private void saveReservationToFirebase() {

        String date = String.valueOf(date_time);
        String time = mHour+":"+mMinute+"1";

        ConferenceRoomDateModel model = new ConferenceRoomDateModel(currentUser,selectedRoom,completeDateTime);
        mRef.child("Conference Room").child(completeDateTime).child(selectedRoom).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(R.id.linearLayout),"Room Reservation Successfully Done",Snackbar.LENGTH_SHORT).show();
                }else {
                    progressDialog.dismiss();
                    Snackbar.make(findViewById(R.id.linearLayout),task.getException().getMessage(),Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }
}
