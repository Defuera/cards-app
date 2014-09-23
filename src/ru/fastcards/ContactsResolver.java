package ru.fastcards;

import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.UUID;

import ru.fastcards.utils.Constants;
import ru.fastcards.utils.DataBaseHelper;
import ru.fastcards.utils.Utils;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.format.DateFormat;
import android.util.Log;

/**
 * 
 * @author Denis V.
 * @since 21.11.2013
 * 
 */
public class ContactsResolver implements LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = "ContactsResolver";
	private static String DATE_FORMAY = "yyyy-MM-dd";
	private FragmentActivity activity;
	private Context context;
	private DataBaseHelper dbHelper;

	ContactsResolver(FragmentActivity activity, Context context) {
		this.activity = activity;
		this.context = context;
		dbHelper = DataBaseHelper.getInstance(context);

		java.text.DateFormat shortDateFormat = DateFormat.getDateFormat(activity.getApplicationContext());
		if (shortDateFormat instanceof SimpleDateFormat) {
			DATE_FORMAY = ((SimpleDateFormat) shortDateFormat).toPattern();
		}
	}

	/**
	 * Checks if there's new contact's and imports such in app's Database
	 * asynchronously.
	 */
	void importContactsToDb() {
		activity.getSupportLoaderManager().initLoader(ContactsQuery.QUERY_ID, null, this);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, final Cursor cursor) {
		new Thread(new Runnable() {
			public void run() {
				Set<String> idsSet = dbHelper.getContactsIdsList();

				while (cursor.moveToNext()) {
					String name;
					// String[] phoneNumbers;
					boolean hasPhone = false;
					String idContacts = "";
					// String[] emails;// = new String[1];
					boolean hasEmail = false;
					String thumbUri = "";
					String recipientId = UUID.randomUUID().toString();

					int idContactsColumn = cursor.getColumnIndex(Contacts._ID);
					idContacts = cursor.getString(idContactsColumn);
					if (idsSet != null && !idsSet.contains(idContacts)) {
						int nameColumn = cursor.getColumnIndex(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME);

						int thumbUriColumn = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI);

						name = cursor.getString(nameColumn);

						hasPhone = savePhoneNumbers(cursor, idContacts, recipientId);
						hasEmail = saveEmails(cursor, idContacts, recipientId, hasPhone);
						if (thumbUriColumn > 0)
							thumbUri = cursor.getString(thumbUriColumn);

						if (hasPhone || hasEmail) {
							getBirthday(cursor, idContacts, dbHelper, recipientId, name);
							dbHelper.saveRecipient(recipientId, name, null, null, thumbUri);
							dbHelper.saveComunication(recipientId, Constants.COMUNICATION_TYPE_CONTACTS_ID, idContacts, false);
						}
					}
				}
			}
		}).start();
	}

	private boolean saveEmails(Cursor cursor, String contactId, String recipientId, boolean hasPhone) {
		String SELECTION = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId;
		Cursor emailCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, SELECTION, null, null);

		boolean hasEmail = emailCursor.getCount() != 0;

		boolean primaryFlag = true;
		
		while (emailCursor.moveToNext()) {
			String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
			if (isEmailIsCorrect(email)) {
				dbHelper.saveComunication(recipientId, Constants.COMUNICATION_TYPE_EMAIL, email, primaryFlag);
				primaryFlag = false;
				// Log.v(TAG, "email "+email);
			} else {
				hasEmail = false;
			}
		}
		emailCursor.close();
		return hasEmail;
	}

	private boolean isEmailIsCorrect(String email) {
		// Log.i(TAG, "isEmailIsCorrect "+email.contains("@")+" "+email);
		return email.contains("@");
	}

	private boolean savePhoneNumbers(Cursor cursor, String contactId, String recipientId) {
		String SELECTION = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId;

		Cursor phonesCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, SELECTION, null, null);

		boolean hasPhone = phonesCursor.getCount() != 0;
		boolean primaryFlag = true;
		while (phonesCursor.moveToNext()) {
			String phone = phonesCursor.getString(phonesCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			phone = phone.replaceAll("[^\\d.]", "");
			Log.v(TAG, "phone " + phone);
			if (isPhoneCorrect(phone)){
				dbHelper.saveComunication(recipientId, Constants.COMUNICATION_TYPE_PHONE, phone, primaryFlag);
				primaryFlag = false;
			}
			else
				hasPhone = false;
		}
		phonesCursor.close();
		// }
		return hasPhone;
	}

	private boolean isPhoneCorrect(String phone) {
		return phone.length() >= 10;
	}

	private String getBirthday(Cursor cursor, String idContacts, DataBaseHelper dbHelper, String recipientId, String recipientName) {
		String birthdayDate = "";
		String columns[] = { ContactsContract.CommonDataKinds.Event.START_DATE, ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.MIMETYPE, };
		String where = ContactsContract.CommonDataKinds.Event.TYPE + "=" + ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY + " and " + ContactsContract.CommonDataKinds.Event.MIMETYPE + " = '"
				+ ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' and " + ContactsContract.Data.CONTACT_ID + " = " + idContacts;
		String[] selectionArgs = null;
		// String sortOrder = ContactsContract.Contacts.DISPLAY_NAME;
		Cursor birthdayCur = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, columns, where, selectionArgs, null);
		if (birthdayCur.getCount() > 0) {
			while (birthdayCur.moveToNext()) {
				birthdayDate = birthdayCur.getString(birthdayCur.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));

				Log.e(TAG, "DATE_FORMAY " + DATE_FORMAY + " birthdayDate  " + birthdayDate);

				long formatDate = Utils.formatDateWithDefault(birthdayDate, DATE_FORMAY);
				System.out.println("formatDate " + formatDate);
				if (formatDate != 0) {
					String eventId = UUID.randomUUID().toString();
					dbHelper.saveEvent(eventId, recipientName, formatDate, Constants.EVENT_CATEGORY_ID_BIRTHDAY, Constants.EVENT_REPEAT_FALSE, Constants.EVENT_TYPE_BIRTHDAY);
					dbHelper.saveEventContact(recipientId, eventId, false);
				}
			}
		}
		birthdayCur.close();
		return birthdayDate;
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		if (loader.getId() == ContactsQuery.QUERY_ID) {
		}
	}

	// @Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == ContactsQuery.QUERY_ID) {
			Uri contentUri = null;
			if (null == null) {
				contentUri = ContactsQuery.CONTENT_URI;
			} else {
			}
			return new CursorLoader(context, contentUri, ContactsQuery.PROJECTION, ContactsQuery.SELECTION, null, ContactsQuery.SORT_ORDER);
		}

		if (id == ContactsQuery.QUERY_PHONE) {
			Uri contentUri = null;
			if (null == null) {
				contentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			} else {
			}
			// the ContactsQuery interface.
			return new CursorLoader(context, contentUri, ContactsQuery.PROJECTION, ContactsQuery.SELECTION, null, ContactsQuery.SORT_ORDER);
		}
		return null;
	}

	public interface ContactsQuery {
		static final int QUERY_PHONE = 2;
		// An identifier for the loader
		final static int QUERY_ID = 1;
		// A content URI for the Contacts table
		final static Uri CONTENT_URI = Contacts.CONTENT_URI;
		// The search/filter query Uri
		final static Uri FILTER_URI = Contacts.CONTENT_FILTER_URI;
		@SuppressLint("InlinedApi")
		final static String SELECTION = (Utils.hasHoneycomb() ? Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME) + "<>''" + " AND " + Contacts.IN_VISIBLE_GROUP + "=1";

		@SuppressLint("InlinedApi")
		final static String SORT_ORDER = Utils.hasHoneycomb() ? Contacts.SORT_KEY_PRIMARY : Contacts.DISPLAY_NAME;

		@SuppressLint("InlinedApi")
		final static String[] PROJECTION = { Contacts._ID, Contacts.LOOKUP_KEY, Utils.hasHoneycomb() ? Contacts.DISPLAY_NAME_PRIMARY : Contacts.DISPLAY_NAME,
				Utils.hasHoneycomb() ? Contacts.PHOTO_THUMBNAIL_URI : Contacts._ID, SORT_ORDER, };
		// The query column numbers which map to each value in the projection
		final static int ID = 0;
		final static int LOOKUP_KEY = 1;
		final static int DISPLAY_NAME = 2;
		final static int PHOTO_THUMBNAIL_DATA = 3;
		final static int SORT_KEY = 4;
	}
}
