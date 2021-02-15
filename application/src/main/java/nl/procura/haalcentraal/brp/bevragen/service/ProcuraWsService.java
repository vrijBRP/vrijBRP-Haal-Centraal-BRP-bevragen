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

package nl.procura.haalcentraal.brp.bevragen.service;

import static nl.procura.burgerzaken.gba.core.enums.GBACat.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import nl.procura.brp.bevragen.client.GbaWsPersonListClient;
import nl.procura.burgerzaken.gba.core.enums.GBADatasource;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRequest;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListResponse;
import nl.procura.haalcentraal.brp.bevragen.model.BsnParam;
import nl.procura.haalcentraal.brp.bevragen.model.exception.NotFoundException;
import nl.procura.haalcentraal.brp.bevragen.model.exception.ReturnException;

@Service
public class ProcuraWsService {

  private final GbaWsPersonListClient client;

  public ProcuraWsService(GbaWsPersonListClient client) {
    this.client = client;
  }

  public GbaWsPersonList get(BsnParam burgerservicenummer) {

    GbaWsPersonListRequest request = new GbaWsPersonListRequest();
    request.setIds(Collections.singletonList(burgerservicenummer.toLong()));
    request.setMaxFindCount(2);

    List<GbaWsPersonList> personsLists = get(request);

    if (personsLists.isEmpty()) {
      throw new NotFoundException();
    }

    if (personsLists.size() > 1) {
      throw new ReturnException(400).setTitle("Meerdere personen gevonden");
    }

    return personsLists.get(0);
  }

  public List<GbaWsPersonList> get(GbaWsPersonListRequest request) {

    request.setDatasource(GBADatasource.STANDAARD.getCode());
    request.setShowSuspended(true);

    if (request.getCategories() == null) {
      request.setCategories(new ArrayList<>());
    }

    if (request.getMaxFindCount() == null) {
      request.setMaxFindCount(10);
    }

    request.getCategories().addAll(Arrays.asList(
        PERSOON.getCode(),
        OUDER_1.getCode(),
        OUDER_2.getCode(),
        NATIO.getCode(),
        HUW_GPS.getCode(),
        OVERL.getCode(),
        INSCHR.getCode(),
        VB.getCode(),
        KINDEREN.getCode(),
        VBTITEL.getCode(),
        GEZAG.getCode(),
        KIESR.getCode()));

    GbaWsPersonListResponse response = client.getPersonLists(request);

    if (response.getErrors() != null && !response.getErrors().isEmpty()) {
      throw new RuntimeException(response.getErrors().get(0));
    }

    List<GbaWsPersonList> personlists = response.getPersonlists();

    if (personlists.size() > 10) {
      throw new ReturnException(400).setTitle("Meer dan 10 personen gevonden. Specificeer de zoekopdracht verder.");
    }

    return personlists;
  }
}
