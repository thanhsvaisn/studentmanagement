package org.example.studentmanagement.DTO;

import java.util.ArrayList;
import java.util.List;

public class ImportResult {
    private int successCount;
    private int errorCount;
    private List<String> errorMessages;

    public ImportResult() {
        this.errorMessages = new ArrayList<>();
    }

    public void addSuccess() {
        this.successCount++;
    }

    public void addError(String errorMessage) {
        this.errorCount++;
        this.errorMessages.add(errorMessage);
    }

    // Getters and Setters
    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }
    public int getErrorCount() { return errorCount; }
    public void setErrorCount(int errorCount) { this.errorCount = errorCount; }
    public List<String> getErrorMessages() { return errorMessages; }
    public void setErrorMessages(List<String> errorMessages) { this.errorMessages = errorMessages; }
}