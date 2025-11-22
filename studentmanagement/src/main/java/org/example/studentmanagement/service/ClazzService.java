package org.example.studentmanagement.service;

import org.example.studentmanagement.models.Clazz;
import org.example.studentmanagement.repository.ClazzRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClazzService {

    @Autowired
    private ClazzRepository clazzRepository;

    public List<Clazz> getAllClazz(){
        return clazzRepository.findAll();
    }
    public Clazz save(Clazz clazz){
        return clazzRepository.save(clazz);
    }

}
