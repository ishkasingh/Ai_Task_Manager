package com.example.ai_task_manager.Repository;

import com.example.ai_task_manager.enums.Priority;
import com.example.ai_task_manager.enums.Status;
import com.example.ai_task_manager.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String keyword, String description
    );
    @Query("""
    SELECT t FROM Task t
    WHERE (:status IS NULL OR t.status = :status)
    AND (:priority IS NULL OR t.priority = :priority)
    AND (:tag IS NULL OR LOWER(t.tag) LIKE CONCAT('%', LOWER(:tag), '%'))
""")


    List<Task> filterTasks(
            @Param("status") Status status,
            @Param("priority") Priority priority,
            @Param("tag") String tag
    );

    @Query("""
    SELECT t FROM Task t
    WHERE t.dueDate<CURRENT_DATE
    AND t.status != com.example.ai_task_manager.enums.Status.DONE
""")
    List<Task> findOverdueTasks();
    @Query("SELECT t FROM Task t " +
            "WHERE (:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:priority IS NULL OR t.priority = :priority) " +
            "AND (:tag IS NULL OR LOWER(t.tag) = LOWER(:tag)) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'deadline' THEN t.dueDate END ASC, " +
            "CASE WHEN :sortBy = 'priority' THEN t.priority END DESC")
    List<Task> searchFilterSortTasks(@Param("keyword") String keyword,
                                     @Param("status") Status status,
                                     @Param("priority") Priority priority,
                                     @Param("tag") String tag,
                                     @Param("sortBy") String sortBy);
}

