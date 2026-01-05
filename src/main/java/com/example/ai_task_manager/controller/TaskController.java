package com.example.ai_task_manager.controller;

import com.example.ai_task_manager.Service.TaskService;
import com.example.ai_task_manager.enums.Priority;
import com.example.ai_task_manager.enums.Status;
import com.example.ai_task_manager.model.Task;
import com.example.ai_task_manager.model.TaskHistory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable long id, @RequestBody Task task) {
        return ResponseEntity.ok(taskService.updateTask(id, task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok("Task Got Deleted");
    }

    @GetMapping("/{id}/aipriority")
    public ResponseEntity<Priority> suggestPriority(@PathVariable long id) {
        return ResponseEntity.ok(taskService.suggestPriority(id));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Task>> overdue() {
        return ResponseEntity.ok(taskService.getOverdueTasks());
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<TaskHistory>> history(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskHistory(id));
    }

    /**
     * 🔍 SEARCH + FILTER + SORT (status supported)
     */
    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchFilterSortTasks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) String tag,
            @RequestParam(defaultValue = "deadline") String sortBy
    ) {
        return ResponseEntity.ok(
                taskService.searchFilterSortTasks(keyword, status, priority, tag, sortBy)
        );
    }
}



