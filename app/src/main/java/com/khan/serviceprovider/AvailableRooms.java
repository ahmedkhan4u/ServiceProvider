package com.khan.serviceprovider;

import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AvailableRooms extends AppCompatActivity {

    private DatabaseReference mRef;
    List arrayList,finalValues;
    private CompactCalendarView compactCalendarView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_rooms);

        mRef = FirebaseDatabase.getInstance().getReference().child("Reservations");

        finalValues = new ArrayList<String>();
        arrayList = new ArrayList<String>();
        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);

        // Set first day of week to Monday, defaults to Monday so calling setFirstDayOfWeek is not necessary
        // Use constants provided by Java Calendar class
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);

        String myDate = "26-7-2019 8:10:2";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(myDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();

        Event ev1 = new Event(Color.RED, (System.currentTimeMillis()-86400000), "Some extra data that I want to store.");
        compactCalendarView.addEvent(ev1);

        Event ev2 = new Event(Color.RED, millis, "Some extra data that I want to store.");
        compactCalendarView.addEvent(ev2);

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

            Event ev1 = new Event(Color.RED, millis, "Some extra data that I want to store.");
            compactCalendarView.addEvent(ev1);

            i++;
        }
    }
}