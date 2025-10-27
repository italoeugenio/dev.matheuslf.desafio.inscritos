package dev.matheuslf.desafio.inscritos.enums;

public enum TaskPriority {
    LOW("Low priority - not urgent"),
    MEDIUM("Medium priority - normal"),
    HIGH("High priority - urgent");

    private String description;

    TaskPriority(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }


}
