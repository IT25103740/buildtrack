package com.buildtrack.supplier.service;

import com.buildtrack.common.NotificationService;
import com.buildtrack.material.entity.*;
import com.buildtrack.material.repository.*;
import com.buildtrack.material.service.MaterialService;
import com.buildtrack.supplier.entity.*;
import com.buildtrack.supplier.repository.*;
import com.buildtrack.user.entity.User;
import com.buildtrack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierService {

    private final SupplierRepository supplierRepo;
    private final PurchaseOrderRepository poRepo;
    private final MaterialRequestRepository requestRepo;
    private final UserRepository userRepo;
    private final MaterialService materialService;
    private final NotificationService notifier;

    // ---- Supplier master ----
    public List<Supplier> all() { return supplierRepo.findAll(); }
    public Supplier get(Long id) {
        return supplierRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Supplier not found"));
    }
    @Transactional public Supplier save(Supplier s, Long userId) {
        if (userId != null) s.setUser(userRepo.findById(userId).orElse(null));
        return supplierRepo.save(s);
    }
    @Transactional public void delete(Long id) { supplierRepo.deleteById(id); }

    // ---- POs ----
    public List<PurchaseOrder> allPOs() { return poRepo.findAllByOrderByCreatedAtDesc(); }
    public List<PurchaseOrder> forSupplier(User user) {
        return supplierRepo.findByUser(user)
            .map(poRepo::findBySupplierOrderByCreatedAtDesc)
            .orElse(List.of());
    }
    public PurchaseOrder getPO(Long id) {
        return poRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("PO not found"));
    }

    /** Raise a PO from a pending material request. */
    @Transactional
    public PurchaseOrder raiseFromRequest(Long requestId, Long supplierId, BigDecimal unitPrice) {
        MaterialRequest r = requestRepo.findById(requestId)
            .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        Supplier s = get(supplierId);

        POItem item = POItem.builder()
            .material(r.getMaterial())
            .qty(r.getQty())
            .unitPrice(unitPrice)
            .siteId(r.getSite().getId())
            .requestId(r.getId())
            .build();

        PurchaseOrder po = PurchaseOrder.builder()
            .supplier(s)
            .project(r.getSite().getProject())
            .status(POStatus.SENT)
            .total(unitPrice.multiply(BigDecimal.valueOf(r.getQty())))
            .build();
        po.getItems().add(item);
        item.setPurchaseOrder(po);

        PurchaseOrder saved = poRepo.save(po);

        r.setStatus(RequestStatus.ORDERED);
        requestRepo.save(r);

        if (s.getUser() != null) {
            notifier.notify(s.getUser(),
                "New purchase order #" + saved.getId() + " sent to you.",
                "/supplier/orders");
        }
        return saved;
    }

    /**
     * Supplier updates PO status. When it becomes DELIVERED, auto-create stock IN
     * movements for each item and mark linked requests FULFILLED.
     */
    @Transactional
    public PurchaseOrder updateStatus(Long poId, POStatus newStatus) {
        PurchaseOrder po = getPO(poId);
        POStatus old = po.getStatus();
        po.setStatus(newStatus);
        po = poRepo.save(po);

        if (newStatus == POStatus.DELIVERED && old != POStatus.DELIVERED) {
            for (POItem it : po.getItems()) {
                if (it.getSiteId() != null) {
                    materialService.recordMovement(
                        it.getSiteId(),
                        it.getMaterial().getId(),
                        it.getQty(),
                        MovementType.IN,
                        "Auto stock-in from PO #" + po.getId()
                    );
                }
                if (it.getRequestId() != null) {
                    requestRepo.findById(it.getRequestId()).ifPresent(req -> {
                        req.setStatus(RequestStatus.FULFILLED);
                        requestRepo.save(req);
                    });
                }
            }
        }
        return po;
    }

    // ---- Helpers ----
    public List<User> supplierUsers() {
        return userRepo.findAll().stream()
            .filter(u -> u.getRole().getName().name().equals("SUPPLIER"))
            .toList();
    }
    public List<MaterialRequest> pendingRequests() {
        return requestRepo.findByStatus(RequestStatus.APPROVED);
    }
}
