package com.example.ai_task_manager.Service;

import com.example.ai_task_manager.enums.Priority;
import com.example.ai_task_manager.enums.Status;
import com.example.ai_task_manager.model.Task;
import com.example.ai_task_manager.model.TaskHistory;

import java.util.List;

public interface TaskService {

    Task createTask(Task task);

    List<Task> getAllTasks();

    Task getTaskById(long id);

    Task updateTask(long id, Task task);

    void deleteTask(long id);

    Priority suggestPriority(long id);

   List<Task> searchTasks(String keyword);

    List<Task> filterTasks(Status status, Priority priority, String tags);

    List<Task> getOverdueTasks();

    List<TaskHistory> getTaskHistory(Long id);

    List<Task> searchFilterSortTasks(String keyword, Status status, Priority priority, String tag, String sortBy);
}
