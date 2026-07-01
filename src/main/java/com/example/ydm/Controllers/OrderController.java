package com.example.ydm.Controllers;

import com.example.ydm.models.AppUser;
import com.example.ydm.models.ItemCart;
import com.example.ydm.models.Order;
import com.example.ydm.models.OrderItem;
import com.example.ydm.repo.repoAppUser;
import com.example.ydm.repo.repoItemCart;
import com.example.ydm.repo.repoOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class OrderController {

    @Autowired
    private repoItemCart cartRepository;

    @Autowired
    private repoOrder orderRepository;

    @Autowired
    private repoAppUser userRepository;

    // ПЕРЕГЛЯД СТОРІНКИ ОФОРМЛЕННЯ
    @GetMapping("/order")
    public String viewOrderPage(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }

        Iterable<ItemCart> cartItems = cartRepository.findAll();
        if (!cartItems.iterator().hasNext()) {
            return "redirect:/cart";
        }

        double totalPrice = 0;
        for (ItemCart item : cartItems) {
            totalPrice += item.getProduct().getPrice() * item.getQuantity();
        }

        // Шукаємо користувача, щоб дізнатися його реальні бонуси з БД
        int bonuses = 0;
        Optional<AppUser> userOpt = userRepository.findByUsername(currentUser.getUsername());
        if (userOpt.isPresent()) {
            bonuses = userOpt.get().getBonuses();
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("userBonuses", bonuses); // Передаємо у Thymeleaf для правай колонки
        return "order";
    }

    //СТВОРЕННЯ ЗАМОВЛЕННЯ
    @PostMapping("/orders/create")
    public String createOrder(@RequestParam String customerName,
                              @RequestParam String customerPhone,
                              @RequestParam String shippingAddress,
                              @RequestParam(required = false, defaultValue = "false") boolean useBonuses,
                              @RequestParam(required = false, defaultValue = "0") double totalSum,
                              @AuthenticationPrincipal UserDetails currentUser) {

        Iterable<ItemCart> cartItems = cartRepository.findAll();
        if (!cartItems.iterator().hasNext()) {
            return "redirect:/cart";
        }

        // Визнач базову суму
        double totalPrice = (totalSum > 0) ? totalSum : 0;
        if (totalPrice == 0) {
            for (ItemCart item : cartItems) {
                totalPrice += item.getProduct().getPrice() * item.getQuantity();
            }
        }

        double originalPrice = totalPrice;
        int bonusesToDiscount = 0;
        AppUser appUser = null;

        if (currentUser != null) {
            Optional<AppUser> userOpt = userRepository.findByUsername(currentUser.getUsername());
            if (userOpt.isPresent()) {
                appUser = userOpt.get();

                // Обробка списання бонусів
                if (useBonuses && appUser.getBonuses() > 0) {
                    if (appUser.getBonuses() >= totalPrice) {
                        bonusesToDiscount = (int) totalPrice;
                        appUser.setBonuses(appUser.getBonuses() - bonusesToDiscount);
                        totalPrice = 0;
                    } else {
                        bonusesToDiscount = appUser.getBonuses();
                        totalPrice -= bonusesToDiscount;
                        appUser.setBonuses(0);
                    }
                }
            }
        }

        // Зберігаємо замовлення
        Order order = new Order(totalPrice);
        order.setCustomerName(customerName);
        order.setCustomerPhone(customerPhone);
        order.setShippingAddress(shippingAddress);
        order = orderRepository.save(order);

        for (ItemCart item : cartItems) {
            OrderItem orderItem = new OrderItem(
                    order,
                    item.getProduct(),
                    item.getQuantity(),
                    item.getProduct().getPrice()
            );
            order.getItems().add(orderItem);
        }
        orderRepository.save(order);

        // Нарахування 10% бонусів від суми замовлення
        if (appUser != null && originalPrice > 0) {
            int earnedBonuses = (int) (originalPrice * 0.10);
            appUser.setBonuses(appUser.getBonuses() + earnedBonuses);
            userRepository.save(appUser);
        }
        cartRepository.deleteAll();
        return "redirect:/orders/history";
    }
    @GetMapping("/orders/history")
    public String viewOrderHistory(Model model) {
        Iterable<Order> orders = orderRepository.findAll();
        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "Історія ваших замовлень");
        return "orders-history";
    }
    @PostMapping("/orders/clear")
    public String clearOrderHistory() {
        orderRepository.deleteAll();
        return "redirect:/orders/history";
    }
}