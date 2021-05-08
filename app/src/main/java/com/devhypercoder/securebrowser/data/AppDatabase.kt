package com.devhypercoder.securebrowser.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [History::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        private var Instance: AppDatabase? = null

        fun getAppDatabase(context: Context): AppDatabase? {
            if (Instance == null) {
                synchronized(AppDatabase::class) {
                    Instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "database"
                    ).build()
                }
            }
            return Instance
        }
    }
}