package com.La_nota.ALLA.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.La_nota.ALLA.Models.SubTaskModel;
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
    private static final String POS_OF_ROOT = "root_pos";

    private static final String CREATE_TASK_TABLE = "CREATE TABLE " + TASK_TABLE +
            "(" + POSITION + " INTEGER, "
            + DATE + " INTEGER, "
            + TASK + " TEXT, "
            + STATUS + " INTEGER)";

    private static final String CREATE_SUBTASK_TABLE = "CREATE TABLE " + SUBTASK_TABLE +
            "(" + POS_OF_ROOT + " INTEGER, "
            + DATE + " INTEGER, "
            + POSITION + " INTEGER, "
            + TASK + " TEXT, "
            + STATUS + " INTEGER)";


    private SQLiteDatabase db;

    public TasksHandler2(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TASK_TABLE);
        db.execSQL(CREATE_SUBTASK_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SUBTASK_TABLE);
        onCreate(db);
    }

    public void openDB() {
        db = this.getWritableDatabase();
    }

    @SuppressLint("Range")
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
    
    public void insertSubTask(SubTaskModel subtask, String date, int rootpos, int pos){
        ContentValues cv = new ContentValues();
        cv.put(DATE, date);
        cv.put(TASK, subtask.getTask());
        cv.put(STATUS, 0);
        cv.put(POSITION, pos + 1);
        cv.put(POS_OF_ROOT, rootpos);
        
        db.insert(SUBTASK_TABLE, null, cv);
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
    
    @SuppressLint("Range")
    private ArrayList<ArrayList<SubTaskModel>> getAllSubTasks(String date){
        Map<Integer, ArrayList<SubTaskModel>> MapOfSubtaskLists = new TreeMap<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(SUBTASK_TABLE,  null, DATE + " = ?",new String[]{date},null,null,POSITION,null);
            if(cur != null){
                if (cur.moveToFirst()){
                    do {
                        int b = cur.getInt(cur.getColumnIndex(POS_OF_ROOT));

                        SubTaskModel subtask = new SubTaskModel();
                        subtask.setAll(b,
                                cur.getInt(cur.getColumnIndex(STATUS)),
                                cur.getString(cur.getColumnIndex(TASK)));

                        if (MapOfSubtaskLists.containsKey(b)){
                            MapOfSubtaskLists.get(b).add(subtask);
                        }else{
                            ArrayList<SubTaskModel> list = new ArrayList<>();
                            list.add(subtask);
                            MapOfSubtaskLists.put(b, list);
                        }

                    }while(cur.moveToNext());
                }
            }
        }finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        Log.d("MYLOG", MapOfSubtaskLists.values().toString());

        return (ArrayList<ArrayList<SubTaskModel>>) MapOfSubtaskLists.values();
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
