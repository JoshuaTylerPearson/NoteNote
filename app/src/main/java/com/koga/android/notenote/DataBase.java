package com.koga.android.notenote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.Toast;

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
    private static final String DIVIDER_KEY = "divider";

    private static final String TABLE_NOTES = "nts";
    private static final String DIV_FOREIGN_KEY = "divider";
    private static final String SBJ_FOREIGN_KEY = "subject";
    private static final String NOTE_PRIMARY_KEY = "note";
    private static final String BITMAP_KEY = "bitmap";
    private Context context;//for toasts, remove later

    private String sbjct;
    private String noot;
    private String dvdr;




    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_SUBJECT_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_SUBJECT + "("
                + SUBJECT_PRIMARY_KEY + " TEXT PRIMARY KEY" + ")";
        db.execSQL(CREATE_SUBJECT_TABLE);

        String CREATE_DIV_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_DIV + "("
                + DIV_PRIMARY_KEY + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SUBJECT_FOREIGN_KEY + " TEXT,"
                + DIVIDER_KEY + " TEXT" + ", "
                + "FOREIGN KEY(" + SUBJECT_FOREIGN_KEY + ")"
                + "REFERENCES " + TABLE_SUBJECT + "("
                + SUBJECT_PRIMARY_KEY + "))";
        db.execSQL(CREATE_DIV_TABLE);

        String CREATE_NOTES_TABLE = "CREATE TABLE IF NOT EXISTS "
                + TABLE_NOTES + "( "
                + DIV_FOREIGN_KEY + " TEXT,"
                + SBJ_FOREIGN_KEY + " TEXT,"
                + NOTE_PRIMARY_KEY + " TEXT PRIMARY KEY,"
                + BITMAP_KEY + " BLOB, "
                + "FOREIGN KEY(" + SBJ_FOREIGN_KEY + ")"
                + "REFERENCES " + TABLE_SUBJECT + "("
                + SUBJECT_PRIMARY_KEY + "),"
                + "FOREIGN KEY(" + DIV_FOREIGN_KEY + ")"
                + "REFERENCES " + TABLE_DIV + "("
                + DIV_PRIMARY_KEY + "))";
        db.execSQL(CREATE_NOTES_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }


    public DataBase(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.dvdr = null;
        this.sbjct = null;
        this.noot = null;

    }

    public void updateReff(String newSbjct, String newDvder, String newNoot){

        this.sbjct = newSbjct;
        this.dvdr = newDvder;
        this.noot = newNoot;

    }

    public ArrayList<String> getSubjects() { //gets all the subjects in database
        ArrayList<String> subjects = new ArrayList<String>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUBJECT, null);

        if(cursor.moveToFirst()) {

            do {
                subjects.add(cursor.getString(0));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return subjects;
    }
    public boolean isSubject(String subject) { //checks database for subject
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_SUBJECT, null);
        boolean herp = false;

        if(cursor.moveToFirst()) {

            do {
                if (cursor.getString(0).equals(subject)){
                    herp = true;
                }
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return herp;
    }

    public boolean isDivider(String subject, String divider) { //checks database for subject
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DIV , null);  //+ " WHERE "+ SUBJECT_FOREIGN_KEY + " = '" + subject +"'"
        boolean herp = false;

        if(cursor.moveToFirst()) {

            do {
                if (cursor.getString(2).equals(divider) && cursor.getString(1).equals(subject)){
                    herp = true;
                }
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return herp;
    }

    public boolean isNote(String subject, String divider, String note) { //checks database for subject
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES , null);  //+ " WHERE "+ SUBJECT_FOREIGN_KEY + " = '" + subject +"'"
        boolean herp = false;

        if(cursor.moveToFirst()) {
            //Toast.makeText(context, "cursor has data", Toast.LENGTH_SHORT).show();
            do {
                if (cursor.getString(0).equals(divider) && cursor.getString(1).equals(subject) && cursor.getString(2).equals(note)){
                    herp = true;
                }
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return herp;
    }

    public ArrayList<String> getDividers(String sbj) { //gets all the dividers for subject
        ArrayList<String> dividers = new ArrayList<String>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_DIV + " WHERE " + SUBJECT_FOREIGN_KEY + "='" + sbj + "';", null);

        if(cursor.moveToFirst()) {
            do {
                dividers.add(cursor.getString(2));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return dividers;
    }

    public ArrayList<String> getNotes(String sbj, String div) { //gets all the notes for div
        ArrayList<String> notes = new ArrayList<String>();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " WHERE " + DIV_FOREIGN_KEY + "='" + div + "' AND " + SBJ_FOREIGN_KEY + "='" + sbj + "';", null);

        if(cursor.moveToFirst()) {
            do {
                notes.add(cursor.getString(2));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return notes;
    }

    public Bitmap getBitmaps() { //gets the bitmap for note
        Bitmap image = null;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES + " WHERE " + DIV_FOREIGN_KEY + "='" + this.dvdr + "' AND " + SBJ_FOREIGN_KEY + "='" + this.sbjct + "' AND " + NOTE_PRIMARY_KEY + "='" + this.noot + "';", null);

        if(cursor.moveToFirst()) {
            do {
                image = (DbBitmapUtility.getImage(cursor.getBlob(3)));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return image;
    }


    public void addDivider(String subject, String divider) { //adds div to subject

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(SUBJECT_FOREIGN_KEY, subject);
        values.put(DIVIDER_KEY, divider);

        db.insert(TABLE_DIV, null, values);
        db.close();

    }

    public void addSubject(String subject) { //adds subject

        ContentValues values = new ContentValues();
        values.put(SUBJECT_PRIMARY_KEY, subject);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_SUBJECT, null, values);
        db.close();

    }

    public void addNote(String note, String subject, String divider, Bitmap image) { //adds note to divider in subject

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DIV_FOREIGN_KEY, divider);
        values.put(SBJ_FOREIGN_KEY, subject);
        values.put(NOTE_PRIMARY_KEY, note);
        values.put(BITMAP_KEY, DbBitmapUtility.getBytes(image));
        //Toast.makeText(context,values.toString(), Toast.LENGTH_LONG).show();
        db.insert(TABLE_NOTES, null, values);
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NOTES, null);
       // Toast.makeText(context, "Subject: "+ cursor.getString(1)+ " Divider: "+ cursor.getString(0) + " Note: " + cursor.getString(2), Toast.LENGTH_LONG ).show();
        db.close();

    }

    public void addBitmap(Bitmap image) { //adds note to divider in subject

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BITMAP_KEY, DbBitmapUtility.getBytes(image));
        String where = "" + NOTE_PRIMARY_KEY + "='" + this.noot + "' AND " + SBJ_FOREIGN_KEY + "='" + this.sbjct +"' AND " + DIV_FOREIGN_KEY + "='" + this.dvdr + "'";
        //Toast.makeText(context,where, Toast.LENGTH_LONG).show();
        db.update(TABLE_NOTES, values, where, null);
        // Toast.makeText(context, "Subject: "+ cursor.getString(1)+ " Divider: "+ cursor.getString(0) + " Note: " + cursor.getString(2), Toast.LENGTH_LONG ).show();
        db.close();

    }

    public void deleteSbj(String subject) { //del subject and all sub bits
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SUBJECT, (SUBJECT_PRIMARY_KEY + " = '" +  subject + "'"), null);
        db.delete(TABLE_DIV, (SUBJECT_FOREIGN_KEY + " = '" +  subject + "'"), null);
        db.delete(TABLE_NOTES, (SBJ_FOREIGN_KEY + " = '" +  subject + "'"), null);

        db.close();
    }

    public void deleteDiv(String divider) { //del div and all sub notes
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DIV, (DIVIDER_KEY + " = '" +  divider + "'"), null);
        db.delete(TABLE_NOTES, (DIV_FOREIGN_KEY + " = '" +  divider + "'"), null);

        db.close();
    }

    public void deleteNote(String note) { //del note
        Toast.makeText(context, note.trim(), Toast.LENGTH_SHORT).show();
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NOTES, (NOTE_PRIMARY_KEY + " = '" +  note.trim() + "'"), null);

        db.close();
    }
}
