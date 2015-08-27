package com.codepath.simpletodo;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ToDoMainActivity extends AppCompatActivity {

    List<Todo> items;
    TodoAdapter itemsAdapter;
    ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("Simple Todo");

        // Fetch all of the items in our database
        TodoItemDatabase todoItemDatabase = TodoItemDatabase.getInstance(this);
        this.items = new ArrayList<Todo>();
        this.items = todoItemDatabase.getTodoItems();

        // Hook up to our adapter class with our items list
        this.itemsAdapter = new TodoAdapter(this, this.items);

        // Find the list items and hook it up with our adapter
        this.lvItems = (ListView)findViewById(R.id.lvItems);
        lvItems.setAdapter(this.itemsAdapter);

        // Attach listViewListener to update and remove items
        this.setupListViewListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAddItem(View v) {
        // Find the text field
        EditText etNewItem = (EditText)findViewById(R.id.etNewItem);

        // Get the input todo item and default date to now
        String itemText = etNewItem.getText().toString();
        Date now = new Date();

        // Add it to our database
        TodoItemDatabase todoItemDatabase = TodoItemDatabase.getInstance(this);
        long todoId = todoItemDatabase.addTodoItem(itemText, now);

        // Create the todo item
        Todo todo = new Todo();
        todo.todoId = todoId;
        todo.todoItem = itemText;
        todo.dueDate = now;

        // Add the todo item to the itemsAdapter
        itemsAdapter.add(todo);

        // Reset the input back to default
        etNewItem.setText("");
    }

    private void showDetailDialog(int position) {
        FragmentManager fm = getSupportFragmentManager();
        TodoDetailDialog todoDetailDialog = TodoDetailDialog.newInstance("Edit Item", position);
        todoDetailDialog.show(fm, "fragment_item_detail");
    }

    private void setupListViewListener() {
        this.lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Sample code if we wanted to do it the Activity way over the fragment way
                //Todo todoItem = items.get(position);
                //Intent intent = new Intent(ToDoMainActivity.this, DetailActivity.class);
                //intent.putExtra("todoItem", todoItem.todoItem);
                //intent.putExtra("todoItemId", todoItem.todoId);
                //startActivity(intent);
                //startActivityForResult(intent, 10);

                showDetailDialog(position);
            }
        });

        this.lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Todo todo = items.get(position);

                // Delete the todo item from our database
                TodoItemDatabase todoDatabase = TodoItemDatabase.getInstance(ToDoMainActivity.this);
                todoDatabase.deleteTodoItem(todo);

                // Remove from the list and notify the adapter that the data has changed
                items.remove(position);
                itemsAdapter.notifyDataSetChanged();

                return true;
            }
        });
    }

    public static void sortActivityItems(List<Todo> items) {
        // Sort by date which would be our priority
        Collections.sort(items, new Comparator<Todo>() {
            @Override
            public int compare(Todo lhs, Todo rhs) {
                if (lhs.dueDate.after(rhs.dueDate)) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });
    }

    // Sample code for the Activity callback
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            TodoItemDatabase todoItemDatabase = TodoItemDatabase.getInstance(this);
            this.items = todoItemDatabase.getTodoItems();
            this.itemsAdapter.clear();
                this.itemsAdapter.addAll(this.items);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }*/
}
