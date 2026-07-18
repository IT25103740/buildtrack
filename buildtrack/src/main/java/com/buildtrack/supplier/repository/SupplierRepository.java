package com.buildtrack.supplier.repository;

import com.buildtrack.supplier.entity.Supplier;
import com.buildtrack.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByUser(User user);
}
