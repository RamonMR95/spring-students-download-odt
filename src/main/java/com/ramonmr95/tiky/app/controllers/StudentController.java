package com.ramonmr95.tiky.app.controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ramonmr95.tiky.app.models.dao.services.IStudentService;
import com.ramonmr95.tiky.app.models.entities.Student;

@Controller
@SessionAttributes("student")
public class StudentController {

	@Autowired
	private IStudentService studentService;

	private String extension;

	@GetMapping({ "/list", "", "/" })
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
			extension = photo.getOriginalFilename().substring(photo.getOriginalFilename().lastIndexOf(".") + 1);
			flash.addFlashAttribute("success", "Student register success");
			this.studentService.save(student);
			status.setComplete();
		} 
		catch (IOException e) {
			return "register";
		}
		return "redirect:/list";
	}

	@RequestMapping("/remove")
	public String delete(@RequestParam Long id, RedirectAttributes flash) {
		this.studentService.delete(id);
		flash.addFlashAttribute("success", "Student delete success");
		return "redirect:/list";
	}

	@GetMapping("/student")
	public String showStudent(@RequestParam Long id, Model model) {
		Student student = this.studentService.findOne(id);
		if (student == null) {
			return "redirect:/list";
		}

		byte[] image = { 0 };
		try {
			image = Base64.getEncoder().encode(student.getPhoto());
			model.addAttribute("image", new String(image, "UTF-8"));
			model.addAttribute("ext", extension);

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

}
