package com.buildtrack.supplier.repository;

import com.buildtrack.supplier.entity.PurchaseOrder;
import com.buildtrack.supplier.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findBySupplierOrderByCreatedAtDesc(Supplier supplier);
    List<PurchaseOrder> findAllByOrderByCreatedAtDesc();
}
