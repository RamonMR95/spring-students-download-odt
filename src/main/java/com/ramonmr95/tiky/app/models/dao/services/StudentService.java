package com.ramonmr95.tiky.app.models.dao.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ramonmr95.tiky.app.models.dao.IStudentDao;
import com.ramonmr95.tiky.app.models.entities.Student;

@Service
public class StudentService implements IStudentService {
	
	@Autowired
	private IStudentDao studentDao;
	
	@Override
	public Student findOne(Long id) {
		return this.studentDao.findById(id).orElse(null);
	}

	@Override
	public List<Student> findAll() {
		return (List<Student>) this.studentDao.findAll();
	}

	@Override
	public void save(Student alumno) {
		this.studentDao.save(alumno);
	}

	@Override
	public void delete(Long id) {
		this.studentDao.deleteById(id);
	}

}
