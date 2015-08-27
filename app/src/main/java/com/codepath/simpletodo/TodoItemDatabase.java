package com.codepath.simpletodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yahuijin on 8/24/15.
 */
public class TodoItemDatabase extends SQLiteOpenHelper {

    private static TodoItemDatabase sInstance;

    private static final String DATABASE_NAME = "TodoDatabase";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_TODO = "Todo";
    private static final String KEY_TODO_ID = "todoId";
    private static final String KEY_TODO_ITEM = "todoItem";
    private static final String KEY_TODO_DATE = "todoDate";

    private static final String TAG = "TodoDatabase";

    private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static synchronized TodoItemDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TodoItemDatabase(context.getApplicationContext());
        }

        return sInstance;
    }

    public TodoItemDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // These is where we need to write create table statements.
    // This is called when database is created.
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL for creating the tables
        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO +
                "(" +
                KEY_TODO_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_TODO_ITEM + " TEXT," +
                KEY_TODO_DATE + " TEXT" +
                ")";

        db.execSQL(CREATE_TODO_TABLE);
    }

    // This method is called when database is upgraded like modifying the table structure, adding
    // constraints to database, etc
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // SQL for upgrading the tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        this.onCreate(db);
    }

    public long addTodoItem(String todoItem, Date dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        long todoId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TODO_ITEM, todoItem);

            // Format date
            SimpleDateFormat format = new SimpleDateFormat(ISO_FORMAT);
            values.put(KEY_TODO_DATE, format.format(dueDate));

            todoId = db.insertOrThrow(TABLE_TODO, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add todo items to database");
        } finally {
            db.endTransaction();
        }

        return todoId;
    }

    public int updateTodoItem(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TODO_ITEM, todo.todoItem);

        // Format date
        SimpleDateFormat format = new SimpleDateFormat(ISO_FORMAT);
        values.put(KEY_TODO_DATE, format.format(todo.dueDate));

        return db.update(TABLE_TODO, values, KEY_TODO_ID + " = ?", new String[]{String.valueOf(todo.todoId)});
    }

    public int deleteTodoItem(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(TABLE_TODO, KEY_TODO_ID + " = ?", new String[] { String.valueOf(todo.todoId) });
    }

    public List<Todo> getTodoItems() {
        ArrayList<Todo> todoItems = new ArrayList<Todo>();

        String TODO_SELECT_QUERY = "SELECT * FROM " + TABLE_TODO;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(TODO_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Todo todo = new Todo();
                    todo.todoId = cursor.getInt(cursor.getColumnIndex(KEY_TODO_ID));
                    todo.todoItem = cursor.getString(cursor.getColumnIndex(KEY_TODO_ITEM));

                    // Convert ISO date string to date
                    String isoDate = cursor.getString(cursor.getColumnIndex(KEY_TODO_DATE));
                    SimpleDateFormat format = new SimpleDateFormat(ISO_FORMAT);
                    todo.dueDate = format.parse(isoDate);

                    todoItems.add(todo);
                } while (cursor.moveToNext());
            }
        } catch (Exception e)  {
            Log.d(TAG, "Error while trying to get todo items from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        ToDoMainActivity.sortActivityItems(todoItems);

        return todoItems;
    }

    public void resetDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        this.onCreate(db);
    }
}
