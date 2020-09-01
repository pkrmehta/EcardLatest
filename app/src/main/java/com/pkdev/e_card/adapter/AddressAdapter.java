package com.pkdev.e_card.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pkdev.e_card.EditProfile;
import com.pkdev.e_card.R;
import com.pkdev.e_card.model.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.TestViewHolder>{

    Context mCtx;
    List<Address> addressList;
    EditProfile editProfile;

    public AddressAdapter(Context mCtx,List<Address> addressList)
    {
        this.mCtx = mCtx;
        this.addressList = addressList;
    }
    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.list_address,
                parent, false);
        TestViewHolder testViewHolder = new TestViewHolder(view);
        return testViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, final int position) {
        final Address address = addressList.get(position);
        holder.address.setText(address.getAddress());
        holder.type.setText(address.getType());
        holder.linearAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    editProfile = (EditProfile) mCtx;
                    if (editProfile.isEditable) {
                        editProfile.showAddressDialog("edit", address, position);
                    }
                }
                catch (Exception e)
                {

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    class TestViewHolder extends RecyclerView.ViewHolder
    {
        TextView address,type;
        LinearLayout linearAddress;
        public TestViewHolder(View itemView) {
            super(itemView);
            address = (TextView) itemView.findViewById(R.id.listAddress_address);
            type = (TextView) itemView.findViewById(R.id.listAddress_type);
            linearAddress = (LinearLayout) itemView.findViewById(R.id.linear_address);
        }
    }
}
