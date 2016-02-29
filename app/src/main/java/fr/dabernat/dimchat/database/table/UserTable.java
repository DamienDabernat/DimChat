package fr.dabernat.dimchat.database.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.dabernat.dimchat.database.DatabaseHelper;
import fr.dabernat.dimchat.model.User;
import fr.dabernat.dimchat.utils.FormatUtils;

public class UserTable {

    private static final String TAG = "UserTable";

    public static final String USER_TABLE = "USER_TABLE";
    private static final String USERID = "userID";
    private static final String USERNAME = "username";
    private static final String IMAGEURL = "imageUrl";

    private static final String DATABASE_CREATE_SYSTEM_ITEM_TABLE = "create table " + USER_TABLE +
            " (" + USERID + " INTEGER primary key, " +
            USERNAME + " TEXT not null, " +
            IMAGEURL + " TEXT not null);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE_SYSTEM_ITEM_TABLE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(TAG, "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(database);
    }

    public static long insert(User user) {
        SQLiteDatabase database = DatabaseHelper.getInstance().getWritableDatabase();

        if (getOne(user.getUserID()) != null) {
            remove(user.getUserID());
        }

        ContentValues newValue = new ContentValues();
        newValue.put(USERID, user.getUserID());
        newValue.put(USERNAME, user.getPseudo());
        newValue.put(IMAGEURL, user.getImageUrl());

        return database.insert(USER_TABLE, null, newValue);
    }

    public static boolean remove(int id) {
        SQLiteDatabase database = DatabaseHelper.getInstance().getWritableDatabase();
        return database.delete(USER_TABLE, USERID + " = " + id, null) > 0;
    }

    public static User getOne(int id) {
        SQLiteDatabase database = DatabaseHelper.getInstance().getReadableDatabase();

        Cursor reponse = database.query(USER_TABLE, new String[]{
                USERID, USERNAME, IMAGEURL
        }, USERID + " = " + id, null, null, null, null);

        User user = null;
        if (reponse.moveToFirst()) {

            user = new User(reponse.getInt(reponse.getColumnIndex(USERID)),
                    reponse.getString(reponse.getColumnIndex(USERNAME)),
                            reponse.getString(reponse.getColumnIndex(IMAGEURL))
            );
        }

        reponse.close();
        return user;
    }

    public static User getByName(String name) {
        SQLiteDatabase database = DatabaseHelper.getInstance().getReadableDatabase();

        String nameEscape = FormatUtils.normalizeString(name);
        nameEscape = nameEscape.replaceAll("'", "''");

        Cursor reponse = database.query(USER_TABLE, new String[]{
                USERID, USERNAME, IMAGEURL
        }, USERNAME + " = '" + nameEscape +"'", null, null, null, null);


        User user = null;
        if (reponse.moveToFirst()) {

            user = new User(reponse.getInt(reponse.getColumnIndex(USERID)),
                    reponse.getString(reponse.getColumnIndex(USERNAME)),
                    reponse.getString(reponse.getColumnIndex(IMAGEURL))
            );
        }

        reponse.close();
        return user;
    }


    public static List<User> getAll() {
        SQLiteDatabase database = DatabaseHelper.getInstance().getReadableDatabase();

        Cursor reponse = database.query(USER_TABLE, new String[]{
                USERID, USERNAME, IMAGEURL
        }, null, null, null, null, USERNAME + " ASC");

        List<User> userList = new ArrayList<>();
        if (reponse.moveToFirst()) {
            do {
                userList.add(
                        new User(reponse.getInt(reponse.getColumnIndex(USERID)),
                                reponse.getString(reponse.getColumnIndex(USERNAME)),
                                reponse.getString(reponse.getColumnIndex(IMAGEURL))
                        ));
            } while (reponse.moveToNext());
        }

        reponse.close();
        return userList;
    }

}
