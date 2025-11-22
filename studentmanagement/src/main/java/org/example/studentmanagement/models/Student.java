package org.example.studentmanagement.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 100, message = "Độ dài tối đa là 100 ký tự")
    @NotBlank(message = "Tên sinh viên không được để trống")
    private String name;


    @Enumerated(EnumType.STRING)
    private StudentStatus status;

    @Email(message = "Email không hợp lệ")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private Clazz clazz;

    @Column(unique = true)
    @Size(max = 20, message = "Mã sinh viên không quá 20 ký tự")
    @NotBlank(message = "Mã sinh viên không được để trống")
    private String code;
}
