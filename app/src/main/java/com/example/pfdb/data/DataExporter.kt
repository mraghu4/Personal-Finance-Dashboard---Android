package com.example.pfdb.data

import android.util.Base64
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ExportData(
    val familyMembers: List<FamilyMember>,
    val assets: List<Asset>,
    val liabilities: List<Liability>
)

object DataExporter {
    private val json = Json { ignoreUnknownKeys = true }

    fun exportToJson(members: List<FamilyMember>, assets: List<Asset>, liabilities: List<Liability>): String {
        val data = ExportData(members, assets, liabilities)
        val jsonString = json.encodeToString(data)
        // Encode to Base64 to make it a secure (non-plain) format
        return Base64.encodeToString(jsonString.toByteArray(), Base64.DEFAULT)
    }

    fun importFromJson(encodedString: String): ExportData {
        // Try to decode Base64, fallback to plain JSON for backward compatibility if needed
        return try {
            val decodedBytes = Base64.decode(encodedString, Base64.DEFAULT)
            val jsonString = String(decodedBytes)
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            // If decoding fails, attempt to parse as plain JSON
            json.decodeFromString(encodedString)
        }
    }
}
