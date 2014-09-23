package ru.fastcards.utils;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.util.TypedValue;

//import com.example.social.api.Photo;

public class ImageManager {

	private static final String TAG = "ImageManager";
	private Context context;
	private static ImageManager instance;

	// private static List<Photo> selectedPhotosList;

	public ImageManager(Context context) {
		this.context = context;
		// selectedPhotosList = new ArrayList<Photo>();
	}

	private boolean availableInPhotoBox(String url, Set<String> availablePhotosSet) {
		return availablePhotosSet.contains(url);
	}

	/**
	 * Converts url to filename without slashes ("/"). Example: for http://site.com/where/is/filename.jpg, returns filename.jpg
	 * 
	 * @param url
	 *            - url to get filename from
	 * @return last part of url filepath
	 */
	public String createFileNameFromUrl(String url) {
		URI uri = null;
		try {
			Log.d(TAG, "createFileNameFromUrl " + url);
			uri = new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		String path = uri.getPath();
		return path.substring(path.lastIndexOf('/') + 1);
	}

	// Should be Async!
	public boolean saveImageToInternalStorage(String filename, Bitmap bitmap, Bitmap.CompressFormat format) {
		FileOutputStream fos = null;
		try {
			fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// Writing the bitmap to the output stream
		bitmap.compress(format, 100, fos);
		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "saved successfully " + filename);
		return true;
	}

	public Bitmap decodeSampledBitmapFromResources(Resources resources, int resId, int reqWidth, int reqHeight) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeResource(resources, resId, options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(resources, resId, options);
	}

	public Bitmap decodeSampledBitmapFromFile(String photoName, int reqWidth, int reqHeight) {

		String pathName = context.getFilesDir().getPath() + "/" + photoName;

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeFile(pathName, options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathName, options);
	}

	/**
	 * Decode a file name into a bitmap. If the specified file name is null, or cannot be decoded into a bitmap, the function returns null.
	 * 
	 * @param filename
	 *            of the image to return
	 * @return Bitmap from file saved in internal storage.
	 */
	public Bitmap decodeBitmapFromFile(String filename) {

		String pathName = context.getFilesDir().getPath() + "/" + filename;

		return BitmapFactory.decodeFile(pathName);
	}

	public String getFilepathFromUrl(String url) {

		return context.getFilesDir().getPath() + "/" + createFileNameFromUrl(url);
	}

	public Bitmap decodeSampledBitmapFromNetwork(String url, int reqWidth, int reqHeight, boolean isCropImage) throws IOException {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		try {
			InputStream is = getInputStream(url);
			BitmapFactory.decodeStream(is, null, options);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		options.inJustDecodeBounds = false;
		InputStream is = getInputStream(url);
		Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
		is.close();
		return bitmap;
	}

	private InputStream getInputStream(String url) {
		InputStream is;
		try {
			is = (new URL(url)).openStream();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return is;
	}

	public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	public Bitmap cropBitmap(Bitmap srcBmp) {
		Bitmap dstBmp;
		if (srcBmp.getWidth() >= srcBmp.getHeight()) {
			dstBmp = Bitmap.createBitmap(srcBmp, srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2, 0, srcBmp.getHeight(), srcBmp.getHeight());
		} else {
			dstBmp = Bitmap.createBitmap(srcBmp, 0, srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2, srcBmp.getWidth(), srcBmp.getWidth());
		}
		return dstBmp;
	}

//	public Bitmap cropBitmapToCircle(Bitmap bitmap) {
//		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
//		Canvas canvas = new Canvas(output);
//
//		final int color = 0xff424242;
//		final Paint paint = new Paint();
//		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//
//		paint.setAntiAlias(true);
//		canvas.drawARGB(0, 0, 0, 0);
//		paint.setColor(color);
//		// canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
//		canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
//		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//		canvas.drawBitmap(bitmap, rect, rect, paint);
//		// Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
//		// return _bmp;
//		return output;
//	}

	/**
	 * Load a contact photo thumbnail and return it as a Bitmap, resizing the image to the provided image dimensions as needed.
	 * 
	 * @param photoData
	 *            photo ID Prior to Honeycomb, the contact's _ID value. For Honeycomb and later, the value of PHOTO_THUMBNAIL_URI.
	 * @return A thumbnail Bitmap, sized to the provided width and height. Returns null if the thumbnail is not found.
	 */
	public Bitmap loadContactPhotoThumbnail(String photoData) {
		AssetFileDescriptor afd = null;
		// try-catch block for file not found
		try {
			// Creates a holder for the URI.
			Uri thumbUri = null;
			// If Android 3.0 or later
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				// Sets the URI from the incoming PHOTO_THUMBNAIL_URI
				thumbUri = Uri.parse(photoData);
			} else {
				final Uri contactUri = Uri.withAppendedPath(Contacts.CONTENT_URI, photoData);
			}
			afd = context.getContentResolver().openAssetFileDescriptor(thumbUri, "r");
			FileDescriptor fileDescriptor = afd.getFileDescriptor();
			if (fileDescriptor != null) {
				return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, null);
			}
		} catch (FileNotFoundException e) {
		}
		finally {
			if (afd != null) {
				try {
					afd.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	public void saveImageToInternalStorageWithCallback(final String filename, final Bitmap bitmap, final Handler handler) {
		new AsyncTask<Object, Object,Object>() {
			@Override
			protected Object doInBackground(Object... params) {
				FileOutputStream fos = null;
				try {
					fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				// Writing the bitmap to the output stream
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d(TAG, "saved sucsesfully " + filename);
				return null;
			}

			protected void onPostExecute(Object result) {
				handler.sendEmptyMessage(0);
			};

		}.execute();

	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int color, int cornerDips, int borderDips, Context context) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int borderSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) borderDips, context.getResources().getDisplayMetrics());
		final int cornerSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) cornerDips, context.getResources().getDisplayMetrics());
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		// prepare canvas for transfer
		paint.setAntiAlias(true);
		paint.setColor(0xFFFFFFFF);
		paint.setStyle(Paint.Style.FILL);
		canvas.drawARGB(0, 0, 0, 0);
		canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

		// draw bitmap
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		// draw border
		paint.setColor(color);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth((float) borderSizePx);
		canvas.drawRoundRect(rectF, cornerSizePx, cornerSizePx, paint);

		return output;
	}

}
