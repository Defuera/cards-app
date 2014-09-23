package ru.fastcards.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.json.JSONException;

import ru.fastcards.ListContacts;
import ru.fastcards.common.Appeal;
import ru.fastcards.common.Category;
import ru.fastcards.common.CategoryGroup;
import ru.fastcards.common.Comunication;
import ru.fastcards.common.Event;
import ru.fastcards.common.ISendableItem;
import ru.fastcards.common.Project;
import ru.fastcards.common.Recipient;
import ru.fastcards.common.Text;
import ru.fastcards.common.TextPack;
import ru.fastcards.common.Theme;
import ru.fastcards.inapp.Purchase;
import ru.fastcards.social.api.Params;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorJoiner.Result;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * 
 * @author Denis V
 * @since 20.11.2013 Add row description to table TextPack
 * @since 22.11.2013 Add getTextPackByPurchaseId
 */
public class DataBaseHelper extends SQLiteOpenHelper {
	private static final String FIELD_TEXT_PACK_ID = "TextPackId";
	private static final String FIELD_TEXT_STRING = "TextString";
	private static final String FIELD_ECARD_THUMB = "ECartdThumb";
	private static final String FIELD_SQUARE_THUMB = "SquareThumb";
	private static final String FIELD_CARD_FRONT_THUMB = "CardFrontThumb";
	private static final String FIELD_CARD_BACK_THUMB = "CardBackThumb";
	private static final String FIELD_DESCRIPTION = "Description";
	private static final String FIELD_APPEAL_MALE = "AppealMale";
	private static final String FIELD_APPEAL_FEMALE = "AppealFemale";
	private static final String FIELD_DATA = "DataField";
	private static final String FIELD_LAST_MODIFIED = "LastModified";
	private static final String FIELD_THEME_ID = "ThemeId";
	private static final String FIELD_TEXT = "TextId";
	private static final String FIELD_APPEALS_ID = "AppealsId";
	private static final String FIELD_SIGNATURE_BITMAP_ID = "SignatureBitmapId";
	private static final String FIELD_SIGNATURE_TEXT = "SignatureText";
	private static final String FIELD_GROUP_ID = "GroupId";
	private static final String FIELD_SIZE = "GroupSize";
	private static final String FIELD_IS_GROUP = "IsItemGroup";
	public final static String FIELD_TYPE = "Type";
	private final String FIELD_RECIPIENT_ID = "ContactID";
	private final String FIELD_CONTACT_INFO = "URI_WHAT";
	private final String FIELD_PRIMARY = "PMRY";
	public static final String FIELD_UUID = BaseColumns._ID;// "_id";
	public static final String FIELD_NAME = "Name";
	public static final String FIELD_NAME_SEARCH = "NameSearch";
	private final String FIELD_NICKNAME = "NickName";
	private final String FIELD_SEX = "Sex";
	public static final String FIELD_THUMB = "ThumbUri";
	private static final String FIELD_JSON = "Json";
	private static final String FIELD_SIGNATURE = "Signature";
	private final String FIELD_EVENT_ID = "EventId";
	public final static String FIELD_DATE = "Date";

	public static final String FIELD_DATE_2 = "Date2";
	public static final String FIELD_CATEGORY_ID = "Category";
	private final String FIELD_PUSH = "Push";
	private final String FIELD_NOTIFICATION = "Repeat";
	private final String FIELD_PURCHASE_ID = "PurchaseId";
	private final String FIELD_AUTOR = "Autor";
	private final String FIELD_PRICE = "Price";
	private final String FIELD_BOUGHT = "Bought";
	private final String FIELD_COVER_IMAGE = "CoverImage";
	private final String FIELD_ECARD_IMAGE = "EcardImage";
	private final String FIELD_SQUARE_IMAGE = "SqureImage";
	private final String FIELD_CARD_FRONT_IMAGE = "CardFrontImage";
	private final String FIELD_CARD_BACK_IMAGE = "CardBackImage";
	private final String FIELD_ETEXT_TOP = "EtextTop";
	private final String FIELD_ETEXT_LEFT = "EtextLeft";
	private final String FIELD_PTEXT_TOP = "PtextTop";
	private final String FIELD_PTEXT_LEFT = "PtextLeft";
	private final String FIELD_TEXT_COLOR_RED = "TextColorRed";
	private final String FIELD_TEXT_COLOR_GREEN = "TextColorGreen";
	private final String FIELD_TEXT_COLOR_BLUE = "TextColorBlue";

	private Context context;
	private static String TAG = "DataBaseHelper";
	private static DataBaseHelper dbHelper;

	private static final int VERSION = 4;

	private DataBaseHelper(Context context) {
		super(context, Constants.DATA_BASE_NAME, null, VERSION);
		this.context = context;
	}

	public static DataBaseHelper getInstance(Context context) {
		if (dbHelper == null) {
			dbHelper = new DataBaseHelper(context);
		}
		return dbHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Log.d("DataBaseHelper", "--- onCreate database ---");

		db.execSQL("CREATE TABLE " + Constants.TABLE_CONTACTS + " (" + FIELD_UUID + " text," + FIELD_NAME + " text," + FIELD_NAME_SEARCH + " text," + FIELD_NICKNAME + " text," + FIELD_SEX + " text,"
				+ FIELD_THUMB + " text" + ");");

		db.execSQL("create table " + Constants.TABLE_COMUNICATION + " (" + FIELD_UUID + " text," + FIELD_RECIPIENT_ID + " text," + FIELD_TYPE + " text," + FIELD_PRIMARY + " integer,"
				+ FIELD_CONTACT_INFO + " text" + ");");

		db.execSQL("create table " + Constants.TABLE_EVENTS + " (" + FIELD_UUID + " text," + FIELD_NAME + " text," + FIELD_DATE + " integer," + FIELD_DATE_2 + " integer," + // long
				FIELD_CATEGORY_ID + " text," + FIELD_PUSH + " integer," + FIELD_NOTIFICATION + " integer," + FIELD_TYPE + " text" + ");");

		db.execSQL("create table " + Constants.TABLE_EVENTS_RECIPIENTS + " (" + FIELD_UUID + " text," + FIELD_EVENT_ID + " text," + FIELD_IS_GROUP + " integer," + FIELD_RECIPIENT_ID + " text" + ");");

		db.execSQL("create table " + Constants.TABLE_CATEGORY + " (" + FIELD_UUID + " text," + FIELD_NAME + " text," + FIELD_CATEGORY_ID + " text" + ");");

		db.execSQL("create table " + Constants.TABLE_CATEGORY_GROUP + " (" + FIELD_UUID + " text," + FIELD_COVER_IMAGE + " text," + FIELD_NAME + " text" + ");");

		db.execSQL("create table " + Constants.TABLE_THEMES + " (" + FIELD_UUID + " text," + FIELD_NAME + " text," + FIELD_CATEGORY_ID + " text," + FIELD_PURCHASE_ID + " text," + FIELD_AUTOR
				+ " text," + FIELD_PRICE + " REAL," + FIELD_BOUGHT + " integer," + FIELD_COVER_IMAGE + " text," + FIELD_ECARD_IMAGE + " text," + FIELD_SQUARE_IMAGE + " text," + FIELD_CARD_FRONT_IMAGE
				+ " text," + FIELD_CARD_BACK_IMAGE + " text," + FIELD_ECARD_THUMB + " text," + FIELD_SQUARE_THUMB + " text," + FIELD_CARD_FRONT_THUMB + " text," + FIELD_CARD_BACK_THUMB + " text,"
				+ FIELD_ETEXT_TOP + " integer," + FIELD_ETEXT_LEFT + " integer," + FIELD_PTEXT_TOP + " integer," + FIELD_PTEXT_LEFT + " integer," + FIELD_TEXT_COLOR_RED + " integer,"
				+ FIELD_TEXT_COLOR_GREEN + " integer," + FIELD_TEXT_COLOR_BLUE + " integer" + ");");

		db.execSQL("create table " + Constants.TABLE_TEXT_PACKS + " (" + FIELD_UUID + " text," + FIELD_NAME + " text," + FIELD_DESCRIPTION + " text," + FIELD_CATEGORY_ID + " text," + FIELD_THEME_ID
				+ " text," + FIELD_PURCHASE_ID + " text," + FIELD_PRICE + " integer," + FIELD_BOUGHT + " integer," + FIELD_COVER_IMAGE + " text" + ");");

		db.execSQL("create table " + Constants.TABLE_TEXTS + " (" + FIELD_UUID + " text," + FIELD_NAME + " text," + FIELD_TEXT_PACK_ID + " text," + FIELD_TEXT_STRING + " text" + ");");

		db.execSQL("create table " + Constants.TABLE_APPEALS + " (" + FIELD_UUID + " text," + FIELD_APPEAL_MALE + " text," + FIELD_APPEAL_FEMALE + " text" + ");");

		db.execSQL("create table " + Constants.TABLE_VERSIONS + " (" + FIELD_DATA + " text," + FIELD_LAST_MODIFIED + " text" + ");");

		db.execSQL("create table " + Constants.TABLE_PROJECTS + " (" + FIELD_UUID + " text," + FIELD_NAME + " text," + FIELD_THEME_ID + " text," + FIELD_EVENT_ID + " text," + FIELD_TEXT + " text,"
				+ FIELD_APPEALS_ID + " text," + FIELD_SIGNATURE_BITMAP_ID + " text," + FIELD_SIGNATURE_TEXT + " text" + ");");

		db.execSQL("create table " + Constants.TABLE_LISTS + " (" + FIELD_UUID + " text," + FIELD_SIZE + " integer," + FIELD_NAME + " text" + ");");

		db.execSQL("create table " + Constants.TABLE_LIST_CONTACTS + " (" + FIELD_UUID + " text," + FIELD_GROUP_ID + " text," + FIELD_RECIPIENT_ID + " text" + ");");

		db.execSQL("create table " + Constants.TABLE_STARS_PURCHASES + " (" + FIELD_UUID + " text," + FIELD_JSON + " text," + FIELD_SIGNATURE + " text" + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "ALTER TABLE " + Constants.TABLE_EVENTS + " ADD COLUMN " + FIELD_DATE_2 + " integer";
		db.execSQL(sql);

		updateDataBase4();
	}

	private void updateDataBase4() {
		new AsyncTask<Object, Object, Object>() {
			@Override
			protected Result doInBackground(Object... params) {
				String tableName = Constants.TABLE_EVENTS;

				SQLiteDatabase db = dbHelper.getReadableDatabase();
				Cursor c = null;
				try {
					c = db.query(tableName, null, null, null, null, null, null);
				} catch (SQLiteException e) {
					Log.w(TAG, tableName + " do not exist");
					return null;
				}
				if (c.getCount() == 0)
					Log.w(TAG, "Table " + tableName + " do not contain any rows with field ");
				else if (c.moveToFirst()) {

					do {
						int idColIndex = c.getColumnIndex(FIELD_UUID);
						String id = c.getString(idColIndex);

						int dateColIndex = c.getColumnIndex(FIELD_DATE);
						long date = c.getLong(dateColIndex);

						int date2ColIndex = c.getColumnIndex(FIELD_DATE_2);
						int date2 = c.getInt(date2ColIndex);
						Log.i(TAG, "date2 " + date2);

						addDate2ToEvent(id, date);

					} while (c.moveToNext());
				}
				c.close();
				return null;
			}

			private void addDate2ToEvent(String id, long date) {
				String tableName = Constants.TABLE_EVENTS;
				String where = FIELD_UUID + "=" + "'" + id + "'";
				SQLiteDatabase db = dbHelper.getWritableDatabase();
				ContentValues cv = new ContentValues();
				cv.put(FIELD_DATE_2, getDate2(date));

				db.update(tableName, cv, where, null);

			}

		}.execute();

	}

	/**
	 * Compares last modified dates on server and on local DataBase
	 * 
	 * @param data
	 *            - field to compare
	 * @param lastModified
	 *            - date in milliseconds
	 * @return true if there's an update, false if local base have actual version.
	 */
	public boolean checkForUpdates(String data, long lastModified) {
		String tableName = Constants.TABLE_VERSIONS;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		boolean update = false;
		String where = FIELD_DATA + "=" + "'" + data + "'";
		Cursor c = null;
		try {
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
		}

		// List<Recipient> recipientsList = new ArrayList<Recipient>();
		if (c.getCount() == 0) {
			Log.w(TAG, "Table " + tableName + " do not contain any row");

			ContentValues cv = new ContentValues();
			cv.put(FIELD_DATA, data);
			cv.put(FIELD_LAST_MODIFIED, lastModified);
			db.insert(tableName, null, cv);
			update = true;
		} else if (c.moveToFirst()) {
			int dataColIndex = c.getColumnIndex(FIELD_DATA);
			int modColIndex = c.getColumnIndex(FIELD_LAST_MODIFIED);

			long modifiedDb = c.getLong(modColIndex);
			if (modifiedDb < lastModified) {
				ContentValues cv = new ContentValues();
				cv.put(FIELD_DATA, data);
				cv.put(FIELD_LAST_MODIFIED, lastModified);
				db.update(tableName, cv, where, null);

				update = true;
			}

		}
		c.close();
		Log.v(TAG, "checkForUpdates " + data + " lastModified " + lastModified + " update " + update);
		return update;
	}

	public Set<String> getContactsIdsList() {
		String tableName = Constants.TABLE_COMUNICATION;
		Set<String> recipientsIds = new HashSet<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			String where = FIELD_TYPE + "=" + "'" + Constants.COMUNICATION_TYPE_CONTACTS_ID + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any rows with field ");
		else if (c.moveToFirst()) {

			do {
				int idColIndex = c.getColumnIndex(FIELD_CONTACT_INFO);
				recipientsIds.add(c.getString(idColIndex));
				// System.out.println(c.getString(idColIndex));
			} while (c.moveToNext());
		}
		c.close();

		return recipientsIds;
	}

	public void saveRecipient(String id, String name, String vkId, String fbId, String thumbUri) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put(FIELD_UUID, id);
		cv.put(FIELD_NAME, name);
		cv.put(FIELD_NAME_SEARCH, name.toUpperCase());
		cv.put(FIELD_SEX, 1);
		cv.put(FIELD_THUMB, thumbUri);
		db.insert(Constants.TABLE_CONTACTS, null, cv);
	}

	public void saveRecipient(Recipient recipient) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		String id = recipient.getId();

		cv.put(FIELD_UUID, id);
		cv.put(FIELD_NAME, recipient.getName());
		cv.put(FIELD_NAME_SEARCH, recipient.getName().toUpperCase());
		cv.put(FIELD_SEX, recipient.getGender());
		cv.put(FIELD_THUMB, recipient.getImageUri());
		db.insert(Constants.TABLE_CONTACTS, null, cv);

		Log.w(TAG, "save recipient " + recipient.getName() + " sex " + recipient.getGender());

		// String[] phoneNumbers = recipient.getPhoneNumber();
		// String[] emails = recipient.getEmail();
		// String vkId = recipient.getVkId();
		// String fbId = recipient.getFbId();
		// String idContacts = recipient.getContactsId();

		// if (phoneNumbers != null && phoneNumbers.length > 0 )
		// for (String phone : phoneNumbers ){
		// if (phone != null && phone != "")
		// saveComunication(id, Constants.COMUNICATION_TYPE_PHONE, phone);
		// }
		//
		// if (emails != null && emails.length > 0)
		// for (String phone : phoneNumbers ){
		// if (phone != null && phone != "")
		// for (String email : emails )saveComunication(id, Constants.COMUNICATION_TYPE_EMAIL, email);
		// }

		// if (!"".equals(vkId) && vkId != null) saveComunication(id, Constants.COMUNICATION_TYPE_VK_ID, vkId);
		// if (!"".equals(fbId) && fbId != null) saveComunication(id, Constants.COMUNICATION_TYPE_FB_ID, fbId);
		// if (!"".equals(idContacts) && idContacts != null) saveComunication(id, Constants.COMUNICATION_TYPE_CONTACTS_ID, idContacts);

	}

	/**
	 * 
	 * @param type
	 * @return
	 */
	public List<Recipient> getRecipientsListByComunicationType(String type) {
		String tableName = Constants.TABLE_COMUNICATION;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			String where = FIELD_TYPE + "=" + "'" + type + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		List<Recipient> recipientsList = new ArrayList<Recipient>();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row with field " + FIELD_TYPE + "=" + "'" + type + "'");
		else if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_RECIPIENT_ID);
				String recipientId = c.getString(idColIndex);

				Recipient recipient = getRecipient(recipientId);

				recipientsList.add(recipient);

				Log.d(TAG, "loaded recipient with type " + type + " name " + recipient.getName() + " gender " + recipient.getGender() + " bday " + recipient.getBirthday());
			} while (c.moveToNext());
		c.close();

		return recipientsList;
	}

	public List<Recipient> getRecipientsList() {
		String tableName = Constants.TABLE_COMUNICATION;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			// String where = FIELD_TYPE + "=" + "'"+type+"'";
			c = db.query(tableName, null, null, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		List<Recipient> recipientsList = new ArrayList<Recipient>();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any rows");
		else if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_RECIPIENT_ID);
				String recipientId = c.getString(idColIndex);

				Recipient recipient = getRecipient(recipientId);

				recipientsList.add(recipient);

				// Log.d(TAG, "loaded recipient with type "+type+" name "+recipient.getName()+" vkId "+recipient.getVkId()+" phone "+recipient.getPhoneNumber());
			} while (c.moveToNext());
		c.close();

		return recipientsList;
	}

	public Cursor getRecipientsCursor() {
		String tableName = Constants.TABLE_CONTACTS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			// String where = FIELD_TYPE + "=" + "'"+type+"'";
			c = db.query(tableName, null, null, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}
		return c;
	}

	public List<ISendableItem> getRecipientsListByEventId(String eventId) {
		String tableName = Constants.TABLE_EVENTS_RECIPIENTS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			String where = FIELD_EVENT_ID + "=" + "'" + eventId + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		List<ISendableItem> recipientsList = new ArrayList<ISendableItem>();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row with field " + FIELD_TYPE + "=" + "'" + eventId + "'");
		else if (c.moveToFirst())
			do {

				int isGroupColIndex = c.getColumnIndex(FIELD_IS_GROUP);
				int idColIndex = c.getColumnIndex(FIELD_RECIPIENT_ID);

				String itemtId = c.getString(idColIndex);

				ISendableItem item = null;
				if (c.getInt(isGroupColIndex) == 1) {
					item = getContactsList(itemtId);
				} else {
					item = getRecipient(itemtId);
				}

				if (item != null)
					recipientsList.add(item);

				// Log.v(TAG, "getRecipientsListByEventId  name "+item.getName());
			} while (c.moveToNext());
		c.close();

		return recipientsList;
	}

	public List<Recipient> getRecipientsListByGroupId(String groupId) {
		String tableName = Constants.TABLE_LIST_CONTACTS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			String where = FIELD_GROUP_ID + "=" + "'" + groupId + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		List<Recipient> recipientsList = new ArrayList<Recipient>();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row with field " + FIELD_TYPE + "=" + "'" + groupId + "'");
		else if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_RECIPIENT_ID);
				String recipientId = c.getString(idColIndex);

				Recipient recipient = getRecipient(recipientId);

				recipientsList.add(recipient);

				Log.v(TAG, "getRecipientsListByGroupId  name " + recipient.getName());
			} while (c.moveToNext());
		c.close();

		return recipientsList;
	}

	public void deleteRecipientsByComunicationType(String comType) {
		String tableComunic = Constants.TABLE_COMUNICATION;
		String tableRec = Constants.TABLE_CONTACTS;

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String where1 = "WHERE TableComunication.Type = '" + comType + "'";
		String query = "SELECT Contacts._id, Contacts.Name FROM " + tableRec + " LEFT JOIN " + tableComunic + " ON " + "Contacts._id = TableComunication." + FIELD_RECIPIENT_ID + " " + where1;

		Cursor c = db.rawQuery(query, null);

		// Log.v(TAG, "deleteRecipientsByComunicationType " + comType);
		//
		// Log.v(TAG, "num rows " + c.getCount());
		// Log.v(TAG, "columns " + Arrays.toString(c.getColumnNames()));

		if (c.moveToFirst())
			do {
				String deleteId = c.getString(c.getColumnIndex(FIELD_UUID));
				Log.i(TAG, "deleting " + c.getString(c.getColumnIndex(FIELD_NAME)) + " id " + deleteId);

				String where2 = "'" + deleteId + "' = " + FIELD_RECIPIENT_ID;
				db.delete(Constants.TABLE_COMUNICATION, where2, null);

				deleteRecFromLists(db, deleteId);

				deleteRecEvents(db, deleteId);

				// Delete from table.Contacts
				where2 = "'" + deleteId + "' = " + FIELD_UUID;
				int del = db.delete(Constants.TABLE_CONTACTS, where2, null);
				Log.d(TAG, "deleting from Contacts table " + del);

			} while (c.moveToNext());

	}

	private void deleteRecEvents(SQLiteDatabase db, String deleteRecId) {
		String tableName = Constants.TABLE_EVENTS_RECIPIENTS;
		String where = FIELD_RECIPIENT_ID + " = '" + deleteRecId + "'";
		// String query = "SELECT " + FIELD_EVENT_ID + " FROM " + Constants.TABLE_EVENTS_RECIPIENTS + " WHERE " + where;

		Cursor deleteEventIdsCursor = db.query(tableName, null, where, null, null, null, null);
		// Cursor deleteEventIdsCursor = db.rawQuery(query, null);

		Log.d(TAG, "deleteEventIdsCursor " + deleteEventIdsCursor.getCount() + " columns " + Arrays.toString(deleteEventIdsCursor.getColumnNames()));
		if (deleteEventIdsCursor.moveToFirst())
			do {
				String id = deleteEventIdsCursor.getString(deleteEventIdsCursor.getColumnIndex(FIELD_EVENT_ID));
				Log.d(TAG, "id " + id);
				id = deleteEventIdsCursor.getString(1);
				Log.i(TAG, "id " + id);

				where = FIELD_UUID + " = '" + id + "'";
				int deletedRows = db.delete(Constants.TABLE_EVENTS, where, null);
				// Log.v(TAG, "deleteEvent " + deleteEventIdsCursor.getCount());
			} while (deleteEventIdsCursor.moveToNext());

		where = FIELD_RECIPIENT_ID + " = '" + deleteRecId + "'";
		db.delete(Constants.TABLE_EVENTS_RECIPIENTS, where, null);

	}

	private void deleteRecFromLists(SQLiteDatabase db, String deleteRecId) {
		String where = FIELD_RECIPIENT_ID + " = '" + deleteRecId + "'";
		db.delete(Constants.TABLE_LIST_CONTACTS, where, null);

		// deleteTableContactsGroupIfEmpty
		/**
		 * нахожу все tableList где присутствовал deleteRecId, проверяю не пустые ли они, если пустые удаляю
		 */
	}

	public Recipient getRecipient(String id) {
		String tableName = Constants.TABLE_CONTACTS;

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {

			String where = FIELD_UUID + "=" + "'" + id + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		Recipient recipient = new Recipient();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row with field " + FIELD_UUID + "=" + "'" + id + "'");
		else if (c.moveToFirst()) {
			int idColIndex = c.getColumnIndex(FIELD_UUID);
			int nameColIndex = c.getColumnIndex(FIELD_NAME);
			int nickColIndex = c.getColumnIndex(FIELD_NICKNAME);
			int sexColIndex = c.getColumnIndex(FIELD_SEX);
			int thumbColIndex = c.getColumnIndex(FIELD_THUMB);

			recipient.setId(c.getString(idColIndex));
			recipient.setName(c.getString(nameColIndex));
			recipient.setNickName(c.getString(nickColIndex));
			recipient.setGender(c.getInt(sexColIndex));
			recipient.setImageUri(c.getString(thumbColIndex));

		}
		c.close();

		return recipient;
	}

	/**
	 * Updates recipient row in DB
	 * 
	 * @param id
	 *            - id of recipient to update
	 * @param gender
	 *            - pass 1 to change sex to "female", 0 "male", -1 to leave unchanged
	 * @param nickname
	 *            - pass null to leave unchanged
	 */
	public void changeRecipient(String id, int gender, String nickname) {
		String tableName = Constants.TABLE_CONTACTS;

		String where = FIELD_UUID + "=" + "'" + id + "'";

		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		if (gender == 0 || gender == 1)
			cv.put(FIELD_SEX, gender);
		if (nickname != null)
			cv.put(FIELD_NICKNAME, nickname);

		db.update(tableName, cv, where, null);

		// Log.d(TAG, "Recipient updated with new fields: gender "+gender+" nick "+nickname);

	}

	public void deleteRecipient(String id) {
		String tableName = Constants.TABLE_CONTACTS;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String where = FIELD_UUID + "=" + "'" + id + "'";

		db.delete(tableName, where, null);

		// Log.d(TAG, "Recipient updated with new fields: gender "+gender+" nick "+nickname);

	}

	/**
	 * Returns list of phones,emails and ids of social networks. Attention! It will not include comunication type Contacts_ID.
	 * 
	 * @param recipientId
	 *            - uuid of the recipient to return comunications for (obligatory parameter).
	 * @param comType
	 *            - comunication type, may be null
	 * @return
	 */
	/**
	 */
	public List<Comunication> getComunicationsList(String recipientId, String comType) {
		String tableName = Constants.TABLE_COMUNICATION;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			String where = FIELD_RECIPIENT_ID + "=" + "'" + recipientId + "'";
			if (comType != null)
				where += " AND " + FIELD_TYPE + "=" + "'" + comType + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		List<Comunication> comunicationsList = new ArrayList<Comunication>();
		// Log.w(TAG, "c.getCount()c.getCount()c.getCount()c.getCount() "+c.getCount()+" "+recipientId);
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row with field " + FIELD_RECIPIENT_ID + "=" + "'" + recipientId + "'");
		else if (c.moveToFirst())
			do {

				int idColIndex = c.getColumnIndex(FIELD_UUID);
				int typeColIndex = c.getColumnIndex(FIELD_TYPE);
				int primaryColIndex = c.getColumnIndex(FIELD_PRIMARY);
				int infoColIndex = c.getColumnIndex(FIELD_CONTACT_INFO);

				String uuid = c.getString(idColIndex);

				Comunication comunication = new Comunication(uuid, recipientId);
				comunication.setType(c.getString(typeColIndex));
				comunication.setPrimaty(c.getInt(primaryColIndex) == 1);
				comunication.setInfo(c.getString(infoColIndex));

				if (!Constants.COMUNICATION_TYPE_CONTACTS_ID.equals(comunication.getType()))
					comunicationsList.add(comunication);

				// Log.d(TAG, "getComunicationsByRecipientId "+comunication.getType()+" info "+comunication.getInfo());
			} while (c.moveToNext());
		c.close();

		return comunicationsList;
	}

	public void setPrimaryComunication(String recId, String comType, String primaryComId) {
		List<Comunication> comList = getComunicationsList(recId, comType);
		for (Comunication com : comList) {
			String comId = com.getUuid();
			if (!primaryComId.equals(comId)) {
				updateComunication(comId, false);
			} else {
				updateComunication(comId, true);
			}
			
			Log.w(TAG, "com "+com.getInfo()+" set primary "+!primaryComId.equals(comId));
		}

	}

	// public void makeComunicationPrimary(String uuid, String recipientId) {
	// String tableName = Constants.TABLE_COMUNICATION;
	// SQLiteDatabase db = dbHelper.getReadableDatabase();
	//
	// String where = FIELD_RECIPIENT_ID + "=" + "'" + recipientId + "'";
	// ContentValues cv = new ContentValues();
	// cv.put(FIELD_PRIMARY, 0);
	// db.update(tableName, cv, where, null);
	//
	// where = FIELD_UUID + "=" + "'" + uuid + "'";
	// cv.clear();
	// cv.put(FIELD_PRIMARY, 1);
	// db.update(tableName, cv, where, null);
	// }

	private void updateComunication(String comId, boolean isPrimary) {
		 String tableName = Constants.TABLE_COMUNICATION;
		 SQLiteDatabase db = dbHelper.getReadableDatabase();

		 ContentValues cv = new ContentValues();
	
		 String where = FIELD_UUID + "=" + "'" + comId + "'";
		 cv.clear();
		 cv.put(FIELD_PRIMARY, isPrimary ? 1 : 0);
		 db.update(tableName, cv, where, null);
		
	}

	/**
	 * rewrites existing contact information from one recipient to another
	 * 
	 * @param fromId
	 *            - id of the user to get contacts from
	 * @param toId
	 *            - id of the user to bind contact info to.
	 */
	public void rewriteComunications(String fromId, String toId) {
		String tableName = Constants.TABLE_COMUNICATION;
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		String where = FIELD_RECIPIENT_ID + "=" + "'" + fromId + "'";
		ContentValues cv = new ContentValues();
		cv.put(FIELD_RECIPIENT_ID, toId);
		db.update(tableName, cv, where, null);
	}

	/**
	 * 
	 * @param recipientId
	 *            - uuid of the recipient to reurn comunications for.
	 * @param comType
	 * @return primary contact or any contact if primary is not set
	 */
	public Comunication getPrimaryComunication(String recipientId, String comType) {
		String tableName = Constants.TABLE_COMUNICATION;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			String where = FIELD_RECIPIENT_ID + "=" + "'" + recipientId + "' AND " + FIELD_PRIMARY + "=" + "'" + 1 + "' AND " + FIELD_TYPE + "=" + "'" + comType + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		Comunication callBackComunication = null;
		// List<Comunication> comunicationsList = new ArrayList<Comunication>();
		// Log.w(TAG, "c.getCount()c.getCount()c.getCount()c.getCount() "+c.getCount()+" "+recipientId);
		if (c.getCount() == 0) {
			String where = FIELD_RECIPIENT_ID + "=" + "'" + recipientId + "' AND " + FIELD_TYPE + "=" + "'" + comType + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		}
		if (c.getCount() == 0) {
			Log.w(TAG, "Table " + tableName + " do not contain any row with field " + FIELD_RECIPIENT_ID + "=" + "'" + recipientId + "' AND " + FIELD_TYPE + "=" + "'" + comType + "'");
		}

		else if (c.moveToFirst())
			do {
				Comunication comunication = null;
				int idColIndex = c.getColumnIndex(FIELD_UUID);
				int typeColIndex = c.getColumnIndex(FIELD_TYPE);
				int primaryColIndex = c.getColumnIndex(FIELD_PRIMARY);
				int infoColIndex = c.getColumnIndex(FIELD_CONTACT_INFO);

				String uuid = c.getString(idColIndex);

				comunication = new Comunication(uuid, recipientId);
				comunication.setType(c.getString(typeColIndex));
				comunication.setPrimaty(c.getInt(primaryColIndex) == 1);
				comunication.setInfo(c.getString(infoColIndex));

				if (comunication.isPrimaty()) {
					callBackComunication = comunication;
					break;
				}

				if (!Constants.COMUNICATION_TYPE_CONTACTS_ID.equals(comunication.getType()))
					callBackComunication = comunication;

				Log.d(TAG, "getComunicationsByRecipientId " + comunication.getType() + " info " + comunication.getInfo());
			} while (c.moveToNext());
		c.close();

		return callBackComunication;
	}

	public void makeComunicationPrimary(String uuid, String recipientId) {
		String tableName = Constants.TABLE_COMUNICATION;
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		String where = FIELD_RECIPIENT_ID + "=" + "'" + recipientId + "'";
		ContentValues cv = new ContentValues();
		cv.put(FIELD_PRIMARY, 0);
		db.update(tableName, cv, where, null);

		where = FIELD_UUID + "=" + "'" + uuid + "'";
		cv.clear();
		cv.put(FIELD_PRIMARY, 1);
		db.update(tableName, cv, where, null);
	}

	/**
	 * For debug only
	 */
	public void printComunications() {
		String tableName = Constants.TABLE_COMUNICATION;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			// String where = FIELD_CONTACT_UUID + "=" + "'"+recipientId+"'";
			c = db.query(tableName, null, null, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
		}

		// List<Comunication> comunicationsList = new ArrayList<Comunication>();
		if (c.getCount() == 0)
			Log.w(TAG, "printComunications     c.getCount() == 0");
		else if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_UUID);
				int typeColIndex = c.getColumnIndex(FIELD_TYPE);
				int primaryColIndex = c.getColumnIndex(FIELD_PRIMARY);
				int infoColIndex = c.getColumnIndex(FIELD_CONTACT_INFO);
				int recIdColIndex = c.getColumnIndex(FIELD_RECIPIENT_ID);

				String uuid = c.getString(idColIndex);

				Log.d(TAG, "printComunications " + c.getString(recIdColIndex) + " " + c.getString(typeColIndex) + " " + c.getString(infoColIndex) + " ");
			} while (c.moveToNext());
		c.close();

	}

	/**
	 * Saves contact information in DB for given recipient
	 * 
	 * @param recipientId
	 *            - recipient id
	 * @param type
	 *            - information type (could be contactsId, phone, email, facebookId or vkid)
	 * @param info
	 *            information itself (phone number, email etc.)
	 */
	public void saveComunication(String recipientId, String type, String info, boolean isPrimary) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(FIELD_UUID, UUID.randomUUID().toString());
		cv.put(FIELD_RECIPIENT_ID, recipientId);
		cv.put(FIELD_TYPE, type); //
		cv.put(FIELD_CONTACT_INFO, info);
		cv.put(FIELD_PRIMARY, isPrimary ? 1 : 0);

		// if (info.equals == null)
		// Log.e(TAG, " INFO NULL");
		// else{
		db.insert(Constants.TABLE_COMUNICATION, null, cv);
		Log.d("DATABASE", "Incerted COMUNICATION " + type + " " + info + " rec id " + recipientId + " isPrimary " + isPrimary);
		// }
	}

	/**
	 * Checks wheather exist contact info with given comInfo
	 * 
	 * @param comInfo
	 *            - Contact info to check for existance
	 * @return
	 */
	public boolean isRecipientExists(String comInfo) {
		String tableName = Constants.TABLE_COMUNICATION;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		String imageUri = null;
		try {
			String where = FIELD_CONTACT_INFO + "=" + "'" + comInfo + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + "do not exist");
			return false;
		}

		return c.getCount() != 0;
	}

	public void saveEvent(String eventId, String eventName, long date, String category, int notification, String type) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(FIELD_UUID, eventId);
		cv.put(FIELD_NAME, eventName);
		cv.put(FIELD_DATE, date); //
		cv.put(FIELD_DATE_2, getDate2(date));
		cv.put(FIELD_CATEGORY_ID, category);
		cv.put(FIELD_PUSH, 1);
		cv.put(FIELD_NOTIFICATION, notification);
		cv.put(FIELD_TYPE, type);

		db.insert(Constants.TABLE_EVENTS, null, cv);
		Log.v("DATABASE", "Incerted Event " + eventName + " " + date + " " + category + " notification " + notification);
	}

	/**
	 * Date2 represents format for dates comparison without year. monthes counts as hundreds and days as ea. F.e.: Jan 2 = 102 Mar 15 = 315
	 * 
	 * @param date
	 * @return
	 */
	private int getDate2(long date) {
		SimpleDateFormat simpleD = new SimpleDateFormat("D");
		// int month = Integer.parseInt(simpleD.format(new Date(date)));
		// simpleD = new SimpleDateFormat("DD");
		// int month = Integer.parseInt(simpleD.format(new Date(date)));
		// Log.i(TAG, "getDate2 "+simpleD.format(new Date(date))+" int "+Integer.parseInt(simpleD.format(new Date(date))));
		return Integer.parseInt(simpleD.format(new Date(date)));
	}

	public void updateEvent(String eventId, String eventName, long date, String category, int notification) {

		String tableName = Constants.TABLE_EVENTS;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(FIELD_NAME, eventName);
		cv.put(FIELD_DATE, date);
		cv.put(FIELD_DATE_2, getDate2(date));
		cv.put(FIELD_CATEGORY_ID, category);
		cv.put(FIELD_NOTIFICATION, notification);

		String where = FIELD_UUID + "=" + "'" + eventId + "'";
		db.update(tableName, cv, where, null);
		Log.v("DATABASE", "updateEvent " + eventName + " " + date + " " + category + " notification " + notification);
	}

	public void saveEventContact(String recipientId, String eventId, boolean isGroup) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(FIELD_UUID, UUID.randomUUID().toString());
		cv.put(FIELD_EVENT_ID, eventId);
		cv.put(FIELD_RECIPIENT_ID, recipientId);
		cv.put(FIELD_IS_GROUP, isGroup ? 1 : 0);

		
		db.insert(Constants.TABLE_EVENTS_RECIPIENTS, null, cv);
	}

	public void clearEventContact(String eventId) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String where = FIELD_EVENT_ID + "=" + "'" + eventId + "'";
		db.delete(Constants.TABLE_EVENTS_RECIPIENTS, where, null);
	}

	public List<Event> getEventsList() {
		String tableName = Constants.TABLE_EVENTS;
		List<Event> eventsList = new ArrayList<Event>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			c = db.query(tableName, null, null, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row");
		else if (c.moveToFirst()) {
			int idColIndex = c.getColumnIndex(FIELD_UUID);
			int nameColIndex = c.getColumnIndex(FIELD_NAME);
			int dateColIndex = c.getColumnIndex(FIELD_DATE);
			int categoryIDColIndex = c.getColumnIndex(FIELD_CATEGORY_ID);
			int typeColIndex = c.getColumnIndex(FIELD_TYPE);
			int notifColIndex = c.getColumnIndex(FIELD_NOTIFICATION);
			do {
				Event event = new Event(c.getString(idColIndex));
				event.setName(c.getString(nameColIndex));
				event.setDate(c.getLong(dateColIndex));
				event.setNotification(c.getInt(notifColIndex));
				event.setCategoryId(c.getString(categoryIDColIndex));
				event.setType(c.getString(typeColIndex));
				Log.d(TAG, "Event loaded " + event.getName() + " date: " + new Date(event.getDate()).toString() + " category id " + event.getCategoryId() + " type " + event.getType());
				eventsList.add(event);
			} while (c.moveToNext());
		}
		c.close();

		return eventsList;
	}

	public Event getEventById(String eventId) {
		String tableName = Constants.TABLE_EVENTS;

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			String where = FIELD_UUID + "=" + "'" + eventId + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		Event event = null;
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row with id " + eventId);
		else if (c.moveToFirst()) {
			int idColIndex = c.getColumnIndex(FIELD_UUID);
			int nameColIndex = c.getColumnIndex(FIELD_NAME);
			int dateColIndex = c.getColumnIndex(FIELD_DATE);
			int categoryIDColIndex = c.getColumnIndex(FIELD_CATEGORY_ID);
			int typeColIndex = c.getColumnIndex(FIELD_TYPE);
			int notifColIndex = c.getColumnIndex(FIELD_NOTIFICATION);

			event = new Event(c.getString(idColIndex));
			event.setName(c.getString(nameColIndex));
			event.setDate(c.getLong(dateColIndex));
			event.setCategoryId(c.getString(categoryIDColIndex));
			event.setType(c.getString(typeColIndex));
			event.setNotification(c.getInt(notifColIndex));
			Log.d(TAG, "Event loaded " + event.getName() + " date: " + event.getDate() + " category id " + event.getCategoryId() + " type " + event.getType());

		}

		c.close();

		return event;

	}

	public void saveNewEventAsynk(final Event event) {
		new AsyncTask<Params, String, Result>() {
			@Override
			protected Result doInBackground(Params... params) {

				String eventId = UUID.randomUUID().toString();
				saveEvent(eventId, event.getName(), event.getDate(), event.getCategoryId(), event.getRepeat(), event.getType());
				return null;
			}
		}.execute();
	}

	/**
	 * Removes event from tables EVENTS, EVENTS_RECIPIENTS
	 * 
	 * @param fileName
	 */
	public void removeEvent(String eventId) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int removed = db.delete(Constants.TABLE_EVENTS, FIELD_UUID + " = " + "'" + eventId + "'", null);

		Log.d(TAG, "removing event from DB " + eventId + " successfull " + (removed == 1));
		db.close();
	}

	/**
	 * 
	 * @param eventId
	 * @return pushId of current event, or "-1" if not specified.
	 */
	public int getNotificationId(String eventId) {
		String tableName = Constants.TABLE_EVENTS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		Cursor c = null;
		try {
			String where = FIELD_UUID + "=" + eventId;
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return -1;
		}

		int pushId = -1;
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row");
		else if (c.moveToFirst()) {
			int pushIdColIndex = c.getColumnIndex(FIELD_PUSH);
			pushId = c.getInt(pushIdColIndex);

			Log.d(TAG, "getNotificationId " + pushId);
		}

		c.close();

		return pushId;
	}

	/**
	 * @param id
	 *            - id of the category to oad from DB
	 * @return Category with specified Id.
	 */
	public Category getCategoryById(String id) {
		String tableName = Constants.TABLE_CATEGORY;

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			String where = FIELD_UUID + "=" + id;
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		Category category = null;// new Category();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row with field " + FIELD_UUID + "=" + "'" + id + "'");
		else if (c.moveToFirst()) {
			int idColIndex = c.getColumnIndex(FIELD_UUID);
			int nameColIndex = c.getColumnIndex(FIELD_NAME);

			// category.setId(c.getString(idColIndex));
			// category.setName(c.getString(nameColIndex));
			category = new Category(c.getString(idColIndex), c.getString(nameColIndex), null);
		}
		c.close();

		return category;
	}

/**
	 * Returns list of Categories for specified group
	 * @param groupId - id of the group to retrieve categories from
	 * @return List'<'Category'>' of specified group
	 */
	public List<Category> getCategoriesList(String groupId) {
		String tableName = Constants.TABLE_CATEGORY;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			String where = FIELD_CATEGORY_ID + "=" + groupId;

			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		List<Category> categoriesList = new ArrayList<Category>();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row with field " + FIELD_CATEGORY_ID + "=" + groupId);
		else if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_UUID);
				int nameColIndex = c.getColumnIndex(FIELD_NAME);
				int categoryGroupIdColIndex = c.getColumnIndex(FIELD_CATEGORY_ID);

				Category category = new Category(c.getString(idColIndex), c.getString(nameColIndex), c.getString(categoryGroupIdColIndex));
				// category.setId(c.getString(idColIndex));
				// category.setName(c.getString(nameColIndex));
				// category.setGroupId(c.getString(categoryGroupIdColIndex));
				categoriesList.add(category);
			} while (c.moveToNext());
		c.close();

		return categoriesList;
	}

	public List<String> getCategoryGroupsIds() {
		String tableName = Constants.TABLE_CATEGORY_GROUP;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			c = db.query(tableName, null, null, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		List<String> ids = new ArrayList<String>();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row");
		else if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_UUID);

				ids.add(c.getString(idColIndex));
				;
			} while (c.moveToNext());
		c.close();

		return ids;
	}

	public List<String> getCategoryIds() {
		String tableName = Constants.TABLE_CATEGORY;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			c = db.query(tableName, null, null, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		List<String> ids = new ArrayList<String>();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row");
		else if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_UUID);

				ids.add(c.getString(idColIndex));
				;
			} while (c.moveToNext());
		c.close();

		return ids;
	}

	public List<CategoryGroup> getCategoryGroupsList() {
		String tableName = Constants.TABLE_CATEGORY_GROUP;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {

			c = db.query(tableName, null, null, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		Log.v(TAG, tableName + " " + Arrays.toString(c.getColumnNames()));
		List<CategoryGroup> categoriesList = new ArrayList<CategoryGroup>();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any rows");
		else if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_UUID);
				int nameColIndex = c.getColumnIndex(FIELD_NAME);
				try {
					int coverColIndex = c.getColumnIndex(FIELD_COVER_IMAGE);
					CategoryGroup category = new CategoryGroup(c.getString(idColIndex), c.getString(nameColIndex), c.getString(coverColIndex));

					categoriesList.add(category);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// category.setId(c.getString(idColIndex));
				// category.setName(c.getString(nameColIndex));
			} while (c.moveToNext());
		c.close();

		if (c.getCount() == 0)
			Log.w(TAG, "No categories stored in DB");

		return categoriesList;
	}

	public void saveCategory(String id, String name, String groupId) {
		// saveItem(Constants.TABLE_CATEGORY, id, name, groupId, coverImage);
		String tableName = Constants.TABLE_CATEGORY;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		String eventId = UUID.randomUUID().toString();
		cv.put(FIELD_UUID, id);
		cv.put(FIELD_NAME, name);
		cv.put(FIELD_CATEGORY_ID, groupId);

		Log.d(TAG, "save CategoryGroup");
		db.insert(tableName, null, cv);
	}

	public void saveCategoryGroup(String id, String name, String coverImage) {
		// saveItem(Constants.TABLE_CATEGORY_GROUP, id, name, null, coverImage);
		String tableName = Constants.TABLE_CATEGORY_GROUP;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		String eventId = UUID.randomUUID().toString();
		cv.put(FIELD_UUID, id);
		cv.put(FIELD_NAME, name);
		if (coverImage != null)
			cv.put(FIELD_COVER_IMAGE, coverImage);
		// if (categoryId != null) cv.put(FIELD_CATEGORY_ID, categoryId);

		Log.d(TAG, "save CategoryGroup");
		db.insert(tableName, null, cv);
	}

	// private void saveItem(String tableName, String id, String name, String categoryId, String coverImage){
	// SQLiteDatabase db = dbHelper.getWritableDatabase();
	//
	// ContentValues cv = new ContentValues();
	// String eventId = UUID.randomUUID().toString();
	// cv.put(FIELD_UUID, id);
	// cv.put(FIELD_NAME, name);
	// cv.put(FIELD_COVER_IMAGE, coverImage);
	// if (categoryId != null) cv.put(FIELD_CATEGORY_ID, categoryId);
	//
	// Log.d(TAG, "save CategoryGroup")
	// db.insert(tableName, null, cv);
	// }

	public List<Theme> getThemesList(String categoryId) {
		String tableName = Constants.TABLE_THEMES;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {

			String where = FIELD_CATEGORY_ID + "=" + categoryId;
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		List<Theme> themesList = new ArrayList<Theme>();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row with field " + FIELD_CATEGORY_ID + "=" + categoryId);
		else if (c.moveToFirst())
			do {
				Theme theme = getTheme(c);

				themesList.add(theme);

				// System.out.println("getThemesList name "+name+" id "+id+" categoryId "+categoryId);
			} while (c.moveToNext());
		c.close();

		return themesList;
	}

	/**
	 * Returns textPack for given purchaseId
	 * 
	 * @param purchaseId
	 * @return textPack for given purchaseId or null if not found
	 */
	public Theme getThemeByPurchaseId(String purchaseId) {
		String tableName = Constants.TABLE_THEMES;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {

			String where = FIELD_PURCHASE_ID + "=" + purchaseId;
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		Theme theme = null;
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain row with field " + FIELD_PURCHASE_ID + "=" + purchaseId);
		else if (c.moveToFirst()) {
			theme = getTheme(c);

			// themesList.add(theme);

			// Log.v(TAG,"getTheme name "+name+" id "+id+" categoryId "+categoryId+" purchaseId "+purchaseId);
		}
		c.close();

		return theme;
	}

	/**
	 * Returns textPack for given purchaseId
	 * 
	 * @param purchaseId
	 * @return textPack for given purchaseId or null if not found
	 */
	public Theme getTheme(String themeId) {
		String tableName = Constants.TABLE_THEMES;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {

			String where = FIELD_UUID + "=" + themeId;
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		Theme theme = null;
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain row with field " + FIELD_UUID + "=" + themeId);
		else if (c.moveToFirst()) {
			theme = getTheme(c);
		}
		c.close();

		return theme;
	}

	private Theme getTheme(Cursor c) {
		int idColIndex = c.getColumnIndex(FIELD_UUID);
		int nameColIndex = c.getColumnIndex(FIELD_NAME);

		int categoryIDColIndex = c.getColumnIndex(FIELD_CATEGORY_ID);
		int purchaseIDColIndex = c.getColumnIndex(FIELD_PURCHASE_ID);
		int authorColIndex = c.getColumnIndex(FIELD_AUTOR);
		int priceColIndex = c.getColumnIndex(FIELD_PRICE);
		int boughtColIndex = c.getColumnIndex(FIELD_BOUGHT);
		int coverImageColIndex = c.getColumnIndex(FIELD_COVER_IMAGE);
		int eCardImageColIndex = c.getColumnIndex(FIELD_ECARD_IMAGE);
		int squareImageColIndex = c.getColumnIndex(FIELD_SQUARE_IMAGE);
		int postCardFrontImageColIndex = c.getColumnIndex(FIELD_CARD_FRONT_IMAGE);
		int postCardBackImageColIndex = c.getColumnIndex(FIELD_CARD_BACK_IMAGE);

		int eCardThumbColIndex = c.getColumnIndex(FIELD_ECARD_THUMB);
		int squareThumbColIndex = c.getColumnIndex(FIELD_SQUARE_THUMB);
		int postCardFrontThumbColIndex = c.getColumnIndex(FIELD_CARD_FRONT_THUMB);
		int postCardBackThumbColIndex = c.getColumnIndex(FIELD_CARD_BACK_THUMB);

		int eTextTopColIndex = c.getColumnIndex(FIELD_ETEXT_TOP);
		int eTextLeftColIndex = c.getColumnIndex(FIELD_ETEXT_LEFT);
		int pTextTopColIndex = c.getColumnIndex(FIELD_PTEXT_TOP);
		int pTextLeftColIndex = c.getColumnIndex(FIELD_PTEXT_LEFT);
		int textColorRedColIndex = c.getColumnIndex(FIELD_TEXT_COLOR_RED);
		int textColorGreenColIndex = c.getColumnIndex(FIELD_TEXT_COLOR_GREEN);
		int textColorBlueColIndex = c.getColumnIndex(FIELD_TEXT_COLOR_BLUE);

		String id = c.getString(idColIndex);
		String name = c.getString(nameColIndex);

		Theme theme = new Theme(id, name);

		theme.setCategoryId(c.getString(categoryIDColIndex));
		theme.setPurchaseId(c.getString(purchaseIDColIndex));
		theme.setAuthor(c.getString(authorColIndex));
		theme.setPrice(c.getFloat(priceColIndex));
		theme.setBought(c.getInt(boughtColIndex) == 1);
		theme.setCoverImage(c.getString(coverImageColIndex));
		theme.setECardImage(c.getString(eCardImageColIndex));
		theme.setSquareImage(c.getString(squareImageColIndex));
		theme.setPostCardFrontImage(c.getString(postCardFrontImageColIndex));
		theme.setPostCardBackImage(c.getString(postCardBackImageColIndex));

		theme.setECardThumb(c.getString(eCardThumbColIndex));
		theme.setSquareThumb(c.getString(squareThumbColIndex));
		theme.setPostCardFrontThumb(c.getString(postCardFrontThumbColIndex));
		theme.setPostCardBackThumb(c.getString(postCardBackThumbColIndex));

		theme.setETextTop(c.getInt(eTextTopColIndex));
		theme.setETextLeft(c.getInt(eTextLeftColIndex));
		theme.setPTextTop(c.getInt(pTextTopColIndex));
		theme.setPTextLeft(c.getInt(pTextLeftColIndex));
		theme.setTextColorRed(c.getInt(textColorRedColIndex));
		theme.setTextColorGreen(c.getInt(textColorGreenColIndex));
		theme.setTextColorBlue(c.getInt(textColorBlueColIndex));
		return theme;
	}

	public void purchaseTheme(String purchaseId) {
		// getTheme(themeId);
		String tableName = Constants.TABLE_THEMES;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(FIELD_BOUGHT, 1);
		db.update(tableName, cv, FIELD_PURCHASE_ID + "=" + purchaseId, null);
		Log.d(TAG, "Purchase of the theme " + purchaseId + " saved in Data Base");

		// getTheme(themeId);
	}

	public void purchaseTextPack(String purchaseId) {
		String tableName = Constants.TABLE_TEXT_PACKS;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(FIELD_BOUGHT, 1);
		db.update(tableName, cv, FIELD_PURCHASE_ID + "=" + purchaseId, null);
		Log.d(TAG, "Purchase of the textPack " + purchaseId + " saved in Data Base");
	}

	public void saveTheme(String id, String name, String categoryId, String purchaseID, String author, String price, String bought, String coverImage, String eCardImage, String eCardThumb,
			String squareImage, String squareThumb, String postCardFrontImage, String postCardFrontThumb, String postCardBackImage, String postCardBackThumb, String eTextTop, String eTextLeft,
			String pTextTop, String pTextLeft, String textColorRed, String textColorGreen, String textColorBlue) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// eCardThumb = o.getString("ECardThumb");
		// String squareThumb = o.getString("SquareThumb");
		// String postCardFrontThumb

		String tableName = Constants.TABLE_THEMES;

		ContentValues cv = new ContentValues();
		String eventId = UUID.randomUUID().toString();
		cv.put(FIELD_UUID, id);
		cv.put(FIELD_NAME, name);

		cv.put(FIELD_CATEGORY_ID, categoryId);
		// + FIELD_CATEGORY_ID + " text"

		cv.put(FIELD_PURCHASE_ID, purchaseID);
		cv.put(FIELD_AUTOR, author);
		cv.put(FIELD_PRICE, price);
		cv.put(FIELD_BOUGHT, bought);
		cv.put(FIELD_COVER_IMAGE, coverImage);
		cv.put(FIELD_ECARD_IMAGE, eCardImage);
		cv.put(FIELD_SQUARE_IMAGE, squareImage);

		cv.put(FIELD_ECARD_THUMB, eCardThumb);
		cv.put(FIELD_SQUARE_THUMB, squareThumb);
		cv.put(FIELD_CARD_FRONT_THUMB, postCardFrontThumb);
		cv.put(FIELD_CARD_BACK_THUMB, postCardBackThumb);

		cv.put(FIELD_CARD_FRONT_IMAGE, postCardFrontImage);
		cv.put(FIELD_CARD_BACK_IMAGE, postCardBackImage);
		cv.put(FIELD_ETEXT_TOP, eTextTop);
		cv.put(FIELD_ETEXT_LEFT, eTextLeft);
		cv.put(FIELD_PTEXT_TOP, pTextTop);
		cv.put(FIELD_PTEXT_LEFT, pTextLeft);
		cv.put(FIELD_TEXT_COLOR_RED, textColorRed);
		cv.put(FIELD_TEXT_COLOR_GREEN, textColorGreen);
		cv.put(FIELD_TEXT_COLOR_BLUE, textColorBlue);

		db.insert(tableName, null, cv);
		Log.w("DATABASE", "Incerted THEME " + id + " " + name + " " + categoryId);

	}

	public void saveTheme(Theme theme) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		// eCardThumb = o.getString("ECardThumb");
		// String squareThumb = o.getString("SquareThumb");
		// String postCardFrontThumb

		String tableName = Constants.TABLE_THEMES;

		ContentValues cv = new ContentValues();
		String eventId = UUID.randomUUID().toString();
		cv.put(FIELD_UUID, theme.getId());
		cv.put(FIELD_NAME, theme.getName());

		cv.put(FIELD_CATEGORY_ID, theme.getCategoryId());
		// + FIELD_CATEGORY_ID + " text"

		cv.put(FIELD_PURCHASE_ID, theme.getPurchaseId());
		cv.put(FIELD_AUTOR, theme.getAuthor());
		cv.put(FIELD_PRICE, theme.getPrice());
		cv.put(FIELD_BOUGHT, theme.isBought());

		cv.put(FIELD_CARD_FRONT_IMAGE, theme.getPostCardFrontImage());
		cv.put(FIELD_CARD_BACK_IMAGE, theme.getPostCardBackImage());
		cv.put(FIELD_COVER_IMAGE, theme.getCoverImage());
		cv.put(FIELD_ECARD_IMAGE, theme.getECardImage());
		cv.put(FIELD_SQUARE_IMAGE, theme.getSquareImage());

		cv.put(FIELD_ECARD_THUMB, theme.getECardThumb());
		cv.put(FIELD_SQUARE_THUMB, theme.getSquareThumb());
		cv.put(FIELD_CARD_FRONT_THUMB, theme.getPostCardFrontThumb());
		cv.put(FIELD_CARD_BACK_THUMB, theme.getPostCardBackThumb());

		cv.put(FIELD_ETEXT_TOP, theme.getETextTop());
		cv.put(FIELD_ETEXT_LEFT, theme.getETextLeft());
		cv.put(FIELD_PTEXT_TOP, theme.getpTextTop());
		cv.put(FIELD_PTEXT_LEFT, theme.getpTextLeft());
		cv.put(FIELD_TEXT_COLOR_RED, theme.getTextColorRed());
		cv.put(FIELD_TEXT_COLOR_GREEN, theme.getTextColorGreen());
		cv.put(FIELD_TEXT_COLOR_BLUE, theme.getTextColorBlue());

		db.insert(tableName, null, cv);
		// Log.w("DATABASE", "Incerted THEME " + id + " " + name+" "+categoryId);

	}

	// public Theme getTheme(String themeId) {
	// String tableName = Constants.TABLE_THEMES;
	// SQLiteDatabase db = dbHelper.getReadableDatabase();
	// Theme theme = null;
	// Cursor c = null;
	// try {
	//
	// String where = FIELD_UUID + "=" + themeId;
	// c = db.query(tableName, null, where, null, null, null, null);
	// // System.out.println("getTheme c.getCount() "+c.getCount());
	// } catch (SQLiteException e) {
	// Log.w(TAG, tableName + " do not exist");
	// return null;
	// }
	// if (c.getCount() == 0)
	// Log.w(TAG, "Table "+tableName+" do not contain any row with field "+FIELD_UUID + "=" + themeId);
	// else
	// if (c.moveToFirst()) {
	// // int idColIndex = c.getColumnIndex(FIELD_UUID);
	// int nameColIndex = c.getColumnIndex(FIELD_NAME);
	// int categoryIDColIndex = c.getColumnIndex(FIELD_CATEGORY_ID);
	//
	// int purchaseIDColIndex = c.getColumnIndex(FIELD_PURCHASE_ID);
	// int authorColIndex = c.getColumnIndex(FIELD_AUTOR);
	// int priceColIndex = c.getColumnIndex(FIELD_PRICE);
	// int boughtColIndex = c.getColumnIndex(FIELD_BOUGHT);
	// int coverImageColIndex = c.getColumnIndex(FIELD_COVER_IMAGE);
	// int eCardImageColIndex = c.getColumnIndex(FIELD_ECARD_IMAGE);
	// int squareImageColIndex = c.getColumnIndex(FIELD_SQUARE_IMAGE);
	// int postCardFrontImageColIndex = c.getColumnIndex(FIELD_CARD_FRONT_IMAGE);
	// int postCardBackImageColIndex = c.getColumnIndex(FIELD_CARD_BACK_IMAGE);
	//
	// int eCardThumbColIndex = c.getColumnIndex(FIELD_ECARD_THUMB);
	// int squareThumbColIndex = c.getColumnIndex(FIELD_SQUARE_THUMB);
	// int postCardFrontThumbColIndex = c.getColumnIndex(FIELD_CARD_FRONT_THUMB);
	// int postCardBackThumbColIndex = c.getColumnIndex(FIELD_CARD_BACK_THUMB);
	//
	// int eTextTopColIndex = c.getColumnIndex(FIELD_ETEXT_TOP);
	// int eTextLeftColIndex = c.getColumnIndex(FIELD_ETEXT_LEFT);
	// int pTextTopColIndex = c.getColumnIndex(FIELD_PTEXT_TOP);
	// int pTextLeftColIndex = c.getColumnIndex(FIELD_PTEXT_LEFT);
	// int textColorRedColIndex = c.getColumnIndex(FIELD_TEXT_COLOR_RED);
	// int textColorGreenColIndex = c.getColumnIndex(FIELD_TEXT_COLOR_GREEN);
	// int textColorBlueColIndex = c.getColumnIndex(FIELD_TEXT_COLOR_BLUE);
	//
	//
	// String name = c.getString(nameColIndex);
	// String categoryId = c.getString(categoryIDColIndex);
	//
	// theme = new Theme(themeId, name);
	//
	// theme.setCategoryId(categoryId);
	// theme.setPurchaseId(c.getString(purchaseIDColIndex));
	// theme.setAuthor(c.getString(authorColIndex));
	// theme.setPrice(c.getFloat(priceColIndex));
	// theme.setBought(c.getInt(boughtColIndex)== 1);
	// theme.setCoverImage(c.getString(coverImageColIndex));
	// theme.setECardImage(c.getString(eCardImageColIndex));
	// theme.setSquareImage(c.getString(squareImageColIndex));
	// theme.setPostCardFrontImage(c.getString(postCardFrontImageColIndex));
	// theme.setPostCardBackImage(c.getString(postCardBackImageColIndex));
	//
	// theme.setECardThumb(c.getString(eCardThumbColIndex));
	// theme.setSquareThumb(c.getString(squareThumbColIndex));
	// theme.setPostCardFrontThumb(c.getString(postCardFrontThumbColIndex));
	// theme.setPostCardBackThumb(c.getString(postCardBackThumbColIndex));
	//
	// theme.setETextTop(c.getInt(eTextTopColIndex));
	// theme.setETextLeft(c.getInt(eTextLeftColIndex));
	// theme.setPTextTop(c.getInt(pTextTopColIndex));
	// theme.setPTextLeft(c.getInt(pTextLeftColIndex));
	// theme.setTextColorRed(c.getInt(textColorRedColIndex));
	// theme.setTextColorGreen(c.getInt(textColorGreenColIndex));
	// theme.setTextColorBlue(c.getInt(textColorBlueColIndex));
	//
	// // theme.add(theme);
	//
	// // System.out.println("getTheme: name "+name+" id "+themeId+" categoryId "+categoryId+" bought "+theme.isBought()+ " bachThump "+ theme.getPostCardBackThumb());
	// }
	// c.close();
	//
	// return theme;
	// }

	public void saveTextPack(String id, String name, String description, String categoryId, String themeId, String purchaseId, double price, boolean bought, String coverImage) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = Constants.TABLE_TEXT_PACKS;

		ContentValues cv = new ContentValues();
		cv.put(FIELD_UUID, id);
		cv.put(FIELD_NAME, name);
		cv.put(FIELD_DESCRIPTION, description);
		cv.put(FIELD_CATEGORY_ID, categoryId);
		cv.put(FIELD_THEME_ID, themeId);
		cv.put(FIELD_PURCHASE_ID, purchaseId);
		cv.put(FIELD_PRICE, price);
		cv.put(FIELD_BOUGHT, bought);
		cv.put(FIELD_COVER_IMAGE, coverImage);

		db.insert(tableName, null, cv);
		Log.w("DATABASE", "saveTextPack " + id + " " + name + " " + price + " ~~~~~~~~~~~~~~~~~~~~~~~~~~ description " + description);

	}

	public void saveTextPack(TextPack textPack) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = Constants.TABLE_TEXT_PACKS;

		ContentValues cv = new ContentValues();
		cv.put(FIELD_UUID, textPack.getUuid());
		cv.put(FIELD_NAME, textPack.getName());
		cv.put(FIELD_DESCRIPTION, textPack.getDescription());
		// cv.put(FIELD_CATEGORY_ID, textPack.getCategoryId());
		// cv.put(FIELD_THEME_ID, textPack.getThemeId());
		cv.put(FIELD_PURCHASE_ID, textPack.getPurchaseId());
		cv.put(FIELD_PRICE, textPack.getPrice());
		cv.put(FIELD_BOUGHT, textPack.isBought());
		cv.put(FIELD_COVER_IMAGE, textPack.getCoverImage());

		db.insert(tableName, null, cv);
		// Log.w("DATABASE", "saveTextPack " + id + " " + name+" "+price+" ~~~~~~~~~~~~~~~~~~~~~~~~~~ description "+description);

	}

	public void saveText(String id, String textPackId, String textName, String textString) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = Constants.TABLE_TEXTS;

		ContentValues cv = new ContentValues();
		cv.put(FIELD_UUID, id);
		cv.put(FIELD_NAME, textName);
		cv.put(FIELD_TEXT_PACK_ID, textPackId);
		cv.put(FIELD_TEXT_STRING, textString);

		db.insert(tableName, null, cv);
		// Log.w("DATABASE", "Incerted THEME " + id + " " + textName+" "+textPackId+" ~~~~~~~~~~~~~~~~~~~~~~~~~~ "+textString);
	}

	public List<TextPack> getTextPacksListByCategoryId(String categoryId) {
		String tableName = Constants.TABLE_TEXT_PACKS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {

			String where = FIELD_CATEGORY_ID + "=" + "'" + categoryId + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		List<TextPack> textPacksList = new ArrayList<TextPack>();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row with field " + FIELD_CATEGORY_ID + "=" + "'" + categoryId + "'");
		else if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_UUID);
				int nameColIndex = c.getColumnIndex(FIELD_NAME);
				int descrColIndex = c.getColumnIndex(FIELD_DESCRIPTION);
				int purchaseIdColIndex = c.getColumnIndex(FIELD_PURCHASE_ID);
				int priceColIndex = c.getColumnIndex(FIELD_PRICE);
				int boughtColIndex = c.getColumnIndex(FIELD_BOUGHT);
				int imageColIndex = c.getColumnIndex(FIELD_COVER_IMAGE);

				TextPack textPack = new TextPack(c.getString(idColIndex), c.getString(nameColIndex));
				textPack.setDescription(c.getString(descrColIndex));
				textPack.setPurchaseId(c.getString(purchaseIdColIndex));
				textPack.setPrice(c.getFloat(priceColIndex));
				textPack.setBought(c.getInt(boughtColIndex) == 1);
				textPack.setCoverImage(c.getString(imageColIndex));

				textPacksList.add(textPack);

				Log.d("DATABASE", "getTextPacksListByCategoryId " + textPack.getName() + " id " + categoryId);
				Log.i("DATABASE", " price " + textPack.getPrice() + " purchaseId " + textPack.getPurchaseId() + " image " + textPack.getCoverImage() + " description " + textPack.getDescription());

				getTextsListByPackId(textPack.getUuid());

			} while (c.moveToNext());
		c.close();

		return textPacksList;
	}

	public List<TextPack> getTextPacksListByThemeId(String themeId) {
		String tableName = Constants.TABLE_TEXT_PACKS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {

			String where = FIELD_THEME_ID + "=" + "'" + themeId + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		List<TextPack> textPacksList = new ArrayList<TextPack>();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row with field " + FIELD_THEME_ID + "=" + "'" + themeId + "'");
		else if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_UUID);
				int nameColIndex = c.getColumnIndex(FIELD_NAME);
				int descrColIndex = c.getColumnIndex(FIELD_DESCRIPTION);
				int purchaseIdColIndex = c.getColumnIndex(FIELD_PURCHASE_ID);
				int priceColIndex = c.getColumnIndex(FIELD_PRICE);
				int boughtColIndex = c.getColumnIndex(FIELD_BOUGHT);
				int imageColIndex = c.getColumnIndex(FIELD_COVER_IMAGE);

				TextPack textPack = new TextPack(c.getString(idColIndex), c.getString(nameColIndex));
				textPack.setDescription(c.getString(descrColIndex));
				textPack.setPurchaseId(c.getString(purchaseIdColIndex));
				textPack.setPrice(c.getFloat(priceColIndex));
				textPack.setBought(c.getInt(boughtColIndex) == 1);
				textPack.setCoverImage(c.getString(imageColIndex));

				textPacksList.add(textPack);

				Log.d("DATABASE", "getTextPacksListByThemeId " + textPack.getName() + " id " + themeId);
				Log.i("DATABASE", " price " + textPack.getPrice() + " purchaseId " + textPack.getPurchaseId() + " image " + textPack.getCoverImage() + " description " + textPack.getDescription());

				getTextsListByPackId(textPack.getUuid());

			} while (c.moveToNext());
		c.close();

		return textPacksList;
	}

	/**
	 * Returns textPack for given purchaseId
	 * 
	 * @param purchaseId
	 * @return textPack for given purchaseId or null if not found
	 */
	public TextPack getTextPackByPurchaseId(String purchaseId) {
		String tableName = Constants.TABLE_TEXT_PACKS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {

			String where = FIELD_PURCHASE_ID + "=" + "'" + purchaseId + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		TextPack textPack = null;
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row with field " + FIELD_PURCHASE_ID + "=" + "'" + purchaseId + "'");
		else if (c.moveToFirst()) {
			int idColIndex = c.getColumnIndex(FIELD_UUID);
			int nameColIndex = c.getColumnIndex(FIELD_NAME);
			int descrColIndex = c.getColumnIndex(FIELD_DESCRIPTION);
			int purchaseIdColIndex = c.getColumnIndex(FIELD_PURCHASE_ID);
			int priceColIndex = c.getColumnIndex(FIELD_PRICE);
			int boughtColIndex = c.getColumnIndex(FIELD_BOUGHT);
			int imageColIndex = c.getColumnIndex(FIELD_COVER_IMAGE);

			textPack = new TextPack(c.getString(idColIndex), c.getString(nameColIndex));
			textPack.setDescription(c.getString(descrColIndex));
			textPack.setPurchaseId(c.getString(purchaseIdColIndex));
			textPack.setPrice(c.getFloat(priceColIndex));
			textPack.setBought(c.getInt(boughtColIndex) == 1);
			textPack.setCoverImage(c.getString(imageColIndex));

			Log.d("DATABASE", "getTextPackByPurchaseId " + textPack.getName() + " id " + purchaseId);
			Log.i("DATABASE", " price " + textPack.getPrice() + " purchaseId " + textPack.getPurchaseId() + " image " + textPack.getCoverImage() + " description " + textPack.getDescription());

		}
		c.close();

		return textPack;
	}

	public List<Text> getTextsListByPackId(String packId) {
		String tableName = Constants.TABLE_TEXTS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			String where = FIELD_TEXT_PACK_ID + "=" + "'" + packId + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + "do not exist");
			return null;
		}

		List<Text> textsList = new ArrayList<Text>();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row with field " + FIELD_TEXT_PACK_ID + "=" + "'" + packId + "'");
		else if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_UUID);
				int nameColIndex = c.getColumnIndex(FIELD_NAME);
				int textStringColIndex = c.getColumnIndex(FIELD_TEXT_STRING);

				Text textPack = new Text(c.getString(idColIndex), c.getString(nameColIndex), c.getString(textStringColIndex));
				Log.i("DATABASE", "getTextsListByPackId id" + c.getString(idColIndex) + " name " + c.getString(nameColIndex) + " text: " + c.getString(textStringColIndex));

				textsList.add(textPack);
			} while (c.moveToNext());
		c.close();

		return textsList;
	}

	//
	public void saveAppeal(String id, String maleAppeal, String femaleAppeal) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = Constants.TABLE_APPEALS;

		ContentValues cv = new ContentValues();
		cv.put(FIELD_UUID, id);
		cv.put(FIELD_APPEAL_MALE, maleAppeal);
		cv.put(FIELD_APPEAL_FEMALE, femaleAppeal);

		db.insert(tableName, null, cv);
		// Log.w("DATABASE", "Incerted THEME " + id + " " + textName+" "+textPackId+" ~~~~~~~~~~~~~~~~~~~~~~~~~~ "+textString);
	}

	public List<Appeal> getAppealsList() {
		String tableName = Constants.TABLE_APPEALS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			c = db.query(tableName, null, null, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + "do not exist");
			return null;
		}

		List<Appeal> appealsList = new ArrayList<Appeal>();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row ");
		else if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_UUID);
				int maleColIndex = c.getColumnIndex(FIELD_APPEAL_MALE);
				int femaleColIndex = c.getColumnIndex(FIELD_APPEAL_FEMALE);

				Appeal appeal = new Appeal(c.getString(idColIndex), c.getString(maleColIndex), c.getString(femaleColIndex));
				// Log.d("DATABASE", "getAppealsList id" + c.getString(idColIndex) + " male " + c.getString(maleColIndex)+" female "+c.getString(femaleColIndex));

				// Appeal appeal = new Appeal(id, maleAppeal, femaleAppeal);

				appealsList.add(appeal);
			} while (c.moveToNext());
		c.close();

		return appealsList;
	}

	public Appeal getAppealById(String appealId) {
		String tableName = Constants.TABLE_APPEALS;

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {

			String where = FIELD_UUID + "=" + "'" + appealId + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}

		Appeal appeal = null;
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row with field " + FIELD_UUID + "=" + "'" + appealId + "'");
		else if (c.moveToFirst()) {
			int idColIndex = c.getColumnIndex(FIELD_UUID);
			int maleColIndex = c.getColumnIndex(FIELD_APPEAL_MALE);
			int femaleColIndex = c.getColumnIndex(FIELD_APPEAL_FEMALE);

			appeal = new Appeal(c.getString(idColIndex), c.getString(maleColIndex), c.getString(femaleColIndex));

		}
		c.close();

		return appeal;
	}

	public void clearCalendarTable() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String where = FIELD_TYPE + "=" + "'" + Constants.EVENT_TYPE_COMMON_HOLIDAYS + "'";
		db.delete(Constants.TABLE_EVENTS, where, null);
	}

	public void clearVersion(String data) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String where = FIELD_DATA + "=" + "'" + data + "'";
		db.delete(Constants.TABLE_VERSIONS, where, null);
	}

	public void clearCategoryGroupsTable() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(Constants.TABLE_CATEGORY_GROUP, null, null);
	}

	public void clearAppealsTable() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(Constants.TABLE_APPEALS, null, null);
	}

	public void clearCategoriesTable() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(Constants.TABLE_CATEGORY, null, null);
	}

	public void clearThemesTable() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(Constants.TABLE_THEMES, null, null);
	}

	public void clearTextsTable() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(Constants.TABLE_TEXT_PACKS, null, null);
		db.delete(Constants.TABLE_TEXTS, null, null);
	}

	public void saveProject(String id, String name, String themeId, String eventId, String appealId, String text, String sigText, String sigFileName) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(FIELD_UUID, id); // UUID.randomUUID().toString()
		cv.put(FIELD_NAME, name);
		cv.put(FIELD_THEME_ID, themeId);
		cv.put(FIELD_EVENT_ID, eventId); //
		cv.put(FIELD_TEXT, text);
		cv.put(FIELD_APPEALS_ID, appealId);
		cv.put(FIELD_SIGNATURE_TEXT, sigText);
		cv.put(FIELD_SIGNATURE_BITMAP_ID, sigFileName);

		db.insert(Constants.TABLE_PROJECTS, null, cv);

		Log.d(TAG, "Incerted PROJECT " + name);

	}

	/**
	 * Returns list of phones,emails and ids of social networks. Attention! It will not include comunication type Contacts_ID.
	 * 
	 * @param recipientId
	 *            - uuid of the recipient to reurn comunications for.
	 * @return
	 */
	public List<Project> getProjectsList() {
		String tableName = Constants.TABLE_PROJECTS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			// String where = FIELD_RECIPIENT_UUID + "=" + "'"+recipientId+"'";
			c = db.query(tableName, null, null, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}
		List<Project> projectsList = new ArrayList<Project>();
		if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_UUID);
				int nameColIndex = c.getColumnIndex(FIELD_NAME);
				int themeIdColIndex = c.getColumnIndex(FIELD_THEME_ID);

				int eventIdColIndex = c.getColumnIndex(FIELD_EVENT_ID);
				int appealsIdColIndex = c.getColumnIndex(FIELD_APPEALS_ID);
				int textColIndex = c.getColumnIndex(FIELD_TEXT);
				int signatureTextColIndex = c.getColumnIndex(FIELD_SIGNATURE_TEXT);
				int signatureBitmapUriColIndex = c.getColumnIndex(FIELD_SIGNATURE_BITMAP_ID);

				String eventId = c.getString(eventIdColIndex);
				String appealsId = c.getString(appealsIdColIndex);
				String text = c.getString(textColIndex);
				String signatureText = c.getString(signatureTextColIndex);
				String signatureBitmapUri = c.getString(signatureBitmapUriColIndex);

				Project project = new Project(c.getString(idColIndex), c.getString(nameColIndex), c.getString(themeIdColIndex));
				project.setEventId(eventId);
				project.setAppealsId(appealsId);
				project.setText(text);
				project.setSignatureText(signatureText);
				project.setSignatureBitmapUri(signatureBitmapUri);

				projectsList.add(project);
			} while (c.moveToNext());
		return projectsList;
	}

	public Project getProjectById(String projectId) {
		String tableName = Constants.TABLE_PROJECTS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			String where = FIELD_UUID + "=" + "'" + projectId + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}
		// List<Project> projectsList = new ArrayList<Project>();
		Project project = null;
		if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_UUID);
				int nameColIndex = c.getColumnIndex(FIELD_NAME);
				int themeIdColIndex = c.getColumnIndex(FIELD_THEME_ID);

				int eventIdColIndex = c.getColumnIndex(FIELD_EVENT_ID);
				int appealsIdColIndex = c.getColumnIndex(FIELD_APPEALS_ID);
				int textColIndex = c.getColumnIndex(FIELD_TEXT);
				int signatureTextColIndex = c.getColumnIndex(FIELD_SIGNATURE_TEXT);
				int signatureBitmapUriColIndex = c.getColumnIndex(FIELD_SIGNATURE_BITMAP_ID);

				String eventId = c.getString(eventIdColIndex);
				String appealsId = c.getString(appealsIdColIndex);
				String text = c.getString(textColIndex);
				String signatureText = c.getString(signatureTextColIndex);
				String signatureBitmapUri = c.getString(signatureBitmapUriColIndex);

				project = new Project(c.getString(idColIndex), c.getString(nameColIndex), c.getString(themeIdColIndex));
				project.setEventId(eventId);
				project.setAppealsId(appealsId);
				project.setText(text);
				project.setSignatureText(signatureText);
				project.setSignatureBitmapUri(signatureBitmapUri);

				// projectsList.add(project);
			} while (c.moveToNext());
		return project;
	}

	public void deleteProject(String projectId) {
		String tableName = Constants.TABLE_PROJECTS;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String where = FIELD_UUID + "=" + "'" + projectId + "'";
		db.delete(tableName, where, null);

	}

	public void createNewGroupRecipients(String groupId, String groupName, int size) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = Constants.TABLE_LISTS;

		ContentValues cv = new ContentValues();
		cv.put(FIELD_UUID, groupId);
		cv.put(FIELD_NAME, groupName);
		// cv.put(FIELD_SIZE, size);

		db.insert(tableName, null, cv);

	}

	/**
	 * Checks if ther
	 * 
	 * @param groupId
	 * @param groupName
	 */
	public void updateContactsGroup(String groupId, String groupName, int size) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = Constants.TABLE_LISTS;
		String where = FIELD_UUID + " = " + "'" + groupId + "'";
		ContentValues cv = new ContentValues();
		// cv.put(FIELD_UUID, groupId);
		cv.put(FIELD_NAME, groupName);
		// cv.put(FIELD_SIZE, size);

		db.update(tableName, cv, where, null);// insert(tableName, null, cv);

	}

	/**
	 * deletes group from TABLE_CONTACTS_GROUPS && deletes all rows from TABLE_GROUPS_RECIPIENTS for the givet group id
	 * 
	 * @param groupId
	 */
	public void deleteContactsGroup(String groupId) {
		clearGroup(groupId);

		String tableName = Constants.TABLE_LISTS;
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String where = FIELD_UUID + "=" + "'" + groupId + "'";
		db.delete(tableName, where, null);

	}

	public void addRecipientToGroup(String id, String groupId, String recId) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();

		String tableName = Constants.TABLE_LIST_CONTACTS;

		ContentValues cv = new ContentValues();
		cv.put(FIELD_UUID, id);
		cv.put(FIELD_GROUP_ID, groupId);
		cv.put(FIELD_RECIPIENT_ID, recId);

		db.insert(tableName, null, cv);
	}

	/**
	 * Delete all recipients from group
	 * 
	 * @param groupId
	 */
	public void clearGroup(String groupId) {
		String tableName = Constants.TABLE_LIST_CONTACTS;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String where = FIELD_GROUP_ID + "=" + "'" + groupId + "'";

		db.delete(tableName, where, null);

	}

	public void renameContactsGroup(String id, String name) {
		String tableName = Constants.TABLE_LISTS;
		String where = FIELD_UUID + "=" + "'" + id + "'";

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(FIELD_NAME, name);
		db.update(tableName, cv, where, null);
	}

	public List<ListContacts> getGroupsList() {
		String tableName = Constants.TABLE_LISTS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			c = db.query(tableName, null, null, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + "do not exist");
			return null;
		}

		List<ListContacts> groupsList = new ArrayList<ListContacts>();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row ");
		else if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_UUID);
				int nameColIndex = c.getColumnIndex(FIELD_NAME);
				// int sizeColIndex = c.getColumnIndex(FIELD_SIZE);

				ListContacts group = new ListContacts(c.getString(idColIndex), c.getString(nameColIndex));

				groupsList.add(group);
			} while (c.moveToNext());
		c.close();

		return groupsList;
	}

	public ListContacts getContactsList(String groupId) {
		String tableName = Constants.TABLE_LISTS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			String where = FIELD_UUID + "=" + "'" + groupId + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + "do not exist");
			return null;
		}

		ListContacts group = null;
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row ");
		else if (c.moveToFirst())
			do {
				int idColIndex = c.getColumnIndex(FIELD_UUID);
				int nameColIndex = c.getColumnIndex(FIELD_NAME);
				// int sizeColIndex = c.getColumnIndex(FIELD_SIZE);

				group = new ListContacts(c.getString(idColIndex), c.getString(nameColIndex));

			} while (c.moveToNext());
		c.close();

		return group;
	}

	public int getListSize(String groupId) {
		String tableName = Constants.TABLE_LIST_CONTACTS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			String where = FIELD_GROUP_ID + "=" + "'" + groupId + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + "do not exist");
			return -1;
		}
		return c.getCount();
	}

	public Cursor getTableCursor(String tableName, String where, String order) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			c = db.query(tableName, null, where, null, null, null, order);

		} catch (SQLiteException e) {
			Log.w(TAG, tableName + " do not exist");
			return null;
		}
		return c;
	}

	public Cursor executeQuery(String query) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		return db.rawQuery(query, null);
	}

	public void saveStarPurchase(Purchase purchase) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String tableName = Constants.TABLE_STARS_PURCHASES;
		ContentValues cv = new ContentValues();
		cv.put(FIELD_UUID, purchase.getDeveloperPayload());
		cv.put(FIELD_JSON, purchase.getOriginalJson());
		cv.put(FIELD_SIGNATURE, purchase.getSignature());

		db.insert(tableName, null, cv);
	}

	public List<Purchase> getStarPurchases() {
		String tableName = Constants.TABLE_STARS_PURCHASES;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			c = db.query(tableName, null, null, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + "do not exist");
			return null;
		}

		List<Purchase> purchasesList = new ArrayList<Purchase>();
		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row ");
		else if (c.moveToFirst())
			do {
				// int idColIndex = c.getColumnIndex(FIELD_UUID);
				int jsonColIndex = c.getColumnIndex(FIELD_JSON);
				int sigColIndex = c.getColumnIndex(FIELD_SIGNATURE);
				try {
					Purchase purchase = new Purchase(c.getString(jsonColIndex), c.getString(sigColIndex));
					purchasesList.add(purchase);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// payloadsList.add(c.getString(idColIndex));

			} while (c.moveToNext());
		c.close();

		return purchasesList;
	}

	public void removeStarPurchase(String developerPayload) {
		String tableName = Constants.TABLE_STARS_PURCHASES;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String where = FIELD_UUID + "=" + "'" + developerPayload + "'";

		db.delete(tableName, where, null);

	}

	public boolean containsStarPurchases() {
		String tableName = Constants.TABLE_STARS_PURCHASES;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		try {
			c = db.query(tableName, null, null, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + "do not exist");
			return false;
		}
		return c.getCount() != 0;
	}

	public String getRecipientPhoto(String eventId) {
		String tableName = Constants.TABLE_EVENTS_RECIPIENTS;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor c = null;
		String imageUri = null;
		try {
			String where = FIELD_EVENT_ID + "=" + "'" + eventId + "'";
			c = db.query(tableName, null, where, null, null, null, null);
		} catch (SQLiteException e) {
			Log.w(TAG, tableName + "do not exist");
			return null;
		}

		if (c.getCount() == 0)
			Log.w(TAG, "Table " + tableName + " do not contain any row ");
		else if (c.moveToFirst()) {

			int idColIndex = c.getColumnIndex(FIELD_RECIPIENT_ID);
			String recId = c.getString(idColIndex);
			imageUri = getRecipient(recId).getImageUri();
		}

		c.close();

		return imageUri;
	}

}
