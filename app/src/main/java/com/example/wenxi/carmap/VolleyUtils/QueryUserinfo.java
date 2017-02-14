package com.example.wenxi.carmap.VolleyUtils;

/**
 * Created by wenxi on 2016/12/20.
 */

public class QueryUserinfo {
    private String Username;
    private String Password;
    private String Telephone;
    private String License_number;

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }

    public String getLicense_number() {
        return License_number;
    }

    public void setLicense_number(String license_number) {
        License_number = license_number;
    }

    @Override
    public String toString() {
        return "QueryUserinfo{" +
                "Username='" + Username + '\'' +
                ", Password='" + Password + '\'' +
                ", Telephone='" + Telephone + '\'' +
                ", License_number='" + License_number + '\'' +
                '}';
    }
}
