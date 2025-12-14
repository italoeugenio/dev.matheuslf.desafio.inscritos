package dev.matheuslf.desafio.inscritos.enums;

public enum UserRole {
    ADMIN("Admin"),
    PM("Project Manager"),
    DEV("Developer"),
    VIEWER("Viewer");

    private String role;

    UserRole(String role){
        this.role = role;
    }

    public String getRole(){
        return role;
    }
}
