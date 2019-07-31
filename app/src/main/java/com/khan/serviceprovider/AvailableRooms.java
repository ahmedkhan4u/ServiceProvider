package com.khan.serviceprovider;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AvailableRooms extends AppCompatActivity {

    private DatabaseReference mRef;
    List arrayList,finalValues;
    private CompactCalendarView compactCalendarView;
    private RelativeLayout startDate,endDate;

    String date_time = "";
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    String mStartDate,mEndDate;
    long calStartDate,calEndDate;
    TextView txtStartDate,txtEndDate;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_rooms);

        mRef = FirebaseDatabase.getInstance().getReference().child("Reservations");

        finalValues = new ArrayList<String>();
        arrayList = new ArrayList<String>();
        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        startDate = findViewById(R.id.calendarStartDate);
        endDate = findViewById(R.id.calendarEndDate);
        txtStartDate = findViewById(R.id.txtStartDate);
        txtEndDate = findViewById(R.id.txtEndDate);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enddatePicker();
            }
        });

        // Set first day of week to Monday, defaults to Monday so calling setFirstDayOfWeek is not necessary
        // Use constants provided by Java Calendar class
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = compactCalendarView.getEvents(dateClicked);
                Toast.makeText(AvailableRooms.this, dateClicked.toString(), Toast.LENGTH_SHORT).show();
                }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

            }
        });
    }

    public void SelectDateTime(View view) {

        if (txtStartDate.getText().equals("")){
            Toast.makeText(this, "Please Choose Start Date", Toast.LENGTH_SHORT).show();
            return;
        }

        if (txtEndDate.getText().equals("")){
            Toast.makeText(this, "Please Choose End Date", Toast.LENGTH_SHORT).show();
            return;
        }

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    if (!dataSnapshot.hasChildren()) {
                        return;
                    }

                    for (DataSnapshot childSnapShot : item.getChildren()) {
                        final String timestamp = childSnapShot.getKey();
                        final DataSnapshot parentSnapShot = childSnapShot.child(timestamp);
                        System.out.println(timestamp);
                        arrayList.add(timestamp);
                    }
                }

                setDateOnCalendar();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setDateOnCalendar(){


        getStartDate();
        getEndDate();

        int i=0;
        while (i<arrayList.size()){
            String value = arrayList.get(i).toString();
            String  []splitArray = value.split(",");
            String date_time = splitArray[0]+" "+splitArray[1];

            String myDate = date_time;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(myDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long millis = date.getTime();

            if (millis>=calStartDate && millis<=calEndDate)
            {
                Event ev1 = new Event(Color.RED, millis, "Some extra data that I want to store.");
                compactCalendarView.addEvent(ev1);
            }

            i++;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getStartDate() {


        String myDate = mStartDate;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calStartDate = date.getTime();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getEndDate() {


        String myDate = mEndDate;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calEndDate = date.getTime();
    }

    private void datePicker(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(AvailableRooms.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year+" ";
                        //*************Call Time Picker Here ********************
                        Toast.makeText(AvailableRooms.this, date_time, Toast.LENGTH_SHORT).show();
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(AvailableRooms.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        mStartDate = date_time+hourOfDay+":"+minute+":1";
                        txtStartDate.setText(mStartDate);
                        Toast.makeText(AvailableRooms
                                        .this, mStartDate ,
                                Toast.LENGTH_SHORT).show();
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void enddatePicker(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(AvailableRooms.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year+" ";
                        //*************Call Time Picker Here ********************
                        Toast.makeText(AvailableRooms.this, date_time, Toast.LENGTH_SHORT).show();
                        endtiemPicker();

                        c.set(mYear,mMonth,mDay);

                    }
                }, mYear, mMonth, mDay);


        datePickerDialog.show();
    }

    private void endtiemPicker(){
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(AvailableRooms.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        mEndDate = date_time+hourOfDay+":"+minute+":1";
                        txtEndDate.setText(mEndDate);
                        Toast.makeText(AvailableRooms
                                        .this, mEndDate ,
                                Toast.LENGTH_SHORT).show();
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
}