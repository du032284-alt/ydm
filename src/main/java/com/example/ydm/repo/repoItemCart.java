package com.example.ydm.repo;
import com.example.ydm.models.ItemCart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface repoItemCart extends CrudRepository<ItemCart, Long> {
    Optional<ItemCart> findByProductId(Long productId);
}
