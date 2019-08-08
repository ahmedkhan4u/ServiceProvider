package com.khan.serviceprovider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class CleaningServicesActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Spinner mSpinner;
    private String selectedCleanService;
    private String [] spinnerValues = {"Size of Suite","200 sq feet","300 sq feet","400 sq feet"};
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaning_services);

        tabLayout = findViewById(R.id.cleanServiceTabLayout);
        viewPager = findViewById(R.id.cleanServiceViewPager);
        
        TabsAccessorAdapter tabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        tabsAccessorAdapter.AddFragment(new OneTimeCleaningFragment(),"One Time Cleaning");
        tabsAccessorAdapter.AddFragment(new ReOccuringCleanFragment(),"Reoccuring Cleaning");
        viewPager.setAdapter(tabsAccessorAdapter);
        tabLayout.setupWithViewPager(viewPager);


        toolbar = findViewById(R.id.toolbar_CleaningServices);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cleaning Services");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSpinner = findViewById(R.id.spinner_cleanService);
        fillSpinnerValues();
    }

    private void fillSpinnerValues() {
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(CleaningServicesActivity.this,android.R.layout.simple_spinner_item,spinnerValues);
        mSpinner.setAdapter(arrayAdapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = mSpinner.getSelectedItemPosition();
                selectedCleanService = spinnerValues[index];

                SharedPreferences sharedPreferences = getSharedPreferences("SizeOfSuite", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Size",selectedCleanService);
                editor.commit();

                Toast.makeText(CleaningServicesActivity.this, spinnerValues[index], Toast.LENGTH_SHORT).show();



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}
