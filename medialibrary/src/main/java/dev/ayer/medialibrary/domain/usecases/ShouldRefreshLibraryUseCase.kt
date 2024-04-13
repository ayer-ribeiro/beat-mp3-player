package dev.ayer.medialibrary.domain.usecases

import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class ShouldRefreshLibraryUseCase(lastUpdateDate: Date) {
    private val currentDate = Date(System.currentTimeMillis())
    private val timeSinceLastUpdate = abs(lastUpdateDate.time - currentDate.time)
    val shouldRefresh = TimeUnit.HOURS.convert(timeSinceLastUpdate, TimeUnit.MILLISECONDS) > 2
}