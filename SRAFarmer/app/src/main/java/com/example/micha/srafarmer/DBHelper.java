package com.example.micha.srafarmer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.micha.srafarmer.Entity.CVS;
import com.example.micha.srafarmer.Entity.Fertilizer;
import com.example.micha.srafarmer.Entity.Field;
import com.example.micha.srafarmer.Entity.NotificationsLocal;
import com.example.micha.srafarmer.Entity.Problem;
import com.example.micha.srafarmer.Entity.Production;
import com.example.micha.srafarmer.Entity.Recommendation;
import com.example.micha.srafarmer.Entity.Tiller;

import java.util.ArrayList;
import java.util.Date;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SRA";

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String fields = "CREATE TABLE " + Contract.Fields.TABLE_NAME+" ( " +
                Contract.Fields.COLUMN_ID+" INTEGER PRIMARY KEY, " +
                Contract.Fields.COLUMN_BARANGAY + " VARCHAR(45), "+
                Contract.Fields.COLUMN_MUNICIPALITY + " VARCHAR(45), "+
                Contract.Fields.COLUMN_AREA +" DECIMAL(6,3), " +
                Contract.Fields.COLUMN_DAMAGE+ " DECIMAL(6,3), " +
                Contract.Fields.COLUMN_BOUNDARY+ " TEXT );";
        sqLiteDatabase.execSQL(fields);
        String cropValidationSurveys = "CREATE TABLE " + Contract.CropValidationSurveys.TABLE_NAME +" ( " +
                Contract.CropValidationSurveys.COLUMN_YEAR + " INTEGER, "+
                Contract.CropValidationSurveys.COLUMN_FIELDS_ID +" INTEGER, "+
                Contract.CropValidationSurveys.COLUMN_VARIETY+" VARCHAR(45), "+
                Contract.CropValidationSurveys.COLUMN_CROP_CLASS +" VARCHAR(45), "+
                Contract.CropValidationSurveys.COLUMN_TEXTURE+" VARCHAR(45), "+
                Contract.CropValidationSurveys.COLUMN_FARMING_SYSTEM+ " VARCHAR(45), "+
                Contract.CropValidationSurveys.COLUMN_TOPOGRAPHY +" VARCHAR(45), "+
                Contract.CropValidationSurveys.COLUMN_FURROW_DISTANCE+" DECIMAL(7,2), "+
                Contract.CropValidationSurveys.COLUMN_PLANTING_DENSITY+ " DECIMAL(7,2), "
                +" PRIMARY KEY ("+ Contract.CropValidationSurveys.COLUMN_YEAR+", " + Contract.CropValidationSurveys.COLUMN_FIELDS_ID+"));";
        sqLiteDatabase.execSQL(cropValidationSurveys);
        String millingSurvey = "CREATE TABLE " + Contract.Milling.TABLE_NAME+" ( " +
                Contract.Milling.COLUMN_YEAR + " INTEGER, "+
                Contract.Milling.COLUMN_FIELDS_ID +" INTEGER, "+
                Contract.Milling.COLUMN_NUM_MILLABLE+ " INTEGER, "+
                Contract.Milling.COLUMN_AVG_MILLABLE_STOOL+" DECIMAL(7,2), "+
                Contract.Milling.COLUMN_BRIX+" DECIMAL(7,2), " +
                Contract.Milling.COLUMN_STALK_LENGTH+" DECIMAL(7,2), "+
                Contract.Milling.COLUMN_DIAMETER+ " DECIMAL(7,2), "+
                Contract.Milling.COLUMN_WEIGHT+" DECIMAL(7,2), "
                +" PRIMARY KEY ("+ Contract.Milling.COLUMN_YEAR+", " + Contract.Milling.COLUMN_FIELDS_ID+"));";
        sqLiteDatabase.execSQL(millingSurvey);

        String fertilizers = "CREATE TABLE "+ Contract.Fertilizers.TABLE_NAME+ " ( "+
                Contract.Fertilizers.COLUMN_YEAR+" INTEGER, "+
                Contract.Fertilizers.COLUMN_FIELDS_ID+" INTEGER, "+
                Contract.Fertilizers.COLUMN_FERTILZER+" VARCHAR(45), "+
                Contract.Fertilizers.COLUMN_FIRST_DOSE+" DECIMAL(7,2), "+
                Contract.Fertilizers.COLUMN_SECOND_DOSE+" DECIMAL(7,2), "+
                " PRIMARY KEY ("+ Contract.Fertilizers.COLUMN_YEAR+", "+ Contract.Fertilizers.COLUMN_FIELDS_ID+", "+ Contract.Fertilizers.COLUMN_FERTILZER+"));";
        sqLiteDatabase.execSQL(fertilizers);
        String tillers = "CREATE TABLE "+ Contract.Tillers.TABLE_NAME+" ( "+
                Contract.Tillers.COLUMN_YEAR+" INTEGER, "+
                Contract.Tillers.COLUMN_FIELDS_ID+" INTEGER, "+
                Contract.Tillers.COLUMN_REP+" INTEGER, "+
                Contract.Tillers.COLUMN_COUNT+" INTEGER, "+
                "PRIMARY KEY (" + Contract.Tillers.COLUMN_YEAR+", "+ Contract.Tillers.COLUMN_FIELDS_ID+", "+ Contract.Tillers.COLUMN_REP+"));";
        sqLiteDatabase.execSQL(tillers);
        String production = "CREATE TABLE "+ Contract.Production.TABLE_NAME+" ( "+
                Contract.Production.COLUMN_ID+" INTEGER PRIMARY KEY, " +
                Contract.Production.COLUMN_YEAR+" INTEGER, "+
                Contract.Production.COLUMN_FIELDS_ID+" INTEGER, "+
                Contract.Production.COLUMN_AREA_HARVESTED+" DECIMAL (10,4), "+
                Contract.Production.COLUMN_TONS_CANE+" DECIMAL (10,4), "+
                Contract.Production.COLUMN_LKG+" DECIMAL (10,4), "+
                Contract.Production.COLUMN_DATE+" INTEGER);";
        sqLiteDatabase.execSQL(production);
        String problem = "CREATE TABLE " + Contract.Problem.TABLE_NAME +" ( "+
                Contract.Problem.COLUMN_ID+" INTEGER PRIMARY KEY, " +
                Contract.Problem.COLUMN_NAME+" VARCHAR(45), "+
                Contract.Problem.COLUMN_DESCRIPTION+" TEXT, "+
                Contract.Problem.COLUMN_TYPE+" VARCHAR(45), "+
                Contract.Problem.COLUMN_STATUS+" VARCHAR(45), "+
                Contract.Problem.COLUMN_FIELDS_ID+" INTEGER, "+
                Contract.Problem.COLUMN_DAMAGE+" DAMAGE(6,3), "+
                Contract.Problem.COLUMN_PROBLEMS_ID+" INTEGER, "+
                Contract.Problem.COLUMN_DATE+" INTEGER);";
        sqLiteDatabase.execSQL(problem);
        String recommendations = "CREATE TABLE " + Contract.Recommendations.TABLE_NAME +" ( "+
                Contract.Recommendations.COLUMN_ID+" INTEGER, " +
                Contract.Recommendations.COLUMN_NAME+" VARCHAR(45), "+
                Contract.Recommendations.COLUMN_DESCRIPTION+" TEXT, "+
                Contract.Recommendations.COLUMN_TYPE+" VARCHAR(45), "+
                Contract.Recommendations.COLUMN_STATUS+" VARCHAR(45), "+
                Contract.Recommendations.COLUMN_FIELDS_ID+" INTEGER, "+
                Contract.Recommendations.COLUMN_PHASE+" VARCHAR(45), "+
                Contract.Recommendations.COLUMN_IMPROVEMENT+" CHAR, "+
                Contract.Recommendations.COLUMN_DURATION+" INTEGER, "+
                Contract.Recommendations.COLUMN_DATE+" INTEGER, " +
        "PRIMARY KEY (" + Contract.Recommendations.COLUMN_FIELDS_ID+", "+ Contract.Recommendations.COLUMN_ID+"));";
        sqLiteDatabase.execSQL(recommendations);
        String notifications = "CREATE TABLE " + Contract.Notifications.TABLE_NAME +" ( "+
                Contract.Notifications.COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Contract.Notifications.COLUMN_TYPE+" VARCHAR(45), "+
                Contract.Notifications.COLUMN_MESSAGE+" TEXT, "+
                Contract.Notifications.COLUMN_PROBLEM_ID+" INTEGER, "+
                Contract.Notifications.COLUMN_POST_ID+" INTEGER, "+
                Contract.Notifications.COLUMN_RECOMMENDATION_ID+" INTEGER, "+
                Contract.Notifications.COLUMN_FIELD_ID+" INTEGER, "+
                Contract.Notifications.COLUMN_DATE+" INTEGER);";
        sqLiteDatabase.execSQL(notifications);
    }

    public void addFields(ArrayList<Field> fields){
        if (!fields.isEmpty()) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv;
            for (int i = 0; i < fields.size(); i++) {
                cv = new ContentValues();
                cv.put(Contract.Fields.COLUMN_ID, fields.get(i).getId());
                cv.put(Contract.Fields.COLUMN_BARANGAY, fields.get(i).getBarangay());
                cv.put(Contract.Fields.COLUMN_MUNICIPALITY, fields.get(i).getMunicipality());
                cv.put(Contract.Fields.COLUMN_AREA, fields.get(i).getArea());
                cv.put(Contract.Fields.COLUMN_BOUNDARY, fields.get(i).getCoordsString());
                cv.put(Contract.Fields.COLUMN_DAMAGE, fields.get(i).getDamage());
                db.insert(Contract.Fields.TABLE_NAME, null, cv);
            }
            db.close();
        }
    }

    public ArrayList<Field> getFields(){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Contract.Fields.TABLE_NAME, null, null, null, null, null, null);
        ArrayList<Field> fields = null;
        if (cursor.moveToFirst()){
            fields = new ArrayList<>();
            do{
                Field field = new Field();
                field.setId(cursor.getInt(cursor.getColumnIndex(Contract.Fields.COLUMN_ID)));
                field.setBarangay(cursor.getString(cursor.getColumnIndex(Contract.Fields.COLUMN_BARANGAY)));
                field.setMunicipality(cursor.getString(cursor.getColumnIndex(Contract.Fields.COLUMN_MUNICIPALITY)));
                field.setArea(cursor.getDouble(cursor.getColumnIndex(Contract.Fields.COLUMN_AREA)));
                field.setCoordsString(cursor.getString(cursor.getColumnIndex(Contract.Fields.COLUMN_BOUNDARY)));
                field.setDamage(cursor.getDouble(cursor.getColumnIndex(Contract.Fields.COLUMN_DAMAGE)));
                fields.add(field);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return fields;
    }

    public Field getFieldByID(int fieldID){
        Field field = null;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Contract.Fields.TABLE_NAME, null, Contract.Fields.COLUMN_ID +" = ? ", new String[]{fieldID+""}, null, null, null);

        if (cursor.moveToFirst()){
            field = new Field();
                field.setId(cursor.getInt(cursor.getColumnIndex(Contract.Fields.COLUMN_ID)));
                field.setBarangay(cursor.getString(cursor.getColumnIndex(Contract.Fields.COLUMN_BARANGAY)));
                field.setMunicipality(cursor.getString(cursor.getColumnIndex(Contract.Fields.COLUMN_MUNICIPALITY)));
                field.setArea(cursor.getDouble(cursor.getColumnIndex(Contract.Fields.COLUMN_AREA)));
                field.setCoordsString(cursor.getString(cursor.getColumnIndex(Contract.Fields.COLUMN_BOUNDARY)));
                field.setDamage(cursor.getDouble(cursor.getColumnIndex(Contract.Fields.COLUMN_DAMAGE)));
            }
        cursor.close();
        db.close();
        return field;
    }

    public void saveCVS(CVS cvs){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Contract.CropValidationSurveys.COLUMN_YEAR, cvs.getYear());
        cv.put(Contract.CropValidationSurveys.COLUMN_FIELDS_ID, cvs.getField());
        cv.put(Contract.CropValidationSurveys.COLUMN_VARIETY, cvs.getVariety());
        cv.put(Contract.CropValidationSurveys.COLUMN_CROP_CLASS, cvs.getCropClass());
        cv.put(Contract.CropValidationSurveys.COLUMN_TEXTURE, cvs.getTexture());
        cv.put(Contract.CropValidationSurveys.COLUMN_FARMING_SYSTEM, cvs.getFarmingSystem());
        cv.put(Contract.CropValidationSurveys.COLUMN_TOPOGRAPHY, cvs.getTopography());
        cv.put(Contract.CropValidationSurveys.COLUMN_FURROW_DISTANCE, cvs.getFurrowDistance());
        cv.put(Contract.CropValidationSurveys.COLUMN_PLANTING_DENSITY, cvs.getPlantingDensity());
        db.insertWithOnConflict(Contract.CropValidationSurveys.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void saveMillingCVS(CVS cvs){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Contract.Milling.COLUMN_YEAR, cvs.getYear());
        cv.put(Contract.Milling.COLUMN_FIELDS_ID, cvs.getField());
        cv.put(Contract.Milling.COLUMN_NUM_MILLABLE, cvs.getNumMillable());
        cv.put(Contract.Milling.COLUMN_AVG_MILLABLE_STOOL, cvs.getAvgMillableStool());
        cv.put(Contract.Milling.COLUMN_BRIX, cvs.getBrix());
        cv.put(Contract.Milling.COLUMN_STALK_LENGTH, cvs.getStalkLength());
        cv.put(Contract.Milling.COLUMN_DIAMETER, cvs.getDiameter());
        cv.put(Contract.Milling.COLUMN_WEIGHT, cvs.getWeight());
        db.insertWithOnConflict(Contract.Milling.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void saveCVSList(ArrayList<CVS> cvsList){
        if (cvsList != null){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            for (int i =0; i < cvsList.size(); i++){
                cv.put(Contract.CropValidationSurveys.COLUMN_YEAR, cvsList.get(i).getYear());
                cv.put(Contract.CropValidationSurveys.COLUMN_FIELDS_ID, cvsList.get(i).getField());
                cv.put(Contract.CropValidationSurveys.COLUMN_VARIETY, cvsList.get(i).getVariety());
                cv.put(Contract.CropValidationSurveys.COLUMN_CROP_CLASS, cvsList.get(i).getCropClass());
                cv.put(Contract.CropValidationSurveys.COLUMN_TEXTURE, cvsList.get(i).getTexture());
                cv.put(Contract.CropValidationSurveys.COLUMN_FARMING_SYSTEM, cvsList.get(i).getFarmingSystem());
                cv.put(Contract.CropValidationSurveys.COLUMN_TOPOGRAPHY, cvsList.get(i).getTopography());
                cv.put(Contract.CropValidationSurveys.COLUMN_FURROW_DISTANCE, cvsList.get(i).getFurrowDistance());
                cv.put(Contract.CropValidationSurveys.COLUMN_PLANTING_DENSITY, cvsList.get(i).getPlantingDensity());
                db.insertWithOnConflict(Contract.CropValidationSurveys.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
            cv = new ContentValues();
            for (int i =0; i < cvsList.size(); i++){
                cv.put(Contract.Milling.COLUMN_YEAR, cvsList.get(i).getYear());
                cv.put(Contract.Milling.COLUMN_FIELDS_ID, cvsList.get(i).getField());
                cv.put(Contract.Milling.COLUMN_NUM_MILLABLE, cvsList.get(i).getNumMillable());
                cv.put(Contract.Milling.COLUMN_AVG_MILLABLE_STOOL, cvsList.get(i).getAvgMillableStool());
                cv.put(Contract.Milling.COLUMN_BRIX, cvsList.get(i).getBrix());
                cv.put(Contract.Milling.COLUMN_STALK_LENGTH, cvsList.get(i).getStalkLength());
                cv.put(Contract.Milling.COLUMN_DIAMETER, cvsList.get(i).getDiameter());
                cv.put(Contract.Milling.COLUMN_WEIGHT, cvsList.get(i).getWeight());
                db.insertWithOnConflict(Contract.Milling.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.close();
        }
    }

    public CVS getMillingCVS(int year, int fieldID){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Contract.Milling.TABLE_NAME, null,
                Contract.Milling.COLUMN_YEAR+" = ? AND "+
                        Contract.Milling.COLUMN_FIELDS_ID+" = ?",
                new String[]{year+"", fieldID+""}, null, null, null);
        CVS milling = null;
        if (cursor.moveToFirst()){
            milling = new CVS();
            milling.setYear(year);
            milling.setField(fieldID);
            milling.setNumMillable(cursor.getInt(cursor.getColumnIndex(Contract.Milling.COLUMN_NUM_MILLABLE)));
            milling.setAvgMillableStool(cursor.getDouble(cursor.getColumnIndex(Contract.Milling.COLUMN_AVG_MILLABLE_STOOL)));
            milling.setBrix(cursor.getDouble(cursor.getColumnIndex(Contract.Milling.COLUMN_BRIX)));
            milling.setStalkLength(cursor.getDouble(cursor.getColumnIndex(Contract.Milling.COLUMN_STALK_LENGTH)));
            milling.setDiameter(cursor.getDouble(cursor.getColumnIndex(Contract.Milling.COLUMN_DIAMETER)));
            milling.setWeight(cursor.getDouble(cursor.getColumnIndex(Contract.Milling.COLUMN_WEIGHT)));
        }
            return milling;
    }

    public void saveFertilizer(Fertilizer fertilizer){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Contract.Fertilizers.COLUMN_YEAR, fertilizer.getYear());
        cv.put(Contract.Fertilizers.COLUMN_FIELDS_ID, fertilizer.getFieldID());
        cv.put(Contract.Fertilizers.COLUMN_FERTILZER, fertilizer.getFertilizer());
        cv.put(Contract.Fertilizers.COLUMN_FIRST_DOSE, fertilizer.getFirstDose());
        cv.put(Contract.Fertilizers.COLUMN_SECOND_DOSE, fertilizer.getSecondDose());
        db.insertWithOnConflict(Contract.Fertilizers.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void saveFertilizers(ArrayList<Fertilizer> fertilizers){
        if (fertilizers!=null){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            for (int i =0; i < fertilizers.size(); i++){
                cv.put(Contract.Fertilizers.COLUMN_YEAR, fertilizers.get(i).getYear());
                cv.put(Contract.Fertilizers.COLUMN_FIELDS_ID, fertilizers.get(i).getFieldID());
                cv.put(Contract.Fertilizers.COLUMN_FERTILZER, fertilizers.get(i).getFertilizer());
                cv.put(Contract.Fertilizers.COLUMN_FIRST_DOSE, fertilizers.get(i).getFirstDose());
                cv.put(Contract.Fertilizers.COLUMN_SECOND_DOSE, fertilizers.get(i).getSecondDose());
                db.insertWithOnConflict(Contract.Fertilizers.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.close();
        }
    }
    public Fertilizer getFertilizer(int year, int fieldID, String fertilizerKind){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Contract.Fertilizers.TABLE_NAME, null,
                Contract.Fertilizers.COLUMN_YEAR+" = ? AND "+
                        Contract.Fertilizers.COLUMN_FIELDS_ID+" = ? AND " + Contract.Fertilizers.COLUMN_FERTILZER+" = ? ",
                new String[]{year+"", fieldID+"", fertilizerKind}, null, null, null);
        Fertilizer fertilizer = null;
        if (cursor.moveToFirst()){
            fertilizer = new Fertilizer();
            fertilizer.setYear(cursor.getInt(cursor.getColumnIndex(Contract.Fertilizers.COLUMN_YEAR)));
            fertilizer.setFieldID(cursor.getInt(cursor.getColumnIndex(Contract.Fertilizers.COLUMN_FIELDS_ID)));
            fertilizer.setFertilizer(cursor.getString(cursor.getColumnIndex(Contract.Fertilizers.COLUMN_FERTILZER)));
            fertilizer.setFirstDose(cursor.getDouble(cursor.getColumnIndex(Contract.Fertilizers.COLUMN_FIRST_DOSE)));
            fertilizer.setSecondDose(cursor.getDouble(cursor.getColumnIndex(Contract.Fertilizers.COLUMN_SECOND_DOSE)));
        }
        cursor.close();
        db.close();
        return fertilizer;
    }
    public CVS getCVS(int year, int field){
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Contract.CropValidationSurveys.TABLE_NAME, null,
                Contract.CropValidationSurveys.COLUMN_YEAR+" = ? AND "+
                        Contract.CropValidationSurveys.COLUMN_FIELDS_ID+" = ? ", new String[]{year+"", field+""}, null, null, null);
        CVS cvs = null;
        if (cursor.moveToFirst()){
            cvs = new CVS();
            cvs.setYear(year);
            cvs.setField(field);
            cvs.setVariety(cursor.getString(cursor.getColumnIndex(Contract.CropValidationSurveys.COLUMN_VARIETY)));
            cvs.setCropClass(cursor.getString(cursor.getColumnIndex(Contract.CropValidationSurveys.COLUMN_CROP_CLASS)));
            cvs.setTexture(cursor.getString(cursor.getColumnIndex(Contract.CropValidationSurveys.COLUMN_TEXTURE)));
            cvs.setFarmingSystem(cursor.getString(cursor.getColumnIndex(Contract.CropValidationSurveys.COLUMN_FARMING_SYSTEM)));
            cvs.setTopography(cursor.getString(cursor.getColumnIndex(Contract.CropValidationSurveys.COLUMN_TOPOGRAPHY)));
            cvs.setFurrowDistance(cursor.getDouble(cursor.getColumnIndex(Contract.CropValidationSurveys.COLUMN_FURROW_DISTANCE)));
            cvs.setPlantingDensity(cursor.getDouble(cursor.getColumnIndex(Contract.CropValidationSurveys.COLUMN_PLANTING_DENSITY)));
        }
        db.close();
        cursor.close();
        return cvs;
    }

    public ArrayList<Tiller> getTillers(int year, int fieldID){
        ArrayList<Tiller> tillers = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Contract.Tillers.TABLE_NAME, null,
                Contract.Tillers.COLUMN_YEAR+" = ? AND "+
                        Contract.Tillers.COLUMN_FIELDS_ID+" = ? ", new String[]{year+"", fieldID+""}, null, null, null);
        if (cursor.moveToFirst()){
            tillers = new ArrayList<>();
            do {
                Tiller tiller = new Tiller();
                tiller.setYear(year);
                tiller.setFieldID(fieldID);
                tiller.setRep(cursor.getInt(cursor.getColumnIndex(Contract.Tillers.COLUMN_REP)));
                tiller.setCount(cursor.getInt(cursor.getColumnIndex(Contract.Tillers.COLUMN_COUNT)));
                tillers.add(tiller);
            } while(cursor.moveToNext());
        }
        return tillers;
    }

    public void addTiller(Tiller tiller){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        int rep;
        ArrayList<Tiller> tillers = getTillers(tiller.getYear(), tiller.getFieldID());
        if (tillers ==null){rep =1;}else { rep = tillers.size()+1;}
        cv.put(Contract.Tillers.COLUMN_YEAR, tiller.getYear());
        cv.put(Contract.Tillers.COLUMN_FIELDS_ID, tiller.getFieldID());
        cv.put(Contract.Tillers.COLUMN_REP, rep);
        cv.put(Contract.Tillers.COLUMN_COUNT, tiller.getCount());
        db.insert(Contract.Tillers.TABLE_NAME, null, cv);
        db.close();
    }
    public void addTillers(ArrayList<Tiller> tillers){
        if (tillers!=null){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            for (int i =0; i < tillers.size(); i++){
                cv.put(Contract.Tillers.COLUMN_YEAR, tillers.get(i).getYear());
                cv.put(Contract.Tillers.COLUMN_FIELDS_ID, tillers.get(i).getFieldID());
                cv.put(Contract.Tillers.COLUMN_REP, tillers.get(i).getRep());
                cv.put(Contract.Tillers.COLUMN_COUNT, tillers.get(i).getCount());
                db.insert(Contract.Tillers.TABLE_NAME, null, cv);
            }
            db.close();
        }
    }

    public void updateProduction(ArrayList<Production> production){
        if (production !=null) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            db.beginTransaction();
            try {
                if (production != null && production.size() > 0) {
                    for (int i = 0; i < production.size(); i++) {
                        cv.put(Contract.Production.COLUMN_ID, production.get(i).getId());
                        cv.put(Contract.Production.COLUMN_YEAR, production.get(i).getYear());
                        cv.put(Contract.Production.COLUMN_FIELDS_ID, production.get(i).getFieldsID());
                        cv.put(Contract.Production.COLUMN_AREA_HARVESTED, production.get(i).getAreaHarvested());
                        cv.put(Contract.Production.COLUMN_TONS_CANE, production.get(i).getTonsCane());
                        cv.put(Contract.Production.COLUMN_LKG, production.get(i).getLkg());
                        cv.put(Contract.Production.COLUMN_DATE, production.get(i).getDate().getTime());
                        db.insertWithOnConflict(Contract.Production.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
                    }
                }
            } catch (Exception e) {
            } finally {
                db.setTransactionSuccessful();
                db.endTransaction();
            }
            db.close();
        }
    }

    public double getTotalAreaHarvested(int year, int fieldID){
        double totalAreaHarvested = 0;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor= db.rawQuery("select sum("+ Contract.Production.COLUMN_AREA_HARVESTED+
                ") from " + Contract.Production.TABLE_NAME+
                " where " + Contract.Production.COLUMN_YEAR +" = ? and " + Contract.Production.COLUMN_FIELDS_ID+" = ?;",
                new String[]{year+"", fieldID+""});
        if (cursor.moveToFirst()) totalAreaHarvested = cursor.getDouble(0);
        cursor.close();
        db.close();
        return totalAreaHarvested;
    }

    public void addProblems(ArrayList<Problem> problems){
        if (problems!=null){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            for (int i =0; i< problems.size(); i++){
                cv.put(Contract.Problem.COLUMN_ID, problems.get(i).getId());
                cv.put(Contract.Problem.COLUMN_PROBLEMS_ID, problems.get(i).getProblemsID());
                cv.put(Contract.Problem.COLUMN_FIELDS_ID, problems.get(i).getFieldsID());
                cv.put(Contract.Problem.COLUMN_DATE, problems.get(i).getDate().getTime());
                cv.put(Contract.Problem.COLUMN_STATUS, problems.get(i).getStatus());
                cv.put(Contract.Problem.COLUMN_DAMAGE, problems.get(i).getDamage());
                cv.put(Contract.Problem.COLUMN_NAME, problems.get(i).getName());
                cv.put(Contract.Problem.COLUMN_DESCRIPTION, problems.get(i).getDescription());
                cv.put(Contract.Problem.COLUMN_TYPE, problems.get(i).getType());
                cv.put(Contract.Problem.COLUMN_STATUS, problems.get(i).getStatus());
                db.insertWithOnConflict(Contract.Problem.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.close();
        }

    }

    public ArrayList<Problem> getProblemsByFieldID(int fieldID){
        ArrayList<Problem> problems = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Contract.Problem.TABLE_NAME, null,
                        Contract.Problem.COLUMN_FIELDS_ID+" = ? ", new String[]{fieldID+""}, null, null,
                Contract.Problem.COLUMN_STATUS+ " , " + Contract.Problem.COLUMN_ID);
        if (cursor.moveToFirst()){
            problems = new ArrayList<>();
            do{
                Problem problem = new Problem();
                problem.setId(cursor.getInt(cursor.getColumnIndex(Contract.Problem.COLUMN_ID)));
                problem.setProblemsID(cursor.getInt(cursor.getColumnIndex(Contract.Problem.COLUMN_PROBLEMS_ID)));
                problem.setFieldsID(cursor.getInt(cursor.getColumnIndex(Contract.Problem.COLUMN_FIELDS_ID)));
                problem.setDate(new Date(cursor.getLong(cursor.getColumnIndex(Contract.Problem.COLUMN_DATE))));
                problem.setStatus(cursor.getString(cursor.getColumnIndex(Contract.Problem.COLUMN_STATUS)));
                problem.setDamage(cursor.getDouble(cursor.getColumnIndex(Contract.Problem.COLUMN_DAMAGE)));
                problem.setName(cursor.getString(cursor.getColumnIndex(Contract.Problem.COLUMN_NAME)));
                problem.setDescription(cursor.getString(cursor.getColumnIndex(Contract.Problem.COLUMN_DESCRIPTION)));
                problem.setType(cursor.getString(cursor.getColumnIndex(Contract.Problem.COLUMN_TYPE)));
                problems.add(problem);
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return problems;
    }

    public Problem getProblemByID(int id){
        Problem problem = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Contract.Problem.TABLE_NAME, null,
                Contract.Problem.COLUMN_ID+" = ? ", new String[]{id+""}, null, null,
                Contract.Problem.COLUMN_STATUS+ " , " + Contract.Problem.COLUMN_ID);
        if (cursor.moveToFirst()){
                problem = new Problem();
                problem.setId(cursor.getInt(cursor.getColumnIndex(Contract.Problem.COLUMN_ID)));
                problem.setProblemsID(cursor.getInt(cursor.getColumnIndex(Contract.Problem.COLUMN_PROBLEMS_ID)));
                problem.setFieldsID(cursor.getInt(cursor.getColumnIndex(Contract.Problem.COLUMN_FIELDS_ID)));
                problem.setDate(new Date(cursor.getLong(cursor.getColumnIndex(Contract.Problem.COLUMN_DATE))));
                problem.setStatus(cursor.getString(cursor.getColumnIndex(Contract.Problem.COLUMN_STATUS)));
                problem.setDamage(cursor.getDouble(cursor.getColumnIndex(Contract.Problem.COLUMN_DAMAGE)));
                problem.setName(cursor.getString(cursor.getColumnIndex(Contract.Problem.COLUMN_NAME)));
                problem.setDescription(cursor.getString(cursor.getColumnIndex(Contract.Problem.COLUMN_DESCRIPTION)));
                problem.setType(cursor.getString(cursor.getColumnIndex(Contract.Problem.COLUMN_TYPE)));
            }
        cursor.close();
        db.close();
        return problem;
    }

    public void deleteProblem(int problemID){
            SQLiteDatabase db = getWritableDatabase();
            db.delete(Contract.Problem.TABLE_NAME, Contract.Problem.COLUMN_ID +" = ? ", new String[]{problemID+""});
            db.close();
    }

    public void addRecommendations(ArrayList<Recommendation> recommendations){
        if (recommendations!=null){
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            for (int i =0; i < recommendations.size(); i++){
                cv.put(Contract.Recommendations.COLUMN_ID, recommendations.get(i).getId());
                cv.put(Contract.Recommendations.COLUMN_FIELDS_ID, recommendations.get(i).getFieldID());
                cv.put(Contract.Recommendations.COLUMN_DATE, recommendations.get(i).getDate().getTime());
                cv.put(Contract.Recommendations.COLUMN_STATUS, recommendations.get(i).getStatus());
                cv.put(Contract.Recommendations.COLUMN_NAME, recommendations.get(i).getRecommendation());
                cv.put(Contract.Recommendations.COLUMN_DESCRIPTION, recommendations.get(i).getDescription());
                cv.put(Contract.Recommendations.COLUMN_TYPE, recommendations.get(i).getType());
                cv.put(Contract.Recommendations.COLUMN_STATUS, recommendations.get(i).getStatus());
                cv.put(Contract.Recommendations.COLUMN_IMPROVEMENT, String.valueOf(recommendations.get(i).getImprovement()));
                cv.put(Contract.Recommendations.COLUMN_PHASE, recommendations.get(i).getPhase());
                cv.put(Contract.Recommendations.COLUMN_DURATION, recommendations.get(i).getDurationDays());
                db.insertWithOnConflict(Contract.Recommendations.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.close();
        }
    }

    public ArrayList<Recommendation> getRecommendationsByFieldID(int fieldID){
        ArrayList<Recommendation> recommendations = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Contract.Recommendations.TABLE_NAME, null,
                Contract.Recommendations.COLUMN_FIELDS_ID+" = ? ", new String[]{fieldID+""}, null, null,
                Contract.Recommendations.COLUMN_STATUS+ " , " + Contract.Recommendations.COLUMN_ID);
        if (cursor.moveToFirst()){
            recommendations = new ArrayList<>();
            do {
                Recommendation recommendation = new Recommendation();
                recommendation.setId(cursor.getInt(cursor.getColumnIndex(Contract.Recommendations.COLUMN_ID)));
                recommendation.setFieldID(cursor.getInt(cursor.getColumnIndex(Contract.Recommendations.COLUMN_FIELDS_ID)));
                recommendation.setStatus(cursor.getString(cursor.getColumnIndex(Contract.Recommendations.COLUMN_STATUS)));
                recommendation.setDate(new Date(cursor.getLong(cursor.getColumnIndex(Contract.Recommendations.COLUMN_DATE))));
                recommendation.setType(cursor.getString(cursor.getColumnIndex(Contract.Recommendations.COLUMN_TYPE)));
                recommendation.setDescription(cursor.getString(cursor.getColumnIndex(Contract.Recommendations.COLUMN_DESCRIPTION)));
                recommendation.setImprovement(cursor.getString(cursor.getColumnIndex(Contract.Recommendations.COLUMN_IMPROVEMENT)).charAt(0));
                recommendation.setPhase(cursor.getString(cursor.getColumnIndex(Contract.Recommendations.COLUMN_PHASE)));
                recommendation.setDurationDays(cursor.getInt(cursor.getColumnIndex(Contract.Recommendations.COLUMN_DURATION)));
                recommendation.setRecommendation(cursor.getString(cursor.getColumnIndex(Contract.Recommendations.COLUMN_NAME)));
                recommendations.add(recommendation);
            }while(cursor.moveToNext());
        }
        return recommendations;
    }

    public Recommendation getRecommendation(int fieldID, int recommendationID){
        Recommendation recommendation = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Contract.Recommendations.TABLE_NAME, null,
                Contract.Recommendations.COLUMN_FIELDS_ID+" = ? AND " + Contract.Recommendations.COLUMN_ID+" = ? ",
                new String[]{fieldID+"", recommendationID+""}, null, null,
                null);
        if (cursor.moveToFirst()){
                recommendation = new Recommendation();
                recommendation.setId(cursor.getInt(cursor.getColumnIndex(Contract.Recommendations.COLUMN_ID)));
                recommendation.setFieldID(cursor.getInt(cursor.getColumnIndex(Contract.Recommendations.COLUMN_FIELDS_ID)));
                recommendation.setStatus(cursor.getString(cursor.getColumnIndex(Contract.Recommendations.COLUMN_STATUS)));
                recommendation.setDate(new Date(cursor.getLong(cursor.getColumnIndex(Contract.Recommendations.COLUMN_DATE))));
                recommendation.setType(cursor.getString(cursor.getColumnIndex(Contract.Recommendations.COLUMN_TYPE)));
                recommendation.setDescription(cursor.getString(cursor.getColumnIndex(Contract.Recommendations.COLUMN_DESCRIPTION)));
                recommendation.setImprovement(cursor.getString(cursor.getColumnIndex(Contract.Recommendations.COLUMN_IMPROVEMENT)).charAt(0));
                recommendation.setPhase(cursor.getString(cursor.getColumnIndex(Contract.Recommendations.COLUMN_PHASE)));
                recommendation.setDurationDays(cursor.getInt(cursor.getColumnIndex(Contract.Recommendations.COLUMN_DURATION)));
                recommendation.setRecommendation(cursor.getString(cursor.getColumnIndex(Contract.Recommendations.COLUMN_NAME)));
        }
        return recommendation;
    }
    public void addNotifications(ArrayList<NotificationsLocal> notificationsLocals){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i=0; i < notificationsLocals.size(); i++){
            //cv.put(Contract.Notifications.COLUMN_ID, notificationsLocals.get(i).getId());
            cv.put(Contract.Notifications.COLUMN_TYPE, notificationsLocals.get(i).getType());
            cv.put(Contract.Notifications.COLUMN_DATE, notificationsLocals.get(i).getDate().getTime());
            cv.put(Contract.Notifications.COLUMN_MESSAGE, notificationsLocals.get(i).getMessage());
            cv.put(Contract.Notifications.COLUMN_POST_ID, notificationsLocals.get(i).getPostID());
            cv.put(Contract.Notifications.COLUMN_FIELD_ID, notificationsLocals.get(i).getFieldID());
            cv.put(Contract.Notifications.COLUMN_RECOMMENDATION_ID, notificationsLocals.get(i).getRecommendationID());
            cv.put(Contract.Notifications.COLUMN_PROBLEM_ID, notificationsLocals.get(i).getProblemID());
            db.insertWithOnConflict(Contract.Notifications.TABLE_NAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }
        db.close();
    }
    public ArrayList<NotificationsLocal> getNotifications(){
        ArrayList<NotificationsLocal> notifications = null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(Contract.Notifications.TABLE_NAME, null,null, null, null, null, null);
        if (cursor.moveToFirst()){
            notifications = new ArrayList<>();
            do {
                NotificationsLocal notification = new NotificationsLocal();
                notification.setId(cursor.getInt(cursor.getColumnIndex(Contract.Notifications.COLUMN_ID)));
                notification.setDate(new Date(cursor.getLong(cursor.getColumnIndex(Contract.Notifications.COLUMN_DATE))));
                notification.setType(cursor.getString(cursor.getColumnIndex(Contract.Notifications.COLUMN_TYPE)));
                notification.setMessage(cursor.getString(cursor.getColumnIndex(Contract.Notifications.COLUMN_MESSAGE)));
                notification.setPostID(cursor.getInt(cursor.getColumnIndex(Contract.Notifications.COLUMN_POST_ID)));
                notification.setRecommendationID(cursor.getInt(cursor.getColumnIndex(Contract.Notifications.COLUMN_RECOMMENDATION_ID)));
                notification.setProblemID(cursor.getInt(cursor.getColumnIndex(Contract.Notifications.COLUMN_PROBLEM_ID)));
                notification.setFieldID(cursor.getInt(cursor.getColumnIndex(Contract.Notifications.COLUMN_FIELD_ID)));
                notifications.add(notification);
            }while(cursor.moveToNext());
        }
        return notifications;
    }

    public void acceptProblem (Problem problem){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Contract.Problem.COLUMN_ID, problem.getId());
        cv.put(Contract.Problem.COLUMN_PROBLEMS_ID, problem.getProblemsID());
        cv.put(Contract.Problem.COLUMN_FIELDS_ID, problem.getFieldsID());
        cv.put(Contract.Problem.COLUMN_DATE, problem.getDate().getTime());
        cv.put(Contract.Problem.COLUMN_DAMAGE, problem.getDamage());
        cv.put(Contract.Problem.COLUMN_NAME, problem.getName());
        cv.put(Contract.Problem.COLUMN_DESCRIPTION, problem.getDescription());
        cv.put(Contract.Problem.COLUMN_TYPE, problem.getType());
        cv.put(Contract.Problem.COLUMN_STATUS, "Active");
        db.update(Contract.Problem.TABLE_NAME, cv, Contract.Problem.COLUMN_ID+" = ? ", new String[]{problem.getId()+""});
        db.close();
    }

    public void acceptRecommendation (Recommendation recommendation){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Contract.Recommendations.COLUMN_ID, recommendation.getId());
        cv.put(Contract.Recommendations.COLUMN_PHASE, recommendation.getPhase());
        cv.put(Contract.Recommendations.COLUMN_FIELDS_ID, recommendation.getFieldID());
        cv.put(Contract.Recommendations.COLUMN_DATE, recommendation.getDate().getTime());
        cv.put(Contract.Recommendations.COLUMN_DURATION, recommendation.getDurationDays());
        cv.put(Contract.Recommendations.COLUMN_NAME, recommendation.getRecommendation());
        cv.put(Contract.Recommendations.COLUMN_DESCRIPTION, recommendation.getDescription());
        cv.put(Contract.Recommendations.COLUMN_TYPE, recommendation.getType());
        cv.put(Contract.Recommendations.COLUMN_STATUS, "Active");
        db.update(Contract.Recommendations.TABLE_NAME, cv, Contract.Recommendations.COLUMN_ID+" = ? AND " +
                Contract.Recommendations.COLUMN_FIELDS_ID+ " = ? ", new String[]{recommendation.getId()+"", recommendation.getFieldID()+""});
        db.close();
    }

    public void updateField(Field field){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(Contract.Fields.COLUMN_ID, field.getId());
        cv.put(Contract.Fields.COLUMN_BARANGAY, field.getBarangay());
        cv.put(Contract.Fields.COLUMN_MUNICIPALITY, field.getMunicipality());
        cv.put(Contract.Fields.COLUMN_AREA, field.getArea());
        cv.put(Contract.Fields.COLUMN_BOUNDARY, field.getCoordsString());
        cv.put(Contract.Fields.COLUMN_DAMAGE, field.getDamage());
        db.update(Contract.Fields.TABLE_NAME, cv, Contract.Fields.COLUMN_ID + " = ? ", new String[]{field.getId()+""});
    }

    public void deleteRecommendation (Recommendation recommendation){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Contract.Recommendations.TABLE_NAME, Contract.Recommendations.COLUMN_ID +" = ? AND " +
                Contract.Recommendations.COLUMN_FIELDS_ID +" = ? ", new String[]{recommendation.getId()+"", recommendation.getFieldID()+""});
        db.close();
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void deleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        db.delete(Contract.Fields.TABLE_NAME, null,null);
        db.delete(Contract.CropValidationSurveys.TABLE_NAME, null,null);
        db.delete(Contract.Milling.TABLE_NAME, null,null);
        db.delete(Contract.Fertilizers.TABLE_NAME, null,null);
        db.delete(Contract.Tillers.TABLE_NAME, null,null);
        db.delete(Contract.Production.TABLE_NAME, null,null);
        db.delete(Contract.Problem.TABLE_NAME, null,null);
        db.delete(Contract.Recommendations.TABLE_NAME, null,null);
        db.delete(Contract.Notifications.TABLE_NAME, null,null);
        db.close();
    }


}
