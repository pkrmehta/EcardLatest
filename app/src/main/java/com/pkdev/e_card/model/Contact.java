package com.pkdev.e_card.model;

import java.util.List;

public class Contact {
    String image;
    String name;
    String title;
    String userid;
    String saved;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;
    List<Email> emailData;

    public List<Email> getEmailData() {
        return emailData;
    }

    public void setEmailData(List<Email> emailData) {
        this.emailData = emailData;
    }

    public String getSaved() {
        return saved;
    }

    public void setSaved(String saved) {
        this.saved = saved;
    }

    public  Contact(){

    }

    public Contact(String image, String name, String title, String userid, String saved, List<Email> emailData, String id) {
        this.image = image;
        this.name = name;
        this.title = title;
        this.userid = userid;
        this.saved = saved;
        this.emailData = emailData;
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

}
