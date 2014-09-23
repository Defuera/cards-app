//package ru.fastcards.send;
//
//import android.annotation.SuppressLint;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Paint;
//import android.graphics.Rect;
//import android.graphics.Bitmap.Config;
//
//public class PostcardCreator {
//
//	/**
//	 * Creates postcard bitmap with given appeal
//	 * @param appeal
//	 * @param text
//	 * @param sigText
//	 * @param postcard
//	 * @param signature
//	 * @return
//	 */
//
//	@SuppressLint("NewApi")
//	public static Bitmap formPostcardBitmap(Bitmap postcardBitmap, Bitmap signatureBitmap, String name, int gender) {
////	    	Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test22);
//	    	
////		Display display = getWindowManager().getDefaultDisplay();
////		Point size = new Point();
////		display.getSize(size);
////		int width = size.x;
////		int height = size.y;
//		
//		int cardWidth = postcardBitmap.getWidth();
//		int cardHeight = postcardBitmap.getHeight();
//		Bitmap bitmap = Bitmap.createBitmap(cardWidth, cardHeight, Config.ARGB_8888);  	
//	
//		Canvas canvas = new Canvas(bitmap);  	
//		Rect dst = new Rect(0, 0, cardWidth,cardHeight);
//	   	canvas.drawBitmap(postcardBitmap, null, dst, new Paint());
//	    		    	
//		if (signatureBitmap != null) 	    {
//			int x = theme.getETextLeft()+textWidth/2;
//			int y = theme.getETextTop() + textHeight/2;
//			
//			Rect dst2 = new Rect(x, y, x + textWidth/2, y + textHeight/2);
////			Rect src = new Rect(0, 0, signatureBitmap.getWidth(), signatureBitmap.getHeight());
////			new Rect(100,100, 300, 500)
//			canvas.drawBitmap(signatureBitmap, null, dst2, null);    	
//		}
//	    	
//		Paint textPaint = new Paint();
//		textPaint.setTextSize(30);
//	    textPaint.setARGB(255, theme.getTextColorRed(), theme.getTextColorGreen(), theme.getTextColorBlue());
//	    	
//	    float padding = 41.25f;
//	    	
//	    int padLeft = theme.getETextLeft();
//	    int padTop = theme.getETextTop();		    
//
//	    int counter = 0;
//	    if (appeal != null){
//	    	canvas.drawText(appeal.get(gender), padLeft, padTop, textPaint);
//	    }
//
//	   	if (name != null){
//	    	counter++;	 
//	    	canvas.drawText(name, padLeft, padTop + padding*counter, textPaint);   		
//	    }
//
//	    if (text != null){
//	   	String[] textArray = text.split("\n");
//	   	for (String line : textArray){
//	   		counter++;
//	   		canvas.drawText(line, padLeft, padTop + padding*counter, textPaint);
//    		}
//    	}
//	    if (sigText != null){
//	    	counter++;
//	    	canvas.drawText(sigText, padLeft, padTop + padding*counter, textPaint);
//	    }
//	return bitmap;
//	}
//	
//}
