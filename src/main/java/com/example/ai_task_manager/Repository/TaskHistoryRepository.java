package com.example.ai_task_manager.Repository;

import com.example.ai_task_manager.model.TaskHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    List<TaskHistory> findByTaskIdOrderByChangedAtDesc(Long task_id);
}
