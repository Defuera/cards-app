package ru.fastcards;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import ru.fastcards.common.Appeal;
import ru.fastcards.common.Theme;
import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.ImageManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.itextpdf.text.DocumentException;

public class PrintPdfActivity extends TrackedActivity{

	private static final String TAG = "PrintPdfActivity";
	private Context context;
	private Theme theme;
	private Appeal appeal;
	private String text;
	private String sigText;
//	private static int counter;
	private boolean sended;
	ImageManager manager;
	private String backFileName;
	
//	private final int textWidth = 530;
//	private final int textHeight = 330;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;		
		manager = new ImageManager(context);
		setContentView(R.layout.activity_print_pdf);
		getExtras();
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}


	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()){
			case R.id.action_home:
				startMainActivity();
				break;
		}
		return true;
	}
	
	private void startMainActivity(){
		clearPicture();
		Intent intent=new Intent(context, MainActivity.class);
    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	private void clearPicture(){
		File file=new File(manager.createFileNameFromUrl(theme.getPostCardBackImage()));
		if (file.exists()) file.delete();
		
		file=new File(manager.createFileNameFromUrl(theme.getPostCardFrontImage()));
		if (file.exists()) file.delete();
		
		file=new File(manager.createFileNameFromUrl(backFileName));
		if (file.exists()) file.delete();
	}
	
	private void getExtras() {
		Intent intent = getIntent();
		String themePurchaseId = intent.getStringExtra(Constants.EXTRA_PURCHASE_ID);
		DataBaseHelper db = DataBaseHelper.getInstance(context);
		theme = db.getThemeByPurchaseId(themePurchaseId);

		text = intent.getStringExtra(Constants.POSTCARD_TEXT);	
		
		backFileName=intent.getStringExtra(Constants.POSTCARD_BACK_IMAGE);
		
		Log.d(TAG, "Extras:");
		Log.i(TAG, "themePurchaseId "+ themePurchaseId);
		Log.i(TAG, "text "+ text);
		Log.w(TAG, "titleText "+ appeal);
		Log.i(TAG, "signatureText "+ sigText);
	}
	

	public void onPrintButtonClick(View v){
//		if (Build.VERSION.SDK_INT >= 19) sendToPrint();
//		else 
		sendToGoogleClaud();
//		Dialog dialog = new CustomDialogClass(context);
//		dialog.show();
	}
	
	public void onDbButtonClick(View v){
		createDialog().show();

	}
	
	private AlertDialog createDialog(){
		LayoutInflater inflater=getLayoutInflater();
		final View v=inflater.inflate(R.layout.dialog_filename_view, null);
		final EditText input=(EditText) v.findViewById(R.id.filename_input);
		
		AlertDialog.Builder builder=new Builder(context)
		.setView(v)
		.setPositiveButton(getResources().getString(R.string.str_ok), null)
		.setNegativeButton(getResources().getString(R.string.str_cancel), null);
		
		final AlertDialog dialog=builder.create();
		dialog.setOnShowListener(new OnShowListener() {
			
			@Override
			public void onShow(DialogInterface d) {
				// TODO Auto-generated method stub
				Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                		String name=input.getText().toString();
                		if (name==null||name.equals("")) Toast.makeText(context, getString(R.string.str_input_name), Toast.LENGTH_SHORT).show();
                		else if (name.trim().equals(""))Toast.makeText(context,getString(R.string.str_not_correct_name), Toast.LENGTH_SHORT).show();
                		else {
                			sendPdf(name);
                			dialog.dismiss();
                			sended=true;
                		}
                    }
                });
            }
		});
		return dialog;
	}
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		File file=new File(manager.createFileNameFromUrl(backFileName));
		if (file.exists()) file.delete();
		
		if (sended) startMainActivity();
		else super.onBackPressed();
	}
	
	class CustomDialogClass extends Dialog {

		public Context context;

		public CustomDialogClass(Context context) {
			super(context);
			this.context = context;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.dialog_test_card);

			final ImageView image = (ImageView) findViewById(R.id.iv_icon);

//			image.setImageBitmap(formPostcardBitmap("Oleg", 1));
			Bitmap postcardBitmap=BitmapFactory.decodeFile(new ImageManager(context).getFilepathFromUrl(backFileName));
			image.setImageBitmap(postcardBitmap);

//			image.setOnClickListener(new android.view.View.OnClickListener() {
//				@Override
//					if (signatureBitma
//				public void onClick(View v) {p != null)
//						if (image.getDrawable().equals(signatureBitmap))
////							image.setImageBitmap(formPostcardBitmap("test", 1));
//							image.setImageBitmap(postcardBitmap);
//						else
//							image.setImageBitmap(signatureBitmap);
//				}
//			});
		}
	}

	
	
