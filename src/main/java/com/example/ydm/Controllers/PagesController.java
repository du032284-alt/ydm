package com.example.ydm.Controllers;

import com.example.ydm.models.Category;
import com.example.ydm.repo.repoCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Controller
public class PagesController {

    @Autowired
    private repoCategory categoryRepository;

    // Виведення всіх категорій
    @GetMapping("/shop")
    public String shop(Model model) {
        Iterable<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        return "shop";
    }

    // інформація про категорію
    @GetMapping("/shop/category/{id}")
    public String detailsCategory(@PathVariable(value = "id") long id, Model model) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            return "redirect:/shop";
        }
        model.addAttribute("category", category.get());
        return "category-details";
    }

    //Видалення категорії
    @PostMapping("/shop/category/{id}/delete")
    public String deleteCategory(@PathVariable(value = "id") long id) {
        categoryRepository.deleteById(id);
        return "redirect:/shop";
    }

    //Сторінка редагування
    @GetMapping("/shop/category/{id}/edit")
    public String editCategoryPage(@PathVariable(value = "id") long id, Model model) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            return "redirect:/shop";
        }
        model.addAttribute("category", category.get());
        return "category-edit";
    }

    //  ЗБЕРЕЖЕННЯ
    @PostMapping("/shop/category/{id}/edit")
    public String saveEditedCategory(
            @PathVariable(value = "id") long id,
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam("file") MultipartFile file) {

        Category category = categoryRepository.findById(id).orElseThrow();
        category.setName(name);
        category.setDescription(description);

        if (!file.isEmpty()) {
            try {
                // Шлях відносно кореня проєкту
                Path staticPath = Paths.get("src/main/resources/static/images/");
                String fileName = file.getOriginalFilename();
                // Форм кінцевий шлях до файлу
                Path filePath = staticPath.resolve(fileName);
                Files.write(filePath, file.getBytes());
                // Зберігаємо ім'я файлу в базу даних
                category.setIcon(fileName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        categoryRepository.save(category);
        return "redirect:/shop";
    }

    // Відображення сторінки додавання категорії
    @GetMapping("/shop/category/add")
    public String addCategoryPage() {
        return "category-add";
    }

    // Збереження нової категорії в базу
    @PostMapping("/shop/category/add")
    public String saveNewCategory(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam("file") MultipartFile file) {

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);

        if (!file.isEmpty()) {
            try {
                Path staticPath = Paths.get("src/main/resources/static/images/");
                String fileName = file.getOriginalFilename();
                Path filePath = staticPath.resolve(fileName);

                Files.write(filePath, file.getBytes());
                category.setIcon(fileName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        categoryRepository.save(category);
        return "redirect:/shop";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
