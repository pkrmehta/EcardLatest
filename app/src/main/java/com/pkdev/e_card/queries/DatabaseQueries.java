package com.pkdev.e_card.queries;

import android.content.Context;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pkdev.e_card.adapter.AddressAdapter;
import com.pkdev.e_card.adapter.EmailAdapter;
import com.pkdev.e_card.adapter.PhoneAdapter;
import com.pkdev.e_card.adapter.WebsiteAdapter;
import com.pkdev.e_card.adapter.WorkAdapter;
import com.pkdev.e_card.model.Address;
import com.pkdev.e_card.model.Email;
import com.pkdev.e_card.model.Phone;
import com.pkdev.e_card.model.Website;
import com.pkdev.e_card.model.Work;

import java.util.ArrayList;
import java.util.List;

public class DatabaseQueries {
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static DocumentReference document = db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

    public List<Email> emailList = new ArrayList<>();
    public EmailAdapter emailAdapter;
    public List<Phone> phoneList = new ArrayList<>();
    public PhoneAdapter phoneAdapter;
    public List<Address> addressList = new ArrayList<>();
    public AddressAdapter addressAdapter;
    public List<Work> workList = new ArrayList<>();
    public WorkAdapter workAdapter;
    public List<Website> websiteList = new ArrayList<>();
    public WebsiteAdapter websiteAdapter;

    public void getEmailList(final Context context, final RecyclerView mEmailList, String userId) {
        db.collection("users").document(userId).collection("email").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    emailList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Email email = doc.toObject(Email.class);
                        emailList.add(email);
                    }
                    emailAdapter = new EmailAdapter(context, emailList);
                    mEmailList.setAdapter(emailAdapter);
                } else {
                }
            }
        });
    }

    public void getWebsiteList(final Context context, final RecyclerView mWebsiteList, String userId) {
        db.collection("users").document(userId).collection("website").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    websiteList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Website website = doc.toObject(Website.class);
                        websiteList.add(website);
                    }
                    websiteAdapter = new WebsiteAdapter(context, websiteList);
                    mWebsiteList.setAdapter(websiteAdapter);
                } else {
                }
            }
        });
    }

    public void getPhoneList(final Context context, final RecyclerView mPhoneList, final String userId) {
        db.collection("users").document(userId).collection("phone").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    phoneList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Phone phone = doc.toObject(Phone.class);
                        phoneList.add(phone);
                    }
                    phoneAdapter = new PhoneAdapter(context, phoneList);
                    mPhoneList.setAdapter(phoneAdapter);
                } else {
                }
            }
        });
    }

    public void getAddressList(final Context context, final RecyclerView mAddressList, String userId) {
        db.collection("users").document(userId).collection("address").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    addressList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Address address = doc.toObject(Address.class);
                        addressList.add(address);
                    }
                    addressAdapter = new AddressAdapter(context, addressList);
                    mAddressList.setAdapter(addressAdapter);

                } else {
                    Toast.makeText(context, "ERROR", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getWorkList(final Context context, final RecyclerView mWorkList, final String userId) {
        db.collection("users").document(userId).collection("job").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    workList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Work work = doc.toObject(Work.class);
                        workList.add(work);
                    }
                    workAdapter = new WorkAdapter(context, workList);
                    mWorkList.setAdapter(workAdapter);
                } else {

                }
            }
        });
    }

    public void addEmail(final Email email) {
        if (emailList.size() < 3) {
            document.collection("email").add(email).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    emailList.add(email);
                    emailAdapter.notifyDataSetChanged();
                }
            });
        } else {

        }
    }

    public void addAddress(final Address address) {
        if (addressList.size() < 3) {
            document.collection("address").add(address).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    addressList.add(address);
                    addressAdapter.notifyDataSetChanged();
                }
            });
        } else {

        }
    }

    public void addWork(final Work work) {
        if (workList.size() < 2) {
            document.collection("job").add(work).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    workList.add(work);
                    workAdapter.notifyDataSetChanged();
                }
            });
        } else {

        }
    }
}
