package org.example.studentmanagement.repository;

import org.example.studentmanagement.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {
    List<Student> findByNameContainingIgnoreCase(String name);

    List<Student> findByCodeContainingIgnoreCase(String code);

    @Query("SELECT s FROM Student s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(s.code) LIKE LOWER(CONCAT('%', :code, '%'))")
    List<Student> findByNameContainingAndCodeContaining(@Param("name") String name, @Param("code") String code);

    Optional<Student> findByCode(String code);

    List<Student> findByClazzId(Long clazzId);
}
