package org.example.studentmanagement.service;
import org.example.studentmanagement.models.Student;
import org.example.studentmanagement.models.StudentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.example.studentmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service

public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }
    public Student save(Student student) {
        return studentRepository.save(student);
    }
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }
    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    public List<Student> filterStudents(String name, String code) {
        if (name != null && code != null && !name.isEmpty() && !code.isEmpty()) {
            return studentRepository.findByNameContainingAndCodeContaining(name, code);
        } else if (name != null && !name.isEmpty()) {
            return studentRepository.findByNameContainingIgnoreCase(name);
        } else if (code != null && !code.isEmpty()) {
            return studentRepository.findByCodeContainingIgnoreCase(code);
        } else  {
            return studentRepository.findAll();
        }
    }
    @Transactional
    public void updateStatus(List<Long> StudentIds, StudentType status) {
        List<Student> students = studentRepository.findAllById(StudentIds);
        for (Student student : students){
            student.setStatus(status);
        }
        studentRepository.saveAll(students);
    }
    public boolean isCodeExist(String code) {
        return studentRepository.findByCode(code).isPresent();
    }
    public boolean isCodeExist(String code, Long excludeId) {
        Optional<Student> student =  studentRepository.findByCode(code);
        return student.isPresent() && !student.get().getId().equals(excludeId);
    }

}