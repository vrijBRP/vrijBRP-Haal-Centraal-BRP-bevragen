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

package nl.procura.brp.bevragen.client.examples;

import java.util.Arrays;
import java.util.Collections;

import nl.procura.brp.bevragen.client.GbaWsPersonListClient;
import nl.procura.brp.bevragen.client.GbaWsPersonListClientConfig;
import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.core.enums.GBARecStatus;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRequest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Example {

  public static void main(String[] args) {
    useWebclient();
  }

  private static void useWebclient() {

    GbaWsPersonListClientConfig config = GbaWsPersonListClientConfig.builder()
        .baseUrl("http://srv-411t:9081/personen-ws/rest")
        .username("test")
        .password("secret")
        .build();

    GbaWsPersonListClient client = GbaWsPersonListClient.builder()
        .config(config)
        .build();

    GbaWsPersonListRequest request = new GbaWsPersonListRequest()
        .setIds(Collections.singletonList(123459898L))
        .setCategories(Arrays.asList(GBACat.PERSOON.getCode(), GBACat.INSCHR.getCode(), GBACat.VB.getCode()))
        .setMaxFindCount(10);

    for (GbaWsPersonList pl : client.getPersonLists(request).getPersonlists()) {
      System.out.println("Response: " + pl
          .getCat(GBACat.PERSOON).orElseThrow(RuntimeException::new)
          .getFirstSet().orElseThrow(RuntimeException::new)
          .getByStatus(GBARecStatus.CURRENT).orElseThrow(RuntimeException::new)
          .getElem(GBAElem.ANR).orElseThrow(RuntimeException::new).getDescr());
    }
  }
}
