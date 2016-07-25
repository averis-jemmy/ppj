package tcubes.dbkl.summons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbUtils extends SQLiteOpenHelper
{
		
	// The Android's default system path of your application database.
	/** The D b_ path. */
	private static String DB_PATH = "/data/data/tcubes.dbkl.summons/databases/";

	/** The D b_ name. */
	private static String DB_NAME = "dbkl_handheld.db";

	/** The my data base. */
	private SQLiteDatabase myDataBase;

	/** The my context. */
	private final Context myContext;
	
	public DbUtils(Context context)
	{
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}
	


	

	/**
	 * Check if the database already exist to avoid re-copying the file each
	 * time you open the application.
	 * 
	 * @return true if it exists, false if it doesn't
	 */
	@SuppressWarnings("unused")
	private boolean checkDataBase()
	{

		SQLiteDatabase checkDB = null;

		try
		{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

		} catch (SQLiteException e)
		{
			CacheManager.ErrorLog(e);
			// database does't exist yet.

		}

		if (checkDB != null)
		{

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	public boolean databaseExist() throws IOException
	{

		File dbFile = new File(DB_PATH + DB_NAME);		
		return dbFile.exists();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.database.sqlite.SQLiteOpenHelper#close()
	 */
	@Override
	public synchronized void close()
	{

		if (myDataBase != null)
			myDataBase.close();

		super.close();

	}

	/**
	 * Close.
	 */
	public void Close()
	{

		if (myDataBase != null)
			myDataBase.close();

		this.close();

	}

	/**
	 * Copies your database from your local assets-folder to the just created
	 * empty database in the system folder, from where it can be accessed and
	 * handled. This is done by transfering bytestream.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void copyDataBase() throws IOException
	{
		try
		{

			InputStream myInput;
			Log.e("copyDataBase", "Get Database from package");
			// Open your local db as the input stream
			myInput = myContext.getAssets().open(DB_NAME);
			Log.e("copyDataBase", "After Get Database from package");
			// Path to the just created empty db
			String outFileName = DB_PATH + DB_NAME;
	
			// Open the empty db as the output stream
			OutputStream myOutput = new FileOutputStream(outFileName);
	
			// transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[9216];
			int length;
			while ((length = myInput.read(buffer)) > 0)
			{
				myOutput.write(buffer, 0, length);
			}
	
			// Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();
		} 
		catch (IOException e)
		{
			Log.e("copying Log", "Get Database from Staging Application");
			throw new Error("Error copying database");

		}

	}
	public boolean FileExist(String strLocation, String strFileName) throws IOException
	{

		File strFile = new File(strLocation + strFileName);		
		return strFile.exists();

	}
	public void copyFileToDevice(String strLocation, String strFileName) throws IOException
	{
		try
		{

			InputStream myInput;
			Log.e("copyDataBase", "Get Database from package");
			// Open your local db as the input stream
			myInput = myContext.getAssets().open(DB_NAME);
			Log.e("copyDataBase", "After Get Database from package");
			// Path to the just created empty db
			String outFileName = strLocation + strFileName;
	
			// Open the empty db as the output stream
			OutputStream myOutput = new FileOutputStream(outFileName);
	
			// transfer bytes from the inputfile to the outputfile
			byte[] buffer = new byte[1000];
			int length;
			while ((length = myInput.read(buffer)) > 0)
			{
				myOutput.write(buffer, 0, length);
			}
	
			// Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();
		} 
		catch (IOException e)
		{
			Log.e("copying Log", "Copy Files");
			throw new Error("Error copying Files");

		}

	}
	/**
	 * Creates a empty database on the system and rewrites it with your own
	 * database.
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void createDataBase() throws IOException
	{

		boolean dbExist = databaseExist();
		try
		{
		if (dbExist)
		{
			// do nothing - database already exist
		} else
		{
			Log.e("Database error", "DB not exist");
			// By calling this method and empty database will be created into
			// the default system path
			// of your application so we are gonna be able to overwrite that
			// database with our database.
			this.getReadableDatabase();
			
			copyDataBase();
			Log.e("createDataBase", "After Copy Database");
		}

			} catch (IOException e)
			{

				throw new Error("Error copying database");

			}
		

	}
	/**
	 * Insert data.
	 * 
	 * @param tableName
	 *            the table name
	 * @param columnsName
	 *            the columns name
	 * @param values
	 *            the values
	 * @return true, if successful
	 */
	public boolean insertData(String tableName, String[] columnsName, String[] values)
	{
		// myDataBase = getWritableDatabase();

		ContentValues insertValues = new ContentValues();

		for (int i = 0; i < columnsName.length; i++)
		{
			insertValues.put(columnsName[i], values[i]);
		}

		myDataBase.insert(tableName, null, insertValues);

		return true;
	}
	
	public boolean deleteTable(String tableName)
	{
		myDataBase.delete(tableName, null, null);
		
		return true;
	}

	/**
	 * Open.
	 */
	public void Open()
	{
		try
		{

			createDataBase();

		} catch (IOException ioe)
		{
			CacheManager.ErrorLog(ioe);
			throw new Error("Unable to create database");

		}

		try
		{

			openDataBase();

		} catch (SQLException sqle)
		{
			CacheManager.ErrorLog(sqle);
			throw sqle;

		}
	}

	/**
	 * Open data base.
	 * 
	 * @throws SQLException
	 *             the sQL exception
	 */
	public void openDataBase() throws SQLException
	{

		// Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

	}

	/**
	 * Query.
	 * 
	 * @param sqlquery
	 *            the sqlquery
	 * @param parameters
	 *            the parameters
	 * @return the cursor
	 */
	public Cursor Query(String sqlquery, String[] parameters)
	{
		Cursor a = null;

		a = myDataBase.rawQuery(sqlquery, parameters);

		return a;
	}

	/**
	 * Update data.
	 * 
	 * @param tableName
	 *            the table name
	 * @param columnsName
	 *            the columns name
	 * @param values
	 *            the values
	 * @param Id
	 *            the id
	 * @return true, if successful
	 */
	public boolean updateData(String tableName, String[] columnsName, String[] values, String Id)
	{
		String where = columnsName[0] + "=" + Id;

		// myDataBase = getWritableDatabase();

		ContentValues updateValues = new ContentValues();

		for (int i = 0; i < columnsName.length - 1; i++)
		{
			updateValues.put(columnsName[i], values[i]);
		}

		int checkUpdate = myDataBase.update(tableName, updateValues, where, null);

		if (checkUpdate == 0)
		{
			insertData(tableName, columnsName, values);
		}

		return true;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
}
