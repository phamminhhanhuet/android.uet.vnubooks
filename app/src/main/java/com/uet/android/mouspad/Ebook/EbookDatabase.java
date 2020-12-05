package com.uet.android.mouspad.Ebook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.uet.android.mouspad.R;
import com.uet.android.mouspad.Utils.Constants;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EbookDatabase extends SQLiteOpenHelper {
    private final static String DBNAME = "bookdb";
    private final static int DBVERSION = 2;
    //epub + html + txt format
    private final static String BOOK_TABLE = "book";
    private final static String COMPOSE_TABLE = "composetable";
    private final static String DEFAULT_TABLE = "defaulttable";

    //pdf format
    private final static String PDF_TABLE = "pdftable";
    //app format
    private final static String MOUSPAD_TABLE = "mouspadtable";

    private final static String BOOK_ID = "id";
    private final static String BOOK_TITLE = "title";
    private final static String BOOK_LIB_TITLE = "libtitle";
    private final static String BOOK_AUTHOR = "author";
    private final static String BOOK_LIB_AUTHOR = "libauthor";
    private final static String BOOK_FILENAME = "filename";
    private final static String BOOK_ADDED = "added";
    private final static String BOOK_LASTREAD = "lastread";
    private final static String BOOK_STATUS = "status";

    private final static String WEBS_TABLE = "webs";
    private final static String WEBS_NAME = "name";
    private final static String WEBS_URL = "url";

    private final Context context;

    private final Pattern authorRX;
    private final Pattern titleRX;

    public final static int STATUS_DONE = 128;
    public final static int STATUS_LATER = 32;
    public final static int STATUS_STARTED = 8;
    public final static int STATUS_NONE = 0;
    public final static int STATUS_ANY = -1;
    public final static int STATUS_SEARCH = -2;

    private int readmode = 0;

    public EbookDatabase(Context context) {
        super(context, DBNAME, null, DBVERSION);
        this.context = context;

        String namePrefixRX="sir|lady|rev(?:erend)?|doctor|dr|mr|ms|mrs|miss";
        String nameSuffixRX="jr|sr|\\S{1,5}\\.d|[jm]\\.?d|[IVX]+|1st|2nd|3rd|esq";
        String nameInfixRX="V[ao]n|De|St\\.?";

        authorRX = Pattern.compile("^\\s*(?:(?i:" + namePrefixRX + ")\\.?\\s+)? (.+?) (?:\\s+|d')?((?:(?:" + nameInfixRX + ")\\s+)? \\S+ (?:\\s+(?i:" + nameSuffixRX + ")\\.?)?)$", Pattern.COMMENTS);
        titleRX = Pattern.compile("^(a|an|the|la|el|le|eine?|der|die)\\s+(.+)$", Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createbooktable =
                "create table " + BOOK_TABLE + "( " +
                        BOOK_ID + " INTEGER PRIMARY KEY," +
                        BOOK_TITLE + " TEXT," +
                        BOOK_LIB_TITLE + " TEXT," +
                        BOOK_AUTHOR + " TEXT," +
                        BOOK_LIB_AUTHOR + " TEXT," +
                        BOOK_FILENAME + " TEXT," +
                        BOOK_ADDED    + " INTEGER," +
                        BOOK_LASTREAD + " INTEGER," +
                        BOOK_STATUS  + " INTEGER" +
                        ")";
        db.execSQL(createbooktable);

        String [] indexcolums = {BOOK_LIB_TITLE, BOOK_LIB_AUTHOR, BOOK_FILENAME, BOOK_ADDED, BOOK_LASTREAD};

        for (String col: indexcolums) {
            db.execSQL("create index ind_" + col + " on " + BOOK_TABLE + " (" + col + ")");
        }

        String createcomposetable =
                "create table " + COMPOSE_TABLE + "( " +
                        BOOK_ID + " INTEGER PRIMARY KEY," +
                        BOOK_TITLE + " TEXT," +
                        BOOK_LIB_TITLE + " TEXT," +
                        BOOK_AUTHOR + " TEXT," +
                        BOOK_LIB_AUTHOR + " TEXT," +
                        BOOK_FILENAME + " TEXT," +
                        BOOK_ADDED    + " INTEGER," +
                        BOOK_LASTREAD + " INTEGER," +
                        BOOK_STATUS  + " INTEGER" +
                        ")";
        db.execSQL(createcomposetable);

        String [] indexcomposecolums = {BOOK_LIB_TITLE, BOOK_LIB_AUTHOR, BOOK_FILENAME, BOOK_ADDED, BOOK_LASTREAD};

        for (String col: indexcomposecolums) {
            db.execSQL("create index indcom_" + col + " on " + COMPOSE_TABLE + " (" + col + ")");
        }

        String createdefaulttable =
                "create table " + DEFAULT_TABLE + "( " +
                        BOOK_ID + " INTEGER PRIMARY KEY," +
                        BOOK_TITLE + " TEXT," +
                        BOOK_LIB_TITLE + " TEXT," +
                        BOOK_AUTHOR + " TEXT," +
                        BOOK_LIB_AUTHOR + " TEXT," +
                        BOOK_FILENAME + " TEXT," +
                        BOOK_ADDED    + " INTEGER," +
                        BOOK_LASTREAD + " INTEGER," +
                        BOOK_STATUS  + " INTEGER" +
                        ")";
        db.execSQL(createdefaulttable);

        String [] indexdefaultcolums = {BOOK_LIB_TITLE, BOOK_LIB_AUTHOR, BOOK_FILENAME, BOOK_ADDED, BOOK_LASTREAD};

        for (String col: indexdefaultcolums) {
            db.execSQL("create index indde_" + col + " on " + DEFAULT_TABLE + " (" + col + ")");
        }

        String createpdftable =
                "create table " + PDF_TABLE + "( " +
                        BOOK_ID + " INTEGER PRIMARY KEY," +
                        BOOK_TITLE + " TEXT," +
                        BOOK_LIB_TITLE + " TEXT," +
                        BOOK_AUTHOR + " TEXT," +
                        BOOK_LIB_AUTHOR + " TEXT," +
                        BOOK_FILENAME + " TEXT," +
                        BOOK_ADDED    + " INTEGER," +
                        BOOK_LASTREAD + " INTEGER," +
                        BOOK_STATUS  + " INTEGER" +
                        ")";
        db.execSQL(createpdftable);

        String [] indexpdfcolums = {BOOK_LIB_TITLE, BOOK_LIB_AUTHOR, BOOK_FILENAME, BOOK_ADDED, BOOK_LASTREAD};

        for (String col: indexpdfcolums) {
            db.execSQL("create index indpdf_" + col + " on " + PDF_TABLE + " (" + col + ")");
        }

        String createapptable =
                "create table " + MOUSPAD_TABLE + "( " +
                        BOOK_ID + " INTEGER PRIMARY KEY," +
                        BOOK_TITLE + " TEXT," +
                        BOOK_LIB_TITLE + " TEXT," +
                        BOOK_AUTHOR + " TEXT," +
                        BOOK_LIB_AUTHOR + " TEXT," +
                        BOOK_FILENAME + " TEXT," +
                        BOOK_ADDED    + " INTEGER," +
                        BOOK_LASTREAD + " INTEGER," +
                        BOOK_STATUS  + " INTEGER" +
                        ")";
        db.execSQL(createapptable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion<2) {
            switch (readmode){
                case Constants.READING_MODE_LIBRARY:
                    db.execSQL("alter table " + BOOK_TABLE + " add column " + BOOK_STATUS + " INTEGER");
                    ContentValues data = new ContentValues();
                    data.put(BOOK_STATUS, STATUS_NONE);
                    db.update(BOOK_TABLE,data,null, null);
                    data = new ContentValues();
                    data.put(BOOK_STATUS, STATUS_STARTED);
                    db.update(BOOK_TABLE,data,BOOK_LASTREAD + ">0", null);
                    break;
                case Constants.READING_MODE_COMPOSE:
                    db.execSQL("alter table " + COMPOSE_TABLE + " add column " + BOOK_STATUS + " INTEGER");
                    ContentValues compos = new ContentValues();
                    compos.put(BOOK_STATUS, STATUS_NONE);
                    db.update(COMPOSE_TABLE,compos,null, null);
                    compos = new ContentValues();
                    compos.put(BOOK_STATUS, STATUS_STARTED);
                    db.update(COMPOSE_TABLE,compos,BOOK_LASTREAD + ">0", null);
                    break;
                case Constants.READING_MODE_DEFAULT:
                    db.execSQL("alter table " + DEFAULT_TABLE + " add column " + BOOK_STATUS + " INTEGER");
                    ContentValues def = new ContentValues();
                    def.put(BOOK_STATUS, STATUS_NONE);
                    db.update(DEFAULT_TABLE,def,null, null);
                    def = new ContentValues();
                    def.put(BOOK_STATUS, STATUS_STARTED);
                    db.update(DEFAULT_TABLE,def,BOOK_LASTREAD + ">0", null);
                    break;
                case Constants.READING_MODE_PDF:
                    db.execSQL("alter table " + PDF_TABLE + " add column " + BOOK_STATUS + " INTEGER");
                    ContentValues pdf = new ContentValues();
                    pdf.put(BOOK_STATUS, STATUS_NONE);
                    db.update(PDF_TABLE,pdf,null, null);
                    pdf = new ContentValues();
                    pdf.put(BOOK_STATUS, STATUS_STARTED);
                    db.update(PDF_TABLE,pdf,BOOK_LASTREAD + ">0", null);
                    break;
                case Constants.READING_MODE_MOUSPAD:
                    db.execSQL("alter table " + MOUSPAD_TABLE + " add column " + BOOK_STATUS + " INTEGER");
                    ContentValues app = new ContentValues();
                    app.put(BOOK_STATUS, STATUS_NONE);
                    db.update(MOUSPAD_TABLE,app,null, null);
                    app = new ContentValues();
                    app.put(BOOK_STATUS, STATUS_STARTED);
                    db.update(MOUSPAD_TABLE,app,BOOK_LASTREAD + ">0", null);
                    break;
            }
        }
    }

    public void setReadmode(int readmode){
        this.readmode = readmode;
    }

    public boolean containsBook(String filename) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (readmode){
            case Constants.READING_MODE_LIBRARY:
                try (Cursor bookcursor = db.query(BOOK_TABLE,new String[] {BOOK_ID},BOOK_FILENAME + "=?", new String[] {filename}, null, null, null)) {
                    return bookcursor.moveToNext();
                }
            case Constants.READING_MODE_COMPOSE:
                try (Cursor bookcursor = db.query(COMPOSE_TABLE,new String[] {BOOK_ID},BOOK_FILENAME + "=?", new String[] {filename}, null, null, null)) {
                    return bookcursor.moveToNext();
                }
            case Constants.READING_MODE_DEFAULT:
                try (Cursor bookcursor = db.query(DEFAULT_TABLE,new String[] {BOOK_ID},BOOK_FILENAME + "=?", new String[] {filename}, null, null, null)) {
                    return bookcursor.moveToNext();
                }
            case Constants.READING_MODE_PDF:
                try (Cursor bookcursor = db.query(PDF_TABLE,new String[] {BOOK_ID},BOOK_FILENAME + "=?", new String[] {filename}, null, null, null)) {
                    return bookcursor.moveToNext();
                }
            case Constants.READING_MODE_MOUSPAD:
                try (Cursor bookcursor = db.query(MOUSPAD_TABLE,new String[] {BOOK_ID},BOOK_FILENAME + "=?", new String[] {filename}, null, null, null)) {
                    return bookcursor.moveToNext();
                }
            default:
                Log.d("Load Database: ", "Error");
                return false;
        }
    }

    public boolean removeBook(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        switch (readmode){
            case Constants.READING_MODE_LIBRARY:
                return db.delete(BOOK_TABLE, BOOK_ID + "=?", new String[] {""+id})>0;
            case Constants.READING_MODE_COMPOSE:
                return db.delete(COMPOSE_TABLE, BOOK_ID + "=?", new String[] {""+id})>0;
            case Constants.READING_MODE_DEFAULT:
                return db.delete(DEFAULT_TABLE, BOOK_ID + "=?", new String[] {""+id})>0;
            case Constants.READING_MODE_PDF:
                return db.delete(PDF_TABLE, BOOK_ID + "=?", new String[] {""+id})>0;
            case Constants.READING_MODE_MOUSPAD:
                return db.delete(MOUSPAD_TABLE, BOOK_ID + "=?", new String[] {""+id})>0;
            default:
                Log.d("Load Database: ", "Error");
                return false;
        }
    }

    public int addBookDf(String filename, String title, String author, long dateadded){
        Log.d("LibrarayFragmnet 2", author);

        Log.d("LibrarayFragmnet 3", author);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        if(!containsBook(filename)){
            data.put(BOOK_TITLE, title);
            data.put(BOOK_LIB_TITLE, title.toLowerCase());
            data.put(BOOK_AUTHOR, author);
            data.put(BOOK_LIB_AUTHOR, author.toLowerCase());
            data.put(BOOK_FILENAME, filename);
            data.put(BOOK_ADDED, dateadded);
            data.put(BOOK_LASTREAD, -1);
            data.put(BOOK_STATUS, STATUS_NONE);

            switch (readmode){
                case Constants.READING_MODE_LIBRARY:
                    Log.d("LibrarayFragmnet succe", "success");
                    return (int)db.insert(BOOK_TABLE,null, data);
                default: return 0;
            }
        }
        else  return 0;
    }

    public int addBook(String filename, String title, String author, long dateadded) {
        Log.d("LibrarayFragmnet 2", author);
        if (filename==null || containsBook(filename)) return -1;
        if (title==null || title.trim().length()==0) title=filename.replaceAll(".*/","");
        if (author==null || author.trim().length()==0) author="Unknown";
        String libtitle = title.toLowerCase();{
            Matcher titlematch = titleRX.matcher(libtitle);
            if (titlematch.find()) {
                libtitle = titlematch.group(2) + ", " + titlematch.group(1);
            }
        }

        String libauthor = author;
        if (!libauthor.contains(",")) {
            Matcher authmatch = authorRX.matcher(libauthor);
            if (authmatch.find()) {
                libauthor = authmatch.group(2) + ", " + authmatch.group(1);
            }
        }
        Log.d("LibrarayFragmnet 3", author);
        libauthor = libauthor.toLowerCase();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues data = new ContentValues();
        data.put(BOOK_TITLE, title);
        data.put(BOOK_LIB_TITLE, libtitle);
        data.put(BOOK_AUTHOR, author);
        data.put(BOOK_LIB_AUTHOR, libauthor);
        data.put(BOOK_FILENAME, filename);
        data.put(BOOK_ADDED, dateadded);
        data.put(BOOK_LASTREAD, -1);
        data.put(BOOK_STATUS, STATUS_NONE);

        switch (readmode){
            case Constants.READING_MODE_LIBRARY:
                Log.d("LibrarayFragmnet succe", "success");
                return (int)db.insert(BOOK_TABLE,null, data);
            case Constants.READING_MODE_COMPOSE:
                return (int)db.insert(COMPOSE_TABLE,null, data);
            case Constants.READING_MODE_DEFAULT:
                return (int)db.insert(DEFAULT_TABLE,null, data);
            case Constants.READING_MODE_PDF:
                return (int)db.insert(PDF_TABLE,null, data);
            case Constants.READING_MODE_MOUSPAD:
                return (int)db.insert(MOUSPAD_TABLE,null, data);
            default:
                Log.d("Load Database: ", "Error");
                return (int)db.insert(BOOK_TABLE,null, data);
        }
    }


    public void updateLastRead(int id, long lastread) {
        SQLiteDatabase db = this.getWritableDatabase();
        switch (readmode){
            case Constants.READING_MODE_LIBRARY:
                ContentValues data = new ContentValues();
                data.put(BOOK_LASTREAD, lastread);
                db.update(BOOK_TABLE, data, BOOK_ID + "=?", new String[]{ id + ""});
                data = new ContentValues();
                data.put(BOOK_STATUS, STATUS_STARTED);
                db.update(BOOK_TABLE, data,BOOK_ID + "=? and " + BOOK_STATUS + "=" + STATUS_NONE, new String[]{ id + ""});
                break;
            case Constants.READING_MODE_COMPOSE:
                ContentValues compose = new ContentValues();
                compose.put(BOOK_LASTREAD, lastread);
                db.update(COMPOSE_TABLE, compose, BOOK_ID + "=?", new String[]{ id + ""});
                compose = new ContentValues();
                compose.put(BOOK_STATUS, STATUS_STARTED);
                db.update(COMPOSE_TABLE, compose,BOOK_ID + "=? and " + BOOK_STATUS + "=" + STATUS_NONE, new String[]{ id + ""});
                break;
            case Constants.READING_MODE_DEFAULT:
                ContentValues defaul = new ContentValues();
                defaul.put(BOOK_LASTREAD, lastread);
                db.update(DEFAULT_TABLE, defaul, BOOK_ID + "=?", new String[]{ id + ""});
                defaul = new ContentValues();
                defaul.put(BOOK_STATUS, STATUS_STARTED);
                db.update(DEFAULT_TABLE, defaul,BOOK_ID + "=? and " + BOOK_STATUS + "=" + STATUS_NONE, new String[]{ id + ""});
                break;
            case Constants.READING_MODE_PDF:
                ContentValues pdf = new ContentValues();
                pdf.put(BOOK_LASTREAD, lastread);
                db.update(PDF_TABLE, pdf, BOOK_ID + "=?", new String[]{ id + ""});
                pdf = new ContentValues();
                pdf.put(BOOK_STATUS, STATUS_STARTED);
                db.update(PDF_TABLE, pdf,BOOK_ID + "=? and " + BOOK_STATUS + "=" + STATUS_NONE, new String[]{ id + ""});
                break;
            case Constants.READING_MODE_MOUSPAD:
                ContentValues app = new ContentValues();
                app.put(BOOK_LASTREAD, lastread);
                db.update(MOUSPAD_TABLE, app, BOOK_ID + "=?", new String[]{ id + ""});
                app = new ContentValues();
                app.put(BOOK_STATUS, STATUS_STARTED);
                db.update(MOUSPAD_TABLE, app,BOOK_ID + "=? and " + BOOK_STATUS + "=" + STATUS_NONE, new String[]{ id + ""});
                break;
            default:
                Log.d("Load Database: ", "Error");
        }
    }

    public void updateStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues data = new ContentValues();
        data.put(BOOK_STATUS, status);
        switch (readmode){
            case Constants.READING_MODE_LIBRARY:
                db.update(BOOK_TABLE, data, BOOK_ID + "=?", new String[]{ id + ""});
                break;
            case Constants.READING_MODE_COMPOSE:
                db.update(COMPOSE_TABLE, data, BOOK_ID + "=?", new String[]{ id + ""});
                break;
            case Constants.READING_MODE_DEFAULT:
                db.update(DEFAULT_TABLE, data, BOOK_ID + "=?", new String[]{ id + ""});
                break;
            case Constants.READING_MODE_PDF:
                db.update(PDF_TABLE, data, BOOK_ID + "=?", new String[]{ id + ""});
                break;
            case Constants.READING_MODE_MOUSPAD:
                db.update(MOUSPAD_TABLE, data, BOOK_ID + "=?", new String[]{ id + ""});
                break;
            default:
                Log.d("Load Database: ", "Error");
        }
    }


    public BookRecord getBookRecord(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (readmode){
            case Constants.READING_MODE_LIBRARY:
                try (Cursor bookscursor = db.query(BOOK_TABLE, new String[] {BOOK_ID, BOOK_FILENAME, BOOK_TITLE, BOOK_AUTHOR, BOOK_LASTREAD, BOOK_ADDED, BOOK_STATUS}, BOOK_ID + "=?", new String[] {""+id}, null, null, null)) {
                    if (bookscursor.moveToNext()) {
                        return getBookRecord(bookscursor);
                    }
                }
                break;
            case Constants.READING_MODE_COMPOSE:
                try (Cursor bookscursor = db.query(COMPOSE_TABLE, new String[] {BOOK_ID, BOOK_FILENAME, BOOK_TITLE, BOOK_AUTHOR, BOOK_LASTREAD, BOOK_ADDED, BOOK_STATUS}, BOOK_ID + "=?", new String[] {""+id}, null, null, null)) {
                    if (bookscursor.moveToNext()) {
                        return getBookRecord(bookscursor);
                    }
                }
                break;
            case Constants.READING_MODE_DEFAULT:
                try (Cursor bookscursor = db.query(DEFAULT_TABLE, new String[] {BOOK_ID, BOOK_FILENAME, BOOK_TITLE, BOOK_AUTHOR, BOOK_LASTREAD, BOOK_ADDED, BOOK_STATUS}, BOOK_ID + "=?", new String[] {""+id}, null, null, null)) {
                    if (bookscursor.moveToNext()) {
                        return getBookRecord(bookscursor);
                    }
                }
                break;
            case Constants.READING_MODE_PDF:
                try (Cursor bookscursor = db.query(PDF_TABLE, new String[] {BOOK_ID, BOOK_FILENAME, BOOK_TITLE, BOOK_AUTHOR, BOOK_LASTREAD, BOOK_ADDED, BOOK_STATUS}, BOOK_ID + "=?", new String[] {""+id}, null, null, null)) {
                    if (bookscursor.moveToNext()) {
                        return getBookRecord(bookscursor);
                    }
                }
                break;
            case Constants.READING_MODE_MOUSPAD:
                try (Cursor bookscursor = db.query(MOUSPAD_TABLE, new String[] {BOOK_ID, BOOK_FILENAME, BOOK_TITLE, BOOK_AUTHOR, BOOK_LASTREAD, BOOK_ADDED, BOOK_STATUS}, BOOK_ID + "=?", new String[] {""+id}, null, null, null)) {
                    if (bookscursor.moveToNext()) {
                        return getBookRecord(bookscursor);
                    }
                }
                break;
            default:
                Log.d("Load Database: ", "Error");
        }
        return null;
    }

    public long getLastReadTime(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (readmode){
            case Constants.READING_MODE_LIBRARY:
                try (Cursor bookscursor = db.query(BOOK_TABLE, new String[] {BOOK_LASTREAD}, BOOK_ID + "=?", new String[] {""+id}, null, null, null)) {
                    if (bookscursor.moveToNext()) {
                        return bookscursor.getLong(bookscursor.getColumnIndex(BOOK_LASTREAD));
                    }
                }
                break;
            case Constants.READING_MODE_COMPOSE:
                try (Cursor bookscursor = db.query(COMPOSE_TABLE, new String[] {BOOK_LASTREAD}, BOOK_ID + "=?", new String[] {""+id}, null, null, null)) {
                    if (bookscursor.moveToNext()) {
                        return bookscursor.getLong(bookscursor.getColumnIndex(BOOK_LASTREAD));
                    }
                }
                break;
            case Constants.READING_MODE_DEFAULT:
                try (Cursor bookscursor = db.query(DEFAULT_TABLE, new String[] {BOOK_LASTREAD}, BOOK_ID + "=?", new String[] {""+id}, null, null, null)) {
                    if (bookscursor.moveToNext()) {
                        return bookscursor.getLong(bookscursor.getColumnIndex(BOOK_LASTREAD));
                    }
                }
                break;
            case Constants.READING_MODE_PDF:
                try (Cursor bookscursor = db.query(PDF_TABLE, new String[] {BOOK_LASTREAD}, BOOK_ID + "=?", new String[] {""+id}, null, null, null)) {
                    if (bookscursor.moveToNext()) {
                        return bookscursor.getLong(bookscursor.getColumnIndex(BOOK_LASTREAD));
                    }
                }
                break;
            case Constants.READING_MODE_MOUSPAD:
                try (Cursor bookscursor = db.query(MOUSPAD_TABLE, new String[] {BOOK_LASTREAD}, BOOK_ID + "=?", new String[] {""+id}, null, null, null)) {
                    if (bookscursor.moveToNext()) {
                        return bookscursor.getLong(bookscursor.getColumnIndex(BOOK_LASTREAD));
                    }
                }
                break;
            default:
                Log.d("Load Database: ", "Error");
        }
        return -1;
    }

    public int getStatus(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (readmode){
            case Constants.READING_MODE_LIBRARY:
                try (Cursor bookscursor = db.query(BOOK_TABLE, new String[] {BOOK_STATUS}, BOOK_ID + "=?", new String[] {""+id}, null, null, null)) {
                    if (bookscursor.moveToNext()) {
                        return bookscursor.getInt(bookscursor.getColumnIndex(BOOK_STATUS));
                    }
                }
                break;
            case Constants.READING_MODE_COMPOSE:
                try (Cursor bookscursor = db.query(COMPOSE_TABLE, new String[] {BOOK_STATUS}, BOOK_ID + "=?", new String[] {""+id}, null, null, null)) {
                    if (bookscursor.moveToNext()) {
                        return bookscursor.getInt(bookscursor.getColumnIndex(BOOK_STATUS));
                    }
                }
                break;
            case Constants.READING_MODE_DEFAULT:
                try (Cursor bookscursor = db.query(DEFAULT_TABLE, new String[] {BOOK_STATUS}, BOOK_ID + "=?", new String[] {""+id}, null, null, null)) {
                    if (bookscursor.moveToNext()) {
                        return bookscursor.getInt(bookscursor.getColumnIndex(BOOK_STATUS));
                    }
                }
                break;
            case Constants.READING_MODE_PDF:
                try (Cursor bookscursor = db.query(PDF_TABLE, new String[] {BOOK_STATUS}, BOOK_ID + "=?", new String[] {""+id}, null, null, null)) {
                    if (bookscursor.moveToNext()) {
                        return bookscursor.getInt(bookscursor.getColumnIndex(BOOK_STATUS));
                    }
                }
                break;
            case Constants.READING_MODE_MOUSPAD:
                try (Cursor bookscursor = db.query(MOUSPAD_TABLE, new String[] {BOOK_STATUS}, BOOK_ID + "=?", new String[] {""+id}, null, null, null)) {
                    if (bookscursor.moveToNext()) {
                        return bookscursor.getInt(bookscursor.getColumnIndex(BOOK_STATUS));
                    }
                }
                break;
            default:
                Log.d("Load Database: ", "Error");
        }
        return 0;
    }

    public int getMostRecentlyRead() {
        SQLiteDatabase db = this.getReadableDatabase();
        switch (readmode){
            case Constants.READING_MODE_LIBRARY:
                try (Cursor bookscursor =
                             db.rawQuery(
                                     "select " + BOOK_ID + " from " + BOOK_TABLE +
                                             " where " + BOOK_LASTREAD +
                                             " = (select max(" + BOOK_LASTREAD +") from " + BOOK_TABLE + " where " + BOOK_LASTREAD + ">0) and " + BOOK_STATUS +"=" + STATUS_STARTED, null)) {
                    if (bookscursor.moveToNext()) {
                        return bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID));
                    }
                }
                break;
            case Constants.READING_MODE_COMPOSE:
                try (Cursor bookscursor =
                             db.rawQuery(
                                     "select " + BOOK_ID + " from " + COMPOSE_TABLE +
                                             " where " + BOOK_LASTREAD +
                                             " = (select max(" + BOOK_LASTREAD +") from " + BOOK_TABLE + " where " + BOOK_LASTREAD + ">0) and " + BOOK_STATUS +"=" + STATUS_STARTED, null)) {
                    if (bookscursor.moveToNext()) {
                        return bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID));
                    }
                }
                break;
            case Constants.READING_MODE_DEFAULT:
                try (Cursor bookscursor =
                             db.rawQuery(
                                     "select " + BOOK_ID + " from " + DEFAULT_TABLE +
                                             " where " + BOOK_LASTREAD +
                                             " = (select max(" + BOOK_LASTREAD +") from " + BOOK_TABLE + " where " + BOOK_LASTREAD + ">0) and " + BOOK_STATUS +"=" + STATUS_STARTED, null)) {
                    if (bookscursor.moveToNext()) {
                        return bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID));
                    }
                }
                break;
            case Constants.READING_MODE_PDF:
                try (Cursor bookscursor =
                             db.rawQuery(
                                     "select " + BOOK_ID + " from " + PDF_TABLE +
                                             " where " + BOOK_LASTREAD +
                                             " = (select max(" + BOOK_LASTREAD +") from " + BOOK_TABLE + " where " + BOOK_LASTREAD + ">0) and " + BOOK_STATUS +"=" + STATUS_STARTED, null)) {
                    if (bookscursor.moveToNext()) {
                        return bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID));
                    }
                }
                break;
            case Constants.READING_MODE_MOUSPAD:
                try (Cursor bookscursor =
                             db.rawQuery(
                                     "select " + BOOK_ID + " from " + MOUSPAD_TABLE +
                                             " where " + BOOK_LASTREAD +
                                             " = (select max(" + BOOK_LASTREAD +") from " + BOOK_TABLE + " where " + BOOK_LASTREAD + ">0) and " + BOOK_STATUS +"=" + STATUS_STARTED, null)) {
                    if (bookscursor.moveToNext()) {
                        return bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID));
                    }
                }
                break;
            default:
                Log.d("Load Database: ", "Error");
        }
        return -1;
    }

    @NonNull
    private BookRecord getBookRecord(Cursor bookscursor) {
        BookRecord br = new BookRecord();
        br.id = bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID));
        br.filename = bookscursor.getString(bookscursor.getColumnIndex(BOOK_FILENAME));
        br.title = bookscursor.getString(bookscursor.getColumnIndex(BOOK_TITLE));
        br.author = bookscursor.getString(bookscursor.getColumnIndex(BOOK_AUTHOR));
        br.lastread = bookscursor.getLong(bookscursor.getColumnIndex(BOOK_LASTREAD));
        br.added = bookscursor.getLong(bookscursor.getColumnIndex(BOOK_ADDED));
        br.status = bookscursor.getInt(bookscursor.getColumnIndex(BOOK_STATUS));
        return br;
    }
    public List<Integer> getBookIds(SortOrder sortOrder, int status) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Integer> books = new ArrayList<>();
        String where = null;
        if (status>=0) {
            where = BOOK_STATUS + "=" + status;
        }
        String orderby = BOOK_STATUS + ", 2 desc, " + BOOK_LIB_TITLE + " asc";
        switch (sortOrder) {
            case Title: orderby = BOOK_LIB_TITLE + ", 2 desc"; break;
            case Author: orderby = BOOK_LIB_AUTHOR + ", " + BOOK_LIB_TITLE + ", 2 desc"; break;
            case Added: orderby = BOOK_ADDED + " desc, " + BOOK_LIB_TITLE + ", " + BOOK_LIB_AUTHOR ; break;
        }
        switch (readmode){
            case Constants.READING_MODE_LIBRARY:
                try (Cursor bookscursor = db.query(BOOK_TABLE,new String[] {BOOK_ID, BOOK_ADDED + "/80000"}, where, null, null, null, orderby)) {
                    while (bookscursor.moveToNext()) {
                        books.add(bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID)));
                    }
                }
                break;
            case Constants.READING_MODE_COMPOSE:
                try (Cursor bookscursor = db.query(COMPOSE_TABLE,new String[] {BOOK_ID, BOOK_ADDED + "/80000"}, where, null, null, null, orderby)) {
                    while (bookscursor.moveToNext()) {
                        books.add(bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID)));
                    }
                }
                break;
            case Constants.READING_MODE_DEFAULT:
                try (Cursor bookscursor = db.query(DEFAULT_TABLE,new String[] {BOOK_ID, BOOK_ADDED + "/80000"}, where, null, null, null, orderby)) {
                    while (bookscursor.moveToNext()) {
                        books.add(bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID)));
                    }
                }
                break;
            case Constants.READING_MODE_PDF:
                try (Cursor bookscursor = db.query(PDF_TABLE,new String[] {BOOK_ID, BOOK_ADDED + "/80000"}, where, null, null, null, orderby)) {
                    while (bookscursor.moveToNext()) {
                        books.add(bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID)));
                    }
                }
                break;
            case Constants.READING_MODE_MOUSPAD:
                try (Cursor bookscursor = db.query(MOUSPAD_TABLE,new String[] {BOOK_ID, BOOK_ADDED + "/80000"}, where, null, null, null, orderby)) {
                    while (bookscursor.moveToNext()) {
                        books.add(bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID)));
                    }
                }
                break;
            default:
                Log.d("Load Database: ", "Error");
        }
        return books;
    }

    public List<Integer> searchBooks(String text, boolean title, boolean author) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Integer> books = new ArrayList<>();
        String whereclause = null;
        List<String> whereargs = new ArrayList<>();
        String orderby = "2";
        if (title) {
            whereclause = BOOK_LIB_TITLE + " like ?";
            whereargs.add("%" + text + "%");
            orderby += "," + BOOK_LIB_TITLE;
        }
        if (author) {
            if (whereclause!=null) {
                whereclause += " or ";
            } else {
                whereclause = "";
            }
            whereclause += BOOK_LIB_AUTHOR + " like ?";
            whereargs.add("%" + text + "%");
            orderby += "," + BOOK_LIB_AUTHOR;
        }
        switch (readmode){
            case Constants.READING_MODE_LIBRARY:
                try (Cursor bookscursor = db.query(BOOK_TABLE,new String[] {BOOK_ID, BOOK_ADDED + "/90000"},
                        whereclause, whereargs.toArray(new String[whereargs.size()])
                        , null, null, orderby)) {
                    while (bookscursor.moveToNext()) {
                        books.add(bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID)));
                    }
                }
                break;
            case Constants.READING_MODE_COMPOSE:
                try (Cursor bookscursor = db.query(COMPOSE_TABLE,new String[] {BOOK_ID, BOOK_ADDED + "/90000"},
                        whereclause, whereargs.toArray(new String[whereargs.size()])
                        , null, null, orderby)) {
                    while (bookscursor.moveToNext()) {
                        books.add(bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID)));
                    }
                }
                break;
            case Constants.READING_MODE_DEFAULT:
                try (Cursor bookscursor = db.query(DEFAULT_TABLE,new String[] {BOOK_ID, BOOK_ADDED + "/90000"},
                        whereclause, whereargs.toArray(new String[whereargs.size()])
                        , null, null, orderby)) {
                    while (bookscursor.moveToNext()) {
                        books.add(bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID)));
                    }
                }
                break;
            case Constants.READING_MODE_PDF:
                try (Cursor bookscursor = db.query(PDF_TABLE,new String[] {BOOK_ID, BOOK_ADDED + "/90000"},
                        whereclause, whereargs.toArray(new String[whereargs.size()])
                        , null, null, orderby)) {
                    while (bookscursor.moveToNext()) {
                        books.add(bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID)));
                    }
                }
                break;
            case Constants.READING_MODE_MOUSPAD:
                try (Cursor bookscursor = db.query(MOUSPAD_TABLE,new String[] {BOOK_ID, BOOK_ADDED + "/90000"},
                        whereclause, whereargs.toArray(new String[whereargs.size()])
                        , null, null, orderby)) {
                    while (bookscursor.moveToNext()) {
                        books.add(bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID)));
                    }
                }
                break;
            default:
                Log.d("Load Database: ", "Error");
        }
        return books;
    }

    public List<Integer> searchBooksWithDirection(String text, boolean title, boolean author) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Integer> books = new ArrayList<>();
        String whereclause = null;
        List<String> whereargs = new ArrayList<>();
        String orderby = "2";
        if (title) {
            whereclause = BOOK_FILENAME + " like ?";
            whereargs.add("%" + text + "%");
            orderby += "," + BOOK_FILENAME;
        }
        if (author) {
            if (whereclause!=null) {
                whereclause += " or ";
            } else {
                whereclause = "";
            }
            whereclause += BOOK_LIB_AUTHOR + " like ?";
            whereargs.add("%" + text + "%");
            orderby += "," + BOOK_LIB_AUTHOR;
        }
        switch (readmode){
            case Constants.READING_MODE_LIBRARY:
                try (Cursor bookscursor = db.query(BOOK_TABLE,new String[] {BOOK_ID, BOOK_ADDED + "/90000"},
                        whereclause, whereargs.toArray(new String[whereargs.size()])
                        , null, null, orderby)) {
                    while (bookscursor.moveToNext()) {
                        books.add(bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID)));
                    }
                }
                break;
            case Constants.READING_MODE_COMPOSE:
                try (Cursor bookscursor = db.query(COMPOSE_TABLE,new String[] {BOOK_ID, BOOK_ADDED + "/90000"},
                        whereclause, whereargs.toArray(new String[whereargs.size()])
                        , null, null, orderby)) {
                    while (bookscursor.moveToNext()) {
                        books.add(bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID)));
                    }
                }
                break;
            case Constants.READING_MODE_DEFAULT:
                try (Cursor bookscursor = db.query(DEFAULT_TABLE,new String[] {BOOK_ID, BOOK_ADDED + "/90000"},
                        whereclause, whereargs.toArray(new String[whereargs.size()])
                        , null, null, orderby)) {
                    while (bookscursor.moveToNext()) {
                        books.add(bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID)));
                    }
                }
                break;
            case Constants.READING_MODE_PDF:
                try (Cursor bookscursor = db.query(PDF_TABLE,new String[] {BOOK_ID, BOOK_ADDED + "/90000"},
                        whereclause, whereargs.toArray(new String[whereargs.size()])
                        , null, null, orderby)) {
                    while (bookscursor.moveToNext()) {
                        books.add(bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID)));
                    }
                }
                break;
            case Constants.READING_MODE_MOUSPAD:
                try (Cursor bookscursor = db.query(MOUSPAD_TABLE,new String[] {BOOK_ID, BOOK_ADDED + "/90000"},
                        whereclause, whereargs.toArray(new String[whereargs.size()])
                        , null, null, orderby)) {
                    while (bookscursor.moveToNext()) {
                        books.add(bookscursor.getInt(bookscursor.getColumnIndex(BOOK_ID)));
                    }
                }
                break;
            default:
                Log.d("Load Database: ", "Error");
        }
        return books;
    }

    public class BookRecord {
        public int id;
        public String filename;
        public String title;
        public String author;
        public long lastread;
        public long added;
        public int status;
    }


}
