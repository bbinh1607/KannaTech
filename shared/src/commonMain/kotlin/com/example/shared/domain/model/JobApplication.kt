package com.example.shared.domain.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class JobApplication(
    val id: Int = 0,
    val companyName: String, // Title
    val position: String,    // Description
    val status: JobStatus,   // Frequency (DAILY, WEEKLY, etc.)
    val applyDate: Instant,  // Start Date
    val notes: String? = null,
    val link: String? = null,    // Unit (e.g., "glasses", "km")
    val contact: String? = null, // Target Value (String for now, can be parsed to Int)
    val createdAt: Instant = kotlin.time.Clock.System.now()
)

enum class JobStatus(val label: String) {
    APPLIED("Đã ứng tuyển"),
    INTERVIEW("Phỏng vấn"),
    OFFER("Đã nhận offer"),
    REJECTED("Đã từ chối"),
}
