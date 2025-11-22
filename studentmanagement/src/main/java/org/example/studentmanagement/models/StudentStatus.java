package org.example.studentmanagement.models;

import lombok.Getter;

@Getter
public enum StudentStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private String displayName;

    StudentStatus(String displayName) {
        this.displayName = displayName;
    }

    // THÊM: Phương thức để lấy enum từ string
    public static StudentStatus fromString(String text) {
        for (StudentStatus type : StudentStatus.values()) {
            if (type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}