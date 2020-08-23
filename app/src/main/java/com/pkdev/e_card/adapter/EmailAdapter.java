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
import com.pkdev.e_card.model.Email;

import java.util.List;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.TestViewHolder>{

    Context mCtx;
    List<Email> emailList;
    EditProfile editProfile;

    public EmailAdapter(Context mCtx,List<Email> emailList)
    {
        this.mCtx = mCtx;
        this.emailList = emailList;
    }
    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.list_email,
                parent, false);
        TestViewHolder testViewHolder = new TestViewHolder(view);
        return testViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, final int position) {
        final Email email = emailList.get(position);
        holder.emailAddress.setText(email.getEmail());
        holder.emailType.setText(email.getType());
        holder.linearEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile = (EditProfile) mCtx;
                if (editProfile.isEditable) {
                    editProfile.showEmalDialog("edit", email, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }

    class TestViewHolder extends RecyclerView.ViewHolder
    {
        TextView emailAddress,emailType;
        LinearLayout linearEmail;
        public TestViewHolder(View itemView) {
            super(itemView);
            emailAddress = (TextView) itemView.findViewById(R.id.listEmail_email);
            emailType = (TextView) itemView.findViewById(R.id.listEmail_type);
            linearEmail = (LinearLayout) itemView.findViewById(R.id.linear_email);
        }
    }
}
