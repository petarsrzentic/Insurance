package com.petarsrzentic.insurance.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Insurance(
    @PrimaryKey(autoGenerate = true)
    var uid: Int,
    var date: String,
    var msisdn: String,
    var category: String,
    var hitno: String,
    var priceOfInsurance: Int
)
