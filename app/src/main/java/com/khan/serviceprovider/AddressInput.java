package com.khan.serviceprovider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khan.serviceprovider.Models.AddressesModel;
import com.khan.serviceprovider.Models.Common;
import com.khan.serviceprovider.Models.UserDataModel;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddressInput extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    FusedLocationProviderClient fusedLocationProviderClient;

    public static final int Request_Code = 101;
    Location myLocation;
    EditText address,state,zipcode,buisinessName;

    String uId;
    String BuisinessString;
    String addressString;
    String cityString;
    String stateString;
    String countryString;
    String postalCodeString;

    Button save_button;


    DatabaseReference firebaseDatabase;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_input);
        Toast.makeText(AddressInput.this, ""+ Common.UID, Toast.LENGTH_SHORT).show();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Addresses");

        address = (EditText) findViewById(R.id.address_name);
        state = (EditText)findViewById(R.id.state);
        zipcode = (EditText) findViewById(R.id.zipcode);
        buisinessName = (EditText) findViewById(R.id.business);

        address.setText(Common.UID);

        save_button = (Button) findViewById(R.id.save_address);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(address.getText().toString().equalsIgnoreCase(""))
                {
                    address.setError("Required");
                    return;
                }

                if(state.getText().toString().equalsIgnoreCase(""))
                {
                    state.setError("Required");
                    return;
                }

                if(zipcode.getText().toString().equalsIgnoreCase(""))
                {
                    zipcode.setError("Fill It Manually");
                    return;
                }

                if(buisinessName.getText().toString().equalsIgnoreCase(""))
                {
                    buisinessName.setError("Fill It Manually");
                    return;
                }
                try {

                    uId = Common.UID;
                    BuisinessString = buisinessName.getText().toString();

                    SaveDataToFirebaseDatabase(uId, BuisinessString, addressString, stateString, zipcode.getText().toString());
                }catch
                (Exception e)
                {
                    Toast.makeText(AddressInput.this,e.getMessage().toString(),Toast.LENGTH_LONG).show();
                }

            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

    }

    private void fetchLastLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]
                        {
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, Request_Code);
                return;
            }
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    myLocation = location;
                    Toast.makeText(AddressInput.this, myLocation.getLatitude() + "" + myLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    supportMapFragment.getMapAsync(AddressInput.this);
                }
            }
        });
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                try {

                    // Add a marker in Sydney and move the camera
                    LatLng sydney = new LatLng(latLng.latitude, latLng.longitude);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(sydney).title("My Location"));


                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(AddressInput.this, Locale.getDefault());


                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                    addressString = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    cityString = addresses.get(0).getLocality();
                    stateString = addresses.get(0).getAdminArea();
                    countryString = addresses.get(0).getCountryName();
                    postalCodeString = addresses.get(0).getPostalCode();
                    String postalCode = addresses.get(0).getPostalCode();


                    System.err.println("postal code is "+ postalCode );

                    address.setText(addressString);
                    state.setText(stateString);
                    zipcode.setText(postalCodeString);

                    Toast.makeText(AddressInput.this, "address is " + address, Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

            }
        });

        if (myLocation != null) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I Am Here");
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
            googleMap.addMarker(markerOptions);
        }
    }
    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
                                             @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case Request_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLastLocation();
                }
                break;
        }
    }

    private void SaveDataToFirebaseDatabase(String uId_user, String businessname, String addressname, String statename,String zipcodem) {

        uId=Common.UID;
        firebaseDatabase = firebaseDatabase.child(uId_user).push();
        AddressesModel addressesModel = new AddressesModel(firebaseDatabase.getKey(),uId_user,businessname,addressname,statename,zipcodem);

       firebaseDatabase.setValue(addressesModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AddressInput.this, "Data Saved Successfully", Toast.LENGTH_SHORT).show();
                    if (getFragmentManager().getBackStackEntryCount() == 0) {
                        finish();
                    }
                }else {
                    Toast.makeText(AddressInput.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            super.onBackPressed(); //replaced
        }
    }

}
