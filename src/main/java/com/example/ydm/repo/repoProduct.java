package com.example.ydm.repo;

import com.example.ydm.models.Product;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface repoProduct extends CrudRepository<Product, Long> { // Репозиторій для роботи з таблицею товарів у базі даних

    List<Product> findByCategoryId(Long categoryId); //сам робить запит ськьюел, щоб дістати товари конкретної категорії

    List<Product> findByFavoriteTrue(); // Метод витягує з бази тільки ті товари, де стоїть обране

    List<Product> findByTitleContainingIgnoreCase(String keyword);// Пошук по назві для вью джс шукає збіги в будь-якій частині слова
}