package com.task_manager.controller;

import com.task_manager.dto.request.TaskCreateRequest;
import com.task_manager.dto.request.TaskStatusUpdateRequest;
import com.task_manager.dto.request.TaskUpdateRequest;
import com.task_manager.dto.response.TaskResponse;
import com.task_manager.service.TaskService;
import com.task_manager.util.ApiResponse;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // ADMIN only — create and assign a task
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TaskResponse>> createTask(
            @Valid @RequestBody TaskCreateRequest request,
            Authentication authentication) {
        TaskResponse response = taskService.createTask(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Task created successfully", response));
    }

    // ADMIN only — view every task in the system
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TaskResponse>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    // USER (and ADMIN) — view tasks assigned to the logged-in user
    @GetMapping("/my")
    public ResponseEntity<List<TaskResponse>> getMyTasks(Authentication authentication) {
        return ResponseEntity.ok(taskService.getMyTasks(authentication.getName()));
    }

    // USER — overdue tasks assigned to them (for dashboard stat card)
    @GetMapping("/my/overdue")
    public ResponseEntity<List<TaskResponse>> getMyOverdueTasks(Authentication authentication) {
        return ResponseEntity.ok(taskService.getOverdueTasksForUser(authentication.getName()));
    }

    // USER updates status of own task; ownership check happens in the service layer
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long id,
             @RequestBody TaskStatusUpdateRequest request,
            Authentication authentication) {
        TaskResponse response = taskService.updateStatus(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    // ADMIN only — full edit (title, description, priority, due date, reassign)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
             @RequestBody TaskUpdateRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    // ADMIN only — delete a task
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
