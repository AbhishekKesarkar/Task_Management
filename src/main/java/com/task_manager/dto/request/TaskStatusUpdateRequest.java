package com.task_manager.dto.request;

import com.task_manager.entity.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;

// USER (and ADMIN) — used to move a task between TODO / IN_PROGRESS / DONE
public class TaskStatusUpdateRequest {

    @NotNull(message = "Status is required")
    private TaskStatus status;

    public TaskStatusUpdateRequest() {
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
