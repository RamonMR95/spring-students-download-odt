package com.ramonmr95.tiky.app.controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ramonmr95.tiky.app.models.dao.services.DownloadService;
import com.ramonmr95.tiky.app.models.dao.services.IStudentService;
import com.ramonmr95.tiky.app.models.entities.Student;

/**
 * Clase controladora que contiene todos los endpoints disponibles del estudiante 
 * con métodos http get, post, etc.
 * 
 * @author Ramón Moñino Rubio - Antonio Ruiz Marín 2º DAM
 *
 */
@Controller
@SessionAttributes("student")
public class StudentController {

	@Autowired
	private IStudentService studentService;
	
	@Autowired
	private DownloadService downloadService;

	private String fileExtension;
	
	/**
	 * Método que gestiona la petición get del endpoint /students y devuelve todos los estudiantes de la db
	 * 
	 * @param model - Clase de spring mvc usada para pasar atributos a la vista
	 * @return list - Vista que muestra todos los estudiantes de la db
	 */
	@GetMapping({"/students"})
	public String list(Model model) {
		List<Student> students = this.studentService.findAll();
		model.addAttribute("students", students);
		model.addAttribute("title", "List of Students");
		return "list";
	}

	/**
	 * Método que gestiona la petición post del endpoint /register y se encarga de guardar el estudiante en la db.
	 * 
	 * @param photo - Parámetro del request que contiene la foto del estudiante
	 * @param student - Objeto de tipo estudiante formado a través de los valores de los campos input con thymeleaf
	 * @param status - Objeto de tipo SessionStatus que nos ayudará a terminar con el atributo de session student
	 * @param model - Clase de spring mvc usada para pasar atributos a la vista
	 * @param flash - Clase de spring framework usada para crear mensajes en las vistas que duran una peticion de http
	 * @return redireccion a /students si sale todo bien, si no se queda en /register
	 */
	@PostMapping("/register")
	public String saveRegister(@RequestParam @ModelAttribute MultipartFile photo, @ModelAttribute Student student, 
			SessionStatus status, Model model, RedirectAttributes flash) {
		model.addAttribute("title", "List of Students");

		try {
			student.setPhoto(photo.getInputStream().readAllBytes());
			fileExtension = photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf(".") + 1);
			this.studentService.save(student);
			status.setComplete();
			flash.addFlashAttribute("success", "Student: " + student.getName() + " registered successfully");
		} 
		catch (IOException e) {
			flash.addFlashAttribute("error", "Error student register");
			return "register";
		}
		return "redirect:/students";
	}

	/**
	 * Método que se encarga del borrado de un estudiante dado su id
	 * 
	 * @param id - id del estudiante recibido por parametro de la petición http
	 * @param flash - Clase de spring framework usada para crear mensajes en las vistas que duran una peticion de http
	 * @return redireccion a la vista /students
	 */
	@GetMapping("/remove")
	public String delete(@RequestParam Long id, RedirectAttributes flash) {
		Student student = studentService.findOne(id);
		if (student.getId() == id) {
			try {
				this.studentService.delete(id);
				flash.addFlashAttribute("success", "Student: " + student.getName() + " has been deleted successfully");
			} 
			catch (Exception e)  {
				flash.addFlashAttribute("error", "Error removing the student");
				return "redirect:/students";
			}
		}
		return "redirect:/students";
	}

	/**
	 * Método que muestra en forma de vista detallada el estudiante dado su id
	 * 
	 * @param id - id del estudiante recibido por parametro de la petición http
	 * @param model - Clase de spring mvc usada para pasar atributos a la vista
	 * @param flash - Clase de spring framework usada para crear mensajes en las vistas que duran una peticion de http
	 * @return /student con la vista de detalle del estudiante si todo sale bien, si no, redirige a la lista de estudiantes
	 */
	@GetMapping("/student")
	public String showStudent(@RequestParam Long id, Model model, RedirectAttributes flash) {
		Student student = this.studentService.findOne(id);
		if (student == null) {
			flash.addFlashAttribute("error", "Student with ID: " + id + " does not exist in the db");
			return "redirect:/students";
		}

		byte[] image = { 0 };
		try {
			image = Base64.getEncoder().encode(student.getPhoto());
			model.addAttribute("image", new String(image, "UTF-8"));
			model.addAttribute("ext", fileExtension);

		} catch (Exception e) {
		}
		model.addAttribute("title", "Student: " + student.getName() + ", id: " + id);
		model.addAttribute("student", student);
		return "student";
	}

	/**
	 * Método que muestra el formulario de registro con endpoint /register
	 * 
	 * @param model - Clase de spring mvc usada para pasar atributos a la vista
	 * @return register - Vista de registro
	 */
	@GetMapping("/register")
	public String showRegister(Model model) {
		model.addAttribute("title", "Register one Student");
		model.addAttribute("student", new Student());
		return "register";

	}
	
	/**
	 * Método que mediante el uso del servicio downloadService obtiene el archivo docx con los datos del estudiante
	 * 
	 * @param student - Estudiante que quiere obtener el archivo docx
	 * @param flash - Clase de spring framework usada para crear mensajes en las vistas que duran una peticion de http
	 * @param resp - Respuesta del server que entrega el archivo al alumno
	 */
	@GetMapping("/student/download")
	public void generateWordStudent(Student student, RedirectAttributes flash, HttpServletResponse resp) {
		downloadService.download(student, flash, fileExtension, resp);
	}
	
}
