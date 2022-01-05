package com.example.cryptoapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.cryptoapp.API.ApiFactory
import com.example.cryptoapp.Database.CryptoDatabase
import com.example.cryptoapp.POJO.CoinPriceInfo
import com.example.cryptoapp.POJO.CoinPriceInfoRawData
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CoinViewModel(application: Application) : AndroidViewModel(application) {
    val db = CryptoDatabase.getInstance(application)
    private val compositeDisposable = CompositeDisposable()

    val priceList = db.coinPriceInfoDao().getPriceList()

    // После создание viewModel, вызывается этот блок
    init {
        loadData()
    }

    fun getDetailInfo(fsym: String): LiveData<CoinPriceInfo> {
        return db.coinPriceInfoDao().getPriceInfoAboutCoin(fsym)
    }

    private fun loadData() {
        //1. Стартуем загрузку
        //2. map - преобразовали полученный List<Datum> в строку
        //3. flatmap - берёт полученную строку и передаём внуть блока ApiFactory.apiService.getFullPriceList(fsyms = it)
        // По итогу мы получаем CoinPriceInfoRawData

        // repeat - бесконечное повторение загрузки, пока не вызывется compositeDisposable
        // retry - выполнит загрузку, если всё прошло не успешно
        // delaySubscription - интервал вызова. В метод передаются 2-а поля {задержка, тип задержки(сек,мин,час...)}
        val disposable = ApiFactory.apiService.getTopCoinInfo(limit = 50)
            .map { it.data?.map { it.coinInfo?.name }?.joinToString(",") }
            .flatMap { ApiFactory.apiService.getFullPriceList(fsyms = it) }
            .map { getPriceListFromRawData(it) }
            .delaySubscription(10,TimeUnit.SECONDS)
            .repeat()
            .retry()
            .subscribeOn(Schedulers.io())
            .subscribe({
                db.coinPriceInfoDao().insertPriceList(it)
                Log.d("TEST_OF_LOADING_DATA", "Success $it")
            }, {
                Log.d("TEST_OF_LOADING_DATA", "Failure: ${it.message}")
            })
        compositeDisposable.add(disposable)
    }

    private fun getPriceListFromRawData(
        coinPriceListRawData: CoinPriceInfoRawData
    ): List<CoinPriceInfo> {
        val result = ArrayList<CoinPriceInfo>()
        val jsonObject = coinPriceListRawData.coinPriceInfoJsonObject ?: return result
        // Берём ключи из jsonobj(BTC,ETH...)
        val coinKeySet = jsonObject.keySet()
        // Проходимся по всем ключам и получаем вложенные json объекты
        for (coinKey in coinKeySet) {
            // Каждый из объектов содержит ключи (USD,RUB...)
            val currencyJson = jsonObject.getAsJsonObject(coinKey)
            val currencyKeySet = currencyJson.keySet()
            // Проходимся по всем ключам и получаем результат
            for (currencyKey in currencyKeySet) {
                val priceInfo = Gson().fromJson(
                    currencyJson.getAsJsonObject(currencyKey),
                    CoinPriceInfo::class.java
                )
                result.add(priceInfo)
            }
        }
        return result
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}
