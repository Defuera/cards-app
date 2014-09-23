package ru.fastcards;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import ru.fastcards.utils.ImageManager;

import android.content.Context;
import android.util.Log;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

public class PdfConverter {

	private Context context;
	private String frontImageName;
	private String backImageName;
//	private byte[] front;
//	private byte[] back;
	private final static float pageWidth=425;
	private final static float pageHeight=281;

	public PdfConverter(Context context,String frontImageName,String backImageName,String filename) throws DocumentException, MalformedURLException, IOException{
		// TODO Auto-generated constructor stub
		this.context=context;
		this.frontImageName=frontImageName;
		this.backImageName=backImageName;
//		this.front=front;
//		this.back=back;
		
		Rectangle documentSize=new Rectangle(pageWidth, pageHeight);

		Document document = new Document(documentSize);
		document.setMargins(0, 0, 0, 0);
		
 		PdfWriter writer=PdfWriter.getInstance(document, 
				 new FileOutputStream(filename));
		 
         document.open();
         document.addTitle("FastCards");
         
         createImages(document);
         document.close();
         

		
	}

	
	private void createImages(Document document) throws MalformedURLException, IOException, DocumentException{
		ImageManager manager=new ImageManager(context);
		Image frontImage=Image.getInstance(manager.getFilepathFromUrl(frontImageName));
//		Image frontImage=Image.getInstance(front);
		
		frontImage.scaleAbsolute(pageWidth, pageHeight);
		
		document.add(frontImage);
        
		document.newPage();
		
//		Image backImage=Image.getInstance(back);
        
		Image backImage=Image.getInstance(manager.getFilepathFromUrl(backImageName));
//		
//		Log.e("Print", manager.getFilepathFromUrl(backImageName));
		backImage.scaleAbsolute(pageWidth, pageHeight);
		
		document.add(backImage);	

	}
	
	
	
	

}

