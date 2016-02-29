package fr.dabernat.dimchat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import fr.dabernat.dimchat.ApplicationManager;
import fr.dabernat.dimchat.database.table.UserTable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dimchat.db";
    private static final int DATABASE_VERSION = 2;

    // private variable which is going to store the singlton object of this class and return it to caller
    private static DatabaseHelper dbHelper = null;
    // private SQLiteDatabase varaible which is going to be responsible for all our db related operation
    private static SQLiteDatabase db = null;

    // will return a singleton object of this class will as well open the connection for convinient
    public static DatabaseHelper getInstance(){
        if(dbHelper==null){
            dbHelper = new DatabaseHelper(ApplicationManager.getContext());
            openConnexion();
        }
           return dbHelper;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        dbHelper = this;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        UserTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        UserTable.onUpgrade(database, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        super.onDowngrade(database, oldVersion, newVersion);
        UserTable.onUpgrade(database, oldVersion, newVersion);
    }

    // will be called only once when singleton is created
    private static void openConnexion(){
        if ( db == null ){
            db = dbHelper.getWritableDatabase();
        }
    }

    // onDestroy method of application
    public synchronized void closeConnecion() {
        if(dbHelper!=null){
            dbHelper.close();
            db.close();
            dbHelper = null;
            db = null;
        }
    }
}