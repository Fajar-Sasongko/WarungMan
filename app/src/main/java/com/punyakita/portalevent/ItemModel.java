package com.punyakita.portalevent;

public class ItemModel {

    private String item_id;
    private String name;
    private String selling_price;

    public ItemModel(String item_id, String name, String selling_price) {
        this.item_id = item_id;
        this.name = name;
        this.selling_price = selling_price;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSelling_price() {
        return selling_price;
    }

    public void setSelling_price(String selling_price) {
        this.selling_price = selling_price;
    }
}
