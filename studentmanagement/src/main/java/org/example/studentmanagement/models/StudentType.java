package org.example.studentmanagement.models;

import lombok.Getter;

@Getter
public enum StudentType {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private String displayName;

    StudentType(String displayName) {
        this.displayName = displayName;
    }

    // THÊM: Phương thức để lấy enum từ string
    public static StudentType fromString(String text) {
        for (StudentType type : StudentType.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}