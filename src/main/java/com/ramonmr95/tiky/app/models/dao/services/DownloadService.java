package com.ramonmr95.tiky.app.models.dao.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ramonmr95.tiky.app.models.entities.IMAGE_TYPES;
import com.ramonmr95.tiky.app.models.entities.Student;


/**
 * Servicio encargado de la creación y descarga del archivo docx del estudiante
 * 
 * @author Ramón Moñino Rubio - Antonio Ruiz Marín 2º DAM
 *
 */
@Service
public class DownloadService {

	/**
	 * Método que se encarga de la descarga de la documentación del estudiante
	 * 
	 * @param student - Estudiante que requiere el documento
	 * @param flash - Clase de spring framework usada para crear mensajes en las vistas que duran una peticion de http
	 * @param fileExtension - Extensión del tipo de la imagen
	 * @param resp - Respuesta Http que devuelve el servidor
	 */
	public void download(Student student, RedirectAttributes flash, String fileExtension, HttpServletResponse resp) {
		XWPFDocument doc = new XWPFDocument();
		XWPFParagraph paragraph = doc.createParagraph();
		XWPFRun run = paragraph.createRun();
		CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
		XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(doc, sectPr);
		
		createHeader(doc, paragraph, run, headerFooterPolicy);
		createBody(doc, paragraph, run, student, fileExtension, flash);
		createFooter(paragraph, run, headerFooterPolicy);
		getWord(resp, doc, student, flash);
		
	}
	
	/**
	 * Método que se encarga de la creación del header del documento docx
	 * 
	 * @param doc - Objeto documento
	 * @param paragraph - Párafo del documento
	 * @param run - Objeto que hace referencia al párrafo en el que nos encontramos
	 * @param headerFooterPolicy - Objeto necesario para crear el header y footer. 
	 */
	private void createHeader(XWPFDocument doc, XWPFParagraph paragraph, XWPFRun run, XWPFHeaderFooterPolicy headerFooterPolicy) {
		XWPFHeader header = headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT);
		
		paragraph = header.getParagraphArray(0);

		if (paragraph == null)
			paragraph = header.createParagraph();

		paragraph.setAlignment(ParagraphAlignment.RIGHT);

