package com.acktos.motoparking;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.acktos.motoparking.helpers.Database;

public class ContentProvider extends android.content.ContentProvider {

	private Database MBD;
	SQLiteDatabase SQLDB;
	private static final String NOMBRE_CP = "content.provider.parkings";
	
	private static final int PARKINGS = 1;
	private static final int PARKINGS_ID = 2;
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static{
		uriMatcher.addURI(NOMBRE_CP, "parkings", PARKINGS);
		uriMatcher.addURI(NOMBRE_CP, "parkings/#", PARKINGS_ID);
	}
	
	
	
	@Override
	public String getType(Uri uri) {
		return null;
	}
	
	
	
	@Override
	public boolean onCreate() {
		MBD = new Database(getContext());
		return true;
	}
	
	
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long registro = 0;
		try {
			if (uriMatcher.match(uri) == PARKINGS) {
				SQLDB = MBD.getWritableDatabase();
				registro = SQLDB.insert("parkings", null, values);
		    }
		} catch (IllegalArgumentException e) {
			Log.e("ERROR", "Argumento no admitido: " + e.toString());
		}
		
		// Comprobar si se inserto bien el registro
		if (registro > 0) {
			Log.e("INSERT", "Registro creado correctamente");
		} else {
			Log.e("Error", "Al insertar registro: " + registro);
		}
		
		return Uri.parse("parkings/" + registro);
	}
	
	
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		String id = "";
		try {
			if (uriMatcher.match(uri) == PARKINGS_ID) {
				id = uri.getLastPathSegment();
				SQLDB = MBD.getWritableDatabase();
				SQLDB.update("parkings", values, "_id=" + id, selectionArgs);
		    }
		} catch (IllegalArgumentException e) {
			Log.e("ERROR", "Argumento no admitido: " + e.toString());
		}
		
		return Integer.parseInt(id);
	}
	
	
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int registro = 0;
		try {
			if (uriMatcher.match(uri) == PARKINGS_ID) {
				String id = "_id=" + uri.getLastPathSegment();
				SQLDB = MBD.getWritableDatabase();
				registro = SQLDB.delete("parkings", id, null);
		    }
		} catch (IllegalArgumentException e) {
			Log.e("ERROR", "Argumento no admitido: " + e.toString());
		}
		
	    return registro;
	}
	
	
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor c = null;
		try {
			switch (uriMatcher.match(uri)) {
			case PARKINGS_ID:
				String id = "_id=" + uri.getLastPathSegment();
				SQLDB = MBD.getReadableDatabase();
				c = SQLDB.query("parkings", projection, id, selectionArgs,
						null, null, null, sortOrder);
				break;
			case PARKINGS:
				SQLDB = MBD.getReadableDatabase();
				c = SQLDB.query("parkings", projection, null, selectionArgs,
						null, null, null, sortOrder);
				break;
			}
		} catch (IllegalArgumentException e) {
			Log.e("ERROR", "Argumento no admitido: " + e.toString());
		}
		
		return c;
	}
	
	
	
}