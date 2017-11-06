package com.zestsed.mobile.data;

/**
 * Created by mdugah on 3/15/2017.
 */

public class Product {
    String name;
    String desc;
    int icon;

    public Product(String name, String desc, int icon) {
        this.name = name;
        this.desc = desc;
        this.icon = icon;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
