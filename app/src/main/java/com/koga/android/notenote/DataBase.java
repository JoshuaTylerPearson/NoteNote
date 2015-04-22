package com.koga.android.notenote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Josh on 4/22/2015.
 * database for subject divider and note storage
 */

public class DataBase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "NoteDB";

    private static final String TABLE_SUBJECT = "sbj";
    private static final String SUBJECT_PRIMARY_KEY = "subject";

    private static final String TABLE_DIV = "div";
    private static final String DIV_PRIMARY_KEY = "pk";
    private static final String SUBJECT_FOREIGN_KEY = "subject";
    private static final String DIVI_KEY = "divider";

    private static final String TABLE_NOTES = "nts";
    private static final String DIV_FOREIGN_KEY = "divider";
    private static final String SBJ_FOREIGN_KEY = "subject";
    private static final String NOTE_PRIMARY_KEY = "note";
    private static final String BITMAP_KEY = "bitmap";



    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_SUBJECT_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_SUBJECT + "("
                + SUBJECT_PRIMARY_KEY + " TEXT PRIMARY KEY," + ")";
        db.execSQL(CREATE_SUBJECT_TABLE);

        String CREATE_DIV_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_DIV + "("
                + DIV_PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SUBJECT_FOREIGN_KEY + " TEXT FOREIGN KEY,"
                + DIVI_KEY + " TEXT," + ")";
        db.execSQL(CREATE_DIV_TABLE);

        String CREATE_NOTES_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NOTES + "( "
                + DIV_FOREIGN_KEY + " TEXT FOREIGN KEY,"
                + SBJ_FOREIGN_KEY + " TEXT FOREIGN KEY,"
                + NOTE_PRIMARY_KEY + " TEXT PRIMARY KEY,"
                + BITMAP_KEY + "BLOB," + ")";
        db.execSQL(CREATE_NOTES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public ArrayList<String> getAllSbjs() { //gets all the subjects in database
        ArrayList<String> sbjs = new ArrayList<String>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUBJECT, null);

        if(cursor.moveToFirst()) {
            do {
                sbjs.add(cursor.getString(0));
            } while(cursor.moveToNext());
        }
        return sbjs;
    }

    public ArrayList<String> getDividers(String sbj) { //gets all the dividers for subject
        ArrayList<String> divid = new ArrayList<String>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DIV + " WHERE " + SUBJECT_FOREIGN_KEY + "='" + sbj + "';", null);

        if(cursor.moveToFirst()) {
            do {
                divid.add(cursor.getString(2));
            } while(cursor.moveToNext());
        }
        return divid;
    }

    public ArrayList<String> getNotes(String sbj, String div) { //gets all the notes for div
        ArrayList<String> notes = new ArrayList<String>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " WHERE " + DIV_FOREIGN_KEY + "='" + div + "' AND ;" + SBJ_FOREIGN_KEY + "='" + sbj + "';", null);

        if(cursor.moveToFirst()) {
            do {
                notes.add(cursor.getString(2));
            } while(cursor.moveToNext());
        }
        return notes;
    }

    public Bitmap getBitmaps(String note, String sbj, String div) { //gets the bitmap for note
        Bitmap notes = null;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " WHERE " + DIV_FOREIGN_KEY + "='" + div + "' AND " + SBJ_FOREIGN_KEY + "='" + sbj + "' AND " + NOTE_PRIMARY_KEY + "='" + note + "';", null);

        if(cursor.moveToFirst()) {
            do {
                notes = (DbBitmapUtility.getImage(cursor.getBlob(3)));
            } while(cursor.moveToNext());
        }
        return notes;
    }


    public void addDiv(String sbj, String div) { //adds div to subject

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SUBJECT_FOREIGN_KEY, sbj);
        values.put(DIVI_KEY, div);

        db.insert(TABLE_DIV, null, values);
        db.close();

    }

    public void addSbj(String sbj) { //adds subject

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SUBJECT_PRIMARY_KEY, sbj);

        db.insert(TABLE_SUBJECT, null, values);
        db.close();

    }

    public void addNote(String note, String sbj, String div, Bitmap bm) { //adds note to divider in subject

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SBJ_FOREIGN_KEY, sbj);
        values.put(DIV_FOREIGN_KEY, div);
        values.put(NOTE_PRIMARY_KEY, note);
        values.put(BITMAP_KEY, DbBitmapUtility.getBytes(bm));

        db.insert(TABLE_SUBJECT, null, values);
        db.close();

    }



    /*
    public void deleteSbj(Sbj m) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBJECT, (SUBJECT_PRIMARY_KEY + " = '" + m.getName() + "'"), null);

        db.close();
    }

    public void addSbj(Sbj m) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SUBJECT_PRIMARY_KEY, m.getName());
        values.put(DIVI_KEY, m.getDividers());

        db.insert(TABLE_SUBJECT, null, values);
        db.close();

    }

    public void removeSbj(Sbj m) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBJECT, (SUBJECT_PRIMARY_KEY + " = '" + m.getName() + "'"), null);
        db.close();
    }
    */

}
