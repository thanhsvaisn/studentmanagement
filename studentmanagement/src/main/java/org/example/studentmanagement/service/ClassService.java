package org.example.studentmanagement.service;

import org.example.studentmanagement.models.Clazz;
import org.example.studentmanagement.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassService {

    @Autowired
    private ClassRepository clazzRepository;

    public List<Clazz> getAllClasses(){
        return clazzRepository.findAll();
    }
    public Clazz save(Clazz clazz){
        return clazzRepository.save(clazz);
    }

}
