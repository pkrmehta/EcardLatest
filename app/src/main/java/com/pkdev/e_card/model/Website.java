package com.pkdev.e_card.model;

public class Website {
    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Website(String website, String id) {
        this.website = website;
        this.id = id;
    }
    public Website(){}

    String website;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;
}
