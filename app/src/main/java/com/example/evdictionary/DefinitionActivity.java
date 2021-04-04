package com.example.evdictionary;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

public class DefinitionActivity extends AppCompatActivity {
    private Word word;

    TextToSpeech textToSpeech;
    boolean isFavorite;

    TextView textViewDefinition;
    TextView appbarTitle;
    ImageButton backButton;
    ImageButton favoriteButton;

    RelativeLayout relativeLayoutAppbar;
    ImageButton speakButton;

    void getFavoriteStatus() {
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();

        String tableName;
        if (word.getLanguage().equals("english")) {
            tableName = "av";
        } else {
            tableName = "va";
        }

        Cursor cursor = database.rawQuery("SELECT favorite.id FROM favorite WHERE favorite.id = " + word.getId() + " AND favorite.tb = '" + tableName + "'", null);
        if (cursor.getCount() != 0) {
            isFavorite = true;
        } else {
            isFavorite = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definition);


        appbarTitle = findViewById(R.id.wordTitle);
        textViewDefinition = findViewById(R.id.text_definition);
        textViewDefinition.setMovementMethod(new ScrollingMovementMethod());
        backButton = findViewById(R.id.back_button);
        relativeLayoutAppbar = findViewById(R.id.appbar_definition);
        favoriteButton = findViewById(R.id.favoriteButton);
        speakButton = findViewById(R.id.speakButton);

        word = (Word) getIntent().getSerializableExtra("word");

        appbarTitle.setText(word.getWord());
        textViewDefinition.setText(Html.fromHtml(styleHtml(word.getHtml()), Html.FROM_HTML_MODE_LEGACY));

        getFavoriteStatus();
        if (word.getLanguage().equals("english")) {
            relativeLayoutAppbar.setBackgroundResource(R.drawable.round_appbar_english);

            if (isFavorite) {
                favoriteButton.setImageResource(R.drawable.ic_star_english_fill);
            } else {
                favoriteButton.setImageResource(R.drawable.ic_star_english_border);
            }
        } else {
            relativeLayoutAppbar.setBackgroundResource(R.drawable.round_appbar_vietnamese);

            if (isFavorite) {
                favoriteButton.setImageResource(R.drawable.ic_star_vietnamese_fill);
            } else {
                favoriteButton.setImageResource(R.drawable.ic_star_vietnamese_border);
            }
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (word.getLanguage().equals("vietnamese")) {
                    // I can't believe it work :))
                    textToSpeech.setLanguage(Locale.forLanguageTag("vi-VN"));
                } else {
                    textToSpeech.setLanguage(Locale.US);
                }
            }
        });

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.speak(word.getWord(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(getApplicationContext());
                SQLiteDatabase database = databaseHelper.getWritableDatabase();

                String tableName;
                if (word.getLanguage().equals("english")) {
                    tableName = "av";
                } else {
                    tableName = "va";
                }

                if (!isFavorite) {
                    database.execSQL("INSERT INTO favorite (id, word, tb) VALUES " + "(" + word.getId() + ", '" + word.getWord() + "', " + "'" + tableName + "')");
                    isFavorite = true;
                } else {
                    database.execSQL("DELETE FROM favorite WHERE id = " + word.getId() + " AND tb = '" + tableName + "'");
                    isFavorite = false;
                }

                if (word.getLanguage().equals("english")) {
                    if (isFavorite) {
                        favoriteButton.setImageResource(R.drawable.ic_star_english_fill);
                    } else {
                        favoriteButton.setImageResource(R.drawable.ic_star_english_border);
                    }
                } else {
                    if (isFavorite) {
                        favoriteButton.setImageResource(R.drawable.ic_star_vietnamese_fill);
                    } else {
                        favoriteButton.setImageResource(R.drawable.ic_star_vietnamese_border);
                    }
                }
            }
        });
    }



    String styleHtml(String html) {
        //TODO: Re-implement this function
        StringBuilder temp = new StringBuilder(html);

        // This is magic :)
        for (int i = 2; i < temp.length() - 2; ++i) {
            if (temp.charAt(i) == '1' && temp.charAt(i - 1) == 'h' && temp.charAt(i - 2) != '/') {
                temp.insert(i + 2, "<font color='#43a047'>");
            }  else if (temp.charAt(i) == '2' && temp.charAt(i - 1) == 'h' && temp.charAt(i - 2) != '/') {
                temp.insert(i + 2, "<font color='#1565c0'>");
            } else if (temp.charAt(i) == '3' && temp.charAt(i - 1) == 'h' && temp.charAt(i - 2) != '/') {
                temp.insert(i + 2, "<font color='red'>");
            } else if (temp.charAt(i) == '/' && temp.charAt(i + 1) == 'h' && (temp.charAt(i + 2) == '1' || temp.charAt(i + 2) == '2' || temp.charAt(i + 2) == '3')) {
                temp.insert(i - 1, "</font>");
                i += 7; // 7 is length of </font>
            }
        }

        return temp.toString();
    }
}