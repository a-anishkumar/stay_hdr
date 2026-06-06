package com.example.hydraflowai.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class HydraSQLiteHelper(context: Context) : SQLiteOpenHelper(context, "hydra_database.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE intake_records (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                timestamp INTEGER NOT NULL,
                amount_ml INTEGER NOT NULL,
                beverage_name TEXT NOT NULL,
                hydration_score REAL NOT NULL,
                is_synced INTEGER NOT NULL DEFAULT 0
            )
        """)

        db.execSQL("""
            CREATE TABLE daily_goals (
                date TEXT PRIMARY KEY,
                goal_ml INTEGER NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE streaks (
                id INTEGER PRIMARY KEY,
                current_streak INTEGER NOT NULL DEFAULT 0,
                longest_streak INTEGER NOT NULL DEFAULT 0,
                last_drinking_date TEXT NOT NULL DEFAULT ''
            )
        """)

        db.execSQL("""
            CREATE TABLE challenges (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                duration_days INTEGER NOT NULL,
                target_daily_ml INTEGER NOT NULL,
                progress_days INTEGER NOT NULL DEFAULT 0,
                is_completed INTEGER NOT NULL DEFAULT 0
            )
        """)

        // Prepopulate default challenges
        db.execSQL("INSERT OR REPLACE INTO streaks (id, current_streak, longest_streak, last_drinking_date) VALUES (1, 0, 0, '')")
        db.execSQL("INSERT OR REPLACE INTO challenges (id, name, description, duration_days, target_daily_ml, progress_days, is_completed) VALUES ('7_day_habit', '7-Day Habit Starter', 'Reach your daily hydration goal 7 days in a row.', 7, 2000, 0, 0)")
        db.execSQL("INSERT OR REPLACE INTO challenges (id, name, description, duration_days, target_daily_ml, progress_days, is_completed) VALUES ('30_day_champion', '30-Day Hydration Hero', 'Stay perfectly hydrated for 30 consecutive days.', 30, 2500, 0, 0)")
        db.execSQL("INSERT OR REPLACE INTO challenges (id, name, description, duration_days, target_daily_ml, progress_days, is_completed) VALUES ('summer_heat_14', 'Summer Heat Challenge', 'Drink 3.0L daily for 14 days to beat the seasonal heat.', 14, 3000, 0, 0)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS intake_records")
        db.execSQL("DROP TABLE IF EXISTS daily_goals")
        db.execSQL("DROP TABLE IF EXISTS streaks")
        db.execSQL("DROP TABLE IF EXISTS challenges")
        onCreate(db)
    }
}
