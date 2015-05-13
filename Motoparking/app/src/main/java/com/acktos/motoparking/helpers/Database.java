package com.acktos.motoparking.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	private static final String TABLA_PARKINGS = "CREATE TABLE parkings " +
			"(_id INT PRIMARY KEY, address TEXT, coordinates TEXT, schedule TEXT, price_minute INT, price_hour INT, price_standard INT, image TEXT, comments TEXT, creation_date TEXT)";


	public Database(Context context) {
		super(context, "parkings.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db)  {
        db.execSQL("DROP TABLE IF EXISTS parkings");
		db.execSQL(TABLA_PARKINGS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS" + TABLA_PARKINGS);
        onCreate(db);
	}
}