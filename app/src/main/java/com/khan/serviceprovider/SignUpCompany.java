package com.khan.serviceprovider;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpCompany extends AppCompatActivity {

    TextInputEditText fullname_textinput_layout, address_textinput_layout, email_textinput_layout, password_textinput_layout;
    Button signupasCOmpany;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_company);

        fullname_textinput_layout = (TextInputEditText) findViewById(R.id.text_input_fullname);
        address_textinput_layout = (TextInputEditText) findViewById(R.id.text_input_address);
        email_textinput_layout = (TextInputEditText) findViewById(R.id.text_input_email);
        password_textinput_layout = (TextInputEditText) findViewById(R.id.text_input_password);
        findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
