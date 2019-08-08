package com.khan.serviceprovider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khan.serviceprovider.Models.AddressesModel;
import com.khan.serviceprovider.Models.Common;
import com.khan.serviceprovider.RecyclerView.AdapterRecycle;

import java.util.ArrayList;


public class addresses_fragment extends Fragment {

    RecyclerView recyclerView;
    AdapterRecycle adapterRecycle;
    ArrayList<AddressesModel> list = new ArrayList<>();
    private DatabaseReference firebaseDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_addresses_fragment, container, false);
        recyclerView = view.findViewById(R.id.myrecv);

adapterRecycle = new AdapterRecycle(list,getActivity());
recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
recyclerView.setAdapter(adapterRecycle);
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Addresses").child(Common.UID);

        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (list != null){
                    list.clear();
                }

                System.err.println("my data0"+dataSnapshot);
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {


                    AddressesModel busAddress = ds.getValue(AddressesModel.class);



                    list.add(busAddress);

                    adapterRecycle.notifyDataSetChanged();



                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(getContext(), "Frag", Toast.LENGTH_SHORT).show();
        Button addnew = (Button) view.findViewById(R.id.add_new);
        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddressInput.class));
            }
        });
        return view;
    }


}
