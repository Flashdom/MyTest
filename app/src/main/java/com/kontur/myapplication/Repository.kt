package com.kontur.myapplication

import android.content.Context
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.kontur.myapplication.models.Contact
import com.kontur.myapplication.models.Period
import com.kontur.myapplication.models.ServerContact
import com.kontur.myapplication.models.Temperament
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


object Repository {


    private lateinit var contactsList: List<Contact>
    private val api = Api.create()
    private const val FIRST_SOURCE = "generated-01.json"
    private const val SECOND_SOURCE = "generated-02.json"
    private const val THIRD_SOURCE = "generated-03.json"

    fun getInfo(file: File, context: Context): Observable<List<Contact>> {
        val shouldMakeRequest = shouldMakeRequest(context)
        val gson = Gson()
        if (!shouldMakeRequest)
            return Observable.just(getContactsFromJson(file, gson))
                .subscribeOn(Schedulers.computation()).observeOn(Schedulers.computation())
        else {
            return Observable.merge(
                api.getContacts(FIRST_SOURCE),
                api.getContacts(SECOND_SOURCE),
                api.getContacts(THIRD_SOURCE)
            )
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation()).map {
                    file.outputStream().use { fileOutputStream ->
                        it.byteStream().use { fileInputStream ->
                            fileInputStream.copyTo(fileOutputStream)
                        }
                    }
                    return@map
                }.map { getContactsFromJson(file, gson) }
        }


    }


    private fun getContactsFromJson(file: File, gson: Gson): List<Contact> {
        val contacts: Array<ServerContact>
        val json = JsonParser.parseReader(
            InputStreamReader(
                FileInputStream(file.absoluteFile),
                "UTF-8"
            )
        )


        contacts = gson.fromJson(json, Array<ServerContact>::class.java)
        return transformModel(contacts.toList())
    }


    private fun shouldMakeRequest(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val timeString =
            sharedPreferences.getString("time", "")
        val currentTime = Instant.now().atZone(ZoneId.systemDefault()).toInstant()
        if (timeString == "") {
            sharedPreferences.edit().putString(
                "time",
                currentTime.atZone(ZoneId.systemDefault()).toLocalDateTime().toString()
            ).apply()
            return true
        }
        val time = LocalDateTime.parse(timeString).toInstant(ZoneOffset.UTC)
        if ((currentTime.toEpochMilli() - time.toEpochMilli())
            > Instant.ofEpochSecond(60).toEpochMilli()
        ) {
            sharedPreferences.edit().putString(
                "time",
                currentTime.atZone(ZoneId.systemDefault()).toLocalDateTime().toString()
            ).apply()
            return true
        }


        return false
    }

    private fun transformModel(serverContacts: List<ServerContact>): List<Contact> {
        val contacts = mutableListOf<Contact>()

        serverContacts.forEach {
            val temperament = when (it.temperament) {
                "sanguine" -> Temperament.Sanguine
                "choleric" -> Temperament.Choleric
                "melancholic" -> Temperament.Melancholic
                "phlegmatic" -> Temperament.Phlegmatic
                else -> Temperament.Unknown
            }

            val dtf = DateTimeFormatter.ISO_DATE_TIME


            val zonedStartDateTime =
                LocalDateTime.parse(it.educationPeriod.start, dtf)
            val zoneFinishDateTime =
                LocalDateTime.parse(it.educationPeriod.end, dtf)
            val startDate = "${zonedStartDateTime.dayOfMonth}." +
                    fixMonth(zonedStartDateTime.monthValue) +
                    ".${zonedStartDateTime.year}"

            val endDate = "${zoneFinishDateTime.dayOfMonth}." +
                    fixMonth(zoneFinishDateTime.monthValue) +
                    ".${zoneFinishDateTime.year}"
            val period = Period(endDate, startDate)

            contacts.add(
                Contact(
                    it.id,
                    it.name,
                    it.height,
                    it.biography,
                    it.phone,
                    temperament,
                    period
                )
            )
        }
        return contacts

    }

    private fun fixMonth(value: Int): String {
        return if (value > 9)
            value.toString()
        else "0$value"

    }


    fun saveTemporalQueryList(contacts: List<Contact>) {
        contactsList = contacts
    }


    fun filter(newText: String): List<Contact> {

        val list = mutableListOf<Contact>()
        for (e in contactsList) {
            if (e.name.contains(newText, ignoreCase = true) ||
                e.phone.contains(newText, ignoreCase = true)
            )
                list.add(e)
        }
        return list


    }


}