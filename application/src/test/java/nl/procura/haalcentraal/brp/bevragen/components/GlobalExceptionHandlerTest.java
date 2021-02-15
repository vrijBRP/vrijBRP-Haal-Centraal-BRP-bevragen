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

package nl.procura.haalcentraal.brp.bevragen.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static spring.PersonRecordSource.enqueue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.Foutbericht;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.InvalidParams;

import okhttp3.mockwebserver.MockResponse;
import spring.PersonRecordSource;

@SpringBootTest
@ContextConfiguration(initializers = PersonRecordSource.class)
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void invalidParamMustReturnBadRequest() throws Exception {
    String response = mockMvc.perform(get("/api/v1/ingeschrevenpersonen/aa"))
        .andExpect(status().isBadRequest())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        .andReturn()
        .getResponse()
        .getContentAsString();
    Foutbericht error = objectMapper.readValue(response, Foutbericht.class);
    assertEquals("Een of meerdere parameters zijn niet correct", error.getTitle());
    InvalidParams param = error.getInvalidParams().get(0);
    assertEquals("burgerservicenummer", param.getName());
  }

  @Test
  void bsnWithoutResultMustReturnNotFound() throws Exception {
    enqueue(new MockResponse()
        .setResponseCode(200)
        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .setBody("{\n" +
            "  \"version\": \"\",\n" +
            "  \"errors\": [],\n" +
            "  \"personlists\": []\n" +
            "}"));
    String response = mockMvc.perform(get("/api/v1/ingeschrevenpersonen/123456789"))
        .andExpect(status().isNotFound())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        .andReturn()
        .getResponse()
        .getContentAsString();
    Foutbericht error = objectMapper.readValue(response, Foutbericht.class);
    assertEquals("Opgevraagde resource bestaat niet.", error.getTitle());
  }

  @Test
  void invalidEndPointMustReturnNotFoundFoutbericht() throws Exception {
    String response = mockMvc.perform(get("/api/v1/invalid"))
        .andExpect(status().isNotFound())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        .andReturn()
        .getResponse()
        .getContentAsString();
    Foutbericht error = objectMapper.readValue(response, Foutbericht.class);
    assertEquals("Opgevraagde resource bestaat niet.", error.getTitle());
  }

  @Test
  void invalidContentTypeMustReturnUnsupportedMediaTypeFoutbericht() throws Exception {
    String response = mockMvc.perform(get("/api/v1/ingeschrevenpersonen/123456789")
        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_PDF_VALUE))
        .andExpect(status().isUnsupportedMediaType())
        .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE))
        .andReturn()
        .getResponse()
        .getContentAsString();
    Foutbericht error = objectMapper.readValue(response, Foutbericht.class);
    assertEquals("Gevraagde contenttype wordt niet ondersteund.", error.getTitle());
  }
}
