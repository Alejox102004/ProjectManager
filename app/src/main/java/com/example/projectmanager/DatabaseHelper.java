package com.example.projectmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ProjectManager.db";
    private static final int DATABASE_VERSION = 1;

    // Nombres de tablas
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PROJECTS = "projects";
    private static final String TABLE_ACTIVITIES = "activities";

    // Columnas de la tabla usuarios
    private static final String COL_USER_ID = "user_id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    // Columnas de la tabla proyectos
    private static final String COL_PROJECT_ID = "project_id";
    private static final String COL_PROJECT_NAME = "name";
    private static final String COL_PROJECT_DESCRIPTION = "description";
    private static final String COL_PROJECT_START_DATE = "start_date";
    private static final String COL_PROJECT_END_DATE = "end_date";
    private static final String COL_PROJECT_USER_ID = "user_id";

    // Columnas de la tabla actividades
    private static final String COL_ACTIVITY_ID = "activity_id";
    private static final String COL_ACTIVITY_NAME = "name";
    private static final String COL_ACTIVITY_DESCRIPTION = "description";
    private static final String COL_ACTIVITY_START_DATE = "start_date";
    private static final String COL_ACTIVITY_END_DATE = "end_date";
    private static final String COL_ACTIVITY_STATUS = "status";
    private static final String COL_ACTIVITY_PROJECT_ID = "project_id";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USERNAME + " TEXT UNIQUE, " +
                COL_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);

        String createProjectsTable = "CREATE TABLE " + TABLE_PROJECTS + " (" +
                COL_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PROJECT_NAME + " TEXT, " +
                COL_PROJECT_DESCRIPTION + " TEXT, " +
                COL_PROJECT_START_DATE + " TEXT, " +
                COL_PROJECT_END_DATE + " TEXT, " +
                COL_PROJECT_USER_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_PROJECT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + "))";
        db.execSQL(createProjectsTable);

        String createActivitiesTable = "CREATE TABLE " + TABLE_ACTIVITIES + " (" +
                COL_ACTIVITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_ACTIVITY_NAME + " TEXT, " +
                COL_ACTIVITY_DESCRIPTION + " TEXT, " +
                COL_ACTIVITY_START_DATE + " TEXT, " +
                COL_ACTIVITY_END_DATE + " TEXT, " +
                COL_ACTIVITY_STATUS + " TEXT, " +
                COL_ACTIVITY_PROJECT_ID + " INTEGER, " +
                "FOREIGN KEY(" + COL_ACTIVITY_PROJECT_ID + ") REFERENCES " + TABLE_PROJECTS + "(" + COL_PROJECT_ID + "))";
        db.execSQL(createActivitiesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACTIVITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Operaciones de usuarios
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERS + " WHERE " +
                COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?", new String[]{username, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_USER_ID + " FROM " + TABLE_USERS + " WHERE " +
                COL_USERNAME + " = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(cursor.getColumnIndex(COL_USER_ID));
            cursor.close();
            db.close();
            return userId;
        }
        cursor.close();
        db.close();
        return -1;
    }

    // Operaciones de proyectos
    public boolean addProject(String name, String description, String startDate, String endDate, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PROJECT_NAME, name);
        values.put(COL_PROJECT_DESCRIPTION, description);
        values.put(COL_PROJECT_START_DATE, startDate);
        values.put(COL_PROJECT_END_DATE, endDate);
        values.put(COL_PROJECT_USER_ID, userId);
        long result = db.insert(TABLE_PROJECTS, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getProjects(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PROJECTS + " WHERE " + COL_PROJECT_USER_ID + " = ?",
                new String[]{String.valueOf(userId)});
    }

    public boolean updateProject(int projectId, String name, String description, String startDate, String endDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PROJECT_NAME, name);
        values.put(COL_PROJECT_DESCRIPTION, description);
        values.put(COL_PROJECT_START_DATE, startDate);
        values.put(COL_PROJECT_END_DATE, endDate);
        int result = db.update(TABLE_PROJECTS, values, COL_PROJECT_ID + " = ?",
                new String[]{String.valueOf(projectId)});
        db.close();
        return result > 0;
    }

    public boolean deleteProject(int projectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_PROJECTS, COL_PROJECT_ID + " = ?",
                new String[]{String.valueOf(projectId)});
        db.delete(TABLE_ACTIVITIES, COL_ACTIVITY_PROJECT_ID + " = ?",
                new String[]{String.valueOf(projectId)});
        db.close();
        return result > 0;
    }

    // Operaciones de actividades
    public boolean addActivity(String name, String description, String startDate, String endDate, String status, int projectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ACTIVITY_NAME, name);
        values.put(COL_ACTIVITY_DESCRIPTION, description);
        values.put(COL_ACTIVITY_START_DATE, startDate);
        values.put(COL_ACTIVITY_END_DATE, endDate);
        values.put(COL_ACTIVITY_STATUS, status);
        values.put(COL_ACTIVITY_PROJECT_ID, projectId);
        long result = db.insert(TABLE_ACTIVITIES, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getActivities(int projectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ACTIVITIES + " WHERE " + COL_ACTIVITY_PROJECT_ID + " = ?",
                new String[]{String.valueOf(projectId)});
    }

    public boolean updateActivity(int activityId, String name, String description, String startDate, String endDate, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ACTIVITY_NAME, name);
        values.put(COL_ACTIVITY_DESCRIPTION, description);
        values.put(COL_ACTIVITY_START_DATE, startDate);
        values.put(COL_ACTIVITY_END_DATE, endDate);
        values.put(COL_ACTIVITY_STATUS, status);
        int result = db.update(TABLE_ACTIVITIES, values, COL_ACTIVITY_ID + " = ?",
                new String[]{String.valueOf(activityId)});
        db.close();
        return result > 0;
    }

    public boolean deleteActivity(int activityId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_ACTIVITIES, COL_ACTIVITY_ID + " = ?",
                new String[]{String.valueOf(activityId)});
        db.close();
        return result > 0;
    }

    // Calcular avance del proyecto
    public float getProjectProgress(int projectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_ACTIVITY_STATUS + " FROM " + TABLE_ACTIVITIES +
                " WHERE " + COL_ACTIVITY_PROJECT_ID + " = ?", new String[]{String.valueOf(projectId)});
        int totalActivities = cursor.getCount();
        if (totalActivities == 0) {
            cursor.close();
            db.close();
            return 0;
        }

        int completedActivities = 0;
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex(COL_ACTIVITY_STATUS)).equals("Realizado")) {
                completedActivities++;
            }
        }
        cursor.close();
        db.close();
        return (completedActivities * 100.0f) / totalActivities;
    }
}