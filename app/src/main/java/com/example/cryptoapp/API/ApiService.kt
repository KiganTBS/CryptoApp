package com.example.cryptoapp.API

import com.example.cryptoapp.POJO.CoinInfoListOfData
import com.example.cryptoapp.POJO.CoinPriceInfoRawData
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

// В данном интерфейся хранятся все методы по работе с сетью
interface ApiService {

    // Список самых популярных валют
    @GET("top/totalvolfull")
    fun getTopCoinInfo(
        @Query(QUERY_PARAM_API) apiKey: String = API_KEY,
        @Query(QUERY_PARAM_LIMIT) limit: Int = 10,
        @Query(QUERY_PARAM_TO_SYMBOL) tsym: String = CURRENCY
    ): Single<CoinInfoListOfData>

    @GET("pricemultifull")
    fun  getFullPriceList(
        @Query(QUERY_PARAM_API) apiKey: String = API_KEY,
        @Query(QUERY_PARAM_FROM_SYMBOLS) fsyms: String,
        @Query(QUERY_PARAM_TO_SYMBOLS) tsyms: String= CURRENCY
    ): Single<CoinPriceInfoRawData>


    // Константы для значений по умолчанию у функций.
    companion object{
        private const val QUERY_PARAM_API = "api_key"
        private const val QUERY_PARAM_LIMIT = "limit"
        private const val QUERY_PARAM_TO_SYMBOL = "tsym"

        private const val QUERY_PARAM_TO_SYMBOLS = "tsyms"
        private const val QUERY_PARAM_FROM_SYMBOLS ="fsyms"

        private const val CURRENCY = "USD"
        private const val API_KEY = "63e481f65df04428a1942497804f2c221d8253d0be358d61c83fab032986457e"
    }
}