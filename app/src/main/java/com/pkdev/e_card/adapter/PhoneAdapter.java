package com.pkdev.e_card.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pkdev.e_card.EditProfile;
import com.pkdev.e_card.R;
import com.pkdev.e_card.model.Phone;

import java.util.List;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.TestViewHolder> {

    Context mCtx;
    List<Phone> phoneList;
    EditProfile editProfile;

    public PhoneAdapter(Context mCtx, List<Phone> phoneList) {
        this.mCtx = mCtx;
        this.phoneList = phoneList;
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.list_phone,
                parent, false);
        TestViewHolder testViewHolder = new TestViewHolder(view);
        return testViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, final int position) {
        final Phone phone = phoneList.get(position);
        holder.number.setText(phone.getNumber());
        holder.linearPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    editProfile = (EditProfile) mCtx;
                    if(editProfile.isEditable)
                        editProfile.showPhoneDialog("edit",phone,position);
                }
                catch (Exception e){

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return phoneList.size();
    }

    class TestViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearPhone;
        TextView countrycode, number;

        public TestViewHolder(View itemView) {
            super(itemView);
            linearPhone = (LinearLayout) itemView.findViewById(R.id.linear_phone);
            countrycode = (TextView) itemView.findViewById(R.id.listPhone_countrycode);
            number = (TextView) itemView.findViewById(R.id.listPhone_phone);
        }
    }
}
