package com.ramonmr95.tiky.app.controllers;

import java.util.List;

import javax.validation.Valid;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ramonmr95.tiky.app.models.dao.services.IStudentService;
import com.ramonmr95.tiky.app.models.entities.Student;

@Controller
@SessionAttributes("student")
public class StudentController {

	@Autowired
	private IStudentService studentService;

	@GetMapping({"/list", "", "/"})
	public String list(Model model) {
		List<Student> students = this.studentService.findAll();
		model.addAttribute("students", students);
		model.addAttribute("title", "List of Students");
		return "list";
	}

	@PostMapping("/register")
	public String saveRegister(@Valid @ModelAttribute Student student, BindingResult result, SessionStatus status,
			Model model, RedirectAttributes flash) {
		model.addAttribute("title", "List of Students");

		if (!result.hasErrors()) {
			flash.addFlashAttribute("success", "Student register success");
			this.studentService.save(student);
			status.setComplete();
			return "redirect:/list";
		}
		return "register";
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
