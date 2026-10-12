package com.example.data.network.hadith

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HadithSectionResponse(
    @Json(name = "metadata") val metadata: HadithMetadataResponse? = null,
    @Json(name = "hadiths") val hadiths: List<HadithRawItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class HadithMetadataResponse(
    @Json(name = "name") val name: String? = null,
    @Json(name = "section") val section: Map<String, String>? = null
)

@JsonClass(generateAdapter = true)
data class HadithRawItem(
    @Json(name = "hadithnumber") val hadithnumber: Int,
    @Json(name = "arabicnumber") val arabicnumber: Int? = null,
    @Json(name = "text") val text: String = ""
)

data class HadithBook(
    val id: String,
    val title: String,
    val author: String,
    val totalHadithEstimate: String,
    val gradeStatus: String,
    val badgeColorHex: Long,
    val description: String,
    val imageRes: Int = 0,
    val sections: List<HadithBookSection>
)

data class HadithBookSection(
    val sectionNumber: Int,
    val titleId: String,
    val titleEn: String,
    val hadithRange: String = "",
    val description: String = "",
    val keyTopics: List<String> = emptyList()
)

data class HadithParsedItem(
    val number: Int,
    val bookId: String,
    val bookName: String,
    val sectionNumber: Int,
    val sectionName: String,
    val arabicText: String,
    val indonesianText: String,
    val gradeTag: String
)
