package com.marknjunge.ledger.utils

import com.marknjunge.ledger.data.local.AppPreferences
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert
import org.junit.Test

class AppUpdateTest {

    @MockK
    private val appPreferences = mockk<AppPreferences>()

    @Test
    fun `current 1, latest 1`() {
        every { appPreferences.currentVersion } returns 1
        every { appPreferences.latestVersion } returns 1
        every { appPreferences.skipUpdateVer } returns 1

        val shouldUpdate = AppUpdate.shouldUpdate(appPreferences, false)
        Assert.assertEquals(false, shouldUpdate)
    }

    @Test
    fun `current 1, latest 2, skip 1, ignoreSkip false`() {
        every { appPreferences.currentVersion } returns 1
        every { appPreferences.latestVersion } returns 2
        every { appPreferences.skipUpdateVer } returns 1

        val shouldUpdate = AppUpdate.shouldUpdate(appPreferences, false)
        Assert.assertEquals(true, shouldUpdate)
    }

    @Test
    fun `current 1, latest 2, skip 1, ignoreSkip true`() {
        every { appPreferences.currentVersion } returns 1
        every { appPreferences.latestVersion } returns 2
        every { appPreferences.skipUpdateVer } returns 1

        val shouldUpdate = AppUpdate.shouldUpdate(appPreferences, true)
        Assert.assertEquals(true, shouldUpdate)
    }

    @Test
    fun `current 1, latest 2, skip 2, ignoreSkip false`() {
        every { appPreferences.currentVersion } returns 1
        every { appPreferences.latestVersion } returns 2
        every { appPreferences.skipUpdateVer } returns 2

        val shouldUpdate = AppUpdate.shouldUpdate(appPreferences, false)
        Assert.assertEquals(false, shouldUpdate)
    }

    @Test
    fun `current 1, latest 2, skip 2, ignoreSkip true`() {
        every { appPreferences.currentVersion } returns 1
        every { appPreferences.latestVersion } returns 2
        every { appPreferences.skipUpdateVer } returns 2

        val shouldUpdate = AppUpdate.shouldUpdate(appPreferences, true)
        Assert.assertEquals(true, shouldUpdate)
    }
}