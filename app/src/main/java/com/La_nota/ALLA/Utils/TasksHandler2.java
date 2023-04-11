package com.La_nota.ALLA.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.La_nota.ALLA.Activities.MainActivity;
import com.La_nota.ALLA.Models.BasicTaskModel;
import com.La_nota.ALLA.Models.SharedTaskModel;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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

    private static final String TODO_SHARED_TABLE = "SHAREDtodo";
    private static final String FREQUENCY = "frequency";
    private static final String ID = "id";

    private static final String TODO_SHARED_STATUS_TABLE = "statusSHAREDtodo";

    private static final String CREATE_TASK_TABLE = "CREATE TABLE " + TODO_TABLE +
            "(" + POSITION + " INTEGER, "
            + DATE + " INTEGER, "
            + TASK + " TEXT, "
            + DESCR + " TEXT, "
            + STATUS + " INTEGER)";

    private static final String CREATE_SHARED_TABLE = "CREATE TABLE " + TODO_SHARED_TABLE +
            "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FREQUENCY + " INTEGER,"
            + DATE + " INTEGER, "
            + TASK + " TEXT, "
            + DESCR + " TEXT)";

    private static final String CREATE_SHARED_STATUSES_TABLE = "CREATE TABLE " + TODO_SHARED_STATUS_TABLE +
            "(" + DATE + " INTEGER, "
            + ID + " INTEGER,"
            + STATUS + " INTEGER)";


    private SQLiteDatabase db;

    public TasksHandler2(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TASK_TABLE);
        db.execSQL(CREATE_SHARED_TABLE);
        try {
            db.execSQL(CREATE_SHARED_STATUSES_TABLE);
        } catch (SQLException e) {
            Log.d("MYLOGanotherbug", e.getMessage());
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TODO_SHARED_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TODO_SHARED_STATUS_TABLE);

        onCreate(db);
    }

    public void openDB() {
        db = this.getWritableDatabase();
    }

    @SuppressLint("Range")
    public void insertTask(BasicTaskModel task, String date, int pos) {
        ContentValues cv = new ContentValues();
        cv.put(DATE, date);
        cv.put(TASK, task.getTask());
        cv.put(DESCR, task.getDescription());
        cv.put(STATUS, 0);
        cv.put(POSITION, pos + 1);
        // Log.d("MYLOG", cv.getAsInteger(POSITION)+"");
        // Log.d("MYLOGoncreate", cv.getAsInteger(POSITION) + " " + cv.getAsString(DATE));
        db.insert(TODO_TABLE, null, cv);
    }

    public void insertSHTask(SharedTaskModel task, String date) {
        ContentValues cv = new ContentValues();
        cv.put(DATE, date);
        cv.put(FREQUENCY, task.getFrequency());
        cv.put(TASK, task.getTask());
        cv.put(DESCR, task.getDescription());

        //Log.d("MYLOGtaskInfo", cv.getAsInteger(DATE) + " " + cv.getAsInteger(FREQUENCY) + '\n' + cv.getAsString(TASK) + " "  + cv.getAsString(DESCR) );
        db.insert(TODO_SHARED_TABLE, null, cv);
        Log.d("MYLOGinsert", String.valueOf(cv.getAsInteger(DATE)));
        //Log.d("MYLOG", "created");
    }

    @SuppressLint("Range")
    public List<BasicTaskModel> getAllTasksForDay(String date) {
        List<BasicTaskModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(TODO_TABLE, null, DATE + " = ?", new String[]{date}, null, null, POSITION, null);

            if (cur != null) {
                List<BasicTaskModel> list = getAllSharedTasksForDay(date);
                if (cur.moveToFirst()) {
                    int expectedpos = 1;
                    int b = 0;
                    boolean flag = true;

                    do {
                        BasicTaskModel task;
                        int position = cur.getInt(cur.getColumnIndex(POSITION));

                        if (position == expectedpos) {
                            task = new BasicTaskModel();
                            task.setAll(position,
                                    cur.getInt(cur.getColumnIndex(STATUS)),
                                    cur.getString(cur.getColumnIndex(TASK)),
                                    cur.getString(cur.getColumnIndex(DESCR)));
                            taskList.add(task);
                        } else {
                            task = list.get(b);
                            task.setPosition(expectedpos);
                            taskList.add(task);
                            flag = false;
                            b++;
                        }

                        expectedpos++;

                        if (flag) {
                            if (!cur.moveToNext()) break;
                        } else {
                            flag = true;
                        }

                    } while (true);

                    if (!list.isEmpty() && b != list.size()) {
                        List<BasicTaskModel> c = list.subList(b, list.size());
                        for (int i = 0; i < c.size(); i++, expectedpos++) {
                            c.get(i).setPosition(expectedpos);
                        }
                        taskList.addAll(c);
                    }
                } else {
                    taskList.addAll(list);
                }
            }
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return taskList;
    }

    @SuppressLint("Range")
    public List<BasicTaskModel> getAllSharedTasksForDay(String date) {
        List<BasicTaskModel> sharedTasklist = new ArrayList<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(TODO_SHARED_TABLE, null, null, null, null, null, null, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    LocalDate localDate = LocalDate.parse(date, MainActivity.formatter);
                    do {
                        @SuppressLint("Range") LocalDate date1 = LocalDate.parse(cur.getString(cur.getColumnIndex(DATE)), MainActivity.formatter);
                        //Log.d("MYLOGget", date1.toString());
                        if (ChronoUnit.DAYS.between(localDate, date1) % cur.getInt(cur.getColumnIndex(FREQUENCY)) == 0
                                && !localDate.isBefore(date1)) {
                            BasicTaskModel task = new BasicTaskModel();
                            task.setSharedtrue(true);
                            task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                            task.setDescription(cur.getString(cur.getColumnIndex(DESCR)));
                            int id = cur.getInt(cur.getColumnIndex(ID));
                            task.setId(id);
                            task.setStatus(getStatusofSharedTask(date, id));

                            sharedTasklist.add(task);
                        }
                    } while (cur.moveToNext());
                }
            }
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        //Log.d("MYLOG", sharedTasklist.isEmpty()+"");
        return sharedTasklist;
    }

    @SuppressLint("Range")
    public int getStatusofSharedTask(String date, int id) {
        Cursor cur = null;
        db.beginTransaction();
        int result = 0;
        try {
            cur = db.query(TODO_SHARED_STATUS_TABLE, null, DATE + " =? " + " AND " + ID + " =?", new String[]{date, id + ""}, null, null, null);
            if (cur != null) {
                if (cur.moveToFirst())
                    result = cur.getInt(cur.getColumnIndex(STATUS));
            }

        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        return result;
    }

    //обновляторы
    public void updateStatus(String date, int position, int status) {
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, DATE + " =? " + " AND " + POSITION + " =?", new String[]{date, String.valueOf(position)});
    }

    public void updateTask(String date, int position, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_TABLE, cv, DATE + " =? " + " AND " + POSITION + " =?", new String[]{date, String.valueOf(position)});
    }

    public void updateDescr(String date, int position, String descr) {
        ContentValues cv = new ContentValues();
        cv.put(DESCR, descr);
        db.update(TODO_TABLE, cv, DATE + " =? " + " AND " + POSITION + " =?", new String[]{date, String.valueOf(position)});
    }

    public void updatePosition(String date, int position, int newposition) {
        ContentValues cv = new ContentValues();
        cv.put(POSITION, newposition);
        db.update(TODO_TABLE, cv, DATE + " =? " + " AND " + POSITION + " =?", new String[]{date, String.valueOf(position)});
    }

    public void updateSharedStatus(String date, int id, int status) {
        db.beginTransaction();
        Cursor cur = null;
        boolean isAlredyAssigned = false;
        try {
            cur = db.query(TODO_SHARED_STATUS_TABLE, null, DATE + " =? " + " AND " + ID + " =?", new String[]{date, id + ""}, null, null, null);
            if (cur != null)
                isAlredyAssigned = cur.moveToFirst();
        } finally {
            db.endTransaction();
            assert cur != null;
            cur.close();
        }
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        if (isAlredyAssigned)
            db.update(TODO_SHARED_STATUS_TABLE, cv, DATE + " =? " + " AND " + ID + " =?", new String[]{date, id + ""});
        else {
            cv.put(ID, id);
            cv.put(DATE, date);
            db.insert(TODO_SHARED_STATUS_TABLE, null, cv);
        }
    }

    public void updateTask(int id, String task) {
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_SHARED_TABLE, cv, ID + " =?", new String[]{id + ""});
    }

    public void updateDescr(int id, String descr) {
        ContentValues cv = new ContentValues();
        cv.put(DESCR, descr);
        db.update(TODO_SHARED_TABLE, cv, ID + " =?", new String[]{id + ""});
    }


    public void deleteTask(String date, int position) {
        db.delete(TODO_TABLE, DATE + " =? " + " AND " + POSITION + " =?", new String[]{date, "" + position});
        deleteHelper(date, position);
    }

    @SuppressLint("Range")
    private void deleteHelper(String date, int position) {
        // FIXME: не работает с долёнными заданиями
        Map<Integer, Integer> map = new TreeMap<>();
        Cursor cur = null;
        db.beginTransaction();
        try {
            cur = db.query(TODO_TABLE, new String[]{POSITION}, DATE + " = ?" + " AND " + POSITION + " > ?", new String[]{date, position + ""}, null, null, POSITION, null);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    int neededPos = position;
                    do {
                        int a = cur.getInt(cur.getColumnIndex(POSITION));
                        map.put(neededPos, a);
                        neededPos++;
                    } while (cur.moveToNext());
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
        db.delete(TODO_TABLE, DATE + "<?", new String[]{dateForClear});
    }

    //ФУНКЦИB ТОЛЬКО ДЛЯ ДЕБАГА
    public void deleteBASIC() {
        db.delete(TODO_TABLE, null, null);
    }

    public void deleteSH() {
        db.delete(TODO_SHARED_TABLE, null, null);
    }
}

