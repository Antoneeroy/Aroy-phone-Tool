package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DeviceProfile
import com.example.data.model.ScreenTimeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("VoltCare", appName)
  }

  @Test
  fun `verify screen time mode calculation`() {
    assertEquals(ScreenTimeMode.SAFE, ScreenTimeMode.fromHours(1.5f))
    assertEquals(ScreenTimeMode.NORMAL, ScreenTimeMode.fromHours(3.5f))
    assertEquals(ScreenTimeMode.DENGUE, ScreenTimeMode.fromHours(5.5f))
    assertEquals(ScreenTimeMode.DANGEROUS, ScreenTimeMode.fromHours(8.0f))
    assertEquals(ScreenTimeMode.END_EYES, ScreenTimeMode.fromHours(11.5f))
  }

  @Test
  fun `verify device profile resolution`() {
    val profile = DeviceProfile.resolveCurrentDevice()
    assertNotNull(profile)
    assertEquals(42.0f, profile.maxSafeTempC, 0.01f)
  }
}
