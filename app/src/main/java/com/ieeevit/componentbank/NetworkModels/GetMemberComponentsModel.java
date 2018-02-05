package com.ieeevit.componentbank.NetworkModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by luci4 on 5/2/18.
 */

public class GetMemberComponentsModel {

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("components")
    @Expose
    private List<TransactionMemberComponentsModel> transactions;

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

    public List<TransactionMemberComponentsModel> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionMemberComponentsModel> transactions) {
        this.transactions = transactions;
    }
}
