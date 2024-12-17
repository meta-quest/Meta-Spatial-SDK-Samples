// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.data.samples.repository

import com.meta.levinriegner.mediaview.data.di.IoDispatcher
import com.meta.levinriegner.mediaview.data.samples.model.SamplesList
import com.meta.levinriegner.mediaview.data.samples.service.DriveSamplesService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject

class SamplesRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val driveSamplesService: DriveSamplesService,
) {
    suspend fun getSamplesList(): SamplesList = withContext(dispatcher) {
        Timber.i("Getting samples list")
        driveSamplesService.getSamplesList()
    }

    fun downloadFile(
        fileId: String,
    ): Flow<InputStream> {
        Timber.i("Downloading file: $fileId")
        return driveSamplesService.downloadFile(fileId).flowOn(dispatcher)
    }
}