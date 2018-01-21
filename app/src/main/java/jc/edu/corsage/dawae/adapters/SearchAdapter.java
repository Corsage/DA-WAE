package jc.edu.corsage.dawae.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import java.util.ArrayList;

import jc.edu.corsage.dawae.R;
import jc.edu.corsage.dawae.mapquest.models.Result;

/**
 * Created by j3chowdh on 1/20/2018.
 */

public class SearchAdapter extends CursorAdapter {
    LayoutInflater inflater;
    private ArrayList<Result> results = null;

    public SearchAdapter(Context context, Cursor c) {
        super(context, c, false);
        this.inflater = LayoutInflater.from(context);
        this.results = results;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = this.inflater.inflate(R.layout.item_search, parent, false);
        } else {
            view = convertView;
        }

        this.getCursor().moveToPosition(pos);
        viewHolder vh = new viewHolder(view);

        vh.name.setText(this.getCursor().getString(this.getCursor().getColumnIndex("name")));

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TODO.
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // TODO.
        return null;
    }

    class viewHolder {
        public TextView name;
        public viewHolder(View view) {
            name = view.findViewById(R.id.name);
        }
    }

}
