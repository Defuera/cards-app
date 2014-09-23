package ru.fastcards;

import java.io.FileOutputStream;
import java.io.IOException;

import ru.fastcards.utils.ImageManager;

import com.itextpdf.text.Image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

@SuppressLint("NewApi")
public class PrintAdapter extends PrintDocumentAdapter{
	
	private Context context;
	private String frontImageName;
	private String backImageName;
	private PrintedPdfDocument document;



	public PrintAdapter(Context context,String frontImageName,String backImageName,String filename) {
		// TODO Auto-generated constructor stub
		this.context=context;
		this.frontImageName=frontImageName;
		this.backImageName=backImageName;
	}

	@Override
	public void onLayout(PrintAttributes oldAttributes,
			PrintAttributes newAttributes,
			CancellationSignal cancellationSignal,
			LayoutResultCallback callback, Bundle extras) {
		// TODO Auto-generated method stub
		
		document=new PrintedPdfDocument(context, newAttributes);
		
		// Respond to cancellation request
	    if (cancellationSignal.isCanceled() ) {
	        callback.onLayoutCancelled();
	        return;
	    }
	    
	    // Compute the expected number of printed pages
	    int pages = 2;

	    if (pages > 0) {
	        // Return print information to print framework
	        PrintDocumentInfo info = new PrintDocumentInfo
	                .Builder("print_output.pdf")
	                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
	                .setPageCount(pages)
	                .build();
	        // Content layout reflow is complete
	        callback.onLayoutFinished(info, true);
	    } else {
	        // Otherwise report an error to the print framework
	        callback.onLayoutFailed("Page count calculation failed.");
	    }
		
	}

	@Override
	public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
			CancellationSignal cancellationSignal, WriteResultCallback callback) {
		// TODO Auto-generated method stub
		
		 // Iterate over each page of the document,
	    // check if it's in the output range.
	    for (int i = 0; i < 2; i++) {
	        // Check to see if this page is in the output range.
	            // If so, add it to writtenPagesArray. writtenPagesArray.size()
	            // is used to compute the next output page index.
	            PdfDocument.Page page = document.startPage(i);

	            // check for cancellation
	            if (cancellationSignal.isCanceled()) {
	                callback.onWriteCancelled();
	                document.close();
	                document = null;
	                return;
	            }

	            // Draw page content for printing
	            drawPage(page,i);

	            // Rendering is complete, so page can be finalized.
	            document.finishPage(page);
	    }

	    // Write PDF document to file
	    try {
	    	document.writeTo(new FileOutputStream(
	                destination.getFileDescriptor()));
	    } catch (IOException e) {
	        callback.onWriteFailed(e.toString());
	        return;
	    } finally {
	    	document.close();
	    	document = null;
	    }
	    PageRange[] writtenPages = {PageRange.ALL_PAGES};
	    // Signal the print framework the document is complete
	    callback.onWriteFinished(writtenPages);
	}
	

	private void drawPage(PdfDocument.Page page, int pageNumber){
		Canvas canvas = page.getCanvas();
		
		ImageManager manager=new ImageManager(context);
		
		Bitmap image;
		if (pageNumber%2==0)
			image=manager.decodeBitmapFromFile(manager.createFileNameFromUrl(frontImageName));
		else 
			image=manager.decodeBitmapFromFile(manager.createFileNameFromUrl(backImageName));
		
		Matrix matrix=new Matrix();
		matrix.setScale(500, 500);
		
	    canvas.drawBitmap(image,0,0,new Paint());
	}
	
	

}
