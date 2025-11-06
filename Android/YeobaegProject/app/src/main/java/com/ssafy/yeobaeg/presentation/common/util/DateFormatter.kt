package com.ssafy.yeobaeg.presentation.common.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * 날짜 포맷팅 유틸리티
 */
object DateFormatter {
    
    private const val API_DATE_FORMAT = "yyyy-MM-dd"
    
    /**
     * Date를 API 형식(YYYY-MM-DD)으로 포맷팅
     */
    fun formatToApiFormat(date: Date): String {
        val formatter = SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault())
        return formatter.format(date)
    }
    
    /**
     * Calendar를 API 형식(YYYY-MM-DD)으로 포맷팅
     */
    fun formatToApiFormat(calendar: Calendar): String {
        return formatToApiFormat(calendar.time)
    }
    
    /**
     * 년, 월, 일을 API 형식(YYYY-MM-DD)으로 포맷팅
     */
    fun formatToApiFormat(year: Int, month: Int, dayOfMonth: Int): String {
        return String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, dayOfMonth)
    }
    
    /**
     * API 형식의 날짜 문자열을 Date로 파싱
     */
    fun parseFromApiFormat(dateString: String): Date? {
        return try {
            val formatter = SimpleDateFormat(API_DATE_FORMAT, Locale.getDefault())
            formatter.parse(dateString)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * API 형식의 날짜 문자열을 표시 형식으로 변환
     * 예: 1990-01-01 -> 1990년 1월 1일
     */
    fun toDisplayFormat(apiDateString: String): String? {
        return parseFromApiFormat(apiDateString)?.let { date ->
            val calendar = Calendar.getInstance().apply { time = date }
            "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH) + 1}월 ${calendar.get(Calendar.DAY_OF_MONTH)}일"
        }
    }
}

