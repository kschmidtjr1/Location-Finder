import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler {
	//Database Version
	private static final int DATABASE_VERSION = 1;
	//Database Name
	private static final String DATABASE_NAME = "locationsManager";
	//table name
	private static final String DATABASE_TABLE = "locations";
	//table column names
	public static final String KEY_ID = "_id";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_DATE = "date";
	public static final String KEY_TIME = "time";
	public static final String KEY_PICTURE = "picture";
	public static final String KEY_NOTE = "note";
	public static final String KEY_FAVORITE = "favorite";
	public static final String KEY_LAT = "lat";
	public static final String KEY_LNG = "lng";
	public static final String KEY_NAME = "name";
	
	private static long identification;
	private DatabaseHelper helper;
	private final Context myContext;
	
	public DatabaseHandler(Context c){
		identification = 0;
		myContext = c;
		helper = new DatabaseHelper(myContext);
	}
	
	public void open(){
		helper = new DatabaseHelper(myContext);
	}
	
	public void close(){
		helper.close();
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " ("+ KEY_ID + " INTEGER PRIMERY KEY AUTOINCREMENT, "+ 
					KEY_LAT + " TEXT, " + KEY_LNG + " TEXT, " + KEY_ADDRESS + " TEXT, " + 
					KEY_DATE + " TEXT, " + KEY_TIME + " TEXT, " + KEY_NAME + " TEXT, " +
					KEY_NOTE + " TEXT, " + KEY_PICTURE + " BLOB, " + KEY_FAVORITE + " INTEGER)";
			);
			
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			
			onCreate(db);
		}
	}
	
	// Adding new location
	public long addLocation(SQLiteLocation data) {
		SQLiteDatabase db = heper.getWritableDatabase();
		identification++;
		int fav;
		if(data.isFavorite())
			fav = 1;
		else
			fav = 0;
		
		ContentValues values = new ContentValues();
		values.put(KEY_LAT, Double.toString(data.getLat()));
		values.put(KEY_LNG, Double.toString(data.getLng()));
		values.put(KEY_ADDRESS, data.getAddress());
		values.put(KEY_DATE, data.getDate());
		values.put(KEY_TIME, data.getTime());
		values.put(KEY_NAME, data.getName());
		values.put(KEY_PICTURE, data.getPicture());
		values.put(KEY_NOTE, data.getNote());
		values.put(KEY_FAVORITE, fav);
	    
		// Inserting Row
		long ID = db.insert(DATABASE_TABLE, null, values);
		db.close();
		return ID;
	}
	 
	// Getting single location
	public SQLiteLocation getLocation(long id) {
		SQLiteDatabase location = new SQLiteLocation();
		SQLiteDatabase db = helper.getReadableDatabase();
		String[] columns = new String[]{ KEY_ID,
				KEY_LAT, KEY_LNG, KEY_ADDRESS, KEY_DATE, KEY_TIME,  KEY_NAME, 
				KEY_NOTE, KEY_PICTURE, KEY_FAVORITE };
		String[] availableIDs = new String[]{ String.valueOf(id) };
		Cursor cursor = db.query(DATABASE_TABLE, columns, KEY_ID + "=?",
				availableIDs , null, null, null, null);
		if (cursor != null && cursor.moveToFirst()) {
			int fav = Integer.parseInt(cursor.getString(9));
			boolean favorite;
			if(fav == 1)
				favorite = true;
			else
				favorite = false;
	 	
			SQLiteLocation location = new SQLiteLocation(
					Long.parseLong(nulltoIntegerDefault(cursor.getString(0))),
					Double.parseDouble(cursor.getString(1)), 
					Double.parseDouble(cursor.getString(2)), 
					cursor.getString(3),cursor.getString(4), 
					cursor.getString(5),cursor.getString(6),
					cursor.getString(7),cursor.getBlob(8), favorite);
		}
		else
			return null;
		cursor.close();
		db.close();
		// return location
		return location;
	}
	
	private String nulltoIntegerDefault(String value) {
		if (!isIntValue(value))
			value = "0";
		return value;
	}

	private boolean isIntValue(String val) {
		try {
			val = val.replace(" ", "");
			Integer.parseInt(val);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	 
	// Getting All locations
	public List<SQLiteLocation> getAllLocations() {
		List<SQLiteLocation> locationList = new ArrayList<SQLiteLocation>();
		SQLiteDatabase db = helper.getWritableDatabase();
		// Select All Query
		String selectQuery = "SELECT * FROM " + DATABASE_TABLE;
	 
		Cursor cursor = db.rawQuery(selectQuery, null);
		int fav;
		boolean favorite;
		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				SQLiteLocation data = new SQLiteLocation();
				fav = Integer.parseInt(cursor.getString(9));
				if(fav == 1)
					favorite = true;
				else
					favorite = false;
				data.setId(Long.parseLong(nulltoIntegerDefault(cursor.getString(0)));
				data.setStringLat(cursor.getString(1));
				data.setStringLng(cursor.getString(2));
				data.setAddress(cursor.getString(3));
				data.setDate(cursor.getString(4));
				data.setTime(cursor.getString(5));
				data.setName(cursor.getString(6));
				data.setNote(cursor.getString(7));
				data.setPicture(cursor.getBlob(8));
				data.setFavorite(favorite);
	            
				// Adding data to list
				locationList.add(data);
			} while (cursor.moveToNext());
		}
		cursor.close();
		// return data list
		db.close();
		return locationList;
	}
	 
	// Getting contacts Count
	public int getLocationsCount() {
		String countQuery = "SELECT * FROM " + DATABASE_TABLE;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);
		int count = cursor.getCount();
		cursor.close();
		db.close();
		// return count
		return count;
	}
	// Updating single data
	public int updateLocation(SQLiteLocation data) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_LAT, data.getLat());
		values.put(KEY_LNG, data.getLng());
		values.put(KEY_ADDRESS, data.getAddress());
		values.put(KEY_DATE, data.getDate());
		values.put(KEY_TIME, data.getTime());
		values.put(KEY_NAME, data.getName());
		values.put(KEY_PICTURE, data.getPicture());
		values.put(KEY_NOTE, data.getNote());
		values.put(KEY_FAVORITE, data.isFavorite());
		values.put(KEY_FAV_POS, data.getPos());
	 
		// updating row
		int updated = db.update(DATABASE_TABLE, values, KEY_ID + "=?",
				new String[] { String.valueOf(data.getId()) });
		db.close();
		return updated;
	}
	 
	// Deleting single data
	public void deleteLocation(SQLiteLocation data) {
		SQLiteDatabase db = helper.gettWritableDatabase();
		db.delete(DATABASE_TABLE, KEY_ID + "=?", 
				new String[] { String.valueOf(data.getId()) });
		db.close();
	}
}
