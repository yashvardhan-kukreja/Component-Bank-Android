package com.ieeevit.componentbank.Classes;

/**
 * Created by Yash 1300 on 13-12-2017.
 */

public class Component {
    String name, value, quantity, id;
    public Component(String name, String value, String quantity, String id) {
        this.name = name;
        this.value = value;
        this.quantity = quantity;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getId() {
        return id;
    }
}
