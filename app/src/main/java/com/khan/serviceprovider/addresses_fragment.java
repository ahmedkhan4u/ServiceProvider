package com.khan.serviceprovider;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


public class addresses_fragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_addresses_fragment, container, false);
        Toast.makeText(getContext(), "Frag", Toast.LENGTH_SHORT).show();
        Button addnew = (Button) view.findViewById(R.id.add_new);
        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),AddressInput.class));
            }
        });
        return view;
    }


}
