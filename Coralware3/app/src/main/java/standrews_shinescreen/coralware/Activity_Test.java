package standrews_shinescreen.coralware;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class Activity_Test extends AppCompatActivity {
    private static final String TAG = "ListDataActivity";

    DatabaseHelper mDatabaseHelper;

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__test);

        mListView = (ListView) findViewById(R.id.listView);


        populatelistView();
    }
    private void populatelistView() {
        Log.d(TAG, "populatelistView: Displaying data in ListView");

        Cursor data = mDatabaseHelper.getData();

        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()) {
            //Take the value from database in column 1 and add to array list
            listData.add(data.getString(1));
        }

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter);
    }
    
    private void toastMessage(String message) {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
    }
}