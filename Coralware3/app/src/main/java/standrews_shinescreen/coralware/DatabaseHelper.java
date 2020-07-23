package standrews_shinescreen.coralware;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String COL1 = "ID";
    private static final String COL3 = "textOCR";
    private static final String OCR_DATA = "saved_data_table";

    public DatabaseHelper(Context context, String name) { super(context, OCR_DATA, null, 1); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + OCR_DATA + " (" + COL1 + " TEXT NOT NULL, " + COL3 + " TEXT NOT NULL);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE " + OCR_DATA + ";");
        onCreate(db);
    }

    public boolean addDatA(String item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL3, item);

        Log.d(TAG, "addData: Adding " + item + " to " + OCR_DATA);

        long result = db.insert(OCR_DATA, null, contentValues);
        return result != -1;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + OCR_DATA;
        return db.rawQuery(query, null);

    }


}
