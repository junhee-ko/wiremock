package jko.wiremock

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.ResponseEntity

@WireMockTest
class WireMockTest {

  @Test
  fun `The static DSL will be automatically configured`(wmRuntimeInfo: WireMockRuntimeInfo) {
    // given
    stubFor(get("/static-dsl").willReturn(ok()))

    // when
    val testRestTemplate = TestRestTemplate()
    val response: ResponseEntity<String> =
      testRestTemplate.getForEntity("${wmRuntimeInfo.httpBaseUrl}/static-dsl", String::class.java)

    // then
    assertEquals(200, response.statusCodeValue)
  }

  @Test
  fun `Instance DSL can be obtained from the runtime info parameter`(wmRuntimeInfo: WireMockRuntimeInfo) {
    // given
    val wireMock: WireMock = wmRuntimeInfo.wireMock
    wireMock.register(get("/instance-dsl").willReturn(ok()))

    // when
    val testRestTemplate = TestRestTemplate()
    val response: ResponseEntity<String> =
      testRestTemplate.getForEntity("${wmRuntimeInfo.httpBaseUrl}/instance-dsl", String::class.java)

    // then
    assertEquals(200, response.statusCodeValue)
  }

  @Test
  fun `basic stub`(wmRuntimeInfo: WireMockRuntimeInfo) {
    // given
    stubFor(
      get(urlEqualTo("/some/thing"))
        .willReturn(
          aResponse()
            .withHeader("Content-Type", "text/plain")
            .withBody("Hello world!")
        )
    )

    // when
    val testRestTemplate = TestRestTemplate()
    val response1 = testRestTemplate.getForEntity("${wmRuntimeInfo.httpBaseUrl}/some/thing", String::class.java)
    val response2 = testRestTemplate.getForEntity("${wmRuntimeInfo.httpBaseUrl}/some/thing/else", String::class.java)

    // then
    assertEquals(200, response1.statusCodeValue)
    assertEquals("Hello world!", response1.body)
    assertTrue(response1.headers["Content-Type"]!!.contains("text/plain"))

    assertEquals(404, response2.statusCodeValue)
  }
}
