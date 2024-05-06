package com.ertaqy.recorder.base.Workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class WorkManager(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        return Result.success()
    }

}