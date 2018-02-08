package com.ieeevit.componentbankredefined.NetworkModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by luci4 on 5/2/18.
 */

public class TransactionReqIssuersModel {

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("componentId")
    @Expose
    private String componentId;

    @SerializedName("componentName")
    @Expose
    private String componentName;

    @SerializedName("memberId")
    @Expose
    private UserModel memberId;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("quantity")
    @Expose
    private int quantity;

    @SerializedName("returned")
    @Expose
    private String returned;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public UserModel getMemberId() {
        return memberId;
    }

    public void setMemberId(UserModel memberId) {
        this.memberId = memberId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReturned() {
        return returned;
    }

    public void setReturned(String returned) {
        this.returned = returned;
    }

}
