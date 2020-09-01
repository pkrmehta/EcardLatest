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
import com.pkdev.e_card.model.Website;

import java.util.List;

public class WebsiteAdapter extends RecyclerView.Adapter<WebsiteAdapter.TestViewHolder> {

    Context mCtx;
    List<Website> websiteList;
    EditProfile editProfile;

    public WebsiteAdapter(Context mCtx, List<Website> websiteList) {
        this.mCtx = mCtx;
        this.websiteList = websiteList;
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.list_website,
                parent, false);
        TestViewHolder testViewHolder = new TestViewHolder(view);
        return testViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, final int position) {
        final Website website = websiteList.get(position);
        holder.websiteAddress.setText(website.getWebsite());
        holder.linearWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    editProfile = (EditProfile) mCtx;
                    if (editProfile.isEditable) {
                        editProfile.showWebsiteDialog("edit", website, position);
                    }
                }
                catch (Exception e){

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return websiteList.size();
    }

    class TestViewHolder extends RecyclerView.ViewHolder {
        TextView websiteAddress;
        LinearLayout linearWebsite;

        public TestViewHolder(View itemView) {
            super(itemView);
            linearWebsite = (LinearLayout) itemView.findViewById(R.id.linear_website);
            websiteAddress = (TextView) itemView.findViewById(R.id.listAddress_address);
        }
    }
}
