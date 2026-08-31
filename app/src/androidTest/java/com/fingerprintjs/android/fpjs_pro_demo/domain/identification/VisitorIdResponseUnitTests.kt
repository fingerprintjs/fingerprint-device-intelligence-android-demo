package com.fingerprintjs.android.fpjs_pro_demo.domain.identification

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.get
import com.github.michaelbull.result.getError
import junit.framework.TestCase
import org.junit.Test

class VisitorIdResponseUnitTests {

    @Test
    fun carriesSuccessResultAndSecret() {
        val mockResponse = com.fingerprintjs.android.fpjs_pro_demo.utils.stateMocks.fingerprintJSResponse
        val response = VisitorIdResponse(result = Ok(mockResponse), secret = "d1f2e879")

        TestCase.assertEquals(mockResponse, response.result.get())
        TestCase.assertEquals("d1f2e879", response.secret)
    }

    @Test
    fun carriesErrorResultAndSecret() {
        val error = com.fingerprintjs.android.fpjs_pro_demo.utils.stateMocks.fingerprintJSOtherError
        val response = VisitorIdResponse(result = Err(error), secret = "00000007")

        TestCase.assertEquals(error, response.result.getError())
        TestCase.assertEquals("00000007", response.secret)
    }
}
