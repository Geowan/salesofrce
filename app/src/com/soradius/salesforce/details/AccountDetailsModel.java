package com.soradius.salesforce.details;

import okhttp3.internal.connection.StreamAllocation;

public class AccountDetailsModel {
    String title,value;

    public AccountDetailsModel() {
    }

    public AccountDetailsModel(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
