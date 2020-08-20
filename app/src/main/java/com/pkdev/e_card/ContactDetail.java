package com.pkdev.e_card;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pkdev.e_card.adapter.AddressAdapter;
import com.pkdev.e_card.adapter.PhoneAdapter;
import com.pkdev.e_card.adapter.WebsiteAdapter;
import com.pkdev.e_card.model.Address;
import com.pkdev.e_card.model.Phone;
import com.pkdev.e_card.model.Website;
import com.pkdev.e_card.queries.DatabaseQueries;

import java.util.ArrayList;
import java.util.List;

public class ContactDetail extends AppCompatActivity {


    GoogleSignInClient mGoogleSignInClient;

    RecyclerView mPhoneList;

    RecyclerView mAddressList;

    RecyclerView mEmailList;

    RecyclerView mWebsiteList;

    RecyclerView mWorkList;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ProgressDialog pd;

    TextView name, title;
    String user_id;

    DatabaseQueries databaseQueries = new DatabaseQueries();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        user_id = getIntent().getStringExtra("USER_ID").toString();

        pd = new ProgressDialog(ContactDetail.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setTitle("Loading....");
        pd.setMessage("Please Wait");

        name = findViewById(R.id.contactDetail_contactNameTextView);
        title = findViewById(R.id.contactDetail_contactDespTextView);


        db.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                name.setText(task.getResult().get("name").toString());
                title.setText(task.getResult().get("title").toString());
            }
        });

        populateAddress();
        populatePhone();
        populateWebsite();
        populateEmail();
        populateWork();
    }

    private void populateWork() {
        mWorkList = (RecyclerView) findViewById(R.id.contactDetail_workexpListRecyclerView);
        mWorkList.setHasFixedSize(true);
        mWorkList.setLayoutManager(new LinearLayoutManager(this));
        databaseQueries.getWorkList(this, mWorkList, user_id);
        setUpExpandButton((ImageButton) findViewById(R.id.expandWork), mWorkList);
    }

    private void populateEmail() {
        mEmailList = (RecyclerView) findViewById(R.id.contactDetail_emailListRecyclerView);
        mEmailList.setHasFixedSize(true);
        mEmailList.setLayoutManager(new LinearLayoutManager(this));
        databaseQueries.getEmailList(this, mEmailList, user_id);
        setUpExpandButton((ImageButton) findViewById(R.id.expandEmail), mEmailList);
    }

    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void populateWebsite() {
        mWebsiteList = (RecyclerView) findViewById(R.id.contactDetail_websiteListRecyclerView);
        mWebsiteList.setHasFixedSize(true);
        mWebsiteList.setLayoutManager(new LinearLayoutManager(this));
        databaseQueries.getWebsiteList(this, mWebsiteList, user_id);
        setUpExpandButton((ImageButton) findViewById(R.id.expandWebsite), mWebsiteList);
    }

    private void populateAddress() {
        mAddressList = (RecyclerView) findViewById(R.id.contactDetail_addressListRecyclerView);
        mAddressList.setHasFixedSize(true);
        mAddressList.setLayoutManager(new LinearLayoutManager(this));
        databaseQueries.getAddressList(this, mAddressList, user_id);
        setUpExpandButton((ImageButton) findViewById(R.id.expandAddress), mAddressList);
    }

    private void populatePhone() {
        mPhoneList = (RecyclerView) findViewById(R.id.contactDetail_phoneListRecyclerView);
        mPhoneList.setHasFixedSize(true);
        mPhoneList.setLayoutManager(new LinearLayoutManager(this));
        databaseQueries.getPhoneList(this, mPhoneList, user_id);
        setUpExpandButton((ImageButton) findViewById(R.id.expandPhone), mPhoneList);
    }

    private void setUpExpandButton(ImageButton expandButton, final RecyclerView recyclerView) {
        expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recyclerView.getVisibility() == View.GONE) {
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                }
            }
        });
    }
}