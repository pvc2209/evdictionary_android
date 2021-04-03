package com.example.evdictionary;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class WordListViewAdapter extends BaseAdapter {
    final ArrayList<Word> listWords;

    ImageView imageView;
    TranslateType translateType = TranslateType.av;

    public WordListViewAdapter(ArrayList<Word> listWords) {
        this.listWords = listWords;
    }

    public void setTranslateType(TranslateType translateType) {
        this.translateType = translateType;
    }

    @Override
    public int getCount() {
        return listWords.size();
    }

    @Override
    public Object getItem(int position) {
        return listWords.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listWords.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewWord;
        if (convertView == null) {
            viewWord = View.inflate(parent.getContext(), R.layout.word_view, null);
        } else {
            viewWord = convertView;
        }

        imageView = viewWord.findViewById(R.id.flagImage);

        if (translateType == TranslateType.av) {
            imageView.setImageResource(R.drawable.english);
        } else {
            imageView.setImageResource(R.drawable.vietnamese);
        }

        Word word = (Word) getItem(position);
        ((TextView) viewWord.findViewById(R.id.text_word)).setText(word.getWord());
        return viewWord;
    }
}
