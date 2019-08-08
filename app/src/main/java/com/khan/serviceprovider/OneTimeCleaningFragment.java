package com.khan.serviceprovider;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khan.serviceprovider.Models.OneTimeCleanModel;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class OneTimeCleaningFragment extends Fragment {

    RelativeLayout calendar;
    TextView dateAndTime;
    Button btnRequestOneTimeClean;
    ProgressDialog progressDialog;
    String current_userId;
    FirebaseAuth mAuth;
    DatabaseReference mRef;
    public OneTimeCleaningFragment() {
        // Required empty public constructor
    }

    String date_time = "";
    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;
    String spinnerValue;
    String completeDateTime="";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView txtCleanPrice;
    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_one_time_cleaning, container, false);
        calendar = mView.findViewById(R.id.cleanServiceOneTime_Calendar);
        dateAndTime = mView.findViewById(R.id.cleanServiceOneTime_datAndTime);
        btnRequestOneTimeClean = mView.findViewById(R.id.btn_CleanServiceOneTime);
        txtCleanPrice = mView.findViewById(R.id.txt_oneTimeCleanCost);

        mAuth = FirebaseAuth.getInstance();
        current_userId = mAuth.getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference()
                .child("Reservations").child("Clean Services")
                .child("One Time Clean");

        progressDialog = new ProgressDialog(getContext());

        btnRequestOneTimeClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences = getContext().getSharedPreferences("SizeOfSuite", Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                spinnerValue = sharedPreferences.getString("Size","Size Of Suite");
                Toast.makeText(getContext(), spinnerValue, Toast.LENGTH_SHORT).show();

                if (completeDateTime.equals("")){
                    Snackbar.make(mView.findViewById(R.id.oneTimeClean),"Please Select Date",Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (spinnerValue.equalsIgnoreCase("size of suite")){
                    txtCleanPrice.setText("$0");
                    Snackbar.make(mView.findViewById(R.id.oneTimeClean),"Please Select Size of Suite",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                else if (spinnerValue.equalsIgnoreCase("200 sq feet")){
                    txtCleanPrice.setText("$20");
                }
                else if (spinnerValue.equalsIgnoreCase("300 sq feet")){
                    txtCleanPrice.setText("$30");
                }
                else if (spinnerValue.equalsIgnoreCase("400 sq feet")){
                    txtCleanPrice.setText("$40");
                }
                else{
                    txtCleanPrice.setText("$0");
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Confirm Service\nService Agent will Call you Shortly");
                View view = LayoutInflater.from(getActivity())
                        .inflate(R.layout.custom_dialog_accept_decline,null,false);

                Button mBtnAccept = view.findViewById(R.id.btn_acceptDialog);
                Button mBtnDecline = view.findViewById(R.id.btn_declineDialog);
                builder.setView(view);
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
                        Snackbar.make(mView.findViewById(R.id.oneTimeClean),"Request Cancled",Snackbar.LENGTH_SHORT).show();
                    }
                });

                dialog.show();
                dialog.setCanceledOnTouchOutside(false);

                showDialogBox();

            }

        });

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        return mView;
    }

    private void saveRequstToFirebaseDatabase() {
        String price = txtCleanPrice.getText().toString();
        OneTimeCleanModel oneTimeCleanModel = new OneTimeCleanModel(spinnerValue,completeDateTime,price,current_userId);
        mRef.push().child(current_userId).setValue(oneTimeCleanModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    txtCleanPrice.setText("$0");
                    dateAndTime.setText("");
                    Snackbar.make(mView.findViewById(R.id.oneTimeClean),"Request Saved Successful",Snackbar.LENGTH_SHORT).show();
                }else {
                    progressDialog.dismiss();
                    Snackbar.make(mView.findViewById(R.id.oneTimeClean),task.getException().getMessage(),Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDialogBox() {



    }

    private void datePicker(){

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date_time = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year+",";
                        //*************Call Time Picker Here ********************
                        Toast.makeText(getContext(), date_time, Toast.LENGTH_SHORT).show();
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
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        completeDateTime = date_time+hourOfDay+":"+minute;
                        dateAndTime.setText(completeDateTime);
                        Toast.makeText(getContext(), completeDateTime ,
                                Toast.LENGTH_SHORT).show();
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

}
