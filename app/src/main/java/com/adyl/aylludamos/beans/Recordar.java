package com.adyl.aylludamos.beans;

import com.orm.SugarRecord;

public class Recordar extends SugarRecord {

    private String email;
    private String password;

    public Recordar() {
    }

    public Recordar(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
