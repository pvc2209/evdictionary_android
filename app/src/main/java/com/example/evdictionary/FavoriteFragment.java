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
import android.widget.ListView;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {
    private ArrayList<Favorite> favorites = new ArrayList<>();
    private ListView listViewFavorites;
    private FavoriteListViewAdapter favoriteListViewAdapter;

    private SQLiteDatabase mDb;
    private DatabaseHelper mDBHelper;

    private void loadListFavorite() {
        favorites.clear();
        Cursor cursor = mDb.rawQuery("SELECT * FROM favorite", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String word = cursor.getString(1);
            String table = cursor.getString(2);

            favorites.add(new Favorite(id, word, table));
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
    public void onStart() {
        super.onStart();
        // Khi back lại từ definition screen thì onStart sẽ được gọi lại

        loadListFavorite();
        favoriteListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        listViewFavorites = view.findViewById(R.id.listViewFavorites);

        mDBHelper = DatabaseHelper.getInstance(getActivity());
        mDb = mDBHelper.getWritableDatabase();

//        loadListFavorite();

        favoriteListViewAdapter = new FavoriteListViewAdapter(favorites);
        listViewFavorites.setAdapter(favoriteListViewAdapter);


        listViewFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long idd) {
                String table;
                TranslateType translateType;
                if (favorites.get(position).getTable().equals("av")) {
                    table = "av";
                    translateType = TranslateType.av;
                } else {
                    table = "va";
                    translateType = TranslateType.va;
                }

                Cursor cursor = mDb.rawQuery("SELECT * FROM " + table + " WHERE " + table + ".id = " + favorites.get(position).getId(), null);

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


        return view;
    }
}