package com.campuslife.solutions.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.campuslife.solutions.data.AssignmentRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: AssignmentRepository

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val assignments = repository.getAllAssignments().first()
                assignments.filter { !it.isCompleted }.forEach { assignment ->
                    alarmScheduler.schedule(assignment)
                }
            }
        }
    }
}
