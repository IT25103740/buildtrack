package com.buildtrack.task.repository;

import com.buildtrack.task.entity.Task;
import com.buildtrack.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findBySiteId(Long siteId);
    List<Task> findByAssignee(User assignee);
    List<Task> findByAssigneeOrderByDueDateAsc(User assignee);
}
