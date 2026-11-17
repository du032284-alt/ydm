package com.example.ydm.models;

import jakarta.persistence.Entity;

@Entity
public class Category extends BaseEntity {

    private String name;
    private String description;
    private String icon;

    public Category() {
        super();
    }

    public Category(String name, String description, String icon) {
        super();
        setName(name);
        this.description = description;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    // Контроль введення для назви категорії
    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва категорії не може бути порожньою!");
        }
        this.name = name.trim();
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

    // ПЕРЕЗАВАНТАЖЕННЯ
    @Override
    public String toString() {
        return "Категорія: " + name;
    }
}