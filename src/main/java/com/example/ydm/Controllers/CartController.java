package com.example.ydm.Controllers;

import com.example.ydm.models.ItemCart;
import com.example.ydm.models.Product;
import com.example.ydm.repo.repoItemCart;
import com.example.ydm.repo.repoProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class CartController {

    @Autowired
    private repoItemCart cartRepository;

    @Autowired
    private repoProduct productRepository;

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping("/cart")
    public String viewCart(Model model) {
        Iterable<ItemCart> cartItems = cartRepository.findAll();
        double totalPrice = 0;

        for (ItemCart item : cartItems) {
            Product prod = item.getProduct();
            // Розрахунок актуальної вартості з урахуванням знижки товару
            double actualPrice = prod.isOnSale() ? prod.calculatePrice(prod.getDiscount()) : prod.calculatePrice();
            totalPrice += actualPrice * item.getQuantity();
        }

        // Округлення підсумкової суми до 2 знаків після коми
        totalPrice = Math.round(totalPrice * 100.0) / 100.0;

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("pageTitle", "Ваш кошик покупок");
        return "cart";
    }

    @PostMapping("/cart/add/{productId}")
    public String addToCart(@PathVariable Long productId, @RequestParam("quantity") int quantity) {
        Product product = productRepository.findById(productId).orElseThrow();

        if (quantity < 1) {
            quantity = 1;
        }

        Optional<ItemCart> existingItem = cartRepository.findByProductId(productId);
        if (existingItem.isPresent()) {
            ItemCart item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartRepository.save(item);
        } else {
            ItemCart newItem = new ItemCart(product, quantity);
            cartRepository.save(newItem);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/update/{id}")
    public String updateCartQuantity(@PathVariable Long id, @RequestParam("quantity") int quantity) {
        ItemCart item = cartRepository.findById(id).orElseThrow();

        if (quantity < 1) {
            cartRepository.deleteById(id);
        } else {
            item.setQuantity(quantity);
            cartRepository.save(item);
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/delete/{id}")
    public String deleteFromCart(@PathVariable Long id) {
        cartRepository.deleteById(id);
        return "redirect:/cart";
    }

    @PostMapping("/cart/clear")
    public String clearCart() {
        cartRepository.deleteAll();
        return "redirect:/cart";
    }

    @GetMapping("/developer")
    public String showDeveloperPage(Model model) {
        model.addAttribute("pageTitle", "Про розробника");
        return "developer";
    }

    @PostMapping("/developer/contact")
    public String handleContactForm(@RequestParam String name,
                                    @RequestParam String email,
                                    @RequestParam String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo("darinau060@gmail.com");
            mailMessage.setSubject("Нове повідомлення від: " + name);
            mailMessage.setText("Відправник: " + name + "\nEmail: " + email + "\n\nТекст повідомлення:\n" + message);

            mailSender.send(mailMessage);
            return "redirect:/developer?success";
        } catch (Exception e) {
            return "redirect:/developer?error";
        }
    }
}