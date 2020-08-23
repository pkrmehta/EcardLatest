package com.pkdev.e_card.model;

public class Email {
    String email;
    String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public Email(){

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Email(String email, String type, String id) {
        this.email = email;
        this.type = type;
        this.id = id;
    }
}
