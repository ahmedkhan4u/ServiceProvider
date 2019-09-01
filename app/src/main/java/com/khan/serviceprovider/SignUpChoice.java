package com.khan.serviceprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SignUpChoice extends AppCompatActivity {

    Button as_user,as_company;
    TextView btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_choice);


        as_user = (Button) findViewById(R.id.as_user);
        as_company = (Button) findViewById(R.id.as_company);
        btnCreateAccount = (TextView) findViewById(R.id.btnCreateAccount);

        as_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpChoice.this,SignUpActivity.class));
                finish();
            }
        });
        as_company.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(SignUpChoice.this,SignUpCompany.class));
                finish();
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpChoice.this,MainActivity.class);
                startActivity(intent);
            }
        });



    }
}
