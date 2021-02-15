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

package nl.procura.haalcentraal.brp.bevragen.resources.bip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static spring.PersonRecordSource.enqueue;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.IngeschrevenPersoonHal;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.IngeschrevenPersoonHalCollectie;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import spring.PersonRecordSource;

@Slf4j
@SpringBootTest
@ContextConfiguration(initializers = PersonRecordSource.class)
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@AutoConfigureMockMvc
class IngeschrevenPersonenResourceV1Test {

  private static final String CONTEXT_PATH = "/haal-centraal-brp-bevragen";

  private MockMvc mockMvc;

  @BeforeEach
  public void setup(WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {

    mockMvc = MockMvcBuilders.webAppContextSetup(context)
        .apply(documentationConfiguration(restDocumentation)
            .snippets().withEncoding("UTF-8").and()
            .uris()
            .withScheme("https")
            .withHost("api.procura.nl")
            .withPort(443))
        .build();
  }

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @SneakyThrows
  void canGetIngeschrevenNatuurlijkPersoon1() {

    ClassPathResource resource = new ClassPathResource("person_ws_778075230.json");
    String personWsResponse = IOUtils.toString(resource.getURL(), StandardCharsets.UTF_8);

    enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .setBody(personWsResponse));

    String response = mockMvc.perform(get(CONTEXT_PATH + "/api/v1/ingeschrevenpersonen?geboorte__datum=1931-09-21")
        .contextPath(CONTEXT_PATH))
        .andDo(documentPrettyPrintReqResp("get_ingeschreven_persoon1"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    log.info(response);
    IngeschrevenPersoonHalCollectie persons = objectMapper.readValue(response, IngeschrevenPersoonHalCollectie.class);
    assertEquals("778075230", persons.getEmbedded().getIngeschrevenpersonen().get(0).getBurgerservicenummer());
    assertEquals("K.D.S.", persons.getEmbedded().getIngeschrevenpersonen().get(0).getNaam().getVoorletters());
  }

  @Test
  @SneakyThrows
  void canGetIngeschrevenNatuurlijkPersoonByBsn1() {

    ClassPathResource resource = new ClassPathResource("person_ws_778075230.json");
    String personWsResponse = IOUtils.toString(resource.getURL(), StandardCharsets.UTF_8);

    enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .setBody(personWsResponse));

    String response = mockMvc.perform(get(CONTEXT_PATH + "/api/v1/ingeschrevenpersonen/778266692")
        .contextPath(CONTEXT_PATH))
        .andDo(documentPrettyPrintReqResp("get_ingeschreven_persoon_by_bsn1"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    log.info(response);
    IngeschrevenPersoonHal persoon = objectMapper.readValue(response, IngeschrevenPersoonHal.class);
    assertEquals("778075230", persoon.getBurgerservicenummer());
    assertEquals("K.D.S.", persoon.getNaam().getVoorletters());
    assertNull(persoon.getEmbedded());
  }

  @Test
  @SneakyThrows
  void canGetIngeschrevenNatuurlijkPersoonByBsnWithExpand() {

    ClassPathResource resource = new ClassPathResource("person_ws_778075230.json");
    String personWsResponse = IOUtils.toString(resource.getURL(), StandardCharsets.UTF_8);

    enqueue(
            new MockResponse()
                    .setResponseCode(200)
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .setBody(personWsResponse));

    String response = mockMvc.perform(get(CONTEXT_PATH + "/api/v1/ingeschrevenpersonen/778266692?expand=kinderen,ouders,partners")
            .contextPath(CONTEXT_PATH))
            .andDo(documentPrettyPrintReqResp("get_ingeschreven_persoon_by_bsn_with_expand"))
            .andReturn()
            .getResponse()
            .getContentAsString();

    log.info(response);
    IngeschrevenPersoonHal persoon = objectMapper.readValue(response, IngeschrevenPersoonHal.class);
    assertEquals("778075230", persoon.getBurgerservicenummer());
    assertEquals("K.D.S.", persoon.getNaam().getVoorletters());
    assertEquals(3, persoon.getEmbedded().getKinderen().size());
    assertEquals(2, persoon.getEmbedded().getOuders().size());
    assertEquals(1, persoon.getEmbedded().getPartners().size());
  }

  /**
   * Pretty print request and response
   *
   * @param useCase the name of the snippet
   * @return RestDocumentationResultHandler
   */
  private RestDocumentationResultHandler documentPrettyPrintReqResp(String useCase) {
    return document(useCase,
        preprocessRequest(prettyPrint()),
        preprocessResponse(prettyPrint()));
  }
}
