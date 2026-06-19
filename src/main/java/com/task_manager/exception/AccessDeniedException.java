package com.task_manager.exception;

//Custom exception — distinct from Spring Security's built-in AccessDeniedException
//Used for resource-level ownership checks (e.g. user updating someone else's task)
public class AccessDeniedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    public AccessDeniedException(String message) {
        super(message);
    }
}
