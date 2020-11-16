package com.example.supervisor_seerem.model;

public class Contact {
    private String email;
    private String id;
    private String link;
    private String phone;

    public Contact(String id, String phone, String email, String link) {
        this.id = id;
        this.email = email;
        this.link = link;
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getLink() {
        return link;
    }

    public String getPhone() {
        return phone;
    }
}
