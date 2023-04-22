package com.La_nota.ALLA.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.La_nota.ALLA.Models.TaskModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TasksHandler2 extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";

    private static final String TASK_TABLE = "tasks";
    private static final String DATE = "date";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    //позиция в списке дня
    private static final String POSITION = "position";

    private static final String SUBTASK_TABLE = "subtasks";

    private static final String CREATE_TASK_TABLE = "CREATE TABLE " + TASK_TABLE +
            "(" + POSITION + " INTEGER, "
            + DATE + " INTEGER, "
            + TASK + " TEXT, "
            + STATUS + " INTEGER)";


    private SQLiteDatabase db;

    public TasksHandler2(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TASK_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE);
        onCreate(db);
    }

    public void openDB() {
        db = this.getWritableDatabase();
    }


    public void insertTask(TaskModel task, String date, int pos) {
        ContentValues cv = new ContentValues();
        cv.put(DATE, date);
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        cv.put(POSITION, pos + 1);
        // Log.d("MYLOG", cv.getAsInteger(POSITION)+"");
        // Log.d("MYLOGoncreate", cv.getAsInteger(POSITION) + " " + cv.getAsString(DATE));
        db.insert(TASK_TABLE, null, cv);
    }



    @SuppressLint("Range")
    public List<TaskModel> getAllTasksForDay(String date){
        List<TaskModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(TASK_TABLE,  null, DATE + " = ?",new String[]{date},null,null,POSITION,null);
            if(cur != null){
                if (cur.moveToFirst()){
                    do {
                        TaskModel task = new TaskModel();
                        int position = cur.getInt(cur.getColumnIndex(POSITION));

                        task.setAll(position,
                                cur.getInt(cur.getColumnIndex(STATUS)),
                                cur.getString(cur.getColumnIndex(TASK)));
                        
                        taskList.add(task);
                    }while(cur.moveToNext());
                }
            }
        }finally {
            db.endTransaction();
            if (cur != null) {
                cur.close();
            }
        }
        return taskList;
    }


    //обновляторы
    public void updateStatus(String date, int position, int status) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TASK_TABLE, cv, DATE + " =? " + " AND " + POSITION + " =?", new String[]{date, String.valueOf(position)});
    }

    public void updateTask(String date, int position, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TASK_TABLE, cv, DATE + " =? " + " AND " + POSITION + " =?", new String[]{date, String.valueOf(position)});
    }

    public void updatePosition(String date, int position, int newposition) {
        ContentValues cv = new ContentValues();
        cv.put(POSITION, newposition);
        db.update(TASK_TABLE, cv, DATE + " =? " + " AND " + POSITION + " =?", new String[]{date, String.valueOf(position)});
    }


    public void deleteTask(String date, int position) {
        db.delete(TASK_TABLE, DATE + " =? " + " AND " + POSITION + " =?", new String[]{date, "" + position});
        deleteHelper(date, position);
    }

    @SuppressLint("Range")
    private void deleteHelper(String date, int position){
        Map<Integer, Integer> map = new TreeMap<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(TASK_TABLE, new String[]{POSITION}, DATE + " = ?" + " AND " + POSITION + " > ?", new String[]{date, position + ""}, null, null, POSITION, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    do {
                        int a = cur.getInt(cur.getColumnIndex(POSITION));
                        map.put(a - 1, a);
                    }
                    while (cur.moveToNext());
                }

            }
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        for (Map.Entry<Integer, Integer> entry :
                map.entrySet()) {
            updatePosition(date, entry.getValue(), entry.getKey());
        }
    }

    //очистититель базы данных от уже прошедших и не отображающихся дней
    public void clearTable(String dateForClear) {
        db.delete(TASK_TABLE, DATE + "<?", new String[]{dateForClear});
    }

    //ФУНКЦИB ТОЛЬКО ДЛЯ ДЕБАГА
    public void deleteBASIC() {
        db.delete(TASK_TABLE, null, null);
    }


}
