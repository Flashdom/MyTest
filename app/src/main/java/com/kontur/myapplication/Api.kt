package com.kontur.myapplication

import com.kontur.myapplication.models.Contact
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import okio.Okio
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.File


interface Api {

    @GET("{file}")
    fun getContacts(@Path("file") file: String): Observable<ResponseBody>






    companion object {
        fun create(): Api {
            val url =
                "https://raw.githubusercontent.com/SkbkonturMobile/mobile-test-droid/master/json/"
            return Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create()

        }





    }

}

