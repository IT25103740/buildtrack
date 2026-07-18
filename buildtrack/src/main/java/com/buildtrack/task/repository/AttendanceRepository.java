package com.buildtrack.task.repository;

import com.buildtrack.task.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findBySiteIdAndDate(Long siteId, LocalDate date);
}
