package org.example.studentmanagement.service;

import org.example.studentmanagement.DTO.ImportResult;
import org.example.studentmanagement.models.Clazz;
import org.example.studentmanagement.models.Student;
import org.example.studentmanagement.models.StudentStatus;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ExcelImportService {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    @Autowired
    private StudentService studentService;

    @Autowired
    private ClassService classService;

    @Transactional
    public ImportResult importStudents(MultipartFile file) {
        ImportResult result = new ImportResult();

        if (file.isEmpty()) {
            result.addError("File không được để trống");
            return result;
        }

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            result.addError("Chỉ hỗ trợ file Excel (.xlsx)");
            return result;
        }

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Bỏ qua header row
            if (rows.hasNext()) {
                rows.next();
            }

            List<Student> validStudents = new ArrayList<>();
            int rowNum = 2; // Bắt đầu từ row 2 (sau header)

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                rowNum++;

                try {
                    Student student = parseStudentFromRow(currentRow, rowNum, result);
                    if (student != null) {
                        validStudents.add(student);
                    }
                } catch (Exception e) {
                    result.addError("Dòng " + rowNum + ": Lỗi không xác định - " + e.getMessage());
                }
            }

            // Batch insert các student hợp lệ - SỬA: sử dụng batchInsertStudents
            if (!validStudents.isEmpty()) {
                studentService.batchInsertStudents(validStudents); // SỬA: Thay vì saveAll
                result.setSuccessCount(validStudents.size());
            }

        } catch (IOException e) {
            result.addError("Lỗi đọc file: " + e.getMessage());
        } catch (Exception e) {
            result.addError("Lỗi hệ thống: " + e.getMessage());
            // Transaction sẽ rollback tự động
            throw new RuntimeException("Import thất bại", e);
        }

        return result;
    }

    private Student parseStudentFromRow(Row row, int rowNum, ImportResult result) {
        if (isRowEmpty(row)) {
            return null;
        }

        Student student = new Student();
        List<String> errors = new ArrayList<>();

        // Name (cột 0)
        String name = getCellValue(row.getCell(0));
        if (name == null || name.trim().isEmpty()) {
            errors.add("Tên sinh viên không được để trống");
        } else if (name.length() > 100) {
            errors.add("Tên sinh viên không quá 100 ký tự");
        } else {
            student.setName(name.trim());
        }

        // Code (cột 1)
        String code = getCellValue(row.getCell(1));
        if (code == null || code.trim().isEmpty()) {
            errors.add("Mã sinh viên không được để trống");
        } else if (code.length() > 20) {
            errors.add("Mã sinh viên không quá 20 ký tự");
        } else if (studentService.isCodeExists(code.trim())) {
            errors.add("Mã sinh viên đã tồn tại");
        } else {
            student.setCode(code.trim());
        }

        // Email (cột 2)
        String email = getCellValue(row.getCell(2));
        if (email != null && !email.trim().isEmpty()) {
            if (!isValidEmail(email.trim())) {
                errors.add("Email không hợp lệ");
            } else {
                student.setEmail(email.trim());
            }
        }

        // Status (cột 3)
        String statusStr = getCellValue(row.getCell(3));
        if (statusStr == null || statusStr.trim().isEmpty()) {
            student.setStatus(StudentStatus.ACTIVE); // Mặc định
        } else {
            try {
                StudentStatus status = StudentStatus.valueOf(statusStr.trim().toUpperCase());
                student.setStatus(status);
            } catch (IllegalArgumentException e) {
                errors.add("Trạng thái không hợp lệ (ACTIVE/INACTIVE)");
            }
        }

        // Class (cột 4)
        String className = getCellValue(row.getCell(4));
        if (className != null && !className.trim().isEmpty()) {
            Optional<Clazz> clazzOpt = findClassByName(className.trim());
            if (clazzOpt.isPresent()) {
                student.setClazz(clazzOpt.get());
            } else {
                errors.add("Lớp không tồn tại: " + className);
            }
        }

        if (errors.isEmpty()) {
            return student;
        } else {
            result.addError("Dòng " + rowNum + ": " + String.join(", ", errors));
            return null;
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Chuyển số thành string (tránh scientific notation)
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception ex) {
                        return null;
                    }
                }
            default:
                return null;
        }
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (int cellNum = 0; cellNum < 5; cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private Optional<Clazz> findClassByName(String className) {
        return classService.getAllClasses().stream()
                .filter(clazz -> clazz.getName().equalsIgnoreCase(className))
                .findFirst();
    }
}