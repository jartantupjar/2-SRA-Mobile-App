package com.example.micha.srafarmer;

import android.provider.BaseColumns;

public class Contract {
    public Contract(){}
    public static abstract class Fields implements BaseColumns{
        public static final String TABLE_NAME = "Fields";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_AREA = "area";
        public static final String COLUMN_BARANGAY = "barangay";
        public static final String COLUMN_MUNICIPALITY = "municipality";
        public static final String COLUMN_BOUNDARY = "boundary";
        public static final String COLUMN_DAMAGE = "damage";
    }

    public static abstract class CropValidationSurveys implements BaseColumns {
        public static final String TABLE_NAME = "CropValidationSurveys";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_FIELDS_ID = "Fields_id";
        public static final String COLUMN_VARIETY = "variety";
        public static final String COLUMN_CROP_CLASS = "crop_class";
        public static final String COLUMN_TEXTURE = "texture";
        public static final String COLUMN_FARMING_SYSTEM = "farming_system";
        public static final String COLUMN_TOPOGRAPHY = "topography";
        public static final String COLUMN_FURROW_DISTANCE = "furrow_distance";
        public static final String COLUMN_PLANTING_DENSITY = "planting_density";
    }

    public static abstract class Milling implements BaseColumns{
        public static final String TABLE_NAME = "Milling";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_FIELDS_ID = "Fields_id";
        public static final String COLUMN_NUM_MILLABLE = "num_millable";
        public static final String COLUMN_AVG_MILLABLE_STOOL = "avg_millable_stool";
        public static final String COLUMN_BRIX = "brix";
        public static final String COLUMN_STALK_LENGTH = "stalk_length";
        public static final String COLUMN_DIAMETER = "diameter";
        public static final String COLUMN_WEIGHT = "weight";

    }

    public static abstract class Fertilizers implements BaseColumns {

        public static final String TABLE_NAME = "Fertilizers";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_FIELDS_ID = "fields_id";
        public static final String COLUMN_FERTILZER = "fertilizer";
        public static final String COLUMN_FIRST_DOSE = "first_dose";
        public static final String COLUMN_SECOND_DOSE = "second_dose";
    }

    public static abstract class Tillers implements BaseColumns{
        public static final String TABLE_NAME = "Tillers";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_FIELDS_ID = "fields_id";
        public static final String COLUMN_REP = "rep";
        public static final String COLUMN_COUNT= "count";
    }

    public abstract class Production implements BaseColumns{
        public static final String TABLE_NAME = "Production";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_FIELDS_ID = "fields_id";
        public static final String COLUMN_AREA_HARVESTED = "area_harvested";
        public static final String COLUMN_TONS_CANE= "tons_cane";
        public static final String COLUMN_LKG= "lkg";
        public static final String COLUMN_DATE= "date";
    }

    public abstract class Problem implements BaseColumns{
        public static final String TABLE_NAME = "Problems";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_PROBLEMS_ID = "problems_id";
        public static final String COLUMN_FIELDS_ID = "fields_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_DAMAGE = "damage";
        public static final String COLUMN_PHASE = "phase";
    }

    public abstract class Recommendations implements BaseColumns{
        public static final String TABLE_NAME = "Recommendations";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "recommendation";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_FIELDS_ID = "fields_id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_IMPROVEMENT = "improvement";
        public static final String COLUMN_PHASE = "phase";
        public static final String COLUMN_DURATION = "duration";
    }

    public abstract class Notifications implements BaseColumns{
        public static final String TABLE_NAME = "Notifications";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_POST_ID = "post_id";
        public static final String COLUMN_PROBLEM_ID = "problem_id";
        public static final String COLUMN_RECOMMENDATION_ID= "recommendation_id";
        public static final String COLUMN_DATE= "date";
        public static final String COLUMN_FIELD_ID = "field_id";
    }
}
