package com.buildtrack.material.repository;

import com.buildtrack.material.entity.MaterialRequest;
import com.buildtrack.material.entity.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaterialRequestRepository extends JpaRepository<MaterialRequest, Long> {
    List<MaterialRequest> findByStatus(RequestStatus status);
    List<MaterialRequest> findBySiteIdOrderByCreatedAtDesc(Long siteId);
}
