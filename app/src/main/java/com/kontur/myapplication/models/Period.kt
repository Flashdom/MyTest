package com.kontur.myapplication.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Period(val startDate: String, val endDate: String) : Parcelable
