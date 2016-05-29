package email_cloud_app;

import java.io.BufferedReader;  
import java.io.DataInputStream;  
import java.io.File;  
import java.io.FileInputStream;  
import java.io.FileOutputStream;  
import java.io.InputStreamReader;  
import com.itextpdf.text.Document;  
import com.itextpdf.text.Element;  
import com.itextpdf.text.Font;  
import com.itextpdf.text.Paragraph;  
import com.itextpdf.text.pdf.PdfWriter;

public class Converter 
{
	public void convertTextToPDF(File file) throws Exception 
	{
		FileInputStream fis = null;
		DataInputStream in = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try 
		{
			Document pdfDoc = new Document();
			String output_file = file.getParent() + "\\" + "message.pdf";
			PdfWriter writer = PdfWriter.getInstance(pdfDoc, new FileOutputStream(output_file));
			pdfDoc.open();
			pdfDoc.setMarginMirroring(true);
			pdfDoc.setMargins(36, 72, 108, 180);
			pdfDoc.topMargin();
			Font myfont = new Font();
			Font bold_font = new Font();
			bold_font.setStyle(Font.BOLD);
			bold_font.setSize(10);
			myfont.setStyle(Font.NORMAL);
			myfont.setSize(10);
			pdfDoc.add(new Paragraph("\n"));
			if (file.exists()) 
			{
				fis = new FileInputStream(file);
				in = new DataInputStream(fis);
				isr = new InputStreamReader(in);
				br = new BufferedReader(isr);
				String strLine;
				while ((strLine = br.readLine()) != null) 
				{
					Paragraph para = new Paragraph(strLine + "\n", myfont);
					para.setAlignment(Element.ALIGN_JUSTIFIED);
					pdfDoc.add(para);
				}
			} 
			else 
			{
				System.out.println("no such file exists!");
			}
			pdfDoc.close();
		} 
		catch (Exception e) 
		{
			System.out.println("Exception: " + e.getMessage());
		} 
		finally 
		{
			if (br != null) {
				br.close();
			}
			if (fis != null) {
				fis.close();
			}
			if (in != null) {
				in.close();
			}
			if (isr != null) {
				isr.close();
			}
		}
	}

}