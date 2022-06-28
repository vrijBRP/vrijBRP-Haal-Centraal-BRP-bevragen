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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.math.NumberUtils.isDigits;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.procura.gbaws.web.rest.v2.tables.GbaWsRestTable;
import nl.procura.gbaws.web.rest.v2.tables.GbaWsRestTableRecord;
import nl.procura.gbaws.web.rest.v2.tables.GbaWsRestTablesRequest;
import nl.procura.haalcentraal.brp.bevragen.model.exception.NotFoundException;
import nl.procura.haalcentraal.brp.bevragen.service.TableWsService;
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
@RequestMapping("/api/v1.0")
public class LandelijkeTabellenResourceV1 implements TabellenApi {

  private TableWsService wsService;

  @Autowired
  public LandelijkeTabellenResourceV1(TableWsService wsService) {
    this.wsService = wsService;
  }

  @Override
  public ResponseEntity<Tabel> getTabel(String tabelIdentification) {
    var request = new GbaWsRestTablesRequest();
    addTableIds(tabelIdentification, request);
    return ResponseEntity.ok(toTabel(wsService.getTable(request)));
  }

  @Override
  public ResponseEntity<TabelCollectie> getTabellen(String tabelidentificatie, String omschrijving) {

    var request = new GbaWsRestTablesRequest();
    request.setDescription(omschrijving);
    addTableIds(tabelidentificatie, request);

    var collectie = new TabelCollectie();
    collectie.setTabellen(wsService.getTables(request).stream()
        .map(this::toTabel)
        .collect(Collectors.toList()));

    if (isNull(collectie.getTabellen())) {
      throw new NotFoundException();
    }

    return ResponseEntity.ok(collectie);
  }

  @Override
  public ResponseEntity<Waarde> getWaarde(String tabelidentificatie, String recordCode) {
    return ResponseEntity.ok(toWaarde(wsService.getWaarde(new GbaWsRestTablesRequest()
        .setCodes(List.of(Integer.valueOf(tabelidentificatie)))
        .setRecordCode(recordCode)
        .setShowHistory(true)
        .setShowRecords(true))));
  }

  @Override
  public ResponseEntity<WaardeCollectie> getWaarden(
      String tabelidentificatie,
      String recordCode,
      String recordOmschrijving,
      Boolean inclusiefbeeindigd) {

    Validate.notBlank(tabelidentificatie); // Required

    var request = new GbaWsRestTablesRequest();
    addTableIds(tabelidentificatie, request);

    request.setShowRecords(true);
    request.setShowHistory(isTrue(inclusiefbeeindigd));

    var waardeCollectie = new WaardeCollectie();
    if (nonNull(recordCode)) {
      request.setRecordCode(recordCode);
      wsService.getWaarden(request).forEach(w -> waardeCollectie.addWaardenItem(toWaarde(w)));
    } else {
      var gbaWsRestTableRecordList = wsService.getWaarden(request);
      if (nonNull(recordOmschrijving)) {
        for (var tableRecord : gbaWsRestTableRecordList) {
          if (tableRecord.getDescription().equalsIgnoreCase(recordOmschrijving)) {
            waardeCollectie.addWaardenItem(toWaarde(tableRecord));
          }
        }
      } else {
        for (var tableRecord : gbaWsRestTableRecordList) {
          waardeCollectie.addWaardenItem(toWaarde(tableRecord));
        }
      }
    }

    if (isNull(waardeCollectie.getWaarden())) {
      throw new NotFoundException();
    }
    return ResponseEntity.ok(waardeCollectie);
  }

  /*
  Procura WS only supports numeric table codes
  */
  private void addTableIds(String tabelidentificatie, GbaWsRestTablesRequest request) {
    if (nonNull(tabelidentificatie)) {
      if (isDigits(tabelidentificatie)) {
        request.setCodes(List.of(Integer.valueOf(tabelidentificatie)));
      } else {
        throw new NotFoundException();
      }
    }
  }

  private static LocalDate parseDate(int date) {
    var dateAsString = String.valueOf(date);
    return LocalDate.parse(dateAsString, DateTimeFormatter.ofPattern("yyyyMMdd"));
  }

  private Tabel toTabel(GbaWsRestTable t) {
    return new Tabel()
        .tabelidentificatie(String.valueOf(t.getCode()))
        .omschrijving(t.getDescription());
  }

  private static Waarde toWaarde(GbaWsRestTableRecord tableRecord) {
    var waarde = new Waarde();
    waarde.setCode(tableRecord.getCode());
    waarde.setOmschrijving(tableRecord.getDescription());
    if (tableRecord.getStartDate() != -1) {
      waarde.setDatumIngang(parseDate(tableRecord.getStartDate()));
    }
    if (tableRecord.getEndDate() != -1) {
      waarde.setDatumEinde(parseDate(tableRecord.getEndDate()));
    }
    return waarde;
  }
}
