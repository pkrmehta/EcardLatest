package com.pkdev.e_card.model;

public class Address {
    String address;
    String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Address(){

    }

    public Address(String address, String type, String id) {
        this.address = address;
        this.type = type;
        this.id = id;
    }
}
