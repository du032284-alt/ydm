package com.example.ydm.Controllers;

import com.example.ydm.models.Category;
import com.example.ydm.models.Product;
import com.example.ydm.repo.repoCategory;
import com.example.ydm.repo.repoProduct;
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
import java.util.List;
import java.util.Optional;

@Controller
public class ProductController {

    @Autowired
    private repoProduct productRepository;

    @Autowired
    private repoCategory categoryRepository;

    // Виведення ВСІХ товарів разом
    @GetMapping("/products")
    public String allProducts(Model model) {
        Iterable<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        model.addAttribute("pageTitle", "Всі товари нашого магазину");
        return "products-list";
    }

    // Виведення товарів лише ОБРАНОЇ категорії
    @GetMapping("/shop/category/{id}/products")
    public String productsByCategory(@PathVariable(value = "id") long id, Model model) {
        Optional<Category> categoryOpt = categoryRepository.findById(id);
        if (categoryOpt.isEmpty()) {
            return "redirect:/shop";
        }

        Category category = categoryOpt.get();

        // Витягуємо з БД тільки ті товари, у яких категорі айді дорівнює нашому айді
        List<Product> products = productRepository.findByCategoryId(id);

        model.addAttribute("products", products);
        model.addAttribute("pageTitle", "Товари категорії: " + category.getName());
        model.addAttribute("categoryId", id);
        return "products-list";
    }

    @GetMapping("/shop/category/{categoryId}/product/add")
    public String addProductPage(@PathVariable(value = "categoryId") long categoryId, Model model) {
        model.addAttribute("categoryId", categoryId);
        return "product-add";
    }

    @PostMapping("/shop/category/{categoryId}/product/add")
    public String saveNewProduct(
            @PathVariable(value = "categoryId") long categoryId,
            @RequestParam String title,
            @RequestParam String text,
            @RequestParam Double price,
            @RequestParam(value = "discount", defaultValue = "0.0") Double discount,
            @RequestParam("file") MultipartFile file) {

        Product product = new Product();
        product.setTitle(title);
        product.setText(text);
        product.setPrice(price);
        product.setDiscount(discount);

        Category category = categoryRepository.findById(categoryId).orElseThrow();
        product.setCategory(category);

        if (!file.isEmpty()) {
            try {
                Path staticPath = Paths.get("src/main/resources/static/images/");
                String fileName = file.getOriginalFilename();
                Path filePath = staticPath.resolve(fileName);

                Files.createDirectories(filePath.getParent());
                Files.write(filePath, file.getBytes());

                product.setImage(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        productRepository.save(product);
        return "redirect:/shop/category/" + categoryId + "/products";
    }

    // ДЕТАЛЬНИЙ ПЕРЕГЛЯД ТОВАРУ
    @GetMapping("/shop/product/{id}")
    public String productDetails(@PathVariable(value = "id") long id, Model model) {
        Product product = productRepository.findById(id).orElseThrow();
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", product.getTitle());
        return "product-details";
    }

    // ВИДАЛЕННЯ ТОВАРУ
    @PostMapping("/shop/product/{id}/delete")
    public String deleteProduct(@PathVariable(value = "id") long id) {
        Product product = productRepository.findById(id).orElseThrow();
        long categoryId = product.getCategory().getId();

        if (product.getImage() != null && !product.getImage().isEmpty()) {
            try {
                Path imagePath = Paths.get("src/main/resources/static/images/").resolve(product.getImage());
                Files.deleteIfExists(imagePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        productRepository.delete(product);
        return "redirect:/shop/category/" + categoryId + "/products";
    }

    // СТОРІНКА РЕДАГУВАННЯ ТОВАРУ
    @GetMapping("/shop/product/{id}/edit")
    public String editProductPage(@PathVariable(value = "id") long id, Model model) {
        Product product = productRepository.findById(id).orElseThrow();
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", "Редагування: " + product.getTitle());
        return "product-edit";
    }

    // ЗБЕРЕЖЕННЯ ВІДРЕДАГОВАНОГО ТОВАРУ
    @PostMapping("/shop/product/{id}/edit")
    public String saveEditedProduct(
            @PathVariable(value = "id") long id,
            @RequestParam String title,
            @RequestParam String text,
            @RequestParam Double price,
            @RequestParam(value = "discount", defaultValue = "0.0") Double discount,
            @RequestParam("file") MultipartFile file) {

        Product product = productRepository.findById(id).orElseThrow();
        product.setTitle(title);
        product.setText(text);
        product.setPrice(price);
        product.setDiscount(discount);

        if (!file.isEmpty()) {
            try {
                Path staticPath = Paths.get("src/main/resources/static/images/");
                String fileName = file.getOriginalFilename();
                Path filePath = staticPath.resolve(fileName);

                Files.createDirectories(filePath.getParent());
                Files.write(filePath, file.getBytes());

                product.setImage(fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        productRepository.save(product);
        return "redirect:/shop/product/" + id;
    }
}

