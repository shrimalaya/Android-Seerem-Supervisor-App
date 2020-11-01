package com.example.supervisor_seerem.model;

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
    public long insertSupervisorData (String username, String password,
    String id, String medicalConsiderations, String emergencyContactName,
                                      String emergencyContactNumber)
    {
        db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SchemaConstants.SAVED_USERNAME, username);
        contentValues.put(SchemaConstants.SAVED_PASSWORD, password);
        contentValues.put(SchemaConstants.ID, id);
        contentValues.put(SchemaConstants.MEDICAL_CONSIDERATIONS, medicalConsiderations);
        contentValues.put(SchemaConstants.EMERGENCY_CONTACT_NAME, emergencyContactName);
        contentValues.put(SchemaConstants.EMERGENCY_CONTACT_NUMBER, emergencyContactNumber);
        long returnedData = db.insert(SchemaConstants.SUPERVISOR_TABLE_NAME, null, contentValues);
        return returnedData;
    }

}
