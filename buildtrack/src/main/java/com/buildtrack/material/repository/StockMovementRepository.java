package com.buildtrack.material.repository;

import com.buildtrack.material.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    List<StockMovement> findBySiteIdOrderByTimestampDesc(Long siteId);

    /** Computes current stock for a material at a site: sum(IN) - sum(OUT). */
    @Query("""
        SELECT COALESCE(
          SUM(CASE WHEN sm.type = com.buildtrack.material.entity.MovementType.IN
                   THEN sm.qty ELSE -sm.qty END), 0)
        FROM StockMovement sm
        WHERE sm.material.id = :materialId AND sm.site.id = :siteId
        """)
    long currentStock(Long materialId, Long siteId);
}
