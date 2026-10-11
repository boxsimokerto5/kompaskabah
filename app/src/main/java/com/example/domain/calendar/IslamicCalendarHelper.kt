package com.example.domain.calendar

import java.util.Calendar

data class HijriDate(
    val year: Int,
    val month: Int,
    val day: Int
) {
    val monthNameId: String
        get() = IslamicCalendarHelper.getHijriMonthName(month)

    val monthNameShort: String
        get() = IslamicCalendarHelper.getHijriMonthShortName(month)

    val monthNameArabic: String
        get() = IslamicCalendarHelper.getHijriMonthArabic(month)

    val formattedString: String
        get() = "$day $monthNameId $year H"

    val formattedShort: String
        get() = "$day $monthNameShort"
}

data class IslamicCalendarDay(
    val gregorianYear: Int,
    val gregorianMonth: Int, // 1 - 12
    val gregorianDay: Int,
    val dayOfWeek: Int, // 1 = Ahad/Minggu, 2 = Senin, ..., 7 = Sabtu (Calendar.SUNDAY..SATURDAY)
    val hijriDate: HijriDate,
    val isToday: Boolean,
    val isCurrentMonth: Boolean,
    val fastingType: FastingType? = null,
    val islamicEvent: String? = null,
    val isHoliday: Boolean = false
) {
    val isSunday: Boolean get() = dayOfWeek == Calendar.SUNDAY
    val isFriday: Boolean get() = dayOfWeek == Calendar.FRIDAY
    val isSaturday: Boolean get() = dayOfWeek == Calendar.SATURDAY
    val isMonday: Boolean get() = dayOfWeek == Calendar.MONDAY
    val isThursday: Boolean get() = dayOfWeek == Calendar.THURSDAY
    val dateKey: String get() = String.format(java.util.Locale.US, "%04d-%02d-%02d", gregorianYear, gregorianMonth, gregorianDay)
}

enum class FastingType(val label: String, val badgeColorHex: Long) {
    AYYAMUL_BIDH("Ayyamul Bidh", 0xFF9333EA), // Purple accent like screenshot
    SENIN_KAMIS("Sunnah", 0xFF0D9488),       // Teal accent
    RAMADHAN("Wajib Ramadhan", 0xFFD97706),  // Amber
    ASYURA("Asyura & Tasu'a", 0xFF2563EB),   // Blue
    ARAFAH("Puasa Arafah", 0xFF059669),      // Emerald
    HARAM_PUASA("Haram Puasa", 0xFFDC2626)   // Red
}

object IslamicCalendarHelper {

    private val HIJRI_MONTH_NAMES_ID = listOf(
        "Muharram",
        "Safar",
        "Rabi'ul Awwal",
        "Rabi'ul Akhir",
        "Jumadil Awwal",
        "Jumadil Akhir",
        "Rajab",
        "Sya'ban",
        "Ramadhan",
        "Syawwal",
        "Dzulqa'dah",
        "Dzulhijjah"
    )

    private val HIJRI_MONTH_SHORT_ID = listOf(
        "Muh",
        "Saf",
        "R.Awwal",
        "R.Akhir",
        "J.Awwal",
        "J.Akhir",
        "Rajab",
        "Sya'ban",
        "Ram",
        "Syaw",
        "Dzulq",
        "Dzulh"
    )

    private val HIJRI_MONTH_ARABIC = listOf(
        "مُحَرَّم",
        "صَفَر",
        "رَبِيع الأَوَّل",
        "رَبِيع الآخِر",
        "جُمَادَى الأُولَى",
        "جُمَادَى الآخِرَة",
        "رَجَب",
        "شَعْبَان",
        "رَمَضَان",
        "شَوَّال",
        "ذُو القَعْدَة",
        "ذُو الحِجَّة"
    )

    val GREGORIAN_MONTH_NAMES_ID = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    val DAY_NAMES_ID = listOf("Ahad", "Senin", "Selasa", "Rabu", "Kamis", "Jum'at", "Sabtu")

    fun getHijriMonthName(month: Int): String {
        val idx = (month - 1).coerceIn(0, 11)
        return HIJRI_MONTH_NAMES_ID[idx]
    }

    fun getHijriMonthShortName(month: Int): String {
        val idx = (month - 1).coerceIn(0, 11)
        return HIJRI_MONTH_SHORT_ID[idx]
    }

    fun getHijriMonthArabic(month: Int): String {
        val idx = (month - 1).coerceIn(0, 11)
        return HIJRI_MONTH_ARABIC[idx]
    }

    /**
     * Converts a Gregorian date (year, month 1-12, day) to Hijri date
     * using the astronomical tabular Umm al-Qura algorithm.
     */
    fun gregorianToHijri(year: Int, month: Int, day: Int, dayAdjustment: Int = 0): HijriDate {
        var y = year
        var m = month
        if (m < 3) {
            y -= 1
            m += 12
        }
        val a = y / 100
        val b = 2 - a + a / 4
        val jd = (365.25 * (y + 4716)).toLong() +
                (30.6001 * (m + 1)).toLong() +
                day + b - 1524 + dayAdjustment

        val l = jd - 1948440 + 10632
        val n = ((l - 1) / 10631).toInt()
        val l2 = l - 10631 * n + 354
        val j = ((10985 - l2) / 5316).toInt() * ((50 * l2) / 17719).toInt() +
                (l2 / 5670).toInt() * ((43 * l2) / 15238).toInt()
        val l3 = l2 - ((30 - j) / 15).toInt() * ((17719 * j) / 50).toInt() -
                (j / 16).toInt() * ((15238 * j) / 43).toInt() + 29
        val mHijri = ((24 * l3) / 709).toInt()
        val dHijri = (l3 - ((709 * mHijri) / 24)).toInt()
        val yHijri = 30 * n + j - 30

        return HijriDate(
            year = yHijri,
            month = mHijri.coerceIn(1, 12),
            day = dHijri.coerceIn(1, 30)
        )
    }

    /**
     * Checks if a given date has an Islamic holiday or special event
     */
    fun getIslamicEvent(hijri: HijriDate): Pair<String, Boolean>? {
        return when (hijri.month) {
            1 -> when (hijri.day) {
                1 -> "Tahun Baru Islam 1 Muharram" to true
                9 -> "Puasa Tasu'a (9 Muharram)" to false
                10 -> "Hari Asyura (10 Muharram)" to true
                else -> null
            }
            3 -> when (hijri.day) {
                12 -> "Maulid Nabi Muhammad SAW" to true
                else -> null
            }
            7 -> when (hijri.day) {
                27 -> "Isra Mi'raj Nabi Muhammad SAW" to true
                else -> null
            }
            8 -> when (hijri.day) {
                15 -> "Malam Nisfu Sya'ban" to false
                else -> null
            }
            9 -> when (hijri.day) {
                1 -> "Awal Puasa Ramadhan" to true
                17 -> "Nuzulul Qur'an" to true
                in 21..29 -> if (hijri.day % 2 != 0) "Malam Lailatul Qadar" to false else null
                else -> null
            }
            10 -> when (hijri.day) {
                1 -> "Hari Raya Idul Fitri 1 Syawwal" to true
                2 -> "Hari Raya Idul Fitri (Hari Kedua)" to true
                else -> null
            }
            12 -> when (hijri.day) {
                8 -> "Hari Tarwiyah (8 Dzulhijjah)" to false
                9 -> "Hari Arafah (9 Dzulhijjah)" to true
                10 -> "Hari Raya Idul Adha 10 Dzulhijjah" to true
                11, 12, 13 -> "Hari Tasyrik (${hijri.day} Dzulhijjah)" to true
                else -> null
            }
            else -> null
        }
    }

    /**
     * Determines the Sunnah or Wajib fasting rule for a specific day
     */
    fun getFastingType(dayOfWeek: Int, hijri: HijriDate): FastingType? {
        // Forbidden fasting days
        if ((hijri.month == 10 && hijri.day == 1) || // Idul Fitri
            (hijri.month == 12 && hijri.day in 10..13) // Idul Adha & Hari Tasyrik
        ) {
            return FastingType.HARAM_PUASA
        }

        // Ramadhan: Wajib Fasting
        if (hijri.month == 9) {
            return FastingType.RAMADHAN
        }

        // Special Sunnah Fasts
        if (hijri.month == 1 && (hijri.day == 9 || hijri.day == 10)) {
            return FastingType.ASYURA
        }
        if (hijri.month == 12 && (hijri.day == 8 || hijri.day == 9)) {
            return FastingType.ARAFAH
        }

        // Ayyamul Bidh (13, 14, 15 Hijriyah)
        if (hijri.day in 13..15) {
            return FastingType.AYYAMUL_BIDH
        }

        // Weekly Sunnah Fasting (Senin & Kamis)
        if (dayOfWeek == Calendar.MONDAY || dayOfWeek == Calendar.THURSDAY) {
            return FastingType.SENIN_KAMIS
        }

        return null
    }

