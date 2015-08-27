package com.codepath.simpletodo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.GregorianCalendar;

/**
 * Created by yahuijin on 8/24/15.
 */
public class TodoDetailDialog extends DialogFragment {

    Todo todo;

    public TodoDetailDialog() {}

    public static TodoDetailDialog newInstance(String title, int position) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("position", position);

        TodoDetailDialog frag = new TodoDetailDialog();
        frag.setArguments(args);

        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_detail, container);

        // Set the dialog title
        String title = getArguments().getString("title", "Todo Details");
        getDialog().setTitle(title);

        // With the position that was tapped on, we can go find the todo item
        final int position = getArguments().getInt("position");
        ToDoMainActivity activity = (ToDoMainActivity)getActivity();
        this.todo = activity.items.get(position);

        // Set the text and calendar
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(this.todo.dueDate);
        final DatePicker datePicker = (DatePicker)view.findViewById(R.id.datePicker);
        datePicker.updateDate(
                calendar.get(GregorianCalendar.YEAR),
                calendar.get(GregorianCalendar.MONTH),
                calendar.get(GregorianCalendar.DAY_OF_MONTH)
        );

        final EditText etTodoItem = (EditText)view.findViewById(R.id.etTodoItem);
        etTodoItem.setText(this.todo.todoItem);

        // Set the text field to focus
        etTodoItem.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        Button btn = (Button)view.findViewById(R.id.btnSaveItem);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Update the text
                TodoDetailDialog.this.todo.todoItem = etTodoItem.getText().toString();

                GregorianCalendar calendar = new GregorianCalendar(
                        datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth()
                );
                TodoDetailDialog.this.todo.dueDate = calendar.getTime();

                // Update the database
                TodoItemDatabase todoItemDatabase = TodoItemDatabase.getInstance(getActivity());
                todoItemDatabase.updateTodoItem(TodoDetailDialog.this.todo);

                // Refresh the main activity
                ToDoMainActivity activity = (ToDoMainActivity)getActivity();
                //activity.items = todoItemDatabase.getTodoItems();
                activity.items.set(position, TodoDetailDialog.this.todo);

                // Sort the list by priority
                ToDoMainActivity.sortActivityItems(activity.items);

                // Notify the list view things have changed
                activity.itemsAdapter.notifyDataSetChanged();

                // Dismiss the dialog
                getDialog().dismiss();
            }
        });

        return view;
    }
}
