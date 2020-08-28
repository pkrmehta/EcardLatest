package com.pkdev.e_card;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.pkdev.e_card.model.Address;
import com.pkdev.e_card.model.Email;
import com.pkdev.e_card.model.Phone;
import com.pkdev.e_card.model.Social;
import com.pkdev.e_card.model.Website;
import com.pkdev.e_card.model.Work;
import com.pkdev.e_card.queries.DatabaseQueries;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class EditProfile extends AppCompatActivity {

    public static ProgressDialog pd;
    EditText name, title;
    LinearLayout logoutButton, addEmail, addWebsite, addWork, addAddress, addPhone;
    ImageButton socialEdit;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
    DatabaseQueries databaseQueries = new DatabaseQueries();
    private StorageReference mImageStorage;

    static DocumentReference document;

    private Menu globalMenu;

    RecyclerView mPhoneList;
    RecyclerView mAddressList;
    RecyclerView mEmailList;
    RecyclerView mWebsiteList;
    RecyclerView mWorkList;

    String EMAIL = "email", PHONE = "phone", WEBSITE = "website", JOB = "job", ADDRESS = "address";

    String ADD = "add", EDIT = "edit";
    Toolbar toolbar;
    public boolean isEditable = false;
    private static final int GALLERY_PICK = 1;

    static ImageButton buttonInstagram, buttonFacebook, buttonLinkedin, buttonTwitter, buttonSnapchat;
    CircleImageView imageView;

    static String instagramLink = "",facebookLink = "",linkedinLink = "",twitterLink = "",snapchatLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setUpToolbar();

        addEmail = findViewById(R.id.editProfile_emailAddButton);
        addAddress = findViewById(R.id.editProfile_addressAddButton);
        addWork = findViewById(R.id.editProfile_workAddButton);
        addWebsite = findViewById(R.id.editProfile_websiteAddButton);
        addPhone = findViewById(R.id.editProfile_phoneAddButton);

        buttonFacebook = findViewById(R.id.editProfile_facebook);
        buttonInstagram = findViewById(R.id.editProfile_instagram);
        buttonTwitter = findViewById(R.id.editProfile_twitter);
        buttonLinkedin = findViewById(R.id.editProfile_linkedin);
        buttonSnapchat = findViewById(R.id.editProfile_snapchat);

        mImageStorage = FirebaseStorage.getInstance().getReference();

        socialEdit = findViewById(R.id.editProfile_socialEdit);

        pd = new ProgressDialog(EditProfile.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setTitle("Loading....");
        pd.setMessage("Please Wait");

        imageView = findViewById(R.id.editProfile_imageProfile);
        name = findViewById(R.id.editProfile_firstnameEditText);
        title = findViewById(R.id.editProfile_decriptionEditText);

        document = db.collection("users").document(user_id);
        document.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                name.setText(task.getResult().get("name").toString());
                title.setText(task.getResult().get("title").toString());
                final String image = task.getResult().get("image").toString();

                if (!image.equals("default")) {
                    Picasso.get().load(image).placeholder(R.drawable.myinfo).into(imageView);
                }
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

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            }
        });

        socialEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Social social = new Social();
                showSocialDialog(EDIT, social);
            }
        });

        buttonInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        setUpSocial();
    }

    public static void setUpSocial(){
        document.collection("social").document("social_id").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Social social = task.getResult().toObject(Social.class);
                    if (social.getInstagram().length() > 0) {
                        buttonInstagram.setVisibility(View.VISIBLE);
                        instagramLink = social.getInstagram();
                    }
                    else {
                        buttonInstagram.setVisibility(View.GONE);
                    }

                    if (social.getFacebook().length() > 0) {
                        buttonFacebook.setVisibility(View.VISIBLE);
                        facebookLink = social.getFacebook();
                    }
                    else {
                        buttonFacebook.setVisibility(View.GONE);
                    }

                    if (social.getLinkedin().length() > 0) {
                        buttonLinkedin.setVisibility(View.VISIBLE);
                        linkedinLink = social.getLinkedin();
                    }
                    else {
                        buttonLinkedin.setVisibility(View.GONE);
                    }

                    if (social.getTwitter().length() > 0) {
                        buttonTwitter.setVisibility(View.VISIBLE);
                        twitterLink = social.getTwitter();
                    }
                    else {
                        buttonTwitter.setVisibility(View.GONE);
                    }

                    if (social.getSnapchat().length() > 0) {
                        buttonSnapchat.setVisibility(View.VISIBLE);
                        snapchatLink = social.getSnapchat();
                    }
                    else {
                        buttonSnapchat.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private boolean isIntentAvailable(Context ctx, Intent intent) {
        final PackageManager packageManager = ctx.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
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
        } else if (field.equals(JOB)) {
            databaseQueries.getWorkList(this, mWorkList, user_id, pd);
            setUpExpandButton((ImageButton) findViewById(R.id.editProfile_expandWork), mWorkList);
            setUpAddWork();
        } else if (field.equals(ADDRESS)) {
            databaseQueries.getAddressList(this, mAddressList, user_id);
            setUpExpandButton((ImageButton) findViewById(R.id.editProfile_expandAddress), mAddressList);
            setUpAddAddress();
        } else if (field.equals(PHONE)) {
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
                Email email = new Email();
                showEmalDialog(ADD, email, 0);
            }
        });
    }

    private void setUpAddAddress() {
        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Address address = new Address();
                showAddressDialog(ADD, address, 0);
            }
        });
    }

    private void setUpAddWork() {
        addWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Work work = new Work();
                showWorkDialog(ADD, work, 0);
            }
        });
    }

    private void setUpAddWebsite() {
        addWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Website website = new Website();
                showWebsiteDialog(ADD, website, 0);
            }
        });
    }

    public void setUpAddPhone() {
        addPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Phone phone = new Phone();
                showPhoneDialog(ADD, phone, 0);
            }
        });
    }

    public void showEmalDialog(final String mode, final Email email, final int position) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfile.this);
        LayoutInflater inflater = LayoutInflater.from(EditProfile.this);
        View popAddressLayout = inflater.inflate(R.layout.popup_email, null);
        final EditText editEmail = popAddressLayout.findViewById(R.id.popupEmail_emailEditText);
        final TextView saveButton = popAddressLayout.findViewById(R.id.popupEmail_saveButton);
        final TextView cancelButton = popAddressLayout.findViewById(R.id.popupEmail_CancelButton);

        final MaterialSpinner spinner = (MaterialSpinner) popAddressLayout.findViewById(R.id.popupEmail_emailTypeSpinner);
        spinner.setItems("primary", "work");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

            }
        });
        dialog.setView(popAddressLayout);
        final AlertDialog alertDialog = dialog.show();

        editEmail.setText(email.getEmail());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButton.setClickable(false);
                email.setEmail(editEmail.getText().toString());
                email.setType(spinner.getItems().get(spinner.getSelectedIndex()).toString());
                if (email.getEmail().length() > 5) {
                    if (mode.equals(ADD))
                        databaseQueries.addEmail(email, alertDialog);
                    else if (mode.equals(EDIT)) {
                        databaseQueries.editEmail(email, alertDialog, position);
                    }
                } else {
                    saveButton.setClickable(true);
                    if (email.getEmail().length() == 0) {
                        editEmail.setError("Please enter your address");
                    } else if (email.getEmail().length() > 0 && email.getEmail().length() < 5) {
                        editEmail.setError("Please enter a valid email");
                    }
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    public void showAddressDialog(final String mode, final Address address, final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfile.this);
        LayoutInflater inflater = LayoutInflater.from(EditProfile.this);
        View popAddressLayout = inflater.inflate(R.layout.popup_address_add, null);

        final EditText editAddress = popAddressLayout.findViewById(R.id.popupAddress_addressEditText);
        final TextView saveButton = popAddressLayout.findViewById(R.id.popupAddress_saveButton);
        final TextView cancelButton = popAddressLayout.findViewById(R.id.popupAddress_cancelButton);

        dialog.setView(popAddressLayout);
        final AlertDialog alertDialog = dialog.show();

        editAddress.setText(address.getAddress());

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButton.setClickable(false);
                address.setAddress(editAddress.getText().toString());
                address.setType("home");

                if (address.getAddress().length() > 5) {
                    if (mode.equals(ADD))
                        databaseQueries.addAddress(address, alertDialog);
                    else if (mode.equals(EDIT)) {
                        databaseQueries.editAddress(address, alertDialog, position);
                    }
                } else {
                    saveButton.setClickable(true);
                    if (address.getAddress().length() == 0) {
                        editAddress.setError("Please enter your address");
                    } else if (address.getAddress().length() > 0 && address.getAddress().length() < 5) {
                        editAddress.setError("Please enter a valid address");
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    public void showWorkDialog(final String mode, final Work work, final int position) {
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

        editCompany.setText(work.getCompany());
        editDesignation.setText(work.getPosition());
        editStart.setText(work.getStart());
        editEnd.setText(work.getEnd());
        if (editEnd.getText().equals("Present"))
            isPresent.setChecked(true);

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
                work.setPosition(editDesignation.getText().toString());
                work.setCompany(editCompany.getText().toString());
                work.setStart(editStart.getText().toString());
                work.setEnd(editEnd.getText().toString());

                if (work.getCompany().length() > 0 && work.getPosition().length() > 0) {
                    if (mode.equals(ADD))
                        databaseQueries.addWork(work, alertDialog);
                    else if (mode.equals(EDIT)) {
                        databaseQueries.editWork(work, alertDialog, position);
                    }
                } else {
                    saveButton.setClickable(true);
                    if (work.getCompany().length() == 0) {
                        editCompany.setError("Please enter your organization name");
                    } else if (work.getPosition().length() == 0) {
                        editDesignation.setError("Please enter your position");
                    }
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    public void showWebsiteDialog(final String mode, final Website website, final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfile.this);
        LayoutInflater inflater = LayoutInflater.from(EditProfile.this);
        View popPhoneLayout = inflater.inflate(R.layout.popup_website_add, null);

        final EditText editWebsite = popPhoneLayout.findViewById(R.id.popupWebsite_websiteEditText);
        final TextView saveButton = popPhoneLayout.findViewById(R.id.popupWebsite_saveButton);
        final TextView cancelButton = popPhoneLayout.findViewById(R.id.popupWebsite_cancelButton);

        editWebsite.setText(website.getWebsite());

        dialog.setView(popPhoneLayout);

        final AlertDialog alertDialog = dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButton.setClickable(false);
                website.setWebsite(editWebsite.getText().toString());

                if (website.getWebsite().length() > 0) {
                    if (mode.equals(ADD))
                        databaseQueries.addWebsite(website, alertDialog);
                    else if (mode.equals(EDIT)) {
                        databaseQueries.editWebsite(website, alertDialog, position);
                    }
                } else {
                    saveButton.setClickable(true);
                    if (website.getWebsite().length() == 0) {
                        editWebsite.setError("Please enter your website address");
                    }
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    public void showPhoneDialog(final String mode, final Phone phone, final int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfile.this);
        LayoutInflater inflater = LayoutInflater.from(EditProfile.this);
        View popPhoneLayout = inflater.inflate(R.layout.popup_phone_add, null);

        final EditText editPhone = popPhoneLayout.findViewById(R.id.popupPhone_phoneEditText);
        final TextView saveButton = popPhoneLayout.findViewById(R.id.popupPhone_saveButton);
        final TextView cancelButton = popPhoneLayout.findViewById(R.id.popupPhone_cancelButton);

        final MaterialSpinner spinner = (MaterialSpinner) popPhoneLayout.findViewById(R.id.popupPhone_phoneSpinner);
        spinner.setItems("+91", "+1", "+2");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

            }
        });

        editPhone.setText(phone.getNumber());

        dialog.setView(popPhoneLayout);
        final AlertDialog alertDialog = dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButton.setClickable(false);
                phone.setNumber(editPhone.getText().toString());
                phone.setCode("+91");
                phone.setType("home");

                if (phone.getNumber().length() == 10) {
                    if (mode.equals(ADD))
                        databaseQueries.addPhone(phone, alertDialog);
                    else if (mode.equals(EDIT)) {
                        databaseQueries.editPhone(phone, alertDialog, position);
                    }
                } else {
                    saveButton.setClickable(true);
                    if (phone.getNumber().length() != 10) {
                        editPhone.setError("Please enter a valid phone number");
                    }
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    public void showSocialDialog(final String mode, final Social social) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfile.this);
        LayoutInflater inflater = LayoutInflater.from(EditProfile.this);
        View popPhoneLayout = inflater.inflate(R.layout.popup_social_add, null);

        final EditText editInstagram = popPhoneLayout.findViewById(R.id.social_editInstagram);
        final EditText editFacebook = popPhoneLayout.findViewById(R.id.social_editFacebook);
        final EditText editSnapchat = popPhoneLayout.findViewById(R.id.social_editSnapchat);
        final EditText editTwitter = popPhoneLayout.findViewById(R.id.social_editTwitter);
        final EditText editLinkedin = popPhoneLayout.findViewById(R.id.social_editLinkedin);

        final TextView saveButton = popPhoneLayout.findViewById(R.id.popupSocial_saveButton);
        final TextView cancelButton = popPhoneLayout.findViewById(R.id.popupSocial_cancelButton);

        editInstagram.setText(instagramLink);
        editTwitter.setText(twitterLink);
        editSnapchat.setText(snapchatLink);
        editFacebook.setText(facebookLink);
        editLinkedin.setText(linkedinLink);

        dialog.setView(popPhoneLayout);
        final AlertDialog alertDialog = dialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveButton.setClickable(false);
                social.setInstagram(editInstagram.getText().toString());
                social.setFacebook(editFacebook.getText().toString());
                social.setLinkedin(editLinkedin.getText().toString());
                social.setSnapchat(editSnapchat.getText().toString());
                social.setTwitter(editTwitter.getText().toString());

                pd.show();
                if (mode.equals(ADD))
                    databaseQueries.addSocial(social, alertDialog);
                else if (mode.equals(EDIT)) {
                    databaseQueries.addSocial(social, alertDialog);
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        globalMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.item_edit):
                name.setEnabled(true);
                title.setEnabled(true);
                item.setVisible(false);
                isEditable = true;
                socialEdit.setVisibility(View.VISIBLE);
                globalMenu.findItem(R.id.item_save).setVisible(true);
                break;
            case (R.id.item_save):
                saveProfileData(name.getText().toString(), title.getText().toString());
                item.setVisible(false);
                isEditable = false;
                socialEdit.setVisibility(View.GONE);
                globalMenu.findItem(R.id.item_edit).setVisible(true);
                break;
        }
        return true;
    }

    private void saveProfileData(final String updatedName, final String updatedTitle) {
        HashMap<String, String> hm = new HashMap<>();
        hm.put("name", updatedName);
        hm.put("title", updatedTitle);
        pd.show();
        document.set(hm, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    name.setText(updatedName);
                    title.setText(updatedTitle);
                    pd.dismiss();
                    name.setEnabled(false);
                    title.setEnabled(false);
                }
            }

        });
    }

    private void setUpToolbar() {
        toolbar = findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_action_back);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Picasso picasso = Picasso.get();
                picasso.setIndicatorsEnabled(false);
                picasso.load(resultUri).placeholder(R.mipmap.ic_launcher).into(imageView);

                pd.setMessage("updating profile");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                final File thumb_file_Path = new File(resultUri.getPath());

                final Bitmap thumb_bitmap = new Compressor(this)
                        .setMaxWidth(200)
                        .setMaxHeight(200)
                        .setQuality(50)
                        .compressToBitmap(thumb_file_Path);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                final byte[] thumb_byte = baos.toByteArray();

                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumb")
                        .child(user_id + ".jpg");

                UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            thumb_filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String thumb_downloadUrl = uri.toString();

                                    Map update_hashMap = new HashMap();
                                    update_hashMap.put("image", thumb_downloadUrl);

                                    document.set(update_hashMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            pd.dismiss();
                                            Toast.makeText(EditProfile.this, "profile updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });

                        }
                    }
                });
            }
        }
    }
}