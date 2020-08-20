package com.pkdev.e_card.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pkdev.e_card.R;
import com.pkdev.e_card.model.NotificationCard;

import java.util.List;

public class NotificationCardAdapter extends RecyclerView.Adapter<NotificationCardAdapter.TestViewHolder>{

    Context mCtx;
    List<NotificationCard> notificationCardList;

    public NotificationCardAdapter(Context mCtx,List<NotificationCard> notificationCardList)
    {
        this.mCtx = mCtx;
        this.notificationCardList = notificationCardList;
    }
    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mCtx).inflate(R.layout.list_notification,
                parent, false);
        TestViewHolder testViewHolder = new TestViewHolder(view);
        return testViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TestViewHolder holder, int position) {
        NotificationCard notification = notificationCardList.get(position);
        holder.desc.setText(notification.getName() + " " +notification.getDesc());
//        holder.time.setText(notification.getTime());
        holder.heading.setText(notification.getHeading());
    }

    @Override
    public int getItemCount() {
        return notificationCardList.size();
    }

    class TestViewHolder extends RecyclerView.ViewHolder
    {
        TextView name,desc,heading,time;
        public TestViewHolder(View itemView) {
            super(itemView);
            desc = (TextView) itemView.findViewById(R.id.listNotification_message);
            heading = (TextView) itemView.findViewById(R.id.listNotification_heading);
            time = (TextView) itemView.findViewById(R.id.notification_timeTextView);
        }
    }
}
