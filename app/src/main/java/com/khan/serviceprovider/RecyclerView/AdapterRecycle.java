package com.khan.serviceprovider.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.khan.serviceprovider.Models.AddressesModel;
import com.khan.serviceprovider.Models.Common;
import com.khan.serviceprovider.R;
import com.khan.serviceprovider.SignUpActivity;

import java.util.HashMap;
import java.util.List;

public class AdapterRecycle extends RecyclerView.Adapter<AdapterRecycle.ViewHolder> {

    private List<AddressesModel> list;
    private Context context;
    DatabaseReference databaseReference;

    public AdapterRecycle(List<AddressesModel> list, Context context) {
        this.list = list;
        this.context = context;




    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_address,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        final AddressesModel model = list.get(position);


        holder.businessName.setText(model.getBusiness_name());
        holder.addressName.setText(model.getAddress_name());
        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference = FirebaseDatabase.getInstance().getReference("Addresses");
                databaseReference.child(Common.UID).child(model.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        holder.edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             final Dialog dialog = new Dialog(context);
             dialog.setContentView(R.layout.custom_dialogbox);
             dialog.setCancelable(false);
             dialog.show();
                final EditText business_name = (EditText) dialog.findViewById(R.id.dialog_buisiness);
                final EditText address_name = (EditText) dialog.findViewById(R.id.dialog_address);
                final EditText state_name = (EditText) dialog.findViewById(R.id.dialog_state);
                final EditText zipcode = (EditText) dialog.findViewById(R.id.dialog_zipcode);
                Button save = (Button)dialog.findViewById(R.id.dialog_save_button);
                Button cancel = (Button)dialog.findViewById(R.id.dialog_cancel_button);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });


                business_name.setText(model.getBusiness_name());
                address_name.setText(model.getAddress_name());
                state_name.setText(model.getState_name());
                zipcode.setText(model.getZip_code());
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      DatabaseReference  database = FirebaseDatabase.getInstance().getReference("Addresses").child(Common.UID).child(model.getId());

                        HashMap<String,Object> map = new HashMap<>();

                        map.put("address_name",address_name.getText().toString());
                        map.put("business_name",business_name.getText().toString());
                        map.put("state_name",state_name.getText().toString());
                        map.put("zip_code",zipcode.getText().toString());

                        database.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                    Toast.makeText(context,"Updated" , Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                            }
                        });

                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {

        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView businessName, addressName ,delete_button,edit_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            businessName = (TextView) itemView.findViewById(R.id.business_name);
            addressName = (TextView) itemView.findViewById(R.id.address_recycler);
            delete_button = (TextView) itemView.findViewById(R.id.delete_id);
            edit_button = (TextView) itemView.findViewById(R.id.edit_id);
        }
    }

}
