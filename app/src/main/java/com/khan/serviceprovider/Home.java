package com.khan.serviceprovider;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private FirebaseAuth mAuth;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    private CircleImageView profileImage;
    private TextView profileName,profileEmail;
    DatabaseReference mRef;
    String currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();

        mRef = FirebaseDatabase.getInstance().getReference().child("Users");

        profileImage = findViewById(R.id.home_ProfileImage);
        profileEmail = findViewById(R.id.home_ProfielEmail);
        profileName = findViewById(R.id.home_ProfileName);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //must be pasted below toggle.syndState();
        if(savedInstanceState==null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
        {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId())
        {
            case R.id.nav_home:
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentHome()).commit();
                break;
            }
            case R.id.nav_profile:
            {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentProfile()).commit();
                break;
            }
            case R.id.nav_rate:
            {
                Toast.makeText(Home.this,"Rate us" ,Toast.LENGTH_LONG).show();
                break;
            }
            case R.id.nav_about:
            {
                Toast.makeText(Home.this,"About us" ,Toast.LENGTH_LONG).show();
                break;
            }

            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getProfileDataFormFirebase();
    }

    private void getProfileDataFormFirebase() {
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentUser).exists()){
                    String name = dataSnapshot
                            .child(currentUser).child("name").getValue().toString();
                    String email = dataSnapshot
                            .child(currentUser).child("email").getValue().toString();
                    String imageUrl = dataSnapshot
                            .child(currentUser).child("imageUrl").getValue().toString();

                    showDataOnDrawer(name,email,imageUrl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showDataOnDrawer(String name, String email, String imageUrl) {
        profileImage = findViewById(R.id.home_ProfileImage);
        profileEmail = findViewById(R.id.home_ProfielEmail);
        profileName = findViewById(R.id.home_ProfileName);
        profileName.setText(name);
        profileEmail.setText(email);
        Picasso.with(getApplicationContext()).load(imageUrl).placeholder(R.drawable.profile_image).into(profileImage);
    }
}
