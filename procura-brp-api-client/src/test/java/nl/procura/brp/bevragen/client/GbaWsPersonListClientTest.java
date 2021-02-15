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

package nl.procura.brp.bevragen.client;

import static nl.procura.burgerzaken.gba.core.enums.GBAElem.*;
import static nl.procura.burgerzaken.gba.core.enums.GBARecStatus.CURRENT;
import static nl.procura.burgerzaken.gba.core.enums.GBARecStatus.HIST;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBADatasource;
import nl.procura.gbaws.web.rest.v2.personlists.*;

import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

class GbaWsPersonListClientTest {

  @Test
  @SneakyThrows
  public void canParsePersonListResponse() {

    ClassPathResource resource = new ClassPathResource("testresponse.json");
    String body = IOUtils.toString(resource.getURL(), StandardCharsets.UTF_8);

    MockWebServer mockWebServer = new MockWebServer();
    mockWebServer.enqueue(
        new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .setBody(body));

    GbaWsPersonListClientConfig config = GbaWsPersonListClientConfig.builder()
        .webClient(WebClient.create(mockWebServer.url("/").toString()))
        .build();

    GbaWsPersonListClient client = GbaWsPersonListClient.builder()
        .config(config)
        .build();

    GbaWsPersonListRequest request = new GbaWsPersonListRequest()
        .setDatasource(GBADatasource.PROCURA.getCode())
        .setIds(Collections.singletonList(123459898L))
        .setCategories(Arrays.asList(GBACat.PERSOON.getCode(), GBACat.INSCHR.getCode(), GBACat.VB.getCode()))
        .setShowHistory(true)
        .setMaxFindCount(10);

    GbaWsPersonListResponse response = client.getPersonLists(request);
    assertEquals(1, response.getPersonlists().size());
    assertEquals(3, response.getPersonlists().get(0).getCats().size());

    Optional<GbaWsPersonListCat> personCat = response.getPersonlists().get(0).getCat(GBACat.PERSOON);
    assertEquals(1, get(personCat).getSets().size());

    Optional<GbaWsPersonListSet> personSet = get(personCat).getFirstSet();

    // Check current state
    GbaWsPersonListRec currentPersonRec = get(get(personSet).getByStatus(CURRENT));
    assertEquals(2, get(personSet).getRecords().size());
    assertEquals(CURRENT.getCode(), currentPersonRec.getStatus().getCode());
    assertEquals(26, currentPersonRec.getElems().size());
    assertEquals("Bergen (L)", get(currentPersonRec.getElem(GEBOORTEPLAATS)).getValue().getDescr());
    assertFalse(currentPersonRec.isIncorrect());
    assertEquals("1010163460", get(currentPersonRec.getElem(ANR)).getValue().getVal());
    assertEquals(".", get(currentPersonRec.getElem(BESCHRIJVING_DOC)).getValue().getDescr());

    // Check history
    GbaWsPersonListRec histPersonRec = get(get(personSet).getByStatus(HIST));
    assertEquals(HIST.getCode(), histPersonRec.getStatus().getCode());
    assertEquals("Adorp", get(histPersonRec.getElem(GEBOORTEPLAATS)).getValue().getDescr());
    assertFalse(histPersonRec.getElem(BESCHRIJVING_DOC).isPresent());
    assertTrue(histPersonRec.isIncorrect());
  }

  private <T> T get(Optional<T> v) {
    return v.orElseThrow(RuntimeException::new);
  }
}
