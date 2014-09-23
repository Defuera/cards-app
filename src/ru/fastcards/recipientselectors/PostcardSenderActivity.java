//package ru.fastcards.recipientselectors;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.PriorityQueue;
//import java.util.Queue;
//import java.util.UUID;
//
//import ru.fastcards.FinishSendingActivity;
//import ru.fastcards.R;
//import ru.fastcards.SendPostcardDialog;
//import ru.fastcards.common.Appeal;
//import ru.fastcards.common.Comunication;
//import ru.fastcards.common.ISendableItem;
//import ru.fastcards.common.Recipient;
//import ru.fastcards.common.Theme;
//import ru.fastcards.send.SendPostcardTask;
//import ru.fastcards.utils.Account;
//import ru.fastcards.utils.Constants;
//import ru.fastcards.utils.DataBaseHelper;
//import ru.fastcards.utils.ImageManager;
//import android.app.AlertDialog;
//import android.app.Dialog;
//import android.app.DialogFragment;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.sromku.simple.fb.SimpleFacebook;
//
//public class PostcardSenderActivity extends SendYourselfActivity {
//	private static final float TEXT_SIZE = 30;
//
//	private static final int PADDING = 41;
//
//	private static Context context;
//
//	private final int textWidth = 530;
//	private final int textHeight = 330;
//
//	// private MenuItem proceedMenuItem;
//	private static String TAG = "RecipientsSelectorActivity";
//	private Bitmap signatureBitmap;
//	private Bitmap postcardBitmap;
//	private Account account = Account.getInstance();
//	private Appeal appeal;
//	String text;
//	String sigText;
//
//	private Bitmap postcardBackBitmap;
//	private Theme theme;
//
//	private String eventId;
//	private String appealId;
//	private SimpleFacebook mSimpleFacebook;
//	private DataBaseHelper dbHelper;
//	private Queue<SendPostcardTask> sendQueue;
//
//	private ArrayList<String> idsList;
//	private ProgressDialog progressDialog;
//	private List<Recipient> recipientsList;
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		mSimpleFacebook = SimpleFacebook.getInstance(this);
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_recipient_select);
//		context = this;
//		dbHelper = DataBaseHelper.getInstance(context);
//		getExtras();
//
//		listView = (ListView) findViewById(R.id.lv_recipients);
//		listView.addHeaderView(getHeaderView());
//		listView.setOnItemClickListener(itemClickListener);
//
//		setListAdapter(new RecipientsSelectorAdapter(this, itemsList));
//	}
//
//	public boolean onTestMenuClick(MenuItem item) {
//		Dialog dialog = new CustomDialogClass(context);
//		dialog.show();
//		return false;
//	}
//
//	class CustomDialogClass extends Dialog {
//
//		public Context context;
//
//		public CustomDialogClass(Context context) {
//			super(context);
//			this.context = context;
//		}
//
//		@Override
//		protected void onCreate(Bundle savedInstanceState) {
//			super.onCreate(savedInstanceState);
//			requestWindowFeature(Window.FEATURE_NO_TITLE);
//			setContentView(R.layout.dialog_test_card);
//
//			final ImageView image = (ImageView) findViewById(R.id.iv_icon);
//
//			// image.setImageBitmap(formPostcardBitmap("Oleg", 1));
//			image.setImageBitmap(postcardBitmap);
//
//			image.setOnClickListener(new android.view.View.OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					if (signatureBitmap != null)
//						if (image.getDrawable().equals(signatureBitmap))
//							// image.setImageBitmap(formPostcardBitmap("test", 1));
//							image.setImageBitmap(postcardBitmap);
//						else
//							image.setImageBitmap(signatureBitmap);
//				}
//			});
//		}
//	}
//
//	private void getExtras() {
//		Intent intent = getIntent();
//		String themePurchaseId = intent.getStringExtra(Constants.EXTRA_PURCHASE_ID);
//		theme = dbHelper.getThemeByPurchaseId(themePurchaseId);
//		//
//		eventId = intent.getStringExtra(Constants.EXTRA_EVENT_ID);
//		if (eventId != null)
//			loadRecipients(eventId);
//		//
//		// appealId = intent.getStringExtra(Constants.POSTCARD_TITLE_TEXT);
//		// appeal = dbHelper.getAppealById(appealId);
//		 text = intent.getStringExtra(Constants.POSTCARD_TEXT);
//		// sigText = intent.getStringExtra(Constants.POSTCARD_SIGNATURE_TEXT);
//		//
//		// byte[] signatureBytes = intent.getByteArrayExtra(Constants.POSTCARD_SIGNATURE_IMAGE);
//		// if (signatureBytes != null)
//		// signatureBitmap = BitmapFactory.decodeByteArray(signatureBytes, 0, signatureBytes.length);
//
//		String postcardUrl = intent.getStringExtra(Constants.POSTCARD_FRONT_IMAGE);
//		//
//		ImageManager manager = new ImageManager(context);
//		String filename = manager.createFileNameFromUrl(postcardUrl);
//
//		postcardBitmap = manager.decodeBitmapFromFile(filename);
//
//		// byte[] postcardBitmapBytes = intent.getByteArrayExtra(Constants.POSTCARD_FRONT_IMAGE);
//		// postcardBitmap = BitmapFactory.decodeByteArray(postcardBitmapBytes, 0, postcardBitmapBytes.length);
//	}
//	
//	
//	
//
//	private void loadRecipients(String eventId) {
//		itemsList = dbHelper.getRecipientsListByEventId(eventId);
//	}
//
//	private void createSendTaskQueue() {
//		idsList = new ArrayList<String>();
//		sendQueue = new PriorityQueue<SendPostcardTask>();
//
//		if (isSendYourself()) {
//			addTaskToSendQueue(new Recipient("0"));
//
//		}
//		for (Recipient rec : recipientsList) {
//			addTaskToSendQueue(rec);
//			idsList.add(rec.getId());
//		}
//	}
//
//	public void proceedSending() {
//		if (!sendQueue.isEmpty())
//			sendQueue.poll().execute();
//		else {
//			if (progressDialog != null)
//				progressDialog.dismiss();
//			startFinishSendingActivity(idsList);
//		}
//	};
//
//	public void failedSending() {
//		Toast.makeText(context, getString(R.string.str_error_sending_mms), Toast.LENGTH_SHORT).show();
//		progressDialog.dismiss();
//		sendQueue.clear();
//	}
//
//	private void addTaskToSendQueue(Recipient rec) {
//		Comunication com;
//		if ("0".equals(rec.getId()))
//			com = account.getPrimaryComunication(context);
//		else
//			com = dbHelper.getPrimaryComunication(rec.getId(), null);
//
//		Bitmap postcard = postcardBitmap;
//		// Bitmap postcard = formPostcardBitmap(rec.getNickName() == null ? rec.getName() : rec.getNickName(), rec.getGender());
//		sendQueue.add(new SendPostcardTask(this, postcard, com, rec.getName(), null));
//
//	}
//
//	private void startFinishSendingActivity(List<String> idsList) {
//		Intent intent = new Intent(this, FinishSendingActivity.class);
//		intent.putExtra(Constants.EXTRA_ID, listToArray(idsList));
//		intent.putExtra(Constants.EXTRA_THEME_ID, theme.getId());
//		startActivity(intent);
//	}
//
//	private String[] listToArray(List<String> idsList2) {
//		int size = idsList2.size();
//		String[] idsArray = new String[size];
//		for (int i = 0; i < size; i++) {
//			idsArray[i] = idsList2.get(i);
//		}
//		return idsArray;
//	}
//
//	private boolean theresARecipient() {
//		return !itemsList.isEmpty() || isSendYourself();
//	}
//
//	/**
//	 * Creates postcard bitmap with given appeal
//	 * 
//	 * @param appeal
//	 * @param text
//	 * @param sigText
//	 * @param postcard
//	 * @param signature
//	 * @return
//	 */
//	// private Bitmap formPostcardBitmap(String name, int gender) {
//	// Log.d(TAG, "postcardBitmap "+postcardBitmap);
//	// int cardWidth = postcardBitmap.getWidth();
//	// int cardHeight = postcardBitmap.getHeight();
//	// Bitmap bitmap = Bitmap.createBitmap(cardWidth, cardHeight, Config.ARGB_8888);
//	//
//	// Canvas canvas = new Canvas(bitmap);
//	// int paddingLeft = canvas.getWidth() / 2;
//	// Rect dst = new Rect(0, 0, cardWidth, cardHeight);
//	// canvas.drawBitmap(postcardBitmap, null, dst, new Paint());
//	//
//	// if (signatureBitmap != null) {
//	// int x = theme.getETextLeft() + textWidth / 2;
//	// int y = theme.getETextTop() + textHeight / 2;
//	//
//	// Rect dst2 = new Rect(x, y, x + textWidth / 2, y + textHeight / 2);
//	// canvas.drawBitmap(signatureBitmap, null, dst2, null);
//	// }
//	//
//	// Paint textPaint = new Paint();
//	// textPaint.setTextAlign(Paint.Align.CENTER);
//	// textPaint.setTextSize(TEXT_SIZE);
//	// textPaint.setARGB(255, theme.getTextColorRed(), theme.getTextColorGreen(), theme.getTextColorBlue());
//	//
//	// int padLeft = theme.getETextLeft();
//	// int padTop = theme.getETextTop() + (int) TEXT_SIZE;
//	//
//	// int counter = 0;
//	// if (appeal != null) {
//	// canvas.drawText(appeal.get(gender), paddingLeft, padTop, textPaint);
//	// counter++;
//	//
//	// if (name != null) {
//	// canvas.drawText(name, paddingLeft, padTop + PADDING * counter, textPaint);
//	// counter++;
//	// }
//	// } else {
//	// padTop += PADDING;
//	// }
//	//
//	// if (text != null) {
//	// String[] textArray = text.split("\n");
//	// for (String line : textArray) {
//	// canvas.drawText(line, paddingLeft, padTop + PADDING * counter,
//	// textPaint);
//	// counter++;
//	// }
//	// }
//	// if (sigText != null) {
//	// padTop += PADDING;
//	// canvas.drawText(sigText, paddingLeft, padTop + PADDING * counter,
//	// textPaint);
//	// counter++;
//	// }
//	// return bitmap;
//	// }
//
//	private void createSendPostcardDialog() {
//		final SendPostcardDialog dialog = SendPostcardDialog.getInstanse(recipientsList.size() + (isSendYourself() ? 1 : 0));
//		dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.SendPostcardDialogTheme);
//
//		dialog.setOnSendButtonClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				createSendTaskQueue();
//				proceedSending();
//				dialog.dismiss();
//				createProgressDialog();
//			}
//		});
//		dialog.setOnSaveButtonClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				createSaveProjectDialog();
//			}
//		});
//
//		dialog.show(getSupportFragmentManager(), TAG);
//	}
//
//	protected void createProgressDialog() {
//		progressDialog = new ProgressDialog(context);
//		progressDialog.setTitle(getString(R.string.str_progress_sending_title));
//		progressDialog.setMessage(getString(R.string.str_progress_sending_body));
//		progressDialog.setCancelable(false);
//		progressDialog.show();
//	}
//
//	private void createSaveProjectDialog() {
//		final EditText editText = new EditText(context);
//		AlertDialog.Builder builder = new AlertDialog.Builder(context);
//		builder.setTitle(getString(R.string.str_save_project_title));
//		builder.setMessage(getString(R.string.str_save_project_message));
//		builder.setView(editText);
//		builder.setPositiveButton(getString(R.string.str_ok), new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				String prName = editText.getText().toString();
//				if (prName != "" && prName != null && prName.length() > 0)
//					saveProject(editText.getText().toString());
//				else {
//					Toast.makeText(context, R.string.str_error_enter_pr_name, Toast.LENGTH_SHORT).show();
//				}
//			}
//		});
//		builder.create().show();
//	}
//
//	private void saveProject(final String projectName) {
//		new AsyncTask<Object, Object, String>() {
//			private ProgressDialog progress;
//
//			@Override
//			protected void onPreExecute() {
//				super.onPreExecute();
//				progress = new ProgressDialog(context);
//				progress.setTitle(null);
//				progress.setMessage(getString(R.string.str_save_project));
//				progress.setCancelable(false);
//			}
//
//			@Override
//			protected String doInBackground(Object... arg0) {
//				String projectId = getIntent().getStringExtra(Constants.EXTRA_PROJECT_ID);
//				// String name = projectName;
//				if (projectId == null)
//					projectId = UUID.randomUUID().toString();
//				dbHelper.saveProject(projectId, projectName, theme.getPurchaseId(), eventId, appealId, text, sigText, saveSignature(projectId));
//
//				return projectName;
//			}
//
//			@Override
//			protected void onPostExecute(String name) {
//				super.onPostExecute(name);
//				Toast.makeText(context, getString(R.string.project) + " " + name + " " + getString(R.string.str_saved), Toast.LENGTH_SHORT).show();
//				progress.dismiss();
//			}
//		}.execute();
//	}
//
//	private String saveSignature(String projectId) {
//		String filename = null;
//		if (signatureBitmap != null) {
//			filename = projectId;
//			ImageManager manager = new ImageManager(context);
//			manager.saveImageToInternalStorage(filename, signatureBitmap, Bitmap.CompressFormat.PNG);
//		}
//		return filename;
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.postcard_sender, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case android.R.id.home:
//			finish();
//			return true;
//		case R.id.menu_proceed: {
//			if (theresARecipient()) {
//				createRecipientsList();
//				createSendPostcardDialog();
//			} else {
//				Toast.makeText(context, getResources().getString(R.string.str_error_recipients_list_empty), Toast.LENGTH_LONG).show();
//			}
//
//		}
//			return true;
//		default:
//			return super.onOptionsItemSelected(item);
//		}
//	}
//
//	private void createRecipientsList() {
//		recipientsList = null;
//		recipientsList = new ArrayList<Recipient>();
//		for (ISendableItem item : itemsList) {
//			if (item.isGroup()) {
//				List<Recipient> recipients = dbHelper.getRecipientsListByGroupId(item.getId());
//				for (Recipient rec : recipients) {
//					recipientsList.add(rec);
//				}
//
//			} else {
//				recipientsList.add((Recipient) item);
//			}
//		}
//	}
//
//	@Override
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		// mSimpleFacebook.onActivityResult(this, requestCode, resultCode, data);
//		super.onActivityResult(requestCode, resultCode, data);
//
//		switch (requestCode) {
//		case Constants.REQUEST_SEND_MMS: {
//			// if (!paused){
//			// Log.d(TAG, "onActivityResult paused= "+paused);
//			proceedSending();
//			// }
//		}
//			break;
//		case Constants.REQUEST_SEND_EMAIL: {
//			proceedSending();
//		}
//			break;
//		default:
//			break;
//		}
//	}
//
//}