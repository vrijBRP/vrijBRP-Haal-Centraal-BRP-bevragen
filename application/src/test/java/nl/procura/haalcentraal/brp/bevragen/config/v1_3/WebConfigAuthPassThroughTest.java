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

package nl.procura.haalcentraal.brp.bevragen.config.v1_3;

import static nl.procura.gbaws.testdata.Testdata.DataSet.GBAV;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static spring.PersonRecordSource.enqueueResponse;
import static spring.PersonRecordSource.takeRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import nl.procura.gbaws.testdata.Testdata;

import okhttp3.mockwebserver.MockResponse;
import spring.PersonRecordSource;

/**
 * Test if GbaWsPersonListClient bean passes through the Authorization header correctly.
 */
@SpringBootTest
@ContextConfiguration(initializers = WebConfigAuthPassThroughTest.Initializer.class)
@AutoConfigureMockMvc
class WebConfigAuthPassThroughTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void authorizationMustBePassedThrough() throws Exception {
    // given
    enqueueResponse(new String(Testdata.getPersonDataAsBytes(999995935L, GBAV)));
    //personWsWebServer.enqueue(getMockResponse());
    String authorization = "authorize me";
    // when
    mockMvc.perform(get("/api/v1.3/ingeschrevenpersonen/999995935").header(HttpHeaders.AUTHORIZATION, authorization));
    // then
    String header = takeRequest().getHeader(HttpHeaders.AUTHORIZATION);
    assertEquals(authorization, header);
  }

  @Test
  void clientMustBeRequestScoped() throws Exception {
    // given 2 different authentications
    enqueueResponse(new String(Testdata.getPersonDataAsBytes(999995935L, GBAV)));
    mockMvc.perform(get("/api/v1.3/ingeschrevenpersonen/999995935").header(HttpHeaders.AUTHORIZATION, "authorize me"));
    takeRequest();
    enqueueResponse(new String(Testdata.getPersonDataAsBytes(999995935L, GBAV)));
    String authorization = "authorize again";
    // when
    mockMvc.perform(get("/api/v1.3/ingeschrevenpersonen/999995935").header(HttpHeaders.AUTHORIZATION, authorization));
    // then
    String header = takeRequest().getHeader(HttpHeaders.AUTHORIZATION);
    assertEquals(authorization, header);
  }

  private MockResponse getMockResponse() {
    return new MockResponse()
        .setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .setBody(new String(Testdata.getPersonDataAsBytes(999995935L, GBAV)));
  }

  static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final PersonRecordSource personRecordSource;

    public Initializer() {
      personRecordSource = new PersonRecordSource();
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      TestPropertyValues.of("procura.personrecordsource.username=").applyTo(applicationContext);
      TestPropertyValues.of("procura.personrecordsource.password=").applyTo(applicationContext);
      personRecordSource.initialize(applicationContext);
    }
  }
}
