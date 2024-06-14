package raf.rma.catapult.database

import androidx.room.Database
import androidx.room.RoomDatabase
import raf.rma.catapult.photos.db.Album
import raf.rma.catapult.photos.db.Photo
import raf.rma.catapult.photos.db.PhotosDao
import raf.rma.catapult.users.db.UserData
import raf.rma.catapult.users.db.UsersDao

@Database(
    entities = [
        UserData::class,
        Album::class,
        Photo::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UsersDao
    abstract fun albumDao() : PhotosDao
}