package com.example.shared.domain.repository

import com.example.shared.domain.model.JobApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface JobRepository {
    fun getAllJobs(): Flow<List<JobApplication>>
    suspend fun getJobById(id: Int): JobApplication?
    suspend fun upsertJob(job: JobApplication)
    suspend fun updateJob(job: JobApplication)
    suspend fun deleteJob(job: JobApplication)
    fun searchJobs(query: String): Flow<List<JobApplication>>
    
    // Habit Completion methods
    fun getCompletedHabitIds(date: LocalDate): Flow<List<Int>>
    suspend fun toggleHabitCompletion(habitId: Int, date: LocalDate, completed: Boolean)
}
