package com.tcl.simpletv.launcher2.toolbar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ToolbarDatabaseHelper extends SQLiteOpenHelper {

	// ����
	private static final String DATABASE_NAME = "toolbar.db";
	private static final int DATABASE_VERSION = 1;
	// ����
	public static final String TOOLBAR_TABLE_NAME = "toolbar";
	// ����һ����ݵ�����
	public static final String _ID = "_id";
	public static final String TITLE = "title";
	public static final String PACKAGENAME = "packagename";
	public static final String INTENT = "intent";
	public static final String ICON = "icon";

	public ToolbarDatabaseHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE " + TOOLBAR_TABLE_NAME + " (" + "packagename TEXT" + ");");
		// + "_id INTEGER PRIMARY KEY,"+"title TEXT," + + "intent TEXT"+ "icon BLOB"
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + TOOLBAR_TABLE_NAME);
		onCreate(db);
	}

}
