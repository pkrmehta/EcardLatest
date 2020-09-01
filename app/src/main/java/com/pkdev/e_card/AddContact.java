package com.pkdev.e_card;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class AddContact extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;
    ImageView imageQR;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);

        currentUser = mAuth.getCurrentUser();

        imageQR = findViewById(R.id.addContact_qrImage);
        final Activity activity = this;

        pd = new ProgressDialog(AddContact.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(true);
        pd.setTitle("Loading....");
        pd.setMessage("Please Wait");

        generateQrCode();

        findViewById(R.id.addContact_scanButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.setCaptureActivity(CapturedActivity.class);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });

        findViewById(R.id.addContact_sendDirectButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                findViewById(R.id.addContact_sendDirectButton).setEnabled(false);
                db.collection("users").document(currentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String name = documentSnapshot.get("name").toString();
                        String image = documentSnapshot.get("image").toString();
                        sendFirebaseDynamicLnk(name, image);
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent startIntent = new Intent(AddContact.this, Login.class);
            startActivity(startIntent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(AddContact.this, Accept.class);
                intent.putExtra("USER_ID", result.getContents());
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void generateQrCode() {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(currentUser.getUid(), BarcodeFormat.QR_CODE, 142, 142);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            imageQR.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFirebaseDynamicLnk(String name, String image) {
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse("https://pkdev.page.link/?link=https://pkdev/" + currentUser.getUid() + "&apn=com.pkdev.e_card&st=Share+Request&sd=" + name + "+Invited+You+To+Share+Contact+Accept+This+Share+Request&si=" + Uri.parse(image)))
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            findViewById(R.id.addContact_sendDirectButton).setEnabled(true);
                            pd.dismiss();
                            Log.i("CHEOK", "success");
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, task.getResult().getShortLink().toString());
                            sendIntent.setType("text/plain");
                            Intent shareIntent = Intent.createChooser(sendIntent, null);
                            startActivity(shareIntent);
                        } else {
                            findViewById(R.id.addContact_sendDirectButton).setEnabled(true);
                            pd.dismiss();
                        }
                    }
                });
    }
}