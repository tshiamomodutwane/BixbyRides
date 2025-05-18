package com.example.bixbyrides;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "BixbyRides.db";
    private static final int DATABASE_VERSION = 1;

    // Users table
    private static final String TABLE_USERS = "Users";
    private static final String COLUMN_USER_ID = "userID";
    private static final String COLUMN_FULL_NAME = "fullName";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PHONE = "phoneNumber";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_CREATED_AT = "createdAt";

    // Rides table
    private static final String TABLE_RIDES = "Rides";
    private static final String COLUMN_RIDE_ID = "rideID";
    private static final String COLUMN_USER_ID_FK = "userID";  // Foreign key to Users table
    private static final String COLUMN_START_LOCATION = "startLocation";
    private static final String COLUMN_END_LOCATION = "endLocation";
    private static final String COLUMN_DISTANCE = "distance";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FULL_NAME + " TEXT, "
                + COLUMN_EMAIL + " TEXT UNIQUE, "
                + COLUMN_PHONE + " TEXT, "
                + COLUMN_PASSWORD + " TEXT, "
                + COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create Rides table
        String CREATE_RIDES_TABLE = "CREATE TABLE " + TABLE_RIDES + "("
                + COLUMN_RIDE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USER_ID_FK + " INTEGER, "
                + COLUMN_START_LOCATION + " TEXT, "
                + COLUMN_END_LOCATION + " TEXT, "
                + COLUMN_DISTANCE + " REAL, "
                + COLUMN_PRICE + " REAL, "
                + COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_RIDES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older tables if they exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RIDES);
        // Create tables again
        onCreate(db);
    }

    // Add a new user
    public long addUser(String fullName, String email, String phoneNumber, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FULL_NAME, fullName);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phoneNumber);
        values.put(COLUMN_PASSWORD, password);

        // Insert row
        long result = db.insert(TABLE_USERS, null, values);
        db.close();  // Close database connection
        return result;
    }

    // Retrieve user by email
    public Cursor getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ?";
        return db.rawQuery(query, new String[]{email});
    }

    // Retrieve user by ID
    public Cursor getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USER_ID + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(userId)});
    }

    // Update user info (email and phone number)
    public boolean updateUser(int userId, String email, String phoneNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PHONE, phoneNumber);
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsAffected > 0;
    }

    // Change user password
    public boolean changePassword(int userId, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, newPassword);
        int rowsAffected = db.update(TABLE_USERS, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsAffected > 0;
    }

    // Delete user by ID
    public boolean deleteUser(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_USERS, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return rowsDeleted > 0;
    }

    // Add a new ride for a user
    public long addRide(int userID, String fromLocation, String toLocation, double distance, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FK, userID);
        values.put(COLUMN_START_LOCATION, fromLocation);
        values.put(COLUMN_END_LOCATION, toLocation);
        values.put(COLUMN_DISTANCE, distance);
        values.put(COLUMN_PRICE, price);

        // Insert into Rides table
        return db.insert(TABLE_RIDES, null, values);
    }




    // Retrieve rides for a specific user
    public Cursor getRidesByUserID(int userID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_RIDES + " WHERE " + COLUMN_USER_ID_FK + " = ?";
        return db.rawQuery(query, new String[]{String.valueOf(userID)});
    }

    public Cursor getUserByEmailAndPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";
        return db.rawQuery(query, new String[]{email, password});
    }



}
