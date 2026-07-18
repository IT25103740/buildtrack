package com.buildtrack.project.service;

import com.buildtrack.project.entity.*;
import com.buildtrack.project.repository.*;
import com.buildtrack.user.entity.User;
import com.buildtrack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepo;
    private final SiteRepository siteRepo;
    private final MilestoneRepository milestoneRepo;
    private final UserRepository userRepo;

    public List<Project> listAll() { return projectRepo.findAll(); }
    public Project get(Long id) {
        return projectRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Project not found: " + id));
    }

    @Transactional
    public Project save(Project p, Long pmId, Long clientId) {
        if (pmId != null) p.setProjectManager(userRepo.findById(pmId).orElse(null));
        if (clientId != null) p.setClient(userRepo.findById(clientId).orElse(null));
        return projectRepo.save(p);
    }

    @Transactional
    public void delete(Long id) { projectRepo.deleteById(id); }

    // ---- Sites ----
    @Transactional
    public Site addSite(Long projectId, Site site, Long engineerId) {
        Project p = get(projectId);
        site.setProject(p);
        if (engineerId != null) site.setSiteEngineer(userRepo.findById(engineerId).orElse(null));
        return siteRepo.save(site);
    }

    @Transactional
    public void deleteSite(Long siteId) { siteRepo.deleteById(siteId); }

    // ---- Milestones ----
    @Transactional
    public Milestone addMilestone(Long projectId, Milestone m) {
        m.setProject(get(projectId));
        return milestoneRepo.save(m);
    }

    @Transactional
    public void deleteMilestone(Long milestoneId) { milestoneRepo.deleteById(milestoneId); }

    // ---- Helpers for dropdowns ----
    public List<User> allUsers() { return userRepo.findAll(); }
}
