package com.example.ydm.Controllers;

import com.example.ydm.models.Product;
import com.example.ydm.repo.repoProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private repoProduct productRepository;

    @GetMapping("/")
    public String home(Model model) {
        // Формуємо масив обраних товарів із бази даних
        List<Product> favoriteProducts = productRepository.findByFavoriteTrue();

        // Передаємо цей масив у шаблон
        model.addAttribute("favoriteProducts", favoriteProducts);

        model.addAttribute("pageTitle", "Головна — GlowGreen");

        return "home";
    }
}