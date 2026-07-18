package com.buildtrack.supplier.entity;

import com.buildtrack.material.entity.Material;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "po_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class POItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "po_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Min(1) @Column(nullable = false)
    private int qty;

    @Builder.Default
    @DecimalMin("0.0") @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    /** Optional link to the site the material is being delivered to (auto stock-in target). */
    @Column(name = "site_id")
    private Long siteId;

    /** Optional link back to the request that produced this item. */
    @Column(name = "request_id")
    private Long requestId;
}