    /**
     * Generates a complete 7-column calendar grid for a given Gregorian year and month (1-12)
     */
    fun getMonthCalendarGrid(
        year: Int,
        month: Int,
        todayYear: Int,
        todayMonth: Int,
        todayDay: Int
    ): List<IslamicCalendarDay> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, 1)
        }

        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 2 = Monday, etc.

        val grid = mutableListOf<IslamicCalendarDay>()

        // 1. Previous month trailing days to align first Sunday column
        val prevMonthDaysCount = firstDayOfWeek - Calendar.SUNDAY
        if (prevMonthDaysCount > 0) {
            val prevCal = (cal.clone() as Calendar).apply {
                add(Calendar.MONTH, -1)
            }
            val maxPrevDays = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val prevYear = prevCal.get(Calendar.YEAR)
            val prevMonth = prevCal.get(Calendar.MONTH) + 1

            for (i in (prevMonthDaysCount - 1) downTo 0) {
                val dayNum = maxPrevDays - i
                val dayDow = Calendar.SUNDAY + (prevMonthDaysCount - 1 - i)
                val hijri = gregorianToHijri(prevYear, prevMonth, dayNum)
                val event = getIslamicEvent(hijri)
                val fasting = getFastingType(dayDow, hijri)

                grid.add(
                    IslamicCalendarDay(
                        gregorianYear = prevYear,
                        gregorianMonth = prevMonth,
                        gregorianDay = dayNum,
                        dayOfWeek = dayDow,
                        hijriDate = hijri,
                        isToday = false,
                        isCurrentMonth = false,
                        fastingType = fasting,
                        islamicEvent = event?.first,
                        isHoliday = event?.second ?: false
                    )
                )
            }
        }

        // 2. Current month days
        for (day in 1..daysInMonth) {
            cal.set(Calendar.DAY_OF_MONTH, day)
            val dayDow = cal.get(Calendar.DAY_OF_WEEK)
            val isToday = (year == todayYear && month == todayMonth && day == todayDay)
            val hijri = gregorianToHijri(year, month, day)
            val event = getIslamicEvent(hijri)
            val fasting = getFastingType(dayDow, hijri)

            grid.add(
                IslamicCalendarDay(
                    gregorianYear = year,
                    gregorianMonth = month,
                    gregorianDay = day,
                    dayOfWeek = dayDow,
                    hijriDate = hijri,
                    isToday = isToday,
                    isCurrentMonth = true,
                    fastingType = fasting,
                    islamicEvent = event?.first,
                    isHoliday = (event?.second ?: false) || (dayDow == Calendar.SUNDAY)
                )
            )
        }

        // 3. Next month leading days to complete the 7-column rows (up to 35 or 42 cells)
        val remaining = (7 - (grid.size % 7)) % 7
        if (remaining > 0) {
            val nextCal = (cal.clone() as Calendar).apply {
                add(Calendar.MONTH, 1)
            }
            val nextYear = nextCal.get(Calendar.YEAR)
            val nextMonth = nextCal.get(Calendar.MONTH) + 1

            for (day in 1..remaining) {
                nextCal.set(Calendar.DAY_OF_MONTH, day)
                val dayDow = nextCal.get(Calendar.DAY_OF_WEEK)
                val hijri = gregorianToHijri(nextYear, nextMonth, day)
                val event = getIslamicEvent(hijri)
                val fasting = getFastingType(dayDow, hijri)

                grid.add(
                    IslamicCalendarDay(
                        gregorianYear = nextYear,
                        gregorianMonth = nextMonth,
                        gregorianDay = day,
                        dayOfWeek = dayDow,
                        hijriDate = hijri,
                        isToday = false,
                        isCurrentMonth = false,
                        fastingType = fasting,
                        islamicEvent = event?.first,
                        isHoliday = event?.second ?: false
                    )
                )
            }
        }

        return grid
    }

    /**
     * Returns a descriptive string of the Hijri month range covered in this Gregorian month.
     * Example: "19 Rabi'ul Akhir - 20 Jumadil Awwal 1448 H"
     */
    fun getHijriMonthRangeString(daysInMonth: List<IslamicCalendarDay>): String {
        val currentMonthDays = daysInMonth.filter { it.isCurrentMonth }
        if (currentMonthDays.isEmpty()) return ""

        val firstDayHijri = currentMonthDays.first().hijriDate
        val lastDayHijri = currentMonthDays.last().hijriDate

        return if (firstDayHijri.month == lastDayHijri.month) {
            "${firstDayHijri.day} - ${lastDayHijri.day} ${firstDayHijri.monthNameId} ${firstDayHijri.year} H"
        } else {
            "${firstDayHijri.day} ${firstDayHijri.monthNameId} - ${lastDayHijri.day} ${lastDayHijri.monthNameId} ${lastDayHijri.year} H"
        }
    }
}
