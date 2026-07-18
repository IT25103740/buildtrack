package com.buildtrack.project.repository;

import com.buildtrack.project.entity.Project;
import com.buildtrack.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByProjectManager(User pm);
    List<Project> findByClient(User client);
}
