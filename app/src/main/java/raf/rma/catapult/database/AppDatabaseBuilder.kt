package raf.rma.catapult.database

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppDatabaseBuilder @Inject constructor(
    @ApplicationContext private val context: Context,
){
    fun build(): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "rma.db",
        )
            .fallbackToDestructiveMigration()
//            .addMigrations(MIGRATION_1_2)
            .build()
    }

//    private val MIGRATION_1_2 = object : Migration(1, 2) {
//        override fun migrate(db: SupportSQLiteDatabase) {
//            db.execSQL("ALTER TABLE UserData ADD COLUMN createdAt INTEGER")
//        }
//    }
}