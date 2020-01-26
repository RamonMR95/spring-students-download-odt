package com.ramonmr95.tiky.app.models.dao.services;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ramonmr95.tiky.app.models.dao.IStudentDao;
import com.ramonmr95.tiky.app.models.entities.Student;

/**
 * Clase que sigue el patrón façade que contiene todos los métodos CRUD de estudiante.
 * 
 * @author Ramón Moñino Rubio - Antonio Ruiz Marín 2º DAM
 */
@Service
public class StudentService implements IStudentService {
	
	@Autowired
	private IStudentDao studentDao;
	
	@Override
	@Transactional(readOnly = true)
	public Student findOne(Long id) {
		return this.studentDao.findById(id).orElse(new Student());
	}

	@Override
	@Transactional(readOnly = true)
	public List<Student> findAll() {
		return (List<Student>) this.studentDao.findAll();
	}

	@Override
	@Transactional
	public void save(Student alumno) {
		this.studentDao.save(alumno);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		this.studentDao.deleteById(id);
	}

}
