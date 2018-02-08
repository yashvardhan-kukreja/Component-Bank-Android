package com.ieeevit.componentbankredefined.NetworkModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by luci4 on 5/2/18.
 */

public class GetIssuersModel {

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("component")
    @Expose
    private ComponentModel component;

    @SerializedName("transactions")
    @Expose
    private List<TransactionReqIssuersModel> transactions;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ComponentModel getComponent() {
        return component;
    }

    public void setComponent(ComponentModel component) {
        this.component = component;
    }

    public List<TransactionReqIssuersModel> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionReqIssuersModel> transactions) {
        this.transactions = transactions;
    }
}
