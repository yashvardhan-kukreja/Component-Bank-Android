package com.ieeevit.componentbank.NetworkModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by luci4 on 5/2/18.
 */

public class GetMemberReqIssueModel {

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("output")
    @Expose
    private List<TransactionReqIssuersModel> output;

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

    public List<TransactionReqIssuersModel> getOutput() {
        return output;
    }

    public void setOutput(List<TransactionReqIssuersModel> output) {
        this.output = output;
    }

}
