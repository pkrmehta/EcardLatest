package com.pkdev.e_card;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.pkdev.e_card.queries.DatabaseQueries;

public class Notification extends AppCompatActivity {

    RecyclerView mNotificationList;
    DatabaseQueries databaseQueries = new DatabaseQueries();
    String user_id;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public static ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        user_id = mAuth.getCurrentUser().getUid();

        pd = new ProgressDialog(Notification.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setTitle("Loading....");
        pd.setMessage("Please Wait");

        pd.show();
        mNotificationList = findViewById(R.id.notification_recyclerView);
        mNotificationList.setHasFixedSize(true);
        mNotificationList.setLayoutManager(new LinearLayoutManager(this));
        databaseQueries.getNotification(Notification.this, mNotificationList, user_id);
    }
}