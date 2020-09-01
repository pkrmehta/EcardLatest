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
import com.pkdev.e_card.model.Contact;
import com.pkdev.e_card.model.Phone;
import com.pkdev.e_card.model.Social;
import com.pkdev.e_card.model.Website;
import com.pkdev.e_card.queries.DatabaseQueries;
import com.pkdev.e_card.util.Common;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactDetail extends AppCompatActivity {

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

    String EMAIL = "email", PHONE = "phone", WEBSITE = "website", JOB = "job", ADDRESS = "address";

    ImageButton buttonInstagram, buttonFacebook, buttonLinkedin, buttonTwitter, buttonSnapchat, buttonWhatsapp;

    String instagramLink = "", facebookLink = "", linkedinLink = "", twitterLink = "", snapchatLink = "",whatsappNo = "";

    CircleImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        user_id = getIntent().getStringExtra("USER_ID").toString();

        mEmailList = (RecyclerView) findViewById(R.id.contactDetail_emailListRecyclerView);
        mAddressList = (RecyclerView) findViewById(R.id.contactDetail_addressListRecyclerView);
        mWorkList = (RecyclerView) findViewById(R.id.contactDetail_workexpListRecyclerView);
        mWebsiteList = (RecyclerView) findViewById(R.id.contactDetail_websiteListRecyclerView);
        mPhoneList = (RecyclerView) findViewById(R.id.contactDetail_phoneListRecyclerView);

        buttonFacebook = findViewById(R.id.contactDetail_facebook);
        buttonInstagram = findViewById(R.id.contactDetail_instagram);
        buttonTwitter = findViewById(R.id.contactDetail_twitter);
        buttonLinkedin = findViewById(R.id.contactDetail_linkedin);
        buttonSnapchat = findViewById(R.id.contactDetail_snapchat);
        buttonWhatsapp = findViewById(R.id.contactDetail_whatsapp);

        pd = new ProgressDialog(ContactDetail.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setTitle("Loading....");
        pd.setMessage("Please Wait");

        name = findViewById(R.id.contactDetail_name);
        title = findViewById(R.id.contactDetail_desc);
        image = findViewById(R.id.contactDetail_imageProfile);

        db.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                name.setText(task.getResult().get("name").toString());
                title.setText(task.getResult().get("title").toString());
                String imageUrl = task.getResult().get("image").toString();
                Picasso.get().load(imageUrl).placeholder(R.drawable.myinfo).into(image);
            }
        });

        addDataToRecyclerVIew(mWebsiteList, WEBSITE);
        addDataToRecyclerVIew(mPhoneList, PHONE);
        addDataToRecyclerVIew(mAddressList, ADDRESS);
        addDataToRecyclerVIew(mEmailList, EMAIL);
        addDataToRecyclerVIew(mWorkList, JOB);

        buttonInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.addInstagram(instagramLink, ContactDetail.this);
            }
        });

        buttonFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.addFacebook(facebookLink,ContactDetail.this);
            }
        });

        buttonTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.addTwitter(twitterLink,ContactDetail.this);
            }
        });

        buttonLinkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.addLinkedin(linkedinLink,ContactDetail.this);
            }
        });

        buttonWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.addWhatsapp(whatsappNo,ContactDetail.this);
            }
        });

        setUpSocial();
    }

    public void setUpSocial() {
        db.collection("users").document(user_id).collection("social").document("social_id").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Social social = task.getResult().toObject(Social.class);
                    if (social != null) {
                        if (social.getInstagram().length() > 0) {
                            buttonInstagram.setVisibility(View.VISIBLE);
                            instagramLink = social.getInstagram();
                        } else {
                            buttonInstagram.setVisibility(View.GONE);
                        }
                        if (social.getFacebook().length() > 0) {
                            buttonFacebook.setVisibility(View.VISIBLE);
                            facebookLink = social.getFacebook();
                        } else {
                            buttonFacebook.setVisibility(View.GONE);
                        }
                        if (social.getLinkedin().length() > 0) {
                            buttonLinkedin.setVisibility(View.VISIBLE);
                            linkedinLink = social.getLinkedin();
                        } else {
                            buttonLinkedin.setVisibility(View.GONE);
                        }
                        if (social.getTwitter().length() > 0) {
                            buttonTwitter.setVisibility(View.VISIBLE);
                            twitterLink = social.getTwitter();
                        } else {
                            buttonTwitter.setVisibility(View.GONE);
                        }
                        if (social.getSnapchat().length() > 0) {
                            buttonSnapchat.setVisibility(View.VISIBLE);
                            snapchatLink = social.getSnapchat();
                        } else {
                            buttonSnapchat.setVisibility(View.GONE);
                        }
                    }
                    else {
                        findViewById(R.id.linearSocial).setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void addDataToRecyclerVIew(RecyclerView recyclerView, String field) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(null));
        if (field.equals(EMAIL)) {
            databaseQueries.getEmailList(this, mEmailList, user_id);
            setUpExpandButton((ImageButton) findViewById(R.id.expandEmail), mEmailList);
        } else if (field.equals(WEBSITE)) {
            databaseQueries.getWebsiteList(this, mWebsiteList, user_id);
            setUpExpandButton((ImageButton) findViewById(R.id.expandWebsite), mWebsiteList);
        } else if (field.equals(JOB)) {
            databaseQueries.getWorkList(this, mWorkList, user_id);
            setUpExpandButton((ImageButton) findViewById(R.id.expandWork), mWorkList);
        } else if (field.equals(ADDRESS)) {
            databaseQueries.getAddressList(this, mAddressList, user_id);
            setUpExpandButton((ImageButton) findViewById(R.id.expandAddress), mAddressList);
        } else if (field.equals(PHONE)) {
            databaseQueries.getPhoneList(this, mPhoneList, user_id);
            setUpExpandButton((ImageButton) findViewById(R.id.expandPhone), mPhoneList);
        }
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