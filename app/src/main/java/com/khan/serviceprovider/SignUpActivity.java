package com.khan.serviceprovider;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.khan.serviceprovider.Models.UserDataModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpActivity extends AppCompatActivity {


    int counter=0;
    Button nextFragmentButton;


    RadioButton step1,step2,step3;
    View view1,view2;

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        SignupPersonalInformation signupFragment = new SignupPersonalInformation();
        final addresses_fragment addresses_fragment = new addresses_fragment();
        final payment_methods payment_methods = new payment_methods();


        step1 = (RadioButton) findViewById(R.id.personal_info_radio);
        step2 = (RadioButton) findViewById(R.id.addresses_radio);
        step3 = (RadioButton) findViewById(R.id.payment_radio);
        view1 = (View) findViewById(R.id.view1);
        view2 = (View) findViewById(R.id.view2);

        step1.setChecked(true);


        nextFragmentButton = (Button) findViewById(R.id.nextbutton);
        nextFragmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                counter++;
                Toast.makeText(SignUpActivity.this,"clicked",Toast.LENGTH_LONG).show();
                if(counter==1)
                {
                    replaceFragment(addresses_fragment);
                    Toast.makeText(SignUpActivity.this,"condition 1",Toast.LENGTH_LONG).show();
                    step2.setChecked(true);
                    view1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                }
               else if(counter == 2)
                {
                    replaceFragment(payment_methods);
                    Toast.makeText(SignUpActivity.this,"condition 2",Toast.LENGTH_LONG).show();
                    step3.setChecked(true);
                    view2.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }


            }
        });


    }
    private void replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment1,fragment);
        fragmentTransaction.commit();

    }
}
