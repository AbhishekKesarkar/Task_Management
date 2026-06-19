package com.task_manager.service;

import com.task_manager.dto.request.TaskCreateRequest;
import com.task_manager.dto.request.TaskStatusUpdateRequest;
import com.task_manager.dto.request.TaskUpdateRequest;
import com.task_manager.dto.response.TaskResponse;
import com.task_manager.entity.Task;
import com.task_manager.entity.User;
import com.task_manager.entity.enums.Role;
import com.task_manager.exception.AccessDeniedException;
import com.task_manager.exception.ResourceNotFoundException;
import com.task_manager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    // ADMIN only — creates and assigns a task
    public TaskResponse createTask(TaskCreateRequest request, String adminUsername) {
        User admin = userService.getByUsername(adminUsername);
        User assignedUser = userService.getById(request.getAssignedUserId());

        Task task = new Task(
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getDueDate(),
                assignedUser,
                admin
        );

        Task saved = taskRepository.save(task);
        return mapToResponse(saved);
    }

    // ADMIN only — view every task in the system
    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // USER — view only tasks assigned to them
    public List<TaskResponse> getMyTasks(String username) {
        User user = userService.getByUsername(username);
        return taskRepository.findByAssignedUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // USER updates status of their own task; ADMIN can update any task's status too
    public TaskResponse updateStatus(Long taskId, TaskStatusUpdateRequest request, String username) {
        Task task = getTaskOrThrow(taskId);
        User currentUser = userService.getByUsername(username);

        if (!isOwner(task, currentUser) && currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("You can only update your own tasks");
        }

        task.setStatus(request.getStatus());
        Task updated = taskRepository.save(task);
        return mapToResponse(updated);
    }

    // ADMIN only — full edit (title, description, priority, due date, reassign)
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request) {
        Task task = getTaskOrThrow(taskId);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        if (request.getAssignedUserId() != null) {
            User newAssignee = userService.getById(request.getAssignedUserId());
            task.setAssignedUser(newAssignee);
        }

        Task updated = taskRepository.save(task);
        return mapToResponse(updated);
    }

    // ADMIN only — delete a task
    public void deleteTask(Long taskId) {
        Task task = getTaskOrThrow(taskId);
        taskRepository.delete(task);
    }

    // Used for "Overdue" stat card
    public List<TaskResponse> getOverdueTasksForUser(String username) {
        User user = userService.getByUsername(username);
        return taskRepository.findOverdueTasksForUser(user, LocalDate.now())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Task getTaskOrThrow(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
    }

    private boolean isOwner(Task task, User user) {
        return task.getAssignedUser().getId().equals(user.getId());
    }

    private TaskResponse mapToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setAssignedUserId(task.getAssignedUser().getId());
        response.setAssignedUserName(task.getAssignedUser().getFullName());
        response.setCreatedByName(task.getCreatedBy().getFullName());
        return response;
    }
}
