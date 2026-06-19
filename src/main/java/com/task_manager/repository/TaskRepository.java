package com.task_manager.repository;

import com.task_manager.entity.Task;
import com.task_manager.entity.User;
import com.task_manager.entity.enums.Priority;
import com.task_manager.entity.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // USER: tasks assigned to them
    List<Task> findByAssignedUser(User assignedUser);

    List<Task> findByAssignedUserAndStatus(User assignedUser, TaskStatus status);

    // ADMIN: tasks they created
    List<Task> findByCreatedBy(User createdBy);

    // Filter helpers — usable by both ADMIN (all tasks) and USER (their own)
    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(Priority priority);

    List<Task> findByAssignedUserAndPriority(User assignedUser, Priority priority);

    // Overdue tasks: due date passed but not yet DONE
    @Query("SELECT t FROM Task t WHERE t.dueDate < :today AND t.status <> 'DONE'")
    List<Task> findOverdueTasks(@Param("today") LocalDate today);

    // Overdue tasks for a specific user (used on User dashboard)
    @Query("SELECT t FROM Task t WHERE t.assignedUser = :user AND t.dueDate < :today AND t.status <> 'DONE'")
    List<Task> findOverdueTasksForUser(@Param("user") User user, @Param("today") LocalDate today);

    // Counts for dashboard stat cards
    long countByAssignedUser(User assignedUser);

    long countByAssignedUserAndStatus(User assignedUser, TaskStatus status);

    long countByStatus(TaskStatus status);
}