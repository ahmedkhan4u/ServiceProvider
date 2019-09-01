package com.khan.serviceprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class Desclaimer extends AppCompatActivity {

    CheckBox accept;
    Button complete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desclaimer);


        accept = (CheckBox) findViewById(R.id.accept);
        complete = (Button) findViewById(R.id.complete);
        complete.setEnabled(false);
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(accept.isChecked()==true)
                {
                    complete.setEnabled(true);
                }
                else if(accept.isChecked()==false)
                {
                    complete.setEnabled(false);
                }
            }
        });
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Desclaimer.this,Home.class));
                finish();
            }
        });


    }
}
