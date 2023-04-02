package org.example;

public class Product {

    Integer price;
    Integer size;
    String type;

    public Product(Integer price, Integer size, String type) {
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

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
