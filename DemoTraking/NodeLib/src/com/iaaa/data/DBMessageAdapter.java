package com.iaaa.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.method.DateTimeKeyListener;
import android.util.Log;

/**
 * Clase que provee el acceso a la base de datos para almacenar/recuperar SMS
 * @author Ricardo Pallás
 */
public class DBMessageAdapter {
	// Log tag
	private static final String TAG = "DBMessageAdapter";
	
	//Database version
	private static final int DATABASE_VERSION = 3;
	
	//Database name
	private static final String DATABASE_NAME = "Message";
	
	//Table names
	private static final String TABLE_MESSAGE = "message";
	
	//Message table column names
	private static final String KEY_ROWID = "_id";
	private static final String KEY_DATE = "date";
	private static final String KEY_PHONE = "phone";
	private static final String KEY_BODY = "body";
	private static final String KEY_FLAG_SENT = "sent";
	
	//Table create statements
	//Light table create statement
	private static final String CREATE_TABLE_MESSAGE = "create table " + TABLE_MESSAGE
			+ " (" + KEY_ROWID + " integer primary key autoincrement, " + ""
			+ KEY_DATE + " integer not null, " + KEY_PHONE + " text not null, "+ KEY_BODY +" text not null, "
			+ KEY_FLAG_SENT +" integer not null);";
	

	final Context context;

	DatabaseHelper DBHelper;
	SQLiteDatabase db;

	public DBMessageAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(CREATE_TABLE_MESSAGE);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			//Log the upgrade
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			//on upgrade drop older tables
			db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_MESSAGE);
			//Create new tables
			onCreate(db);
		}
	}

	/**
	 * Abre la base de datos
	 * @return Una instancia de la clase 
	 * @throws SQLException Ha ocurrido un problema al abrir la base de datos
	 */
	public DBMessageAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Cierra la base de datos
	 */
	public void close() {
		DBHelper.close();
	}

	/**
	 * Almacena un mensaje en la base de datos
	 * @param phoneNumber Número de teléfono del mensaje
	 * @param body Cuerpo del mensaje
	 * @param sent Flag que indica si el mensaje se ha enviado (true si se ha enviado)
	 * @return El ID del mensaje insertado
	 */
	public long insertMessage(String phoneNumber, String body, boolean sent) {
		ContentValues initialValues = new ContentValues();
		// Get the date in seconds
		int fecha = (int) (System.currentTimeMillis());
		initialValues.put(KEY_DATE, fecha);
		initialValues.put(KEY_PHONE, phoneNumber);
		initialValues.put(KEY_BODY, body);
		initialValues.put(KEY_FLAG_SENT, sent);
		return db.insert(TABLE_MESSAGE, null, initialValues);
	}

	/**
	 * Borra el mensaje con Id == rowId de la base de datos 
	 * @param rowId Id del mensaje a borrar
	 * @return True si se ha borrado, false en caso contrario
	 */
	public boolean deleteMessage(long rowId) {
		return db.delete(TABLE_MESSAGE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	/**
	 * Actualiza el valor del flag que indica si el mensaje
	 * ha sido enviado del mensaje con id == rowId
	 * @param rowId Id del mensaje a actualizar
	 * @param isSent Valor del flag a actualizar
	 * @return True si se ha actializado, false en caso contrario.
	 */
	public boolean updateMessage(int rowId, boolean isSent){
		ContentValues updatedValue = new ContentValues();
		updatedValue.put(KEY_FLAG_SENT, isSent);
		return db.update(TABLE_MESSAGE, updatedValue, KEY_ROWID+"="+rowId, null) > 0;
		
	}

	/**
	 * Recupera todos los mensajes almacenados en la base de datos
	 * @return Un objeto Cursor con todos los mensajes almacenados en la base de datos
	 */
	public Cursor getAllMessages() {
		return db.query(TABLE_MESSAGE, new String[] { KEY_ROWID, KEY_DATE,
				KEY_PHONE, KEY_BODY, KEY_FLAG_SENT }, null, null, null, null, null);
	}
	
	/**
	 * Devuelve todos los mensajes almacenados que no han sido enviados,
	 * es decir que su flag sent es false
	 * @return Mensajes almacenados que no han sido enviados
	 */
	public Cursor getUnsentMessages(){
		return db.query(TABLE_MESSAGE, new String[] { KEY_ROWID, KEY_DATE,
				KEY_PHONE, KEY_BODY, KEY_FLAG_SENT }, KEY_FLAG_SENT+" = 0", null, null, null, null);
	}
	
	
	/**
	 * Devuelve el último mensaje almacenado en la base de datos
	 * @return último mensaje almacenado en la base de datos
	 * @throws SQLException 
	 */
	public Cursor getLastValue() throws SQLException {
		Cursor mCursor = db.query(true, TABLE_MESSAGE, new String[] {
				KEY_ROWID, KEY_DATE, KEY_PHONE, KEY_BODY }, KEY_ROWID + "=" + "(SELECT MAX("+KEY_ROWID+")  FROM "+ TABLE_MESSAGE+")",
				null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	/**
	 * Borra todos los mensajes en la base de datos
	 * @return False si no se ha borrado ningún mensaje, true en caso contrario.
	 */
	public boolean deleteAllMessages(){
		return db.delete(TABLE_MESSAGE, null, null) > 0;
	}
	/**
	 * Borra todos los mensajes en la base de datos que tengan el flag isSent a true, 
	 * es decir los mensajes que ya han sido enviados.
	 * @return False si no se ha borrado ningún mensaje, true en caso contrario.
	 */
	public boolean deleteSentMessages(){
		return db.delete(TABLE_MESSAGE, KEY_FLAG_SENT+"=1", null) > 0;
	}

}
