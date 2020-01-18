package com.ramonmr95.tiky.app.models.dao.services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ramonmr95.tiky.app.models.entities.IMAGE_TYPES;
import com.ramonmr95.tiky.app.models.entities.Student;

@Service
public class DownloadService {
	
	
	public void download(Student student, RedirectAttributes flash, String fileExtension) {
		XWPFDocument doc = new XWPFDocument();

		XWPFParagraph paragraph = doc.createParagraph();
		XWPFRun run = paragraph.createRun();

		CTSectPr sectPr = doc.getDocument().getBody().addNewSectPr();
		XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(doc, sectPr);
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

		paragraph = doc.createParagraph();
		run = paragraph.createRun();

		if (student.getPhoto() != null && student.getPhoto().length > 1) {
			BufferedImage imgByte;
			try {
				imgByte = ImageIO.read(new ByteArrayInputStream(student.getPhoto()));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(imgByte, fileExtension, baos);
				baos.flush();
				ByteArrayInputStream bis = new ByteArrayInputStream(baos.toByteArray());
				baos.close();

				
				run.addPicture(bis, XWPFDocument.PICTURE_TYPE_JPEG, "image file", Units.toEMU(130), Units.toEMU(180));
				bis.close();
			} catch (IOException | InvalidFormatException e) {

			}

			run.addBreak();
			paragraph = doc.createParagraph();
		} else {
			flash.addFlashAttribute("warning", "Word generated with no photo by Student with ID: " + student.getId());
		}

		XmlCursor cursor = paragraph.getCTP().newCursor();
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

		XWPFFooter footer = headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT);

		paragraph = footer.getParagraphArray(0);

		if (paragraph == null)
			paragraph = footer.createParagraph();

		paragraph.setAlignment(ParagraphAlignment.CENTER);

		run = paragraph.createRun();
		run.setText("Proyecto creado por Ramón Moñino Rubio y Antonio Ruiz Marín © 2020-21");
		
		try {
			doc.write(new FileOutputStream(student.getName() + ".docx"));
			doc.close();
			flash.addFlashAttribute("success", "Student word creation success");
		} 
		catch (IOException e) {
			flash.addFlashAttribute("error", "Student with ID: " + student.getId() + " error generating word");
		} 
	}
}
