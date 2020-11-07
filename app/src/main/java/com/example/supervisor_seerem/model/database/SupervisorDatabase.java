package com.example.supervisor_seerem.model.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Class for inserting data into the database defined in SupervisorHelper
 * @Author Michael Mora
 */
public class SupervisorDatabase {

    private SQLiteDatabase db;
    private Context context;
    private final SupervisorHelper helper;

    public SupervisorDatabase (Context c){
        context = c;
        helper = new SupervisorHelper(context);
    }

    // Should be called in Activity where user can enter their personal information
    public long insertSupervisorData (
    String firstName, String lastName, String id, String medicalConsiderations,
    String emergencyContactType, int emergencyContactNumber, String emergencyContactName)
    {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SchemaConstants.SUPERVISOR_FIRST_NAME, firstName);
        contentValues.put(SchemaConstants.SUPERVISOR_LAST_NAME, lastName);
        contentValues.put(SchemaConstants.ID, id);
        contentValues.put(SchemaConstants.MEDICAL_CONSIDERATIONS, medicalConsiderations);
        contentValues.put(SchemaConstants.EMERGENCY_CONTACT_TYPE, emergencyContactType);
        contentValues.put(SchemaConstants.EMERGENCY_CONTACT_NUMBER, emergencyContactNumber);
        contentValues.put(SchemaConstants.EMERGENCY_CONTACT_NAME, emergencyContactName);
        long returnedData = db.insert(SchemaConstants.SUPERVISOR_TABLE_NAME, null, contentValues);
        return returnedData;
    }

    public int wipeData(){
        SQLiteDatabase db = helper.getWritableDatabase();
//        String[] whereArgs = {"herb"};
        int count = db.delete(SchemaConstants.SUPERVISOR_TABLE_NAME,  null, null);
        return count;
    }
}