//	private String createBackFilename(){
//		
//		String backFileName=manager.createFileNameFromUrl(theme.getPostCardBackImage());
//		
//	   	String name=backFileName.substring(0,backFileName.indexOf('.'))+"_text.jpg";
//	   	
//	   	if (new File(manager.createFileNameFromUrl(name)).exists()) return name;
//		
//		int left=theme.getpTextLeft();
//		int top=theme.getpTextTop()-textHeight;
//		int right=theme.getpTextLeft()+textWidth;
//		int bottom=theme.getpTextTop();
//		
//		Bitmap postcardBackBitmap = manager.decodeBitmapFromFile(backFileName); 
//		
//		int cardWidth = postcardBackBitmap.getWidth();
//		int cardHeight = postcardBackBitmap.getHeight();
//		
//		Bitmap bitmap = Bitmap.createBitmap(cardWidth, cardHeight, Config.ARGB_8888);  	
//		
//		Canvas canvas = new Canvas(bitmap); 
//	   	
//
//
//	   	Log.e("Print", name);
//	   	
//	   	Paint paint=new Paint();
//
//
//	   	
//	   	canvas.drawBitmap(postcardBackBitmap, 0, 0, new Paint());
//	   	
//	   	postcardBackBitmap.recycle();
//	   	postcardBackBitmap=null;
//	   	
//	   	byte[] signatureBytes = getIntent().getByteArrayExtra(Constants.POSTCARD_SIGNATURE_IMAGE);
//		if (signatureBytes != null&& signatureBytes.length!=0)
//		{
//			Bitmap signatureBitmap = BitmapFactory.decodeByteArray(signatureBytes, 0, signatureBytes.length);
//			int x = theme.getpTextLeft();
//			int y = theme.getpTextTop();
//
//			Rect dst2 = new Rect(x, y, x + textWidth / 2, y + textHeight / 2);
//
//			canvas.drawBitmap(signatureBitmap, null, dst2, null);
//		}
//		
//		
//	   	paint.setColor(Color.TRANSPARENT);
//	   	
//	   	canvas.drawRect(left, top, right, bottom, paint);
//	   	
//	    float padding = 41.25f;
//	   	
//	   	Paint textPaint=new Paint();
//	   	textPaint.setColor(Color.BLACK);
//	   	textPaint.setTextSize(30);
//
//	   	
//	   	if (text != null){
//		   	String[] textArray = text.split("\n");
//		   	for (int i=0;  i<textArray.length; i++){
//		   		String line=textArray[textArray.length-1-i];
//		   		canvas.drawText(line, left, bottom - padding*i, textPaint);
//	    		}
//	    	}
//	   	
//	   	ByteArrayOutputStream backPictureStream = new ByteArrayOutputStream();
//	   	bitmap.compress(Bitmap.CompressFormat.JPEG, 100, backPictureStream);
//	   	manager.saveImageToInternalStorage(name, bitmap,Bitmap.CompressFormat.JPEG);
//	   	
//	   	return name;
//	}
		
	private void createPdf(String name){
			String fileName=Environment.getExternalStorageDirectory() + "/" + name+".pdf";

			try {
				new PdfConverter(context, theme.getPostCardFrontImage(),backFileName,fileName);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	private void sendPdf(String name){
			createPdf(name);
			
			String fileName=Environment.getExternalStorageDirectory() + "/" + name+".pdf";
			File file = new File(fileName);
	        Uri path = Uri.fromFile(file);
	                
		    Intent shareIntent = new Intent(Intent.ACTION_SEND); 
		    shareIntent.setType("application/image");
		    shareIntent.putExtra(Intent.EXTRA_STREAM, path);	    
		    startActivity(Intent.createChooser(shareIntent, "Send mail..."));
		}
		
	private void sendToGoogleClaud(){
			createPdf("to_print");
			
			String fileName=Environment.getExternalStorageDirectory() + "/" + "to_print"+".pdf";
			File file = new File(fileName);
	        Uri path = Uri.fromFile(file);

		    
			Intent printIntent = new Intent(this, PrintDialogActivity.class);
			printIntent.setDataAndType(path, "application/pdf");
			printIntent.putExtra(Intent.EXTRA_STREAM, path);
			startActivity(printIntent);
		}
	
//	@SuppressLint("NewApi")
//	private void sendToPrint(){
//		// Get a PrintManager instance
//		PrintManager printManager = (PrintManager)context
//	            .getSystemService(Context.PRINT_SERVICE);
//	    
////	 // Set job name, which will be displayed in the print queue
////		counter++;
////	    String jobName = "fastCards_"+counter;
////	    PrintDocumentAdapter adapter=new PrintAdapter(context, theme.getPostCardFrontImage(), createBackFilename(), "print.pdf");
////		createPdf("jobName");
////	 // Start a print job, passing in a PrintDocumentAdapter implementation
////	    // to handle the generation of a print document
////	    printManager.print(jobName, adapter,
////	            null);
//	}
	
	
	
	
}


