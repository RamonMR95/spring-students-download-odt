package com.ramonmr95.tiky.app.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
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

	@GetMapping("/remove")
	public String delete(@RequestParam Long id, RedirectAttributes flash) {
		Student uno = studentService.findOne(id);
		if (uno.getId() == id) {
			try {
				this.studentService.delete(id);
				flash.addFlashAttribute("success", "Student delete success");
			} catch (Exception e)  {
				return "redirect:/list";
			}
		}
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
	
	@GetMapping("/student/download")
	public String generateWordStudent(Student student, @RequestParam Long id) {
		XWPFDocument doc = new XWPFDocument();
		
		XWPFParagraph paragraph = doc.createParagraph();
		XWPFRun run = paragraph.createRun();

		CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
		XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(doc, sectPr);
		XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);

		paragraph = header.getParagraphArray(0);
		if (paragraph == null) paragraph = header.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.RIGHT);
		
		run = paragraph.createRun();
		run.setFontSize(14);
		run.setFontFamily("Times New Roman");
		run.setBold(true);
		run.setText("Ramón Moñino Rubio	 -	Antonio Ruiz Marin");
		run.addCarriageReturn();
		run.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		
		
		paragraph = doc.createParagraph();
		run = paragraph.createRun();
		

		// convert buffered image to Input Stream
		
		BufferedImage screenFullImage;
		try {
			screenFullImage = ImageIO.read(new ByteArrayInputStream(student.getPhoto()));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(screenFullImage, "jpeg", baos);
			baos.flush();
			ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
			baos.close();
			
			// add image to word doc
			run.addBreak();
			run.addPicture(bis, XWPFDocument.PICTURE_TYPE_JPEG, "image file", Units.toEMU(180), Units.toEMU(150));
			bis.close();

		} catch (IOException | InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	
		
//		// create table in header
//		paragraph = header.createParagraph();
//		XmlCursor cursor = paragraph.getCTP().newCursor();
//		XWPFTable table = header.insertNewTbl(cursor);
//		XWPFTableRow row = table.getRow(0); if (row == null) row = table.createRow();
//		int twipsPerInch =  1440;
//		table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(6 * twipsPerInch));
//		for (int i = 0; i < 3; i++) {
//		XWPFTableCell cell = row.getCell(i); if (cell == null) cell = row.createCell();
//		CTTblWidth tblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
//		tblWidth.setW(BigInteger.valueOf(2 * twipsPerInch));
//		tblWidth.setType(STTblWidth.DXA);
//		paragraph = cell.getParagraphs().get(0);
//		run = paragraph.createRun();
//		run.setText("Header Table Cell " + i);
//		}

		// create footer start
		XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

		paragraph = footer.getParagraphArray(0);
		if (paragraph == null) paragraph = footer.createParagraph();
		paragraph.setAlignment(ParagraphAlignment.CENTER);

		run = paragraph.createRun();
		run.setText("The Footer:");

		try {
			doc.write(new FileOutputStream("CreateWordHeaderFooterTable.docx"));
			doc.close();
		} catch (IOException e) {
			return "redirect:/student?id=" + id;
		} 
		return "redirect:/student?id=" + id;
	}

}
