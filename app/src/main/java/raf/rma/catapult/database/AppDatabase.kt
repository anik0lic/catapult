package raf.rma.catapult.database

import androidx.room.Database
import androidx.room.RoomDatabase
import raf.rma.catapult.cats.db.Cat
import raf.rma.catapult.cats.db.CatsDao
import raf.rma.catapult.photos.db.Photo
import raf.rma.catapult.photos.db.PhotoDao
import raf.rma.catapult.quiz.db.QuizResult
import raf.rma.catapult.quiz.db.QuizResultDao

@Database(
    entities = [
        Cat::class,
        Photo::class,
        QuizResult::class
    ],
    version = 6,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catsDao(): CatsDao
    abstract fun photoDao() : PhotoDao
    abstract fun quizResultDao() : QuizResultDao
}