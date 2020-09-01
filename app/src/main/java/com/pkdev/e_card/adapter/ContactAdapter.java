package com.pkdev.e_card.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pkdev.e_card.ContactDetail;
import com.pkdev.e_card.MyContacts;
import com.pkdev.e_card.R;
import com.pkdev.e_card.model.Contact;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.TestViewHolder> {

    Context mCtx;
    List<Contact> contactList;
    MyContacts myContacts;
    FirebaseFirestore db;
    public ContactAdapter(Context mCtx, List<Contact> contactList, FirebaseFirestore db) {
        this.mCtx = mCtx;
        this.contactList = contactList;
        this.myContacts = (MyContacts) mCtx;
        this.db = db;
    }

    @NonNull
    @Override
    public TestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.list_my_contacts,
                parent, false);
        TestViewHolder testViewHolder = new TestViewHolder(view);
        return testViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TestViewHolder holder, final int position) {
        final Contact contact = contactList.get(position);

        db.collection("users").document(contact.getUserid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                holder.contactName.setText(documentSnapshot.get("name").toString());
                holder.contactTitle.setText(documentSnapshot.get("title").toString());
                Picasso.get().load(documentSnapshot.get("image").toString()).placeholder(R.drawable.profile).into(holder.contactImage);
            }
        });

        if (myContacts.pos == position) {
            holder.checkBox.setChecked(true);
            myContacts.pos = -1;
        }

        holder.contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!myContacts.isActionMode) {
                    Intent intent = new Intent(mCtx, ContactDetail.class);
                    intent.putExtra("USER_ID", contact.getUserid());
                    mCtx.startActivity(intent);
                }
            }
        });

        if (myContacts.isActionMode) {
            Anim anim = new Anim(100, holder.layoutCheck);
            anim.setDuration(300);
            holder.layoutCheck.setAnimation(anim);
        } else {
            Anim anim = new Anim(0, holder.layoutCheck);
            anim.setDuration(300);
            holder.layoutCheck.setAnimation(anim);
            holder.checkBox.setChecked(false);
        }

        holder.contact.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                myContacts.startSelection(position);
                return true;
            }
        });
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myContacts.check(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    class TestViewHolder extends RecyclerView.ViewHolder {
        CircleImageView contactImage;
        TextView contactName, contactTitle;
        LinearLayout contact;
        RelativeLayout layoutCheck;
        CheckBox checkBox;

        public TestViewHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox) itemView.findViewById(R.id.list_check);
            layoutCheck = (RelativeLayout) itemView.findViewById(R.id.listContact_checkbox);
            contactImage = (CircleImageView) itemView.findViewById(R.id.myContacts_listImageView);
            contactName = (TextView) itemView.findViewById(R.id.myContacts_listNameTextView);
            contact = (LinearLayout) itemView.findViewById(R.id.myContact_List);
            contactTitle = (TextView) itemView.findViewById(R.id.myContacts_listDescTextView);
        }
    }

    class Anim extends Animation {
        private int width, startWidth;
        private View view;

        public Anim(int width, View v) {
            this.width = width;
            this.view = v;
            this.startWidth = view.getWidth();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newWidth = startWidth + (int) ((width - startWidth) * interpolatedTime);
            view.getLayoutParams().width = newWidth;
            view.requestLayout();

            super.applyTransformation(interpolatedTime, t);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }
}
