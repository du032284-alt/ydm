package com.example.ydm.Controllers;

import com.example.ydm.repo.repoCategory;
import com.example.ydm.repo.repoOrder;
import com.example.ydm.repo.repoProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class StatsController {

    @Autowired
    private repoProduct productRepository;

    @Autowired
    private repoCategory categoryRepository;

    @Autowired
    private repoOrder orderRepository;

    @GetMapping("/stats")
    public String showStats(Model model) {
        model.addAttribute("pageTitle", "Статистика магазину");

        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalCategories", categoryRepository.count());
        model.addAttribute("totalOrders", orderRepository.count());

        // Дані для кругової діаграми
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        categoryRepository.findAll().forEach(cat -> {
            labels.add(cat.getName());
            data.add((long) productRepository.findByCategoryId(cat.getId()).size());
        });

        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartData", data);

        return "stats";
    }
}