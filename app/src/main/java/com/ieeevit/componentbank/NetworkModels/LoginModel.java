package com.ieeevit.componentbank.NetworkModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by luci4 on 5/2/18.
 */

public class LoginModel {

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("regno")
    @Expose
    private String regno;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("phoneno")
    @Expose
    private String phoneno;

    @SerializedName("issuedComponents")
    @Expose
    private int issuedComponents;

    @SerializedName("requestedComponents")
    @Expose
    private int requesedComponents;

    @SerializedName("isAdmin")
    @Expose
    private String isAdmin;

    @SerializedName("token")
    @Expose String token;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public int getIssuedComponents() {
        return issuedComponents;
    }

    public void setIssuedComponents(int issuedComponents) {
        this.issuedComponents = issuedComponents;
    }

    public int getRequesedComponents() {
        return requesedComponents;
    }

    public void setRequesedComponents(int requesedComponents) {
        this.requesedComponents = requesedComponents;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
