package com.pkdev.e_card;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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
import com.pkdev.e_card.adapter.EmailAdapter;
import com.pkdev.e_card.model.Address;
import com.pkdev.e_card.model.Contact;
import com.pkdev.e_card.model.Email;
import com.pkdev.e_card.model.Phone;
import com.pkdev.e_card.model.Website;
import com.pkdev.e_card.model.Work;
import com.pkdev.e_card.queries.DatabaseQueries;

import java.util.ArrayList;
import java.util.List;

public class MyContacts extends AppCompatActivity {
    RecyclerView mContactList;
    List<Contact> contactList;
    ContactAdapter contactAdapter;
    Toolbar toolbar;
    TextView toolbartxt;
    ImageButton backButton;
    SwipeRefreshLayout myContactRefresh;
    RelativeLayout mainLayout;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Contact> selectionList = new ArrayList<>();
    private int counter = 0;
    ProgressDialog pd;
    public boolean isActionMode = false;
    public int pos = -1;
    public static final int PERMISSION_REQUEST_READ = 1;
    DatabaseQueries databaseQueries = new DatabaseQueries();

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

        mainLayout = (RelativeLayout) findViewById(R.id.mainViewMyContacts);
        myContactRefresh = (SwipeRefreshLayout) findViewById(R.id.myContactRefresh);

        myContactRefresh.setRefreshing(true);
        mainLayout.setVisibility(View.GONE);

        myContactRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainLayout.setVisibility(View.GONE);
                showData();
            }
        });

        showData();

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                final Contact contact = contactList.get(position);
                db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("contacts").document(contact.getUserid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MyContacts.this, "Deleted",Toast.LENGTH_SHORT).show();
                    }
                });
                contactList.remove(position);
                contactAdapter.notifyItemRemoved(position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mContactList);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearActionMode();
            }
        });

        findViewById(R.id.myContactsBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void showData(){
        //Populating RecyclerView With contactList
        mContactList = (RecyclerView) findViewById(R.id.myContact_RecyclerView);
        mContactList.setHasFixedSize(true);
        mContactList.setLayoutManager(new LinearLayoutManager(this));
        contactList = new ArrayList<>();
        db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("contacts").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Contact contact = doc.toObject(Contact.class);
                        contactList.add(contact);
                    }
                    contactAdapter = new ContactAdapter(MyContacts.this, contactList,db);
                    mContactList.setAdapter(contactAdapter);
                    if(contactList.size() < 1){
                        findViewById(R.id.mycontact_noContactMessage).setVisibility(View.VISIBLE);
                        mContactList.setVisibility(View.GONE);
                    }
                    myContactRefresh.setRefreshing(false);
                    mainLayout.setVisibility(View.VISIBLE);
                } else {
                    myContactRefresh.setRefreshing(false);
                    mainLayout.setVisibility(View.VISIBLE);
                }
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
            toolbartxt.setText("0 Contacts Selected");
        }
        if (counter == 1) {
            toolbartxt.setText("1 Contacts Selected");
        } else {
            toolbartxt.setText(counter + " Contacts Selected");
        }
    }

    public void check(View v, int position) {
        if (((CheckBox) v).isChecked()) {
            selectionList.add(contactList.get(position));
            counter++;
            updateToolbarText(counter);
        } else {
            selectionList.remove(contactList.get(position));
            counter--;
            updateToolbarText(counter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.item_delete):
                requestContactPermission();
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

    void addContactName(ArrayList<ContentProviderOperation> list, String name) {
        list.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());
        list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                .build());
    }

    void addContactPhone(ArrayList<ContentProviderOperation> list, Phone phone) {
        list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.getNumber())
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                .build());
    }

    void addContactEmail(ArrayList<ContentProviderOperation> list, Email email) {
        list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, email.getEmail())
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, email.getType().equals("work") ? ContactsContract.CommonDataKinds.Email.TYPE_WORK : email.getType().equals("home") ? ContactsContract.CommonDataKinds.Email.TYPE_HOME : ContactsContract.CommonDataKinds.Email.TYPE_OTHER)
                .build());
    }

    void addContactWork(ArrayList<ContentProviderOperation> list, Work work) {
        if (work.getEnd().equals("Present")) {
            list.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY, work.getCompany())
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Organization.TITLE, work.getPosition())
                    .build());
        }
    }

    void addContactWebsite(ArrayList<ContentProviderOperation> list, Website website) {
        list.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Email.DATA, website.getWebsite())
                .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Website.URL)
                .build());
    }

    void addContactAddress(ArrayList<ContentProviderOperation> list, Address address) {
        list.add(ContentProviderOperation
                .newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, address.getAddress())
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, address.getType().equals("work") ? ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK : address.getType().equals("home") ? ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME : ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER)
                .build());
    }

    private void addContact(String name, List<Email> emailList, List<Website> websiteList, List<Address> addressList, List<Work> workList, List<Phone> phoneList) {
        ArrayList<ContentProviderOperation> op_list = new ArrayList<ContentProviderOperation>();
        //Name
        addContactName(op_list, name);
        //Phone
        for (Phone phone : phoneList) addContactPhone(op_list, phone);
        //Organization
        for (Work work : workList) addContactWork(op_list, work);
        //Email
        for (Email email : emailList) addContactEmail(op_list, email);
        //Website
        for (Website website : websiteList) addContactWebsite(op_list, website);
        //Address
        for (Address address : addressList) addContactAddress(op_list, address);
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

    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.WRITE_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Write Contacts permission");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.WRITE_CONTACTS}
                                    , PERMISSION_REQUEST_READ);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.WRITE_CONTACTS},
                            PERMISSION_REQUEST_READ);
                }
            } else {
                addBatchContact(selectionList);
            }
        } else {
            addBatchContact(selectionList);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    addBatchContact(selectionList);
                } else {
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}