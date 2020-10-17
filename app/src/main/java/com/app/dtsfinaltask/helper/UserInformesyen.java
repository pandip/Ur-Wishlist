package com.app.dtsfinaltask.helper;

public class UserInformesyen {

    public String currentFinance;
    public String nama;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public UserInformesyen() {
    }

    public UserInformesyen(String nama) {
        this.nama = nama;
    }

    public UserInformesyen(String currentFinance, String nama) {
        this.currentFinance = currentFinance;
        this.nama = nama;
    }

    public String getCurrentFinance() {
        return currentFinance;
    }

    public void setCurrentFinance(String currentFinance) {
        this.currentFinance = currentFinance;
    }
}