		run = paragraph.createRun();
		run.setFontSize(14);
		run.setFontFamily("Times New Roman");
		run.setBold(true);
		run.setText("Ramón Moñino Rubio	 -	Antonio Ruiz Marín");
		run.addCarriageReturn();
		run.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
	}
	
	/**
	 * Método que se encarga de la creación del cuerpo del documento docx
	 * 
	 * @param doc - Objeto documento
	 * @param paragraph - Párafo del documento
	 * @param run - Objeto que hace referencia al párrafo en el que nos encontramos
	 * @param student - Estudiante que requiere el documento
	 * @param fileExtension - Extensión del tipo de la imagen
	 * @param flash - Clase de spring framework usada para crear mensajes en las vistas que duran una peticion de http
	 */
	private void createBody(XWPFDocument doc, XWPFParagraph paragraph, XWPFRun run, Student student, String fileExtension, RedirectAttributes flash) {
		paragraph = doc.createParagraph();
		run = paragraph.createRun();

		if (student.getPhoto() != null && student.getPhoto().length > 1) {
			addPictureWord(run, student, fileExtension);
			run.addBreak();
			paragraph = doc.createParagraph();
		} else {
			flash.addFlashAttribute("warning", "Word generated with no photo by: " + student.getName());
		}

		XmlCursor cursor = paragraph.getCTP().newCursor();
		createWordTable(doc, student, cursor);
	}

	/**
	 * Metodo que se encarga de crear la tabla con los datos del estudiante.
	 * 
	 * @param doc - Objeto documento
	 * @param student - Estudiante que requiere el documento
	 * @param cursor - Puntero que hace referencia a la posición actual
	 */
	private void createWordTable(XWPFDocument doc, Student student, XmlCursor cursor) {
		XWPFParagraph paragraph;
		XWPFRun run;
		XWPFTable table = doc.insertNewTbl(cursor);
		XWPFTableRow firstRow = table.getRow(0);

		if (firstRow == null)
			firstRow = table.createRow();

		int twipsPerInch = 1440;

		table.getCTTbl().addNewTblGrid().addNewGridCol().setW(BigInteger.valueOf(5 * twipsPerInch));

		String[] fields = { "Name", "Address", "Town", "ZIP Code" };
		String[] stValues = { student.getName(), student.getAddress(), student.getTown(), student.getZipCode() };

		for (int i = 0; i < fields.length; i++) {
			XWPFTableCell cell = firstRow.getCell(i);
			if (cell == null)
				cell = firstRow.createCell();
			cell.setColor("33FFFF");

			CTTblWidth tblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
			tblWidth.setW(BigInteger.valueOf(2 * twipsPerInch));
			tblWidth.setType(STTblWidth.DXA);

			paragraph = cell.getParagraphs().get(0);
			run = paragraph.createRun();
			run.setText(fields[i]);
		}

		XWPFTableRow tableOneRowTwo = table.createRow();

		for (int i = 0; i < stValues.length; i++) {
			XWPFTableCell cell = tableOneRowTwo.getCell(i);
			if (cell == null)
				cell = tableOneRowTwo.createCell();

			CTTblWidth tblWidth = cell.getCTTc().addNewTcPr().addNewTcW();
			tblWidth.setW(BigInteger.valueOf(2 * twipsPerInch));
			tblWidth.setType(STTblWidth.DXA);

			paragraph = cell.getParagraphs().get(0);
			run = paragraph.createRun();
			run.setText(stValues[i]);
		}
	}

	/**
	 * Método que se encarga de añadir una imagen con formato jpeg, jpg, gif y png. 
	 * 
	 * @param run - Objeto que hace referencia al párrafo en el que nos encontramos
	 * @param student - Estudiante que requiere el documento
	 * @param fileExtension - Extensión del tipo de la imagen
	 */
	private void addPictureWord(XWPFRun run, Student student, String fileExtension) {
		BufferedImage imgByte;
		try {
			imgByte = ImageIO.read(new ByteArrayInputStream(student.getPhoto()));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(imgByte, fileExtension, baos);
			baos.flush();
			ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
			baos.close();

			run.addPicture(bis, IMAGE_TYPES.getTypes(fileExtension), "image file", Units.toEMU(130),
					Units.toEMU(180));
			bis.close();
		} catch (IOException | InvalidFormatException e) {
			
		}
	}
	
	/**
	 * Método encargado de la creación del footer del documento
	 * 
	 * @param paragraph - Párafo del documento
	 * @param run - Objeto que hace referencia al párrafo en el que nos encontramos
	 * @param headerFooterPolicy - Objeto necesario para crear el header y footer. 
	 */
	private void createFooter(XWPFParagraph paragraph, XWPFRun run, XWPFHeaderFooterPolicy headerFooterPolicy) {
		XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

		paragraph = footer.getParagraphArray(0);

		if (paragraph == null)
			paragraph = footer.createParagraph();

		paragraph.setAlignment(ParagraphAlignment.CENTER);

		run = paragraph.createRun();
		run.setText("Proyecto creado por Ramón Moñino Rubio y Antonio Ruiz Marín © 2020-21");
	}
	
	/**
	 * Método que mediante la respuesta http, envía el fichero docx al estudiante.
	 * 
	 * @param resp - Respuesta del server que entrega el archivo al alumno 
	 * @param doc - Objeto documento
	 * @param student - Estudiante que requiere el documento
	 * @param flash - Clase de spring framework usada para crear mensajes en las vistas que duran una peticion de http
	 */
	private void getWord(HttpServletResponse resp, XWPFDocument doc, Student student, RedirectAttributes flash) {
		try {
			resp.setContentType("application/msword");
			resp.setHeader("Content-disposition", "attachment; fileName=\"" + student.getName() + ".docx" + "\"");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			doc.write(baos);
			StreamUtils.copy(baos.toByteArray(), resp.getOutputStream());
			flash.addFlashAttribute("success", "Student word creation success");
			baos.close();
		} catch (IOException e) {
			flash.addFlashAttribute("error", "Student with ID: " + student.getId() + " error generating word");
		}
	}
}
