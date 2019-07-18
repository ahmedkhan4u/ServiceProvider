package com.khan.serviceprovider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int a;
        a=2;
        System.out.println("hello world");

        String name = "Cjnomi";
        System.out.println(name);
    }
}
