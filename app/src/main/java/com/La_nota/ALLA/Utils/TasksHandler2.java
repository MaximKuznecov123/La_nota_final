package com.La_nota.ALLA.Utils;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.La_nota.ALLA.Activities.MainActivity;
import com.La_nota.ALLA.Interfaces.TaskOperator;
import com.La_nota.ALLA.Models.TaskModel;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TasksHandler2 extends SQLiteOpenHelper {
    public static final int VERSION = 1;

    private static final String DATABASE_NAME = "toDoListDatabase";

    private static final String TABLE_NAME = "tasks";
    private static final String SH_TABLE_NAME = "SHAREDtodo";
    private static final String SH_STATUS_TABLE_NAME = "statusSHAREDtodo";

    private static final String DATE_COLUMN = "date";
    private static final String TITLE_COLUMN = "title";
    private static final String DESCR_COLUMN = "description";
    private static final String STATUS_COLUMN = "status";

    private static final String ID_COLUMN = "id";

    private static final String CREATE_BASIC_TABLE = "CREATE TABLE " + TABLE_NAME +
            "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DATE_COLUMN + " INTEGER, "
            + TITLE_COLUMN + " TEXT, "
            + DESCR_COLUMN + " TEXT, "
            + STATUS_COLUMN + " INTEGER)";

    private static final String FREQUENCY_COLUMN = "frequency";

    private static final String CREATE_SHARED_TABLE = "CREATE TABLE " + SH_TABLE_NAME +
            "(" + ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + FREQUENCY_COLUMN + " INTEGER,"
            + DATE_COLUMN + " INTEGER, "
            + TITLE_COLUMN + " TEXT, "
            + DESCR_COLUMN + " TEXT)";

    private static final String CREATE_SHARED_STATUSES_TABLE = "CREATE TABLE " + SH_STATUS_TABLE_NAME +
            "(" + DATE_COLUMN + " INTEGER, "
            + ID_COLUMN + " INTEGER)";

    private SQLiteDatabase db;
    private BasicTaskOperator basicOperator;
    private SHTaskOperator shOperator;

    public TasksHandler2(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BASIC_TABLE);
        db.execSQL(CREATE_SHARED_TABLE);
        db.execSQL(CREATE_SHARED_STATUSES_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SH_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SH_STATUS_TABLE_NAME);
        onCreate(db);
    }

    public void openDB() {
        db = this.getWritableDatabase();
        basicOperator = new BasicTaskOperator();
        shOperator = new SHTaskOperator();
    }

    public List<TaskModel> getTasks(String date, boolean sh) {
        return sh ? shOperator.getTasks(date) : basicOperator.getTasks(date);
    }

    public void insertTask(TaskModel task, String date) {
        if (task.getFrequency() != 0)
            shOperator.insertTask(task, date);
        else
            basicOperator.insertTask(task, date);
    }

    public void updateStatus(String date, int id, int status) {
        if (date != null)
            shOperator.updateStatus(date, id, status);
        else
            basicOperator.updateStatus(id, status);
    }

    public void updateTask(int id, String title, boolean isSH) {
        if (isSH)
            shOperator.updateTitle(id, title);
        else
            basicOperator.updateTitle(id, title);
    }

    public void updateDescr(int id, String descr, boolean isSH) {
        if (isSH)
            shOperator.updateDescr(id, descr);
        else
            basicOperator.updateDescr(id, descr);
    }

    public void deleteTask(int id, boolean isSH) {
        if (isSH)
            shOperator.deleteTask(id);
        else
            basicOperator.deleteTask(id);
    }

    public void clearTable(String dateForClear) {
        db.delete(TABLE_NAME, DATE_COLUMN + "<?", new String[]{dateForClear});
    }

    class BasicTaskOperator implements TaskOperator {
        @SuppressLint("Range")
        @Override
        public List<TaskModel> getTasks(String date) {
            List<TaskModel> taskList = new ArrayList<>();
            Cursor cur = null;
            db.beginTransaction();
            try {
                cur = db.query(TABLE_NAME, null, DATE_COLUMN + " = ?", new String[]{date}, null, null, null, null);
                if (cur != null) {
                    if (cur.moveToFirst()) {
                        do {
                            TaskModel task = new TaskModel();
                            task.setID(cur.getInt(cur.getColumnIndex(ID_COLUMN)));
                            task.setTitle(cur.getString(cur.getColumnIndex(TITLE_COLUMN)));
                            task.setDescription(cur.getString(cur.getColumnIndex(DESCR_COLUMN)));
                            task.setStatus(cur.getInt(cur.getColumnIndex(STATUS_COLUMN)));
                            taskList.add(task);
                        } while (cur.moveToNext());
                    }
                }
            } finally {
                db.endTransaction();
                assert cur != null;
                cur.close();
            }
            return taskList;
        }

        @Override
        public void insertTask(TaskModel task, String date) {
            ContentValues cv = new ContentValues();
            cv.put(DATE_COLUMN, date);
            cv.put(TITLE_COLUMN, task.getTitle());
            cv.put(DESCR_COLUMN, task.getDescription());
            cv.put(STATUS_COLUMN, 0);
            db.insert(TABLE_NAME, null, cv);
        }

        public void updateStatus(int id, int status) {
            ContentValues cv = new ContentValues();
            cv.put(STATUS_COLUMN, status);
            db.update(TABLE_NAME, cv, ID_COLUMN + " =?", new String[]{String.valueOf(id)});
        }

        @Override
        public void updateTitle(int id, String title) {
            ContentValues cv = new ContentValues();
            cv.put(TITLE_COLUMN, title);
            db.update(TABLE_NAME, cv, ID_COLUMN + " =?", new String[]{String.valueOf(id)});
        }

        @Override
        public void updateDescr(int id, String descr) {
            ContentValues cv = new ContentValues();
            cv.put(DESCR_COLUMN, descr);
            db.update(TABLE_NAME, cv, ID_COLUMN + " =?", new String[]{String.valueOf(id)});
        }

        @Override
        public void deleteTask(int id) {
            db.delete(TABLE_NAME, ID_COLUMN + " =?", new String[]{String.valueOf(id)});
        }
    }

    class SHTaskOperator implements TaskOperator {
        @SuppressLint("Range")
        @Override
        public List<TaskModel> getTasks(String date) {
            List<TaskModel> sharedTasklist = new ArrayList<>();
            Cursor cur = null;
            db.beginTransaction();
            try {
                cur = db.query(SH_TABLE_NAME, null, DATE_COLUMN + " <= " + date, null, null, null, null, null);
                if (cur != null) {
                    if (cur.moveToFirst()) {
                        LocalDate dateForGet = LocalDate.parse(date, MainActivity.formatter);
                        do {
                            @SuppressLint("Range") LocalDate dateOfTask = LocalDate.parse(cur.getString(cur.getColumnIndex(DATE_COLUMN)), MainActivity.formatter);
                            int frequency = cur.getInt(cur.getColumnIndex(FREQUENCY_COLUMN));
                            if (ChronoUnit.DAYS.between(dateForGet, dateOfTask) % frequency == 0) {
                                TaskModel task = new TaskModel();
                                task.setFrequency(frequency);
                                task.setTitle(cur.getString(cur.getColumnIndex(TITLE_COLUMN)));
                                task.setDescription(cur.getString(cur.getColumnIndex(DESCR_COLUMN)));

                                int id = cur.getInt(cur.getColumnIndex(ID_COLUMN));
                                task.setID(id);
                                task.setStatus(getStatusOfSharedTask(date, id));

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
            return sharedTasklist;
        }
        
        @SuppressLint("Range")
        public int getStatusOfSharedTask(String date, int id) {
            Cursor cur = null;
            db.beginTransaction();
            int result = 0;
            try {
                cur = db.query(SH_STATUS_TABLE_NAME, null, DATE_COLUMN + " =? " + " AND " + ID_COLUMN + " =?", new String[]{date, id + ""}, null, null, null);
                if (cur != null) {
                    if (cur.moveToFirst())
                        result = 1;
                }
            } finally {
                db.endTransaction();
                assert cur != null;
                cur.close();
            }
            return result;
        }

        @Override
        public void insertTask(TaskModel task, String date) {
            ContentValues cv = new ContentValues();
            cv.put(DATE_COLUMN, date);
            cv.put(TITLE_COLUMN, task.getTitle());
            cv.put(DESCR_COLUMN, task.getDescription());
            cv.put(FREQUENCY_COLUMN, task.getFrequency());
            db.insert(SH_TABLE_NAME, null, cv);
        }

        public void updateStatus(String date, int id, int status) {
            if (status == 0)
                db.delete(SH_STATUS_TABLE_NAME, ID_COLUMN + " =? " + " AND " + DATE_COLUMN + " =?", new String[]{String.valueOf(id), date});
            else {
                ContentValues cv = new ContentValues();
                cv.put(ID_COLUMN, id);
                cv.put(DATE_COLUMN, date);
                db.insert(SH_STATUS_TABLE_NAME, null, cv);
            }
        }

        @Override
        public void updateTitle(int id, String title) {
            ContentValues cv = new ContentValues();
            cv.put(TITLE_COLUMN, title);
            db.update(SH_TABLE_NAME, cv, ID_COLUMN + " =?", new String[]{String.valueOf(id)});
        }

        @Override
        public void updateDescr(int id, String descr) {
            ContentValues cv = new ContentValues();
            cv.put(DESCR_COLUMN, descr);
            db.update(SH_TABLE_NAME, cv, ID_COLUMN + " =?", new String[]{String.valueOf(id)});
        }

        @Override
        public void deleteTask(int id) {
            db.delete(SH_STATUS_TABLE_NAME, ID_COLUMN + " =?", new String[]{String.valueOf(id)});
            db.delete(SH_TABLE_NAME, ID_COLUMN + " =?", new String[]{String.valueOf(id)});
        }

    }
    public void deleteAll(){
        db.delete(SH_STATUS_TABLE_NAME, null,null);
        db.delete(TABLE_NAME, null, null);
        db.delete(SH_TABLE_NAME, null, null);
    }

}