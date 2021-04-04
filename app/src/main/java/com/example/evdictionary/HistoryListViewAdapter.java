package com.example.evdictionary;

import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryListViewAdapter extends BaseAdapter {
    final ArrayList<History> listHistories;

    ImageView imageView;
    ImageButton deleteButton;

    public HistoryListViewAdapter(ArrayList<History> listHistories) {
        this.listHistories = listHistories;
    }


    @Override
    public int getCount() {
        return listHistories.size();
    }

    @Override
    public Object getItem(int position) {
        return listHistories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listHistories.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewHistory;
        if (convertView == null) {
            viewHistory = View.inflate(parent.getContext(), R.layout.custom_view_with_delete, null);
        } else {
            viewHistory = convertView;
        }

        imageView = viewHistory.findViewById(R.id.flagImage);
        deleteButton = viewHistory.findViewById(R.id.deleteItem);

        History history = (History) getItem(position);
        ((TextView) viewHistory.findViewById(R.id.text)).setText(history.getWord());

        if (history.getTable().equals("av")) {
            imageView.setImageResource(R.drawable.english);
        } else {
            imageView.setImageResource(R.drawable.vietnamese);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(viewHistory.getContext());
                SQLiteDatabase database = databaseHelper.getWritableDatabase();

                database.execSQL("DELETE FROM history WHERE position = " + listHistories.get(position).getPosition());
                listHistories.remove(position);

                notifyDataSetChanged();
            }
        });

        return viewHistory;
    }
}
