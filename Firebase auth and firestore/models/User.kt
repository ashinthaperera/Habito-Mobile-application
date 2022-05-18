package com.sun.todo.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class User(
    val id: String = "",
    val firstname: String = "",
    val lastname: String = "",
    val email: String = "",
    val image: String = "",

): Parcelable