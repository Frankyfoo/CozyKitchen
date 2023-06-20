package com.example.cozykitchen.helper

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.cozykitchen.model.TimeList
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TimeListGenerator {
    @RequiresApi(Build.VERSION_CODES.O)
    fun generateTimeList(): List<TimeList> {
        val availableDates = mutableListOf<LocalDateTime>()

        // Generate a list of available dates within the 3-day advance ordering range
        for (i in 0 until 3) {
            val date = LocalDate.now().plusDays(i.toLong())

            // Add lunch and dinner times for each available date
            val lunchTime = date.atTime(12, 0)
            val dinnerTime = date.atTime(18, 0)

            // Check if the current time is at least 2 hours before the lunch and dinner times
            val currentTime = LocalDateTime.now()
            if (currentTime.plusHours(2) <= lunchTime) {
                availableDates.add(lunchTime)
            }

            if (currentTime.plusHours(2) <= dinnerTime) {
                availableDates.add(dinnerTime)
            }
        }

        // Sort the available dates in ascending order
        availableDates.sort()

        // Convert the available dates to dropdown options
        val dropdownList = availableDates.map { date ->
            TimeList(
                value = date.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")),
                text = "${if (date.hour <= 12) "Lunch" else "Dinner"} (${date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"))})"
            )
        }

        return dropdownList
    }
}