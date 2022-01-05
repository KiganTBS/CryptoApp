package com.example.cryptoapp.Database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cryptoapp.POJO.CoinPriceInfo

@Dao
interface CoinPriceInfoDao {

    //Получение всех валют
    @Query("SELECT * FROM full_price_list ORDER BY lastupdate DESC")
    fun getPriceList(): LiveData<List<CoinPriceInfo>>

    // Получение 1-ой валюты. LIMIT - проверка на баг (может быть только 1 валюта)
    @Query("SELECT * FROM full_price_list WHERE fromsymbol == :fsym LIMIT 1")
    fun getPriceInfoAboutCoin(fsym: String): LiveData<CoinPriceInfo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPriceList(priceList: List<CoinPriceInfo>)
}