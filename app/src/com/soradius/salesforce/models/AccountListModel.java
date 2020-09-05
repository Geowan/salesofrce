package com.soradius.salesforce.models;

public class AccountListModel {
    public String Name,Balance__c,AccountId, Most_Recent_Opportunity_Created__c,Most_Recent_Payment_Date__c,
            Most_Recent_Payment_Date_Hebrew__c
    ;


    public String getMost_Recent_Payment_Date_Hebrew__c() {
        return Most_Recent_Payment_Date_Hebrew__c;
    }

    public void setMost_Recent_Payment_Date_Hebrew__c(String most_Recent_Payment_Date_Hebrew__c) {
        Most_Recent_Payment_Date_Hebrew__c = most_Recent_Payment_Date_Hebrew__c;
    }

    public String getMost_Recent_Payment_Date__c() {
        return Most_Recent_Payment_Date__c;
    }

    public void setMost_Recent_Payment_Date__c(String most_Recent_Payment_Date__c) {
        Most_Recent_Payment_Date__c = most_Recent_Payment_Date__c;
    }

    public String getMost_Recent_Opportunity_Created__c() {
        return Most_Recent_Opportunity_Created__c;
    }

    public void setMost_Recent_Opportunity_Created__c(String most_Recent_Opportunity_Created__c) {
        Most_Recent_Opportunity_Created__c = most_Recent_Opportunity_Created__c;
    }

    public String getAccountId() {
        return AccountId;
    }

    public void setAccountId(String accountId) {
        AccountId = accountId;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setBalance__c(String balance__c) {
        Balance__c = balance__c;
    }

    public String getName() {
        return Name;
    }

    public String getBalance__c() {
        return Balance__c;
    }
}
