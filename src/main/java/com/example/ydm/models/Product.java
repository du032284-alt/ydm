package com.example.ydm.models;

import jakarta.persistence.*;

@Entity
public class Product extends BaseEntity {

    private String title;
    private String text;
    private Double price;
    private String image;
    private boolean favorite;

    // Поле індивідуальної знижки у відсотках
    private Double discount = 0.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public Product() {
        super();
        this.discount = 0.0;
        this.favorite = false;
    }

    public Product(String title, String text, Double price, String image, Category category) {
        super();
        setTitle(title);
        setText(text);
        setPrice(price);
        this.image = image;
        this.category = category;
        this.favorite = false;
        this.discount = 0.0;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва товару не може бути порожньою!");
        }
        this.title = title.trim();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Ціна не може бути від'ємною!");
        }
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.favorite = isFavorite;
    }

    public Double getDiscount() {
        return discount != null ? discount : 0.0;
    }

    public void setDiscount(Double discount) {
        if (discount != null && (discount < 0 || discount > 100)) {
            throw new IllegalArgumentException("Знижка має бути в межах від 0 до 100%!");
        }
        this.discount = (discount != null) ? discount : 0.0;
    }

    public boolean isOnSale() {
        return this.discount != null && this.discount > 0.0;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    //ПЕРЕЗАВАНТАЖЕННЯ
    @Override
    public String toString() {
        return "Товар: " + title + " | Ціна: " + price + " грн" + (isOnSale() ? " (Знижка: " + discount + "%)" : "");
    }

    //ПЕРЕВАНТАЖЕННЯ МЕТОДІВ
    public Double calculatePrice() {
        return this.price != null ? this.price : 0.0;
    }

    public Double calculatePrice(double discountPercentage) {
        if (discountPercentage < 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Знижка має бути в межах від 0 до 100%!");
        }
        return calculatePrice() * (1.0 - (discountPercentage / 100.0));
    }
}