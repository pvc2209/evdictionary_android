package com.example.evdictionary;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;


public class OfflineFragment extends Fragment {
    TranslateType translateType = TranslateType.av;

    EditText editTextSearch;
    ListView listViewWords;
    ImageButton imageButtonChangeType;
    ImageView imageFlagLeft;
    ImageView imageFlagRight;
    TextView textViewFlagLeft;
    TextView textViewFlagRight;
    RelativeLayout appbar;

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    private ArrayList<Word> words = new ArrayList<>();
    private WordListViewAdapter wordListViewAdapter;

    private void loadListWords(String inputWord) {
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("UnableToUpdateDatabase");
        }

        mDb = mDBHelper.getWritableDatabase();
        String table;
        if (translateType == TranslateType.av) {
            table = "av";
        } else {
            table = "va";
        }
        words.clear();

        Cursor cursor = mDb.rawQuery("SELECT * from " + table +" WHERE word LIKE '" + inputWord + "%' LIMIT 50", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(0);
            String word = cursor.getString(1);
            String html = cursor.getString(2);
            String description = cursor.getString(3);
            String pronounce = cursor.getString(4);

            String language;
            if (table == "av") {
                language = "english";
            } else {
                language = "vietnamese";
            }
            words.add(new Word(id, word, html, description, pronounce, language));
            cursor.moveToNext();
        }

        cursor.close();
//        Collections.sort(words);
    }

    private void goToDefinitionScreen(int wordPosition) {
        Intent intent = new Intent(getActivity(), DefinitionActivity.class);

        intent.putExtra("word", words.get(wordPosition));

        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offline, container, false);

        editTextSearch = view.findViewById(R.id.editTextOfflineSearch);
        listViewWords = view.findViewById(R.id.listViewWords);
        imageButtonChangeType = view.findViewById(R.id.imageButtonChangeType);
        imageFlagLeft = view.findViewById(R.id.flagIconLeft);
        imageFlagRight = view.findViewById(R.id.flagIconRight);
        textViewFlagLeft = view.findViewById(R.id.textViewLeft);
        textViewFlagRight = view.findViewById(R.id.textViewRight);
        appbar = view.findViewById(R.id.appbar);

        // Get DatabaseHelper instance
        mDBHelper = DatabaseHelper.getInstance(getActivity());
        // Load init words
        loadListWords("");

        wordListViewAdapter = new WordListViewAdapter(words);
        wordListViewAdapter.setTranslateType(translateType);
        listViewWords.setAdapter(wordListViewAdapter);

        editTextSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                // you can call or do what you want with your EditText here

                // yourEditText...
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String input = s.toString();
                loadListWords(input);
                wordListViewAdapter.notifyDataSetChanged();
            }
        });

        editTextSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!words.isEmpty()) {
                        goToDefinitionScreen(0);
                    }
                    return true;
                }
                return false;
            }
        });


        listViewWords.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToDefinitionScreen(position);
            }
        });

        imageButtonChangeType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (translateType == TranslateType.av) {
                    translateType = TranslateType.va;

                    imageFlagLeft.setImageResource(R.drawable.vietnamese);
                    imageFlagRight.setImageResource(R.drawable.english);

                    textViewFlagLeft.setText("Vietnamese");
                    textViewFlagRight.setText("English");

                    appbar.setBackgroundResource(R.drawable.round_appbar_va);
                } else {
                    translateType = TranslateType.av;

                    imageFlagLeft.setImageResource(R.drawable.english);
                    imageFlagRight.setImageResource(R.drawable.vietnamese);

                    textViewFlagLeft.setText("English");
                    textViewFlagRight.setText("Vietnamese");

                    appbar.setBackgroundResource(R.drawable.round_appbar_av);
                }

                loadListWords("");
                wordListViewAdapter.setTranslateType(translateType);
                wordListViewAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

}