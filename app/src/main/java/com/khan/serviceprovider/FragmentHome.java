package com.khan.serviceprovider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khan.serviceprovider.Models.ItemsModel;
import com.squareup.picasso.Picasso;

import java.sql.Ref;

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
                                    Toast.makeText(getContext(), itemName, Toast.LENGTH_SHORT).show();

                                    if (itemName.equals("Conference Room")){
                                        Toast.makeText(getContext(), "Conference Room", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(),ConferenceRoom.class));
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
