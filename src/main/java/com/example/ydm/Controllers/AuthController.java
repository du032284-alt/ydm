package com.example.ydm.Controllers;

import com.example.ydm.models.AppUser;
import com.example.ydm.repo.repoAppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private repoAppUser userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //Показуємо сторінку входу
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    //Показуємо сторінку реєстрації
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    // Збереження користувача в базу
    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               Model model) {

        // Перевіряємо, чи немає вже користувача з таким логіном
        Optional<AppUser> existingUser = userRepo.findByUsername(username);
        if (existingUser.isPresent()) {
            model.addAttribute("error", "Користувач з таким логіном вже існує!");
            return "register";
        }

        String encodedPassword = passwordEncoder.encode(password);

        AppUser newUser = new AppUser(username, encodedPassword, "USER");

        userRepo.save(newUser);

        return "redirect:/login?success";
    }
}