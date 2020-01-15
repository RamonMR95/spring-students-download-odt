package com.ramonmr95.tiky.app.models.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ramonmr95.tiky.app.models.entities.Student;

@Repository
public interface IStudentDao extends CrudRepository<Student, Long>{

}
