package com.example.myapplication.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.Adapters.VPadapter;
import com.example.myapplication.Models.TaskModel2;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class TasksHandler2 extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String DATE = "date";
    private static final String TASK = "task";
    private static final String DESCR = "descr";
    private static final String STATUS = "status";
    //позиция в списке дня
    private static final String POSITION = "position";

    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE +
            "(" + POSITION + " INTEGER, "
             + DATE + " INTEGER, "
            + TASK + " TEXT, "
            + DESCR + " TEXT, "
            + STATUS + " INTEGER)";
    private SQLiteDatabase db;

    public TasksHandler2(Context context){
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        onCreate(db);
    }

    public void openDB(){
            db = this.getWritableDatabase();
    }



    @SuppressLint("Range")
    public void insertTask(TaskModel2 task, String date){
        ContentValues cv = new ContentValues();
        Cursor cur = null;
        db.beginTransaction();
        int maxpos = 0;
        try {
            cur = db.query(TODO_TABLE, new String[]{"MAX(position)"}, DATE + " = ?",new String[]{date},null,null,null,null);
            if (cur != null) {
                if (cur.moveToFirst()) maxpos = cur.getInt(cur.getColumnIndex("MAX(position)"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            assert cur != null;
            cur.close();
            db.endTransaction();
        }


        cv.put(DATE, date);
        cv.put(TASK, task.getTask());
        cv.put(DESCR, task.getDescription());
        cv.put(STATUS, 0);
        cv.put(POSITION, maxpos+1);
       // Log.d("MYLOG", cv.getAsInteger(POSITION)+"");
        db.insert(TODO_TABLE, null, cv);
    }

    @SuppressLint("Range")
    public List<TaskModel2> getAllTasksForDay(String date){
        List<TaskModel2> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(TODO_TABLE,  null, DATE + " = ?",new String[]{date},null,null,POSITION,null);
            if(cur != null){
                if (cur.moveToFirst()){
                    do {
                        TaskModel2 task = new TaskModel2();

                        int a = cur.getInt(cur.getColumnIndex(POSITION));

                        task.setAll(a,
                                cur.getInt(cur.getColumnIndex(STATUS)),
                                cur.getString(cur.getColumnIndex(TASK)),
                                cur.getString(cur.getColumnIndex(DESCR)));
                        taskList.add(task);
                    }while(cur.moveToNext());
                }
            }
        }finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return taskList;
    }
    //обновляторы
    public void updateStatus(String date, int position, int status){
            ContentValues cv = new ContentValues();
            cv.put(STATUS,status);
            db.update(TODO_TABLE, cv, DATE + " =? " + " AND " + POSITION + " =?", new String[]{date, ""+position});
    }

    public void updateTask(String date, int position, String task){
            ContentValues cv = new ContentValues();
            cv.put(TASK, task);
        db.update(TODO_TABLE, cv, DATE + " =? " + " AND " + POSITION + " =?", new String[]{date, ""+position});
    }
    public void updateDescr(String date, int position , String descr){
            ContentValues cv = new ContentValues();
            cv.put(DESCR, descr);
        db.update(TODO_TABLE, cv, DATE + " =? " + " AND " + POSITION + " =?", new String[]{date, ""+position});
    }
    public void updatePosition(String date, int position , int newposition){
        ContentValues cv = new ContentValues();
        cv.put(POSITION, newposition);
    }


    public void deleteTask(String date, int position){
        db.delete(TODO_TABLE, DATE + " =? " + " AND " + POSITION + " =?", new String[]{date, ""+position});
        deleteHelper(date, position);
    }

    @SuppressLint("Range")
    private void deleteHelper(String date, int position){
        Map<Integer, Integer> map = new TreeMap<>();
            Cursor cur = null;
            db.beginTransaction();
            try {
                cur = db.query(TODO_TABLE, new String[]{POSITION}, DATE + " = ?" + " AND " + POSITION + " > ?",new String[]{date,position+""},null,null,POSITION,null);
                if(cur != null){
                    if (cur.moveToFirst()){
                        int neededPos = position;
                        do {
                            int a = cur.getInt(cur.getColumnIndex(POSITION));
                            map.put(neededPos,a);
                            neededPos++;
                        }while(cur.moveToNext());
                    }
                }
            }finally {
                db.endTransaction();
                assert cur != null;
                cur.close();
            }
        for (Map.Entry<Integer,Integer> entry:
             map.entrySet()) {
            updatePosition(date, entry.getValue(), entry.getKey());
        }
    }
    //очистититель базы данных от уже прошедших и не отображающихся дней
    public void clearTable(Calendar c){
        c.add(Calendar.DATE, -VPadapter.defaultpage);
        db.delete(TODO_TABLE,DATE + "<?" , new String[]{new SimpleDateFormat("yyMMdd").format(c.getTime())});
    }

    //ФУНКЦИЯ ТОЛЬКО ДЛЯ ДЕБАГА
    public void deleteAll(){
        db.delete(TODO_TABLE,null,null);
    }
}

