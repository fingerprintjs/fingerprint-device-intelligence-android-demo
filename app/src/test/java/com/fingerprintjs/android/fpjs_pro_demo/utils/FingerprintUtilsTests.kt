package com.fingerprintjs.android.fpjs_pro_demo.utils

import com.fingerprintjs.android.fpjs_pro.Configuration
import junit.framework.TestCase
import org.junit.Test

class FingerprintUtilsTests {
    @Test
    fun givenUSRegion_whenGetDescription_thenGlobalUSReturned() {
        val result = Configuration.Region.US.description
        TestCase.assertEquals("Global (US)", result)
    }

    @Test
    fun givenEURegion_whenGetDescription_thenEUReturned() {
        val result = Configuration.Region.EU.description
        TestCase.assertEquals("EU", result)
    }

    @Test
    fun givenAPRegion_whenGetDescription_thenAsiaMumbaiReturned() {
        val result = Configuration.Region.AP.description
        TestCase.assertEquals("Asia (Mumbai)", result)
    }

    @Test
    fun givenAllRegions_whenGetDescription_thenAllDescriptionsAreUnique() {
        val descriptions = setOf(
            Configuration.Region.US.description,
            Configuration.Region.EU.description,
            Configuration.Region.AP.description
        )
        TestCase.assertEquals(3, descriptions.size)
    }

    @Test
    fun givenAllRegions_whenGetDescription_thenNoDescriptionIsEmpty() {
        val regions = listOf(
            Configuration.Region.US,
            Configuration.Region.EU,
            Configuration.Region.AP
        )
        regions.forEach { region ->
            TestCase.assertTrue(region.description.isNotEmpty())
        }
    }
}
