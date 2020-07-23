package standrews_shinescreen.coralware;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.view.View;

public class Savadata_db {
    private static String OCR_table = "saveddata.db";
    private static final int VERSION = 1;
    private static final String COL1 = "ID";
    private static final String COL2 = "textOCR";
    private SQLiteDatabase db;
    private DatabaseHelper hDB;

    public Savadata_db(View.OnClickListener context) {
        hDB = new DatabaseHelper ((Context) context, OCR_table);
    }

    public void open() {
        db = hDB.getWritableDatabase();
    }
    public void close() {
        hDB.close();
    }
    public SQLiteDatabase getDb() {
        return db;
    }

}
