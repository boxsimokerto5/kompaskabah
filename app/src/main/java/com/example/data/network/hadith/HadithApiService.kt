package com.example.data.network.hadith

import retrofit2.http.GET
import retrofit2.http.Path

interface HadithApiService {

    /**
     * Mengambil teks hadits bahasa Arab dari edisi kitab dan bab/bagian tertentu
     */
    @GET("editions/ara-{book}/sections/{section}.json")
    suspend fun getArabicSection(
        @Path("book") book: String,
        @Path("section") section: Int
    ): HadithSectionResponse

    /**
     * Mengambil terjemahan hadits bahasa Indonesia dari edisi kitab dan bab/bagian tertentu
     */
    @GET("editions/ind-{book}/sections/{section}.json")
    suspend fun getIndonesianSection(
        @Path("book") book: String,
        @Path("section") section: Int
    ): HadithSectionResponse
}
