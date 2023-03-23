package com.adrianj.tugenius.BBDD;

public class Estructura_BBDD_Api {

    private Estructura_BBDD_Api(){

    }

    public static final String TABLE_NAME = "Api";
    public static final String NOMBRE_COLUMNA0 = "ID";
    public static final String NOMBRE_COLUMNA1 = "key";

    public static final String SQL_CREATE_ENTRIES_API =
            "CREATE TABLE " + Estructura_BBDD_Api.TABLE_NAME + " (" +
                    Estructura_BBDD_Api.NOMBRE_COLUMNA0 + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Estructura_BBDD_Api.NOMBRE_COLUMNA1 + " TEXT)";
}
