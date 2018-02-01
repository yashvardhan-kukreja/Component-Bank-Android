package com.ieeevit.componentbank.Classes;

/**
 * Created by Yash 1300 on 04-12-2017.
 */

public class User {
    String name, regNum, email, phonenum;

    public User(String name, String regNum, String email, String phonenum) {
        this.name = name;
        this.regNum = regNum;
        this.email = email;
        this.phonenum = phonenum;
    }

    public String getName() {
        return name;
    }

    public String getRegNum() {
        return regNum;
    }

    public String getEmail() {
        return email;
    }

    public String getPhonenum() {
        return phonenum;
    }
}
