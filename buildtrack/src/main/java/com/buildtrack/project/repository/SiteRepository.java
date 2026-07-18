package com.buildtrack.project.repository;

import com.buildtrack.project.entity.Site;
import com.buildtrack.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SiteRepository extends JpaRepository<Site, Long> {
    List<Site> findByProjectId(Long projectId);
    List<Site> findBySiteEngineer(User engineer);
}
