package org.example.studentmanagement.controller;


import org.example.studentmanagement.models.Clazz;
import org.example.studentmanagement.models.Student;
import org.example.studentmanagement.models.StudentType;
import org.example.studentmanagement.service.ClazzService;
import org.example.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private ClazzService clazzService;

    @GetMapping
    public String listStudent(@RequestParam(required = false) String name, @RequestParam(required = false) String surname, @RequestParam(required = false) String code, Model model) {
        List<Student> students = studentService.filterStudents(name, code);
        model.addAttribute("students", students);
        model.addAttribute("nameFilter", name);
        model.addAttribute("codeFilter", code);
        return "student-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {

        model.addAttribute("student",new Student());
        model.addAttribute("classes", clazzService.getAllClazz());
        model.addAttribute("statuses", StudentType.values());
        return "student-form";
    }

    @PostMapping("/new")
    public String createStudent(@Valid @ModelAttribute Student student, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (studentService.isCodeExist(student.getCode())) {
            bindingResult.rejectValue("code", "error.exists", "Mã sinh viên đã tồn tại");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("classes", clazzService.getAllClazz());
            model.addAttribute("statuses", StudentType.values());
            return "student-form";
        }
        studentService.save(student);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm sinh viên thành công!");
            return "redirect:/students";
    }
    @PostMapping("/update-status")
    public String updateStatus(@RequestParam List<Long> studentIds, @RequestParam StudentType status, RedirectAttributes redirectAttributes) {
        if(studentIds == null || studentIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn ít nhất một sinh viên");

        } else {
            studentService.updateStatus(studentIds, status);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhập trạng thái thành công " + studentIds.size() + "sinh viên");

        }
        return "redirect:/students";
    }

}
