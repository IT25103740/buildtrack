package com.buildtrack.material.service;

import com.buildtrack.common.NotificationService;
import com.buildtrack.material.entity.*;
import com.buildtrack.material.repository.*;
import com.buildtrack.project.repository.SiteRepository;
import com.buildtrack.user.entity.User;
import com.buildtrack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository materialRepo;
    private final StockMovementRepository stockRepo;
    private final MaterialRequestRepository requestRepo;
    private final SiteRepository siteRepo;
    private final UserRepository userRepo;
    private final NotificationService notifier;

    // ---- Master ----
    public List<Material> all() { return materialRepo.findAll(); }
    public Material get(Long id) {
        return materialRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Material not found"));
    }
    @Transactional public Material save(Material m) { return materialRepo.save(m); }
    @Transactional public void delete(Long id)     { materialRepo.deleteById(id); }

    // ---- Stock ----
    public long currentStock(Long materialId, Long siteId) {
        return stockRepo.currentStock(materialId, siteId);
    }

    public List<StockMovement> movementsForSite(Long siteId) {
        return stockRepo.findBySiteIdOrderByTimestampDesc(siteId);
    }

    @Transactional
    public StockMovement recordMovement(Long siteId, Long materialId, int qty, MovementType type, String note) {
        StockMovement sm = StockMovement.builder()
            .site(siteRepo.findById(siteId).orElseThrow())
            .material(materialRepo.findById(materialId).orElseThrow())
            .qty(qty)
            .type(type)
            .note(note)
            .build();
        return stockRepo.save(sm);
    }

    // ---- Requests ----
    public List<MaterialRequest> allRequests() { return requestRepo.findAll(); }
    public List<MaterialRequest> pending() { return requestRepo.findByStatus(RequestStatus.PENDING); }
    public List<MaterialRequest> forSite(Long siteId) {
        return requestRepo.findBySiteIdOrderByCreatedAtDesc(siteId);
    }
    public MaterialRequest getRequest(Long id) {
        return requestRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Request not found"));
    }

    @Transactional
    public MaterialRequest createRequest(Long siteId, Long materialId, int qty, User requester) {
        MaterialRequest r = MaterialRequest.builder()
            .site(siteRepo.findById(siteId).orElseThrow())
            .material(materialRepo.findById(materialId).orElseThrow())
            .qty(qty)
            .status(RequestStatus.PENDING)
            .requestedBy(requester)
            .build();
        MaterialRequest saved = requestRepo.save(r);
        userRepo.findAll().stream()
            .filter(u -> u.getRole().getName().name().equals("ADMIN"))
            .forEach(admin -> notifier.notify(admin,
                "New material request: " + saved.getMaterial().getName() + " x" + saved.getQty(),
                "/admin/requests"));
        return saved;
    }

    @Transactional
    public MaterialRequest updateRequestStatus(Long id, RequestStatus status) {
        MaterialRequest r = getRequest(id);
        r.setStatus(status);
        return requestRepo.save(r);
    }
}
