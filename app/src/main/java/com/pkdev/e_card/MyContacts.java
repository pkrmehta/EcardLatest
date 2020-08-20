package com.pkdev.e_card;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pkdev.e_card.adapter.ContactAdapter;
import com.pkdev.e_card.model.Address;
import com.pkdev.e_card.model.Contact;
import com.pkdev.e_card.model.Email;
import com.pkdev.e_card.model.Phone;
import com.pkdev.e_card.model.Website;
import com.pkdev.e_card.model.Work;

import java.util.ArrayList;
import java.util.List;

public class MyContacts extends AppCompatActivity {


    RecyclerView mContactList;
    List<Contact> contactList;
    ContactAdapter contactAdapter;
    Toolbar toolbar;
    TextView toolbartxt;
    ImageButton backButton;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Contact> selectionList = new ArrayList<>();
    private int counter = 0;
    ProgressDialog pd;
    public boolean isActionMode = false;
    public int pos = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contacts);

        pd = new ProgressDialog(MyContacts.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setTitle("Loading....");
        pd.setMessage("Please Wait");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);

        toolbartxt = findViewById(R.id.mycontact_head);
        backButton = findViewById(R.id.mycontact_back);

        //Populating RecyclerView With contactList
        mContactList = (RecyclerView) findViewById(R.id.myContact_RecyclerView);
        mContactList.setHasFixedSize(true);
        mContactList.setLayoutManager(new LinearLayoutManager(this));
        contactList = new ArrayList<>();
        pd.show();
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("contacts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Contact contact = doc.toObject(Contact.class);
                        contactList.add(contact);
                    }
                    contactAdapter = new ContactAdapter(MyContacts.this, contactList);
                    mContactList.setAdapter(contactAdapter);
                    //If ProgressDialog is showing Dismiss it
                    if (pd.isShowing()) {
                        pd.dismiss();
                    }
                } else {
                    pd.dismiss();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearActionMode();
            }
        });
    }

    private void clearActionMode() {
        isActionMode = false;
        toolbartxt.setText("0 item selected");
        findViewById(R.id.main_toolbar).setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.GONE);
        counter = 0;
        selectionList.clear();
        toolbar.getMenu().clear();
        contactAdapter.notifyDataSetChanged();
    }

    public void startSelection(int position) {
        if (!isActionMode) {
            isActionMode = true;
            selectionList.add(contactList.get(position));
            counter++;
            updateToolbarText(counter);
            pos = position;
            toolbar.setVisibility(View.VISIBLE);
            toolbar.inflateMenu(R.menu.menu_action_mode);
            findViewById(R.id.main_toolbar).setVisibility(View.GONE);
            contactAdapter.notifyDataSetChanged();
        }
    }

    private void updateToolbarText(int counter) {
        if (counter == 0) {
            toolbartxt.setText("0 item selected");
        }
        if (counter == 1) {
            toolbartxt.setText("1 item selected");
        } else {
            toolbartxt.setText(counter + "  item selected");
        }
    }

    public void check(View v, int position) {
        if (((CheckBox) v).isChecked()) {
            selectionList.add(contactList.get(position));
            counter++;
            updateToolbarText(counter);
            Toast.makeText(this, String.valueOf(counter), Toast.LENGTH_LONG).show();
        } else {
            selectionList.remove(contactList.get(position));
            counter--;
            updateToolbarText(counter);
            Toast.makeText(this, String.valueOf(counter), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.item_delete):
                addBatchContact(selectionList);
                break;
        }
        return true;
    }

    private void addBatchContact(List<Contact> selectionList) {
        for (Contact contact : selectionList) {
            final String name = contact.getName();
            final List<Work> workList = new ArrayList<>();
            final List<Email> emailList = new ArrayList<>();
            final List<Website> websiteList = new ArrayList<>();
            final List<Address> addressList = new ArrayList<>();
            final List<Phone> phoneList = new ArrayList<>();

            final DocumentReference documentReference = db.collection("users").document(contact.getUserid());

            pd.show();
            documentReference.collection("email").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    emailList.addAll(queryDocumentSnapshots.toObjects(Email.class));
                    documentReference.collection("website").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            websiteList.addAll(queryDocumentSnapshots.toObjects(Website.class));
                            documentReference.collection("address").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    addressList.addAll(queryDocumentSnapshots.toObjects(Address.class));
                                    documentReference.collection("job").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            workList.addAll(queryDocumentSnapshots.toObjects(Work.class));
                                            documentReference.collection("phone").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                @Override
                                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                    phoneList.addAll(queryDocumentSnapshots.toObjects(Phone.class));
                                                    addContact(name, emailList, websiteList, addressList, workList, phoneList);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
    }


    private void addContact(String name, List<Email> emailList, List<Website> websiteList, List<Address> addressList, List<Work> workList, List<Phone> phoneList) {
        ArrayList<ContentProviderOperation> op_list = new ArrayList<ContentProviderOperation>();
        op_list.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());

        // First and Last Name
        op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());

        //Phone
        for (Phone phone : phoneList) {
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.getNumber())
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                    .build());
        }
        //Organization
        for (Work work : workList) {
            if (work.getEnd().equals("Present")) {
                op_list.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, work.getCompany())
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, work.getPosition())
                        .build());
            }
        }
        //Email
        for (Email email : emailList) {
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, email.getEmail())
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, email.getType().equals("work") ? ContactsContract.CommonDataKinds.Email.TYPE_WORK : email.getType().equals("home") ? ContactsContract.CommonDataKinds.Email.TYPE_HOME : ContactsContract.CommonDataKinds.Email.TYPE_OTHER)
                    .build());
        }
        //Website
        for (Website website : websiteList) {
            op_list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Email.DATA, website.getWebsite())
                    .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Website.URL)
                    .build());
        }
        //Address
        for (Address address : addressList) {
            op_list.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address.getAddress())
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, address.getType().equals("work") ? ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK : address.getType().equals("home") ? ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME : ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER)
                    .build());
        }
        try {
            ContentProviderResult[] results = getContentResolver().applyBatch(ContactsContract.AUTHORITY, op_list);
            Toast.makeText(this, "Contacts Successfully Saved, Rate us 5*", Toast.LENGTH_LONG).show();
            pd.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Some Error Received", Toast.LENGTH_LONG).show();
            pd.dismiss();
        }
    }

}