package com.example.evdictionary;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    private ArrayList<History> histories = new ArrayList<>();
    private ListView listViewHistories;
    private HistoryListViewAdapter historyListViewAdapter;
    private ImageButton deleteAllButton;

    private SQLiteDatabase mDb;
    private DatabaseHelper mDBHelper;

    private void loadListHistories() {
        Cursor cursor = mDb.rawQuery("SELECT * FROM history ORDER BY position DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int position = cursor.getInt(0);
            int id = cursor.getInt(1);
            String word = cursor.getString(2);
            String table = cursor.getString(3);

            histories.add(new History(position, id, word, table));
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void goToDefinitionScreen(Word word) {
        Intent intent = new Intent(getActivity(), DefinitionActivity.class);

        intent.putExtra("word", word);

        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        listViewHistories = view.findViewById(R.id.listViewHistories);
        deleteAllButton = view.findViewById(R.id.deleteAllHistories);

        mDBHelper = DatabaseHelper.getInstance(getActivity());
        mDb = mDBHelper.getWritableDatabase();

        loadListHistories();

        historyListViewAdapter = new HistoryListViewAdapter(histories);
        listViewHistories.setAdapter(historyListViewAdapter);

        listViewHistories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long idd) {
                String table;
                TranslateType translateType;
                if (histories.get(position).getTable().equals("av")) {
                    table = "av";
                    translateType = TranslateType.av;
                } else {
                    table = "va";
                    translateType = TranslateType.va;
                }

                Cursor cursor = mDb.rawQuery("SELECT * FROM " + table + " WHERE " + table + ".id = " + histories.get(position).getId(), null);

                cursor.moveToFirst();
                int id = cursor.getInt(0);
                String word = cursor.getString(1);
                String html = cursor.getString(2);
                String description = cursor.getString(3);
                String pronounce = cursor.getString(4);

                String language;
                if (translateType == TranslateType.av) {
                    language = "english";
                } else {
                    language = "vietnamese";
                }

                Word result = new Word(id, word, html, description, pronounce, language);

                goToDefinitionScreen(result);
            }
        });

//        listViewHistories.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                mDb.execSQL("DELETE FROM history WHERE position = " + histories.get(position).getPosition());
//                histories.remove(position);
//
//                historyListViewAdapter.notifyDataSetChanged();
//                return true;
//            }
//        });
        
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDb.execSQL("DELETE FROM history");
                histories.clear();
                historyListViewAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }
}