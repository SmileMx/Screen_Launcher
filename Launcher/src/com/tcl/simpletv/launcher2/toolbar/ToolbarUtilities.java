package com.tcl.simpletv.launcher2.toolbar;

import java.util.ArrayList;

import com.tcl.simpletv.launcher2.R;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.util.DisplayMetrics;

public class ToolbarUtilities {
	private static int sIconWidth = -1;
	private static int sIconHeight = -1;
	private static int sIconTextureWidth = -1;
	private static int sIconTextureHeight = -1;

	private static final Paint sBlurPaint = new Paint();
	private static final Paint sGlowColorPressedPaint = new Paint();
	private static final Paint sGlowColorFocusedPaint = new Paint();
	private static final Paint sDisabledPaint = new Paint();
	private static final Rect sOldBounds = new Rect();
	private static final Canvas sCanvas = new Canvas();

	static int sColors[] = { 0xffff0000, 0xff00ff00, 0xff0000ff };
	static int sColorIndex = 0;

	public static ArrayList<String> favoriateList = new ArrayList<String>();

	/**
	 * Returns a bitmap suitable for the all apps view.
	 */
	static Bitmap createIconBitmap(Drawable icon, Context context) {
		synchronized (sCanvas) { // we share the statics :-(
			if (sIconWidth == -1) {
				initStatics(context);
			}

			int width = sIconWidth;
			int height = sIconHeight;

			if (icon instanceof PaintDrawable) {
				PaintDrawable painter = (PaintDrawable) icon;
				painter.setIntrinsicWidth(width);
				painter.setIntrinsicHeight(height);
			} else if (icon instanceof BitmapDrawable) {
				// Ensure the bitmap has a density.
				BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
				Bitmap bitmap = bitmapDrawable.getBitmap();
				if (bitmap.getDensity() == Bitmap.DENSITY_NONE) {
					bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
				}
			}
			int sourceWidth = icon.getIntrinsicWidth();
			int sourceHeight = icon.getIntrinsicHeight();
			if (sourceWidth > 0 && sourceHeight > 0) {
				// There are intrinsic sizes.
				if (width < sourceWidth || height < sourceHeight) {
					// It's too big, scale it down.
					final float ratio = (float) sourceWidth / sourceHeight;
					if (sourceWidth > sourceHeight) {
						height = (int) (width / ratio);
					} else if (sourceHeight > sourceWidth) {
						width = (int) (height * ratio);
					}
				} else if (sourceWidth < width && sourceHeight < height) {
					// Don't scale up the icon
					width = sourceWidth;
					height = sourceHeight;
				}
			}

			Bitmap backBitmap = BitmapFactory.decodeResource(  
            		context.getResources(),  
            		R.drawable.app_icon_back);
			
			// no intrinsic size --> use default size
            int textureWidth = backBitmap.getWidth();//sIconTextureWidth;
            int textureHeight = backBitmap.getHeight();//sIconTextureHeight;

			final Bitmap bitmap = Bitmap.createBitmap(textureWidth, textureHeight,
					Bitmap.Config.ARGB_8888);
			final Canvas canvas = sCanvas;
			canvas.setBitmap(bitmap);

			final int left = (textureWidth - width) / 2;
			final int top = (textureHeight - height) / 2;

			@SuppressWarnings("all")
			// suppress dead code warning
			final boolean debug = false;
			if (debug) {
				// draw a big box for the icon for debugging
				canvas.drawColor(sColors[sColorIndex]);
				if (++sColorIndex >= sColors.length)
					sColorIndex = 0;
				Paint debugPaint = new Paint();
				debugPaint.setColor(0xffcccc00);
				canvas.drawRect(left, top, left + width, top + height, debugPaint);
			}

			canvas.drawBitmap(backBitmap, 0.0f, 0.0f, null);
			
			sOldBounds.set(icon.getBounds());
			icon.setBounds(left, top, left + width, top + height);
			icon.draw(canvas);
			icon.setBounds(sOldBounds);
			canvas.setBitmap(null);

			return bitmap;
		}
	}

	private static void initStatics(Context context) {
		final Resources resources = context.getResources();
		final DisplayMetrics metrics = resources.getDisplayMetrics();
		final float density = metrics.density;

		sIconWidth = sIconHeight = (int) resources.getDimension(R.dimen.app_icon_size);
		sIconTextureWidth = sIconTextureHeight = sIconWidth;

		sBlurPaint.setMaskFilter(new BlurMaskFilter(5 * density, BlurMaskFilter.Blur.NORMAL));
		sGlowColorPressedPaint.setColor(0xffffc300);
		sGlowColorFocusedPaint.setColor(0xffff8e00);

		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0.2f);
		sDisabledPaint.setColorFilter(new ColorMatrixColorFilter(cm));
		sDisabledPaint.setAlpha(0x88);
	}

	//database operation
	public static void queryFavoritesdb(Context context) {
		ToolbarDatabaseHelper dbHelper = new ToolbarDatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String packageDescription;
		favoriateList.clear();
		Cursor c = db.query(ToolbarDatabaseHelper.TOOLBAR_TABLE_NAME, null, null, null, null, null,
				null);
		while (c.moveToNext()) {
			packageDescription = c.getString(0);
			favoriateList.add(packageDescription);
		}
		db.close();
	}

	public static void queryFavoritesdb(SQLiteDatabase db) {
		String packageDescription;
		favoriateList.clear();
		Cursor c = db.query(ToolbarDatabaseHelper.TOOLBAR_TABLE_NAME, null, null, null, null, null,
				null);
		while (c.moveToNext()) {
			packageDescription = c.getString(0);
			favoriateList.add(packageDescription);
			// c.moveToNext();
		}
		db.close();
	}

	public static void delFavorites(Context context, String packageName) {
		ToolbarDatabaseHelper dbHelper = new ToolbarDatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("delete from toolbar where packagename=" + "'" + packageName + "'");
		queryFavoritesdb(db);
	}

	public static void addFavorites(Context context, String packageName) {
		ToolbarDatabaseHelper dbHelper = new ToolbarDatabaseHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (packageName != null) {
			db.execSQL("insert into toolbar(packagename) values('" + packageName + "')");
		}
		queryFavoritesdb(db);
	}
}
