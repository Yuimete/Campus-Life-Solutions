package com.campuslife.solutions.data

import com.campuslife.solutions.data.local.Assignment
import com.campuslife.solutions.data.local.AssignmentDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssignmentRepository @Inject constructor(
    private val assignmentDao: AssignmentDao
) {
    fun getAllAssignments(): Flow<List<Assignment>> = assignmentDao.getAllAssignments()

    suspend fun getAssignmentById(id: Int): Assignment? = assignmentDao.getAssignmentById(id)

    suspend fun insertAssignment(assignment: Assignment): Long = assignmentDao.insertAssignment(assignment)

    suspend fun updateAssignment(assignment: Assignment) = assignmentDao.updateAssignment(assignment)

    suspend fun deleteAssignment(assignment: Assignment) = assignmentDao.deleteAssignment(assignment)

    fun getOverdueAssignments(currentTime: Long): Flow<List<Assignment>> = 
        assignmentDao.getOverdueAssignments(currentTime)
}
