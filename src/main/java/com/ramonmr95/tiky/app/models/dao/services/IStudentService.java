package com.ramonmr95.tiky.app.models.dao.services;

import java.util.List;

import com.ramonmr95.tiky.app.models.entities.Student;

public interface IStudentService {
	
	public Student findOne(Long id);

	public List<Student> findAll();
	
	public void save(Student student);
	
	public void delete(Long id);
	
}
