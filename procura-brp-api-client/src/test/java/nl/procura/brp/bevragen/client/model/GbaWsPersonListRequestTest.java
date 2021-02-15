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

package nl.procura.brp.bevragen.client.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRequest;

import lombok.SneakyThrows;

class GbaWsPersonListRequestTest {

  @Test
  @SneakyThrows
  public void canParseRequest() {

    ObjectMapper mapper = new ObjectMapper();
    ClassPathResource resource = new ClassPathResource("testrequest.json");
    String body = mapper.readTree(IOUtils.toString(resource.getURL(), StandardCharsets.UTF_8)).toString();

    GbaWsPersonListRequest request = mapper.readValue(body, GbaWsPersonListRequest.class);
    assertEquals(body, mapper.writeValueAsString(request));
  }
}
