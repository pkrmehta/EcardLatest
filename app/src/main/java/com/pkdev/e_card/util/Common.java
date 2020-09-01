package com.pkdev.e_card.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class Common {
    public static void addInstagram(String id, Context context){
        Uri uri = Uri.parse("http://instagram.com/_u/"+id);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
        likeIng.setPackage("com.instagram.android");
        try {
            context.startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://instagram.com/"+id)));
        }
    }

    public static void addFacebook(String id, Context context){
        Uri uri = Uri.parse("http://facebook.com/_u/"+id);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
        likeIng.setPackage("com.facebook.katana");
        try {
            context.startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://facebook.com/"+id)));
        }
    }

    public static void addTwitter(String id, Context context){
        Uri uri = Uri.parse("http://twitter.com/"+id);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
        likeIng.setPackage("com.twitter.android");
        try {
            context.startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://twitter.com/"+id)));
        }
    }

    public static void addLinkedin(String id, Context context){
        Uri uri = Uri.parse("http://www.linkedin.com/profile/view?id="+id);
        Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
        likeIng.setPackage("com.linkedin.android");
        try {
            context.startActivity(likeIng);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://linkedin.com/"+id)));
        }
    }

    public static void addWhatsapp(String number, Context context){
        String url = "https://api.whatsapp.com/send?phone=+91"+number;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }
}
