package ca.thotmail.fahcontrol.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val DATABASE = "connections"

@Database(entities = [ConnectionInfo::class], version = 1)
abstract class DB: RoomDatabase() {

    abstract fun ConnInfoDao(): ConnInfoDao

    //code below courtesy of https://github.com/googlesamples/android-sunflower; it     is open
    //source just like this application.
    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: DB? = null

        fun getInstance(context: Context): DB {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): DB {
            return Room.databaseBuilder(context, DB::class.java, DATABASE)
                .build()
        }
    }
}