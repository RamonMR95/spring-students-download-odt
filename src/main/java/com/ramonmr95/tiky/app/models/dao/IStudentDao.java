package com.ramonmr95.tiky.app.models.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ramonmr95.tiky.app.models.entities.Student;

/**
 * Interfaz que utiliza el patrón dao que hereda de la interfaz CrudRepository
 * la cual contiene multitud de métodos de creación, lectura, actualización y
 * borrado de registros de base de datos.
 * 
 * @author Ramón Moñino Rubio - Antonio Ruiz Marín 2º DAM
 *
 */
@Repository
public interface IStudentDao extends CrudRepository<Student, Long> {

}
