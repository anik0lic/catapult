package raf.rma.catapult.database

import androidx.room.Database
import androidx.room.RoomDatabase
import raf.rma.catapult.photos.db.Photo
import raf.rma.catapult.photos.db.PhotosDao
import raf.rma.catapult.cats.db.Cat
import raf.rma.catapult.cats.db.CatsDao

@Database(
    entities = [
        Cat::class,
//        Album::class,
        Photo::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun catsDao(): CatsDao
    abstract fun photoDao() : PhotosDao
}