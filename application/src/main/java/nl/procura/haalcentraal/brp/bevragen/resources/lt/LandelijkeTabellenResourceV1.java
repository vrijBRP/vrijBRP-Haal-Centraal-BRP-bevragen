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

package nl.procura.haalcentraal.brp.bevragen.resources.lt;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.vng.realisatie.haalcentraal.rest.generated.api.lt.TabellenApi;
import nl.vng.realisatie.haalcentraal.rest.generated.model.lt.Tabel;
import nl.vng.realisatie.haalcentraal.rest.generated.model.lt.TabelCollectie;
import nl.vng.realisatie.haalcentraal.rest.generated.model.lt.Waarde;
import nl.vng.realisatie.haalcentraal.rest.generated.model.lt.WaardeCollectie;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Landelijke tabellen", description = "Opvragen landelijke tabellen")
@RequestMapping("/api/v1")
public class LandelijkeTabellenResourceV1 implements TabellenApi {

  @Override
  public ResponseEntity<Tabel> getTabel(String tabelidentificatie) {
    //TODO implement this method
    log.info("Get tabel");
    return null;
  }

  @Override
  public ResponseEntity<TabelCollectie> getTabellen(String tabelidentificatie, String omschrijving) {
    //TODO implement this method the correct way
    TabelCollectie collectie = new TabelCollectie();
    Tabel tabel = new Tabel();
    tabel.setTabelidentificatie("voorvoegsels");
    tabel.setOmschrijving("Voorvoegsels");
    collectie.addTabellenItem(tabel);
    return ResponseEntity.ok(collectie);
  }

  @Override
  public ResponseEntity<Waarde> getWaarde(String tabelidentificatie, String code) {
    //TODO implement this method
    log.info("Get waarde");
    return null;
  }

  @Override
  public ResponseEntity<WaardeCollectie> getWaarden(String tabelidentificatie, String code, String omschrijving,
      Boolean inclusiefbeeindigd) {
    //TODO implement this method
    log.info("Get waarden");
    return null;
  }
}
