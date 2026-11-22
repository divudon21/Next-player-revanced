package dev.anilbeesetti.nextplayer.feature.player.hdr

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ShizukuPermissionRequestStateTest {
    @Test
    fun savedGrantDoesNotRequestAgain() {
        val state = ShizukuPermissionRequestState()
        assertFalse(state.beginRequest(alreadyGranted = true))
        assertFalse(state.inFlight)
    }

    @Test
    fun concurrentStartupAndPlaybackRequestsShowOnePrompt() {
        val state = ShizukuPermissionRequestState()
        assertTrue(state.beginRequest(alreadyGranted = false))
        assertTrue(state.inFlight)
        assertFalse(state.beginRequest(alreadyGranted = false))
    }

    @Test
    fun denialDoesNotLoopWhenActivitiesAreRecreated() {
        val state = ShizukuPermissionRequestState()
        assertTrue(state.beginRequest(alreadyGranted = false))
        state.finishRequest()
        assertFalse(state.inFlight)
        assertFalse(state.beginRequest(alreadyGranted = false))
    }

    @Test
    fun restartedShizukuCanRequestAgainIfStillDenied() {
        val state = ShizukuPermissionRequestState()
        assertTrue(state.beginRequest(alreadyGranted = false))
        state.finishRequest()
        state.resetForNewBinder()
        assertTrue(state.beginRequest(alreadyGranted = false))
    }
}
