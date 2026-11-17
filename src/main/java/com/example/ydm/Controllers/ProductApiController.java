package com.example.ydm.Controllers;

import com.example.ydm.models.Product;
import com.example.ydm.repo.repoProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

// Робимо рест контроллер, щоб повертати чисті дані
@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    @Autowired
    private repoProduct productRepository; // Підключаємо репозиторій товарів

    // Обробка запит на пошук
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam(name = "keyword", required = false) String keyword) {

        // Якщо користувач нічого не ввів або просто поставив пробіли то повертаємо пустий список
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return productRepository.findByTitleContainingIgnoreCase(keyword);
    }
}