package com.example.cryptoapp.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.cryptoapp.POJO.CoinPriceInfo


@Database(entities = [CoinPriceInfo::class], version = 1, exportSchema = false)
abstract class CryptoDatabase : RoomDatabase() {
    abstract fun coinPriceInfoDao(): CoinPriceInfoDao
    companion object {

        private var db: CryptoDatabase? = null
        private const val DB_NAME = "crypton.db"
        private val LOCK = Any()

        fun getInstance(context: Context): CryptoDatabase {

            synchronized(LOCK) {

                db?.let { return it }

                val instance =
                    Room.databaseBuilder(
                        context,
                        CryptoDatabase::class.java,
                        DB_NAME
                    ).build()
                db = instance
                return instance
            }
        }
    }

}