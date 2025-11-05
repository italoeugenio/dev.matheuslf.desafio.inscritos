package dev.matheuslf.desafio.inscritos.config.swagger.docs.project;

public class ProjectApiDocConstants {
    public static final String CREATE_PROJECT_SUMMARY = "Create a new project";
    public static final String CREATE_PROJECT_DESC = "Creates a new project with the provided information. The start date must be in the future and the end date must be after the start date.";
    public static final String GET_ALL_PROJECTS_SUMMARY = "Get all projects with pagination";
    public static final String GET_ALL_PROJECTS_DESC = "Retrieves a paginated list of all projects. You can control the page size, page number, and sorting. Use 'page' for page number (starts at 0), 'size' for items per page, and 'sort' for ordering (e.g., sort=startDate,desc).";
    public static final String GET_PROJECT_BY_ID_SUMMARY = "Get a project by ID";
    public static final String DELETE_PROJECT_SUMMARY = "Delete a project by ID";
    public static final String DELETE_PROJECT_DESC = "Deletes the project by its ID. However, if you delete a project that has tasks all those tasks will be removed";
    public static final String UPDATE_PROJECT_SUMMARY = "Update an existing project";
    public static final String UPDATE_PROJECT_DESC = "Updates all information of an existing project. The start date must be in the future and the end date must be after the start date.";
    public static final String RESPONSE_201_CREATED = "Project created";
    public static final String RESPONSE_200_OK = "Project updated successfully";
    public static final String RESPONSE_200_RETRIEVED = "Projects retrieved successfully";
    public static final String RESPONSE_200_FOUND = "You can find a project with all it´s informations by ID";
    public static final String RESPONSE_204_DELETED = "Project deleted successfully";
    public static final String RESPONSE_400_INVALID = "Invalid project data - Check if dates are valid";
    public static final String RESPONSE_404_NOT_FOUND = "Project not found with this ID";
}
