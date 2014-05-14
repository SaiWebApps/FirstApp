package com.firstApp;

import java.text.SimpleDateFormat;
import java.util.*;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

/**
 * Manage access to SQLite database.
 * @author Sairam Krishnan (sbkrishn)
 */
public class DatabaseManager {
	
	private SQLiteDatabase database;
	private Context context;
	private SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
	private final String DB_NAME = "Posts";
	private final int VERSION = 1;
	
	/**
	 * Initializes this database manager with the given context.
	 * @param context - Interface to global info about an application environment
	 */
	public DatabaseManager(Context context) {
		this.context = context;
	}

	/**
	 * Open connection to database.
	 */
	public void open() {
		DatabaseOpenHelper h = new DatabaseOpenHelper(context, DB_NAME, null, VERSION);
		database = h.getWritableDatabase();
	}
	
	/**
	 * Close connection to database.
	 */
	public void close() {
		if (database != null) {
			database.close();
		}
	}
	
	/**
	 * Create a new post with the given message. Keep track of creation date.
	 * @param message - Message for new post
	 */
	public void createPost(String message) {
		ContentValues vals = new ContentValues();
		vals.put("message", message);
		vals.put("postDate", fmt.format(new Date()));
		database.insert("posts", null, vals);
	}
	
	/**
	 * @return all posts in database
	 */
	public Map<String, String> getAllPosts() {
		Map<String, String> postMap = new TreeMap<String, String>();		
		Cursor c = database.rawQuery("SELECT * FROM posts", null);
		
		while (c.moveToNext()) {
			String date = c.getString(c.getColumnIndex("postDate")); 
			String msg = c.getString(c.getColumnIndex("message"));
			postMap.put(date, msg);
		}
		
		c.close();
		return postMap;
	}
	
	/**
	 * Inner class that creates/upgrades and opens a connection to a database.
	 * @author Sairam Krishnan
	 */
	private class DatabaseOpenHelper extends SQLiteOpenHelper {
		
		public DatabaseOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			StringBuffer buf = new StringBuffer("CREATE TABLE posts (");
			buf.append("postId INTEGER PRIMARY KEY AUTOINCREMENT, ");
			buf.append("message TEXT NOT NULL,");
			buf.append("postDate TEXT NOT NULL)");
			db.execSQL(buf.toString());
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS posts");
			db.setVersion(newVersion); //Update version
			onCreate(db); //Recreate the posts table.
		}
	}
}