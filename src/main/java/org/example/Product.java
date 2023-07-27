package org.example;

import java.io.Serializable;

public class Product implements Serializable {

    int price;
    int size;
    char type;

    public Product(int price, int size, char type) {
        this.price = price;
        this.size = size;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Products{" +
                "price=" + price +
                ", size=" + size +
                ", type='" + type + '\'' +
                '}';
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }
}
