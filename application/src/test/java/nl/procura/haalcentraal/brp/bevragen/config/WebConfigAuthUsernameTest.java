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

import static nl.procura.haalcentraal.brp.bevragen.config.WebConfigAuthUsernameTest.Initializer.enqueue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static spring.PersonRecordSource.takeRequest;

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
import spring.PersonRecordSource;

/**
 * Test if GbaWsPersonListClient bean uses credentials when username has been configured.
 */
@SpringBootTest
@ContextConfiguration(initializers = WebConfigAuthUsernameTest.Initializer.class)
@AutoConfigureMockMvc
class WebConfigAuthUsernameTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void authorizationMustUserBasicAuth() throws Exception {
    // given
    enqueue(new MockResponse().setResponseCode(200));
    String authorization = "authorize me";
    // when
    mockMvc.perform(get("/api/v1/ingeschrevenpersonen/123456789").header(HttpHeaders.AUTHORIZATION, authorization));
    // then
    String header = takeRequest().getHeader(HttpHeaders.AUTHORIZATION);
    assertEquals("Basic dGVzdDpwdw==", header);
  }

  static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    private final PersonRecordSource personRecordSource;

    public Initializer() {
      personRecordSource = new PersonRecordSource();
    }

    public static void enqueue(MockResponse response) {
      PersonRecordSource.enqueue(response);
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
      TestPropertyValues.of("procura.personrecordsource.username=test")
          .applyTo(applicationContext);
      TestPropertyValues.of("procura.personrecordsource.password=pw")
          .applyTo(applicationContext);
      personRecordSource.initialize(applicationContext);
    }
  }
}
