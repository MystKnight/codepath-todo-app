package com.codepath.simpletodo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by yahuijin on 8/24/15.
 */
public class TodoAdapter extends ArrayAdapter<Todo> {

    public TodoAdapter(Context context, List<Todo> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Todo todo = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_detail, parent, false);
        }

        // Find UI and set values
        TextView tvTodoItemTitle = (TextView)convertView.findViewById(R.id.tvTodoItemTitle);
        tvTodoItemTitle.setText(todo.todoItem);

        TextView tvDueDate = (TextView)convertView.findViewById(R.id.tvDueDate);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        tvDueDate.setText(format.format(todo.dueDate));

        return convertView;
    }
}
