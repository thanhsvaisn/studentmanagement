package org.example.studentmanagement.controller;


import org.example.studentmanagement.DTO.ImportResult;
import org.example.studentmanagement.models.Student;
import org.example.studentmanagement.models.StudentStatus;
import org.example.studentmanagement.service.ClassService;
import org.example.studentmanagement.service.ExcelImportService;
import org.example.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private ClassService clazzService;

    @Autowired
    private ExcelImportService excelImportService;

    @GetMapping("/import")
    public String showImportForm(Model model) {
        return "student-import";
    }
    @PostMapping("/import")
    public String importStudents(@RequestParam("file") MultipartFile file,
                                 RedirectAttributes redirectAttributes) {
        try {
            ImportResult result = excelImportService.importStudents(file);

            if (result.getErrorCount() > 0) {
                redirectAttributes.addFlashAttribute("errorMessages", result.getErrorMessages());
            }

            if (result.getSuccessCount() > 0) {
                redirectAttributes.addFlashAttribute("successMessage",
                        "Import thành công " + result.getSuccessCount() + " sinh viên");
            }

            if (result.getSuccessCount() == 0 && result.getErrorCount() == 0) {
                redirectAttributes.addFlashAttribute("errorMessage",
                        "Không có dữ liệu nào được import");
            }

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Lỗi import: " + e.getMessage());
        }

        return "redirect:/students";
    }

    @GetMapping
    public String listStudent(@RequestParam(required = false) String name, @RequestParam(required = false) String code, Model model) {
        List<Student> students = studentService.filterStudents(name, code);
        model.addAttribute("students", students);
        model.addAttribute("nameFilter", name);
        model.addAttribute("codeFilter", code);
        return "student-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("classes", clazzService.getAllClasses());
        model.addAttribute("statuses", StudentStatus.values());
        return "student-form";
    }

    @PostMapping("/new")
    public String createStudent(@Valid @ModelAttribute Student student, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (studentService.isCodeExists(student.getCode())) {
            bindingResult.rejectValue("code", "error.exists", "Mã sinh viên đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("classes", clazzService.getAllClasses());
            model.addAttribute("statuses", StudentStatus.values());
            return "student-form";
        }
        studentService.save(student);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm sinh viên thành công!");
            return "redirect:/students";
    }
    @PostMapping("/update-status")
    public String updateStatus(@RequestParam List<Long> studentIds, @RequestParam StudentStatus status, RedirectAttributes redirectAttributes) {
        if(studentIds == null || studentIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn ít nhất một sinh viên");

        } else {
            studentService.updateStatus(studentIds, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái thành công " + studentIds.size() + " sinh viên");

        }
        return "redirect:/students";
    }

}
