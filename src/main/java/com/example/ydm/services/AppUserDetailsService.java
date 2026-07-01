package com.example.ydm.services;

import com.example.ydm.models.AppUser;
import com.example.ydm.repo.repoAppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserDetailsService implements UserDetailsService {

    @Autowired
    private repoAppUser userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Шукаємо користувача в БД за логіном
        Optional<AppUser> userOpt = userRepo.findByUsername(username);

        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("Користувача не знайдено: " + username);
        }

        AppUser user = userOpt.get();

        // Повертаємо користувача з його зашифрованим паролем та роллю
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole() != null ? user.getRole() : "USER")
                .build();
    }
}