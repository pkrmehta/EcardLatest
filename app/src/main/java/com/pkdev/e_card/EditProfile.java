package com.pkdev.e_card;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pkdev.e_card.model.Address;
import com.pkdev.e_card.model.Email;
import com.pkdev.e_card.model.Phone;
import com.pkdev.e_card.model.Website;
import com.pkdev.e_card.model.Work;
import com.pkdev.e_card.queries.DatabaseQueries;

public class EditProfile extends AppCompatActivity {

    public static ProgressDialog pd;
    EditText name, title;
    LinearLayout logoutButton, addEmail, addWebsite, addWork, addAddress, addPhone;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseQueries databaseQueries = new DatabaseQueries();
    DocumentReference document;

    RecyclerView mPhoneList;
    RecyclerView mAddressList;
    RecyclerView mEmailList;
    RecyclerView mWebsiteList;
    RecyclerView mWorkList;

    String EMAIL = "email", PHONE = "phone", WEBSITE = "website", JOB = "job", ADDRESS = "address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        addEmail = findViewById(R.id.editProfile_emailAddButton);
        addAddress = findViewById(R.id.editProfile_addressAddButton);
        addWork = findViewById(R.id.editProfile_workAddButton);
        addWebsite = findViewById(R.id.editProfile_websiteAddButton);
        addPhone = findViewById(R.id.editProfile_phoneAddButton);

        pd = new ProgressDialog(EditProfile.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setTitle("Loading....");
        pd.setMessage("Please Wait");

        name = findViewById(R.id.editProfile_firstnameEditText);
        title = findViewById(R.id.editProfile_decriptionEditText);

        document = db.collection("users").document(user_id);
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                name.setText(task.getResult().get("name").toString());
                title.setText(task.getResult().get("title").toString());
            }
        });

        pd.show();

        mEmailList = (RecyclerView) findViewById(R.id.editprofile_emailAddRecyclerView);
        mAddressList = (RecyclerView) findViewById(R.id.editprofile_addressAddRecyclerView);
        mWorkList = (RecyclerView) findViewById(R.id.editprofile_workAddRecyclerView);
        mWebsiteList = (RecyclerView) findViewById(R.id.editprofile_websiteAddRecyclerView);
        mPhoneList = (RecyclerView) findViewById(R.id.editProfile_phoneAddRecyclerView);

        addDataToRecyclerVIew(mWebsiteList, WEBSITE);
        addDataToRecyclerVIew(mPhoneList, PHONE);
        addDataToRecyclerVIew(mAddressList, ADDRESS);
        addDataToRecyclerVIew(mEmailList, EMAIL);
        addDataToRecyclerVIew(mWorkList, JOB);
    }

    private void addDataToRecyclerVIew(RecyclerView recyclerView, String field) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(null));

        if (field.equals(EMAIL)) {
            databaseQueries.getEmailList(this, mEmailList, user_id);
            setUpExpandButton((ImageButton) findViewById(R.id.editProfile_expandEmail), mEmailList);
            setUpAddEmail();
        } else if (field.equals(WEBSITE)) {
            databaseQueries.getWebsiteList(this, mWebsiteList, user_id);
            setUpExpandButton((ImageButton) findViewById(R.id.editProfile_expandWebsite), mWebsiteList);
            setUpAddWebsite();
        }
        else if (field.equals(JOB)) {
            databaseQueries.getWorkList(this, mWorkList, user_id, pd);
            setUpExpandButton((ImageButton) findViewById(R.id.editProfile_expandWork), mWorkList);
            setUpAddWork();
        }
        else if (field.equals(ADDRESS)) {
            databaseQueries.getAddressList(this, mAddressList, user_id);
            setUpExpandButton((ImageButton) findViewById(R.id.editProfile_expandAddress), mAddressList);
            setUpAddAddress();
        }
        else if (field.equals(PHONE)) {
            databaseQueries.getPhoneList(this, mPhoneList, user_id);
            setUpExpandButton((ImageButton) findViewById(R.id.editProfile_expandPhone), mPhoneList);
            setUpAddPhone();
        }
    }

    /**
     * description - This function is used to load email data in recycler view and add new email data
     */
    private void setUpAddEmail() {
        addEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfile.this);
                LayoutInflater inflater = LayoutInflater.from(EditProfile.this);
                View popAddressLayout = inflater.inflate(R.layout.popup_email, null);
                final EditText editEmail = popAddressLayout.findViewById(R.id.popupEmail_emailEditText);
                final TextView saveButton = popAddressLayout.findViewById(R.id.popupEmail_saveButton);
                final TextView cancelButton = popAddressLayout.findViewById(R.id.popupEmail_CancelButton);
                dialog.setView(popAddressLayout);
                final AlertDialog alertDialog = dialog.show();

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveButton.setClickable(false);
                        final Email email = new Email();
                        email.setEmail(editEmail.getText().toString());
                        email.setType("work"); //TO BE CODED
                        databaseQueries.addEmail(email, alertDialog);
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });
            }
        });
    }

    private void setUpAddAddress() {
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfile.this);
                LayoutInflater inflater = LayoutInflater.from(EditProfile.this);
                View popAddressLayout = inflater.inflate(R.layout.popup_address_add, null);

                final EditText editAddress = popAddressLayout.findViewById(R.id.popupAddress_addressEditText);
                final TextView saveButton = popAddressLayout.findViewById(R.id.popupAddress_saveButton);
                final TextView cancelButton = popAddressLayout.findViewById(R.id.popupAddress_cancelButton);

                dialog.setView(popAddressLayout);
                final AlertDialog alertDialog = dialog.show();

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveButton.setClickable(false);
                        final Address address = new Address();
                        address.setAddress(editAddress.getText().toString());
                        address.setType("home");
                        databaseQueries.addAddress(address, alertDialog);
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });
            }
        });
    }

    private void setUpAddWork() {
        addWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfile.this);
                LayoutInflater inflater = LayoutInflater.from(EditProfile.this);
                View popAddressLayout = inflater.inflate(R.layout.popup_workexp_add, null);

                final EditText editDesignation = popAddressLayout.findViewById(R.id.popupWork_profileEditText);
                final EditText editCompany = popAddressLayout.findViewById(R.id.popupWork_employerEdiText);
                final EditText editStart = popAddressLayout.findViewById(R.id.popupWork_startDateEditText);
                final EditText editEnd = popAddressLayout.findViewById(R.id.popupWork_endDateEditText);
                final CheckBox isPresent = popAddressLayout.findViewById(R.id.popupWork_presentCheck);
                final TextView saveButton = popAddressLayout.findViewById(R.id.popupWork_saveButton);
                final TextView cancelButton = popAddressLayout.findViewById(R.id.popupWork_cancelButton);

                dialog.setView(popAddressLayout);
                final AlertDialog alertDialog = dialog.show();

                isPresent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (isPresent.isChecked()) {
                            editEnd.setText("Present");
                            editEnd.setEnabled(false);
                        } else {
                            editEnd.setText("");
                            editEnd.setEnabled(true);
                        }
                    }
                });

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveButton.setClickable(false);
                        final Work work = new Work();
                        work.setPosition(editDesignation.getText().toString());
                        work.setCompany(editCompany.getText().toString());
                        work.setStart(editStart.getText().toString());
                        work.setEnd(editEnd.getText().toString());
                        databaseQueries.addWork(work, alertDialog);
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });
            }
        });
    }

    private void setUpAddWebsite() {
        addWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfile.this);
                LayoutInflater inflater = LayoutInflater.from(EditProfile.this);
                View popPhoneLayout = inflater.inflate(R.layout.popup_website_add, null);

                final EditText editWebsite = popPhoneLayout.findViewById(R.id.popupWebsite_websiteEditText);
                final TextView saveButton = popPhoneLayout.findViewById(R.id.popupWebsite_saveButton);
                final TextView cancelButton = popPhoneLayout.findViewById(R.id.popupWebsite_cancelButton);

                dialog.setView(popPhoneLayout);
                final AlertDialog alertDialog = dialog.show();

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveButton.setClickable(false);
                        final Website website = new Website();
                        website.setWebsite(editWebsite.getText().toString());
                        databaseQueries.addWebsite(website, alertDialog);
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });
            }
        });
    }

    private void setUpAddPhone() {
        addPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfile.this);
                LayoutInflater inflater = LayoutInflater.from(EditProfile.this);
                View popPhoneLayout = inflater.inflate(R.layout.popup_phone_add, null);

                final EditText editPhone = popPhoneLayout.findViewById(R.id.popupPhone_phoneEditText);
                final TextView saveButton = popPhoneLayout.findViewById(R.id.popupPhone_saveButton);
                final TextView cancelButton = popPhoneLayout.findViewById(R.id.popupPhone_cancelButton);

                dialog.setView(popPhoneLayout);
                final AlertDialog alertDialog = dialog.show();

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveButton.setClickable(false);
                        final Phone phone = new Phone();
                        phone.setNumber(editPhone.getText().toString());
                        phone.setCode("+91");
                        phone.setType("home");
                        databaseQueries.addPhone(phone, alertDialog);
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.cancel();
                    }
                });
            }
        });
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