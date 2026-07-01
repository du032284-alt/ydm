package com.example.ydm.models;
import jakarta.persistence.*;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String text;
    private Double price;
    private String image;

    // Нове поле для обраних товарів
    private boolean favorite;

    // Зв'язок багатьох товарів до однієї категорії
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    public Product() {}

    public Product(String title, String text, Double price, String image, Category category) {
        this.title = title;
        this.text = text;
        this.price = price;
        this.image = image;
        this.category = category;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public boolean isFavorite() { return favorite; }
    public void setFavorite(boolean isFavorite) { favorite = isFavorite; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}