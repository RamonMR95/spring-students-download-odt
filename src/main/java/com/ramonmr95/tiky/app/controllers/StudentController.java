package com.ramonmr95.tiky.app.controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

@Controller
@SessionAttributes("student")
public class StudentController {

	@Autowired
	private IStudentService studentService;
	
	@Autowired
	private DownloadService downloadService;

	private String fileExtension;
	
	@GetMapping({"/students"})
	public String list(Model model) {
		List<Student> students = this.studentService.findAll();
		model.addAttribute("students", students);
		model.addAttribute("title", "List of Students");
		return "list";
	}

	@PostMapping("/register")
	public String saveRegister(@RequestParam @ModelAttribute MultipartFile photo, @ModelAttribute Student student,
			BindingResult result, SessionStatus status, Model model, RedirectAttributes flash) {
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

	@GetMapping("/register")
	public String showRegister(Model model) {
		model.addAttribute("title", "Register one Student");
		model.addAttribute("student", new Student());
		return "register";

	}
	
	@GetMapping("/student/download")
	public void generateWordStudent(Student student, RedirectAttributes flash, HttpServletResponse resp) {
		downloadService.download(student, flash, fileExtension, resp);
	}
	
}
