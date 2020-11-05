package com.example.supervisor_seerem.model.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.supervisor_seerem.model.database.SchemaConstants;

/**
 * Helper class for creating the SQL Database for the Supervisor Class
 * @Author Michael Mora
 */
public class SupervisorHelper extends SQLiteOpenHelper {

    private Context context;

    private static final String CREATE_TABLE =
            "CREATE TABLE "+
                    SchemaConstants.SUPERVISOR_TABLE_NAME+ " (" +
                    SchemaConstants.SAVED_USERNAME + " TEXT, " +
                    SchemaConstants.SAVED_PASSWORD + " TEXT, " +
                    SchemaConstants.ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SchemaConstants.MEDICAL_CONSIDERATIONS + " TEXT, " +
                    SchemaConstants.EMERGENCY_CONTACT_NAME + " TEXT, " +
                    SchemaConstants.EMERGENCY_CONTACT_NUMBER + " INTEGER PRIMARY KEY AUTOINCREMENT); ";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + SchemaConstants.SUPERVISOR_TABLE_NAME;

    public SupervisorHelper(Context context){
        super(context, SchemaConstants.SUPERVISOR_DATABASE_NAME, null, SchemaConstants.SUPERVISOR_DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Toast.makeText(context, "DATABASE SUCCESSFULLY CREATED!", Toast.LENGTH_LONG).show();
            db.execSQL(CREATE_TABLE);
        } catch (SQLException e) {
            Toast.makeText(context, "ERROR: COULD NOT CREATE DATABASE!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(DROP_TABLE);
            onCreate(db);
            Toast.makeText(context, "DATABASE SUCCESSFULLY UPGRADED!", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(context, "ERROR: COULD NOT UPGRAADE DATABASE!", Toast.LENGTH_LONG).show();
        }
    }
}
