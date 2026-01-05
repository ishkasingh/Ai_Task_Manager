package com.example.ai_task_manager.model;

import com.example.ai_task_manager.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Enumerated(EnumType.STRING)
    private Status oldStatus;

    @Enumerated(EnumType.STRING)
    private Status newStatus;

    private LocalDateTime changedAt;

    private TaskHistory(Builder builder) {
        this.task = builder.task;
        this.oldStatus = builder.oldStatus;
        this.newStatus = builder.newStatus;
        this.changedAt = builder.changedAt;
    }

    // static builder() method
    public static Builder builder() {
        return new Builder();
    }

    // Builder class
    public static class Builder {

        private Task task;
        private Status oldStatus;
        private Status newStatus;
        private LocalDateTime changedAt;

        public Builder task(Task task) {
            this.task = task;
            return this;
        }

        public Builder oldStatus(Status oldStatus) {
            this.oldStatus = oldStatus;
            return this;
        }

        public Builder newStatus(Status newStatus) {
            this.newStatus = newStatus;
            return this;
        }

        public Builder changedAt(LocalDateTime changedAt) {
            this.changedAt = changedAt;
            return this;
        }

        public TaskHistory build() {
            return new TaskHistory(this);
        }
    }

        public TaskHistory(LocalDateTime changedAt, Long id, Status newStatus, Status oldStatus, Task task) {
            this.changedAt = changedAt;
            this.id = id;
            this.newStatus = newStatus;
            this.oldStatus = oldStatus;
            this.task = task;
        }

        public LocalDateTime getChangedAt() {
            return changedAt;
        }

        public void setChangedAt(LocalDateTime changedAt) {
            this.changedAt = changedAt;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Status getNewStatus() {
            return newStatus;
        }

        public void setNewStatus(Status newStatus) {
            this.newStatus = newStatus;
        }

        public Status getOldStatus() {
            return oldStatus;
        }

        public void setOldStatus(Status oldStatus) {
            this.oldStatus = oldStatus;
        }

        public Task getTask() {
            return task;
        }

        public void setTask(Task task) {
            this.task = task;
        }
    }


