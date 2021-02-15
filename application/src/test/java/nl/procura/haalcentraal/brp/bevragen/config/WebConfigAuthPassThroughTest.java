/*
 * Copyright 2021 - 2022 Procura B.V.
 *
 * In licentie gegeven krachtens de EUPL, versie 1.2
 * U mag dit werk niet gebruiken, behalve onder de voorwaarden van de licentie.
 * U kunt een kopie van de licentie vinden op:
 *
 *   https://github.com/vrijBRP/vrijBRP/blob/master/LICENSE.md
 *
 * Deze bevat zowel de Nederlandse als de Engelse tekst
 *
 * Tenzij dit op grond van toepasselijk recht vereist is of schriftelijk
 * is overeengekomen, wordt software krachtens deze licentie verspreid
 * "zoals deze is", ZONDER ENIGE GARANTIES OF VOORWAARDEN, noch expliciet
 * noch impliciet.
 * Zie de licentie voor de specifieke bepalingen voor toestemmingen en
 * beperkingen op grond van de licentie.
 */

package nl.procura.haalcentraal.brp.bevragen.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

/**
 * Test if GbaWsPersonListClient bean passes through the Authorization header correctly.
 */
@SpringBootTest
@ContextConfiguration(initializers = WebConfigAuthPassThroughTest.Initializer.class)
@AutoConfigureMockMvc
class WebConfigAuthPassThroughTest {

  @ClassRule
  public static final MockWebServer personWsWebServer = new MockWebServer();

  @Autowired
  private MockMvc mockMvc;

  @Test
  void authorizationMustBePassedThrough() throws Exception {
    // given
    personWsWebServer.enqueue(new MockResponse().setResponseCode(200));
    String authorization = "authorize me";
    // when
    mockMvc.perform(get("/api/v1/ingeschrevenpersonen/123456789").header(HttpHeaders.AUTHORIZATION, authorization));
    // then
    String header = personWsWebServer.takeRequest().getHeader(HttpHeaders.AUTHORIZATION);
    assertEquals(authorization, header);
  }

  @Test
  void clientMustBeRequestScoped() throws Exception {
    // given 2 different authentications
    personWsWebServer.enqueue(new MockResponse().setResponseCode(200));
    mockMvc.perform(get("/api/v1/ingeschrevenpersonen/123456789").header(HttpHeaders.AUTHORIZATION, "authorize me"));
    personWsWebServer.takeRequest();
    personWsWebServer.enqueue(new MockResponse().setResponseCode(200));
    String authorization = "authorize again";
    // when
    mockMvc.perform(get("/api/v1/ingeschrevenpersonen/123456789").header(HttpHeaders.AUTHORIZATION, authorization));
    // then
    String header = personWsWebServer.takeRequest().getHeader(HttpHeaders.AUTHORIZATION);
    assertEquals(authorization, header);
  }

  static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      TestPropertyValues.of("procura.personrecordsource.url=" + personWsWebServer.url("/").toString())
          .applyTo(applicationContext);
      TestPropertyValues.of("procura.personrecordsource.username=")
          .applyTo(applicationContext);
      TestPropertyValues.of("procura.personrecordsource.password=")
          .applyTo(applicationContext);
    }
  }
}
