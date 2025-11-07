package dev.matheuslf.desafio.inscritos.config.swagger.docs.tasks;

public final class TaskApiDocConstants {
    public static final String CREATE_TASK_SUMMARY = "Create a new task";
    public static final String CREATE_TASK_DESC = "Creates a new task with the provided information. The task due date must be between the project's start and end dates.";
    public static final String GET_ALL_TASKS_SUMMARY = "Get all tasks";
    public static final String GET_ALL_TASKS_DESC = "Retrieves a list of all tasks with their details including project information.";
    public static final String GET_TASK_BY_ID_SUMMARY = "Get a task by ID";
    public static final String GET_TASK_BY_ID_DESC = "Retrieves detailed information about a specific task by its unique identifier.";
    public static final String DELETE_TASK_SUMMARY = "Delete a task by ID";
    public static final String DELETE_TASK_DESC = "Permanently deletes a task from the system using its unique identifier.";
    public static final String FIND_TASKS_SUMMARY = "Find tasks with filters";
    public static final String FIND_TASKS_DESC = "Search for tasks using optional filters: status, priority, and/or project ID. All parameters are optional and can be combined.";
    public static final String UPDATE_TASK_SUMMARY = "Update an existing task";
    public static final String UPDATE_TASK_DESC = "Updates all information of an existing task. The due date must still be between the project's start and end dates.";
    public static final String UPDATE_TASK_STATUS_SUMMARY = "Update task status";
    public static final String UPDATE_TASK_STATUS_DESC = "Updates only the status of an existing task without modifying other fields.";
    public static final String RESPONSE_200_OK = "Task updated successfully";
    public static final String RESPONSE_200_RETRIEVED = "Tasks retrieved successfully";
    public static final String RESPONSE_200_FOUND = "Task found with all its information";
    public static final String RESPONSE_201_CREATED = "Task created successfully";
    public static final String RESPONSE_204_DELETED = "Task deleted successfully";
    public static final String RESPONSE_400_INVALID = "Invalid task data - Check if the due date is between the project's start and end dates";
    public static final String RESPONSE_404_NOT_FOUND = "Task not found with this ID";
    public static final String RESPONSE_404_PROJECT_NOT_FOUND = "Project not found with the provided ID";
}