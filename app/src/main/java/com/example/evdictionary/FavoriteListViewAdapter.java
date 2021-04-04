package com.example.evdictionary;

import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class FavoriteListViewAdapter extends BaseAdapter {
    final ArrayList<Favorite> listFavorites;

    ImageView imageView;
    ImageButton deleteButton;

    public FavoriteListViewAdapter(ArrayList<Favorite> listFavorites) {
        this.listFavorites = listFavorites;
    }


    @Override
    public int getCount() {
        return listFavorites.size();
    }

    @Override
    public Object getItem(int position) {
        return listFavorites.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listFavorites.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewFavorite;
        if (convertView == null) {
            viewFavorite = View.inflate(parent.getContext(), R.layout.custom_view_with_delete, null);
        } else {
            viewFavorite = convertView;
        }

        imageView = viewFavorite.findViewById(R.id.flagImage);
        deleteButton = viewFavorite.findViewById(R.id.deleteItem);

        Favorite favorite = (Favorite) getItem(position);
        ((TextView) viewFavorite.findViewById(R.id.text)).setText(favorite.getWord());

        if (favorite.getTable().equals("av")) {
            imageView.setImageResource(R.drawable.english);
        } else {
            imageView.setImageResource(R.drawable.vietnamese);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance(viewFavorite.getContext());
                SQLiteDatabase database = databaseHelper.getWritableDatabase();

                String query = "DELETE FROM favorite WHERE id = " + listFavorites.get(position).getId() + " AND tb = '" + listFavorites.get(position).getTable() + "'";
                database.execSQL(query);
                listFavorites.remove(position);

                notifyDataSetChanged();
            }
        });

        return viewFavorite;
    }
}
