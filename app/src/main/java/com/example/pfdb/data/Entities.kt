package com.example.pfdb.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "family_members")
data class FamilyMember(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Serializable
@Entity(tableName = "assets")
data class Asset(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val familyMemberId: Int, // 0 for "Entire Family" or specific ID
    val type: String, // Bank Account, Stock, Real Estate, etc.
    val investedAmount: Double,
    val marketValue: Double,
    val institution: String, // Bank name or AMC
    val notes: String = ""
)

@Serializable
@Entity(tableName = "liabilities")
data class Liability(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val familyMemberId: Int,
    val amount: Double,
    val notes: String = ""
)
