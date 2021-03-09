package com.kontur.myapplication.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: String,
    val name: String,
    val height: Float,
    val biography: String,
    val phone: String,
    val temperament: Temperament,
    val period: Period
) : Parcelable
