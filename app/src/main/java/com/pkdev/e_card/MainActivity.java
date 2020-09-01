package com.pkdev.e_card;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;

    GoogleSignInClient mGoogleSignInClient;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ImageView imageView;
    TextView name, description;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = mAuth.getCurrentUser();

        pd = new ProgressDialog(MainActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setTitle("Loading....");

        imageView = (ImageView) findViewById(R.id.main_myImageView);
        name = (TextView) findViewById(R.id.main_nameTextView);
        description = (TextView) findViewById(R.id.main_descTextView);

        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);
        navigationView = (NavigationView) findViewById(R.id.main_navigationView);
        View header = navigationView.getHeaderView(0);
        toolbar = findViewById(R.id.main_toolbar);
        bottomNavigationView = findViewById(R.id.main_bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        setSupportActionBar(toolbar);

        pd.show();

        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot ds = task.getResult();
                        Picasso.get().load(ds.get("image").toString()).placeholder(R.drawable.myinfo).into(imageView);
                        name.setText(ds.get("name").toString());
                        description.setText(ds.get("title").toString());
                        pd.dismiss();
                    }
                }
            });
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        header.findViewById(R.id.nav_logOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                createRequest();
                mGoogleSignInClient.signOut();
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });
        header.findViewById(R.id.nav_aboutUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AboutUs.class));
            }
        });
        header.findViewById(R.id.nav_contactUs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ContactUs.class));
            }
        });
        header.findViewById(R.id.nav_privacyPolicy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PrivacyPolicy.class));
            }
        });
        header.findViewById(R.id.nav_tnc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TnC.class));
            }
        });

        findViewById(R.id.main_basicInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EditProfile.class));

            }
        });
        findViewById(R.id.main_addContactButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddContact.class));
            }
        });

        findViewById(R.id.main_notificationButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Notification.class));
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.bnav_mycontacts:
                startActivity(new Intent(MainActivity.this, MyContacts.class));
                break;
            case R.id.bnav_myinfoedit:
                startActivity(new Intent(MainActivity.this, EditProfile.class));
                break;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent startIntent = new Intent(MainActivity.this, Login.class);
            startActivity(startIntent);
            finish();
        }
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
}