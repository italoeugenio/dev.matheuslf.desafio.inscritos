package dev.matheuslf.desafio.inscritos.enums;

public enum TaskStatus {
    TODO("Task not started yet"),
    DOIN("Task in progress"),
    DONE("Task completed");

    private String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
