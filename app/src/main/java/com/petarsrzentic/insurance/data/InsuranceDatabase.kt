package com.petarsrzentic.insurance.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase


@Database(entities = [Insurance::class], version = 1, exportSchema = false)
abstract class InsuranceDatabase : RoomDatabase() {

    abstract fun insuranceDao(): InsuranceDao

    companion object {

        @Volatile
        private var INSTANCE: InsuranceDatabase? = null

        fun getDatabase(context: Context): InsuranceDatabase {
            return INSTANCE
                ?: synchronized(this) {
                val instance = databaseBuilder(
                        context.applicationContext,
                        InsuranceDatabase::class.java,
                        "InsuranceDatabase.db"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
        fun destroyInstance() {
            if (INSTANCE!!.isOpen) INSTANCE!!.close()
            INSTANCE = null
        }
    }

}