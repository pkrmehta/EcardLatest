package com.pkdev.e_card.queries;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.pkdev.e_card.EditProfile;
import com.pkdev.e_card.MyContacts;
import com.pkdev.e_card.Notification;
import com.pkdev.e_card.R;
import com.pkdev.e_card.adapter.AddressAdapter;
import com.pkdev.e_card.adapter.EmailAdapter;
import com.pkdev.e_card.adapter.NotificationCardAdapter;
import com.pkdev.e_card.adapter.PhoneAdapter;
import com.pkdev.e_card.adapter.WebsiteAdapter;
import com.pkdev.e_card.adapter.WorkAdapter;
import com.pkdev.e_card.model.Address;
import com.pkdev.e_card.model.Contact;
import com.pkdev.e_card.model.Email;
import com.pkdev.e_card.model.NotificationCard;
import com.pkdev.e_card.model.Phone;
import com.pkdev.e_card.model.Social;
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
    public List<NotificationCard> notificationCardList = new ArrayList<>();
    public NotificationCardAdapter notificationCardAdapter;

    public void getNotification(final Context context, final RecyclerView mNotificationList, String userId) {
        db.collection("users").document(userId).collection("notifications").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    notificationCardList.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        NotificationCard notification = doc.toObject(NotificationCard.class);
                        notificationCardList.add(notification);
                    }
                    notificationCardAdapter = new NotificationCardAdapter(context, notificationCardList);
                    mNotificationList.setAdapter(notificationCardAdapter);
                    Notification.pd.dismiss();
                } else {
                }
            }
        });
    }

    public void getEmailList(final Context context, final RecyclerView mEmailList, final String userId) {
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

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder holder) {
                if (EditProfile.isEditable) {
                    return super.getSwipeDirs(recyclerView, holder);
                }
                return 0;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                final Email email = emailList.get(position);
                deleteEmail(userId, email.getId(), position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mEmailList);
    }

    public void getWebsiteList(final Context context, final RecyclerView mWebsiteList, final String userId) {
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

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder holder) {
                if (EditProfile.isEditable) {
                    return super.getSwipeDirs(recyclerView, holder);
                }
                return 0;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                final Website website = websiteList.get(position);
                deleteWebsite(userId, website.getId(), position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mWebsiteList);
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

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder holder) {
                if (EditProfile.isEditable) {
                    return super.getSwipeDirs(recyclerView, holder);
                }
                return 0;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                final Phone phone = phoneList.get(position);
                deletePhone(userId, phone.getId(), position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mPhoneList);
    }

    public void getAddressList(final Context context, final RecyclerView mAddressList, final String userId) {
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

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder holder) {
                if (EditProfile.isEditable) {
                    return super.getSwipeDirs(recyclerView, holder);
                }
                return 0;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                final Address address = addressList.get(position);
                deleteAddress(userId, address.getId(), position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mAddressList);
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

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder holder) {
                if (EditProfile.isEditable) {
                    return super.getSwipeDirs(recyclerView, holder);
                }
                return 0;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                final int position = viewHolder.getAdapterPosition();
                final Work work = workList.get(position);
                deleteWork(userId, work.getId(), position);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mWorkList);
    }

    public void addEmail(final Email email, final AlertDialog dialog) {
        if (emailList.size() < 3) {
            EditProfile.pd.show();
            final String doc_id = document.collection("email").document().getId();
            email.setId(doc_id);
            document.collection("email").document(doc_id).set(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    emailList.add(email);
                    emailAdapter.notifyDataSetChanged();
                    dialog.cancel();
                    EditProfile.pd.dismiss();
                }
            });
        } else {

        }
    }

    public void addAddress(final Address address, final AlertDialog dialog) {
        if (addressList.size() < 3) {
            EditProfile.pd.show();
            final String doc_id = document.collection("address").document().getId();
            address.setId(doc_id);
            document.collection("address").document(doc_id).set(address).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    addressList.add(address);
                    addressAdapter.notifyDataSetChanged();
                    dialog.cancel();
                    EditProfile.pd.dismiss();
                }
            });
        } else {

        }
    }

    public void addWork(final Work work, final AlertDialog dialog) {
        if (workList.size() < 2) {
            EditProfile.pd.show();
            final String doc_id = document.collection("job").document().getId();
            work.setId(doc_id);
            document.collection("job").document(doc_id).set(work).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    workList.add(work);
                    workAdapter.notifyDataSetChanged();
                    dialog.cancel();
                    EditProfile.pd.dismiss();
                }
            });
        } else {

        }
    }

    public void addWebsite(final Website website, final AlertDialog dialog) {
        if (websiteList.size() < 2) {
            EditProfile.pd.show();
            final String doc_id = document.collection("website").document().getId();
            website.setId(doc_id);
            document.collection("website").document(doc_id).set(website).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    websiteList.add(website);
                    websiteAdapter.notifyDataSetChanged();
                    dialog.cancel();
                    EditProfile.pd.dismiss();
                }
            });
        } else {

        }
    }

    public void addPhone(final Phone phone, final AlertDialog dialog) {
        if (phoneList.size() < 2) {
            EditProfile.pd.show();
            final String doc_id = document.collection("phone").document().getId();
            phone.setId(doc_id);
            document.collection("phone").document(doc_id).set(phone).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    phoneList.add(phone);
                    phoneAdapter.notifyDataSetChanged();
                    dialog.cancel();
                    EditProfile.pd.dismiss();
                }
            });
        } else {

        }
    }

    public void addSocial(final Social social, final AlertDialog dialog) {
        EditProfile.pd.show();
        document.collection("social").document("social_id").set(social).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.cancel();
                EditProfile.pd.dismiss();
                EditProfile.setUpSocial();
            }
        });
    }

    public void editPhone(final Phone phone, final AlertDialog dialog, final int position) {
        EditProfile.pd.show();
        document.collection("phone").document(phone.getId()).set(phone).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                phoneList.remove(position);
                phoneList.add(phone);
                phoneAdapter.notifyDataSetChanged();
                dialog.cancel();
                EditProfile.pd.dismiss();
            }
        });
    }

    public void editWebsite(final Website website, final AlertDialog dialog, final int position) {
        EditProfile.pd.show();
        document.collection("website").document(website.getId()).set(website).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                websiteList.remove(position);
                websiteList.add(website);
                websiteAdapter.notifyDataSetChanged();
                dialog.cancel();
                EditProfile.pd.dismiss();
            }
        });
    }

    public void editWork(final Work work, final AlertDialog dialog, final int position) {
        EditProfile.pd.show();
        document.collection("job").document(work.getId()).set(work).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                workList.remove(position);
                workList.add(work);
                workAdapter.notifyDataSetChanged();
                dialog.cancel();
                EditProfile.pd.dismiss();
            }
        });
    }

    public void editAddress(final Address address, final AlertDialog dialog, final int position) {
        EditProfile.pd.show();
        document.collection("address").document(address.getId()).set(address).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                addressList.remove(position);
                addressList.add(address);
                addressAdapter.notifyDataSetChanged();
                dialog.cancel();
                EditProfile.pd.dismiss();
            }
        });
    }

    public void editEmail(final Email email, final AlertDialog dialog, final int position) {
        EditProfile.pd.show();
        document.collection("email").document(email.getId()).set(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                emailList.remove(position);
                emailList.add(email);
                emailAdapter.notifyDataSetChanged();
                dialog.cancel();
                EditProfile.pd.dismiss();
            }
        });
    }

    public void deleteEmail(String userId, String id, final int position) {
        db.collection("users").document(userId).collection("email").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                emailList.remove(position);
                emailAdapter.notifyItemRemoved(position);
            }
        });
    }

    public void deletePhone(String userId, String id, final int position) {
        db.collection("users").document(userId).collection("phone").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                phoneList.remove(position);
                phoneAdapter.notifyItemRemoved(position);
            }
        });
    }

    public void deleteAddress(String userId, String id, final int position) {
        db.collection("users").document(userId).collection("address").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                addressList.remove(position);
                addressAdapter.notifyItemRemoved(position);
            }
        });
    }

    public void deleteWebsite(String userId, String id, final int position) {
        db.collection("users").document(userId).collection("website").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                websiteList.remove(position);
                websiteAdapter.notifyItemRemoved(position);
            }
        });
    }

    public void deleteWork(String userId, String id, final int position) {
        db.collection("users").document(userId).collection("job").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                workList.remove(position);
                workAdapter.notifyItemRemoved(position);
            }
        });
    }
}
