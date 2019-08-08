package com.khan.serviceprovider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khan.serviceprovider.Models.ItemsModel;
import com.squareup.picasso.Picasso;

public class FragmentHome extends Fragment {

    private FirebaseAuth mAuth;
    private String currentUserId;
    private FloatingActionButton floatingActionButton;
    private DatabaseReference mRef,mDatabase;
    RecyclerView mRecyclerView;
    GridLayoutManager gridLayoutManager;
    FirebaseRecyclerOptions<ItemsModel> firebaseRecyclerOptions;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home,container,false);

        mRecyclerView = view.findViewById(R.id.items_recyclerView);
        gridLayoutManager = new GridLayoutManager(container.getContext(),2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.setSmoothScrollbarEnabled(true);

        floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fab);
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Items Data");

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(),AdminItemUpdate.class));
            }
        });

        checkAdminStatus();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<ItemsModel>()
                .setQuery(mDatabase,ItemsModel.class)
                .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ItemsModel,ItemsViewHolder>(firebaseRecyclerOptions) {

            @NonNull
            @Override
            public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getContext())
                        .inflate(R.layout.custom_item_list,viewGroup,false);
                return new ItemsViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ItemsViewHolder holder, final int position, @NonNull ItemsModel model) {
                holder.setItemName(model.getItemName());
                holder.setItemImage(model.getItemImage(),getContext());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String PostKey = getRef(position).getKey();
                        String itemName;
                        mDatabase.child(PostKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    String itemName = dataSnapshot.child("itemName").getValue().toString();

                                    if (itemName.equals("Conference Room")){
                                        Toast.makeText(getContext(), "Conference Room", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(),ConferenceRoom.class));
                                    }
                                    else if(itemName.equals("Car Wash")){
                                        Toast.makeText(getContext(), "CarWash", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(),CarWash.class));
                                    }
                                    else if(itemName.equals("Dry Clean")){
                                        Toast.makeText(getContext(), "Dry Clean", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(),DryCleanActivity.class));
                                    }else if (itemName.equalsIgnoreCase("clean service")){
                                        Toast.makeText(getContext(), "Cleaning Services", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(),CleaningServicesActivity.class));
                                    }else if (itemName.equalsIgnoreCase("deskside pedicures")){
                                        Toast.makeText(getContext(), "Deskside Manicures", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getContext(),DesksideManicures.class));
                                    }else if (itemName.equalsIgnoreCase("shopping pickup")){
                                        Toast.makeText(getContext(), "Shopping Pickup", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getContext(),PickUpActivity.class));
                                    }else if (itemName.equalsIgnoreCase("car gas refuel")){
                                        Toast.makeText(getContext(), "Car Gas Refuel", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getContext(),CarGasRefuelActivity.class));
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        };

        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void checkAdminStatus() {
        mRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentUserId).exists()){
                    String designation = dataSnapshot.child(currentUserId)
                            .child("designation").getValue().toString();

                    if (designation.equalsIgnoreCase("admin") ){
                        floatingActionButton.setVisibility(View.VISIBLE);

                    }

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static class ItemsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public ItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setItemName(String itemName) {
            TextView mName = mView.findViewById(R.id.custom_ItemName);
            mName.setText(itemName);
        }

        public void setItemImage(String itemImage, Context context) {
            ImageView mImage = mView.findViewById(R.id.custom_itemImage);
            Picasso.with(context).load(itemImage).into(mImage);

        }
    }

}
