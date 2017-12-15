package com.ieeevit.componentbank.Classes;

/**
 * Created by Yash 1300 on 13-12-2017.
 */

public class Component {
    String name, code, quantity;
    public Component(String name, String code, String quantity) {
        this.name = name;
        this.code = code;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getQuantity() {
        return quantity;
    }
}
