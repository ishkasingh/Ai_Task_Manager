package com.example.ai_task_manager.ServiceImpl;

import com.example.ai_task_manager.Repository.TaskHistoryRepository;
import com.example.ai_task_manager.Repository.TaskRepository;
import com.example.ai_task_manager.Service.TaskService;
import com.example.ai_task_manager.enums.Priority;
import com.example.ai_task_manager.enums.Status;
import com.example.ai_task_manager.model.Task;
import com.example.ai_task_manager.model.TaskHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepo;

    private final TaskHistoryRepository taskHistoryRepo;



    public TaskServiceImpl(TaskRepository taskRepo, TaskHistoryRepository taskHistoryRepo) {
        this.taskRepo = taskRepo;
        this.taskHistoryRepo = taskHistoryRepo;
    }

//    @Override
//    public Task createTask(Task task){
//        return taskRepo.save(task);
//    }

    @Override
    public List<Task> getAllTasks(){
        return taskRepo.findAll();
    }

    @Override
    public Task getTaskById(long id){
        return taskRepo.findById(id).orElseThrow(() -> new RuntimeException("Task Not Found with id: "+id));
    }

    @Override
    public void deleteTask(long id){
         taskRepo.deleteById(id);
    }

    @Override
    public Task createTask(Task task) {
        Task savedTask = taskRepo.save(task);

        // ✅ Initial history
        TaskHistory history = TaskHistory.builder()
                .task(savedTask)
                .oldStatus(null)
                .newStatus(savedTask.getStatus())
                .changedAt(LocalDateTime.now())
                .build();

        taskHistoryRepo.save(history);

        return savedTask;
    }

    @Override
    public Task updateTask(long id, Task task) {
        Task existingTask = getTaskById(id);

        // 🔴 Capture OLD status first
        Status oldStatus = existingTask.getStatus();
        Status newStatus = task.getStatus();

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setPriority(task.getPriority());
        existingTask.setStatus(newStatus);
        existingTask.setDueDate(task.getDueDate());
        existingTask.setTag(task.getTag());
        existingTask.setEstimatedTime(task.getEstimatedTime());

        Task updatedTask = taskRepo.save(existingTask);

        // ✅ Save history ONLY if status changed
        if (oldStatus != newStatus) {
            TaskHistory history = TaskHistory.builder()
                    .task(updatedTask)
                    .oldStatus(oldStatus)
                    .newStatus(newStatus)
                    .changedAt(LocalDateTime.now())
                    .build();

            taskHistoryRepo.save(history);
        }

        return updatedTask;
    }


    @Override
    public Priority suggestPriority(long id){
        Task task = getTaskById(id);
        String description = task.getDescription();
        if(description ==null || description.isBlank())
            return Priority.MEDIUM;

        description=description.toLowerCase();
        if(description.contains("urgent") || description.contains("asap")|| description.contains("deadline"))
            return Priority.HIGH;

        return Priority.MEDIUM;
    }
    @Override
    public List<Task> searchTasks(String keyword){
        if(keyword == null || keyword.isBlank())
            return taskRepo.findAll();

//        String key = keyword.toLowerCase();
//
//        return taskRepo.findAll()
//                .stream()
//                .filter(task ->
//                        (task.getTitle() != null &&
//                        task.getTitle().toLowerCase().contains(key) )   ||
//                (task.getDescription()!=null &&
//                        task.getDescription().toLowerCase().contains(key)))
//                .collect(Collectors.toList());
        return taskRepo
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        keyword, keyword);
    }


    @Override
    public List<Task> filterTasks(Status status, Priority priority,String tag){

//        return taskRepo.findAll()
//                .stream()
//                .filter(task -> status == null || task.getStatus() == status)
//                .filter(task -> priority == null || task.getPriority() == priority)
//                .filter(task-> tag == null || task.getTags().equalsIgnoreCase(tag))
//                .collect(Collectors.toList());
        return taskRepo.filterTasks(status, priority, tag);

    }
    @Override
    public List<Task> getOverdueTasks(){
        return taskRepo.findOverdueTasks();
    }
    @Override
    public List<TaskHistory> getTaskHistory(Long id){
        return taskHistoryRepo.findByTaskIdOrderByChangedAtDesc(id);
    }
    @Override
    public List<Task> searchFilterSortTasks(
            String keyword,
            Status status,
            Priority priority,
            String tag,
            String sortBy
    ) {

        List<Task> tasks = taskRepo.findAll();

        // 🔍 Filters
        if (keyword != null && !keyword.isBlank()) {
            tasks = tasks.stream()
                    .filter(t ->
                            (t.getTitle() != null && t.getTitle().toLowerCase().contains(keyword.toLowerCase())) ||
                                    (t.getDescription() != null && t.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                    )
                    .toList();
        }

        if (status != null) {
            tasks = tasks.stream()
                    .filter(t -> t.getStatus() == status)
                    .toList();
        }

        if (priority != null) {
            tasks = tasks.stream()
                    .filter(t -> t.getPriority() == priority)
                    .toList();
        }

        if (tag != null && !tag.isBlank()) {
            tasks = tasks.stream()
                    .filter(t -> t.getTag() != null && t.getTag().equalsIgnoreCase(tag))
                    .toList();
        }

        // ✅ Convert to MUTABLE list once
        List<Task> mutableTasks = new ArrayList<>(tasks);

        // 🔃 Sorting
        if ("priority".equalsIgnoreCase(sortBy)) {
            // HIGH → MEDIUM → LOW
            mutableTasks.sort(
                    (t1, t2) -> t2.getPriority().ordinal() - t1.getPriority().ordinal()
            );
        }
        else if ("status".equalsIgnoreCase(sortBy)) {
            // Custom business order
            mutableTasks.sort(
                    (t1, t2) -> t1.getStatus().ordinal() - t2.getStatus().ordinal()
            );
        }
        else if ("deadline".equalsIgnoreCase(sortBy)) {
            mutableTasks.sort(
                    (t1, t2) -> t1.getDueDate().compareTo(t2.getDueDate())
            );
        }

        return mutableTasks;
    }



}

