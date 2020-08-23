package com.pkdev.e_card.model;

public class Work {
    String company;
    String position;
    String start;
    String end;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;

    public Work(String company, String position, String start, String end, String id) {
        this.company = company;
        this.position = position;
        this.start = start;
        this.end = end;
        this.id = id;
    }

    public Work(){

    }
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
