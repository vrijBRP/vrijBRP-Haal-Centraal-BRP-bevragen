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

package nl.procura.haalcentraal.brp.bevragen.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListElem;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListVal;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.DatumOnvolledig;

class UtilTest {

  @Test
  void toLeeftijd() {
    LocalDate refDate = LocalDate.of(2020, Month.JANUARY, 1);
    LocalDate birthDate = LocalDate.of(1980, Month.NOVEMBER, 10);
    assertEquals(39, Util.toLeeftijd(Optional.of(birthDate), refDate).get());
  }

  @Test
  void toBsn() {
    GbaWsPersonListRec rec = new GbaWsPersonListRec();
    rec.getElems().add(new GbaWsPersonListElem()
        .setCode(GBAElem.BSN.getCode())
        .setValue(new GbaWsPersonListVal().setVal("11300474")));
    assertEquals("011300474", Util.toBsn(rec));
  }

  @Test
  void toDatumOnvolledig() {
    DatumOnvolledig normal = Util.toDatumOnvolledig("20091112");
    assert normal != null;
    assertEquals(2009, normal.getJaar());
    assertEquals(11, normal.getMaand());
    assertEquals(12, normal.getDag());
    assertEquals(LocalDate.of(2009, 11, 12), normal.getDatum());

    DatumOnvolledig noDayAndMonth = Util.toDatumOnvolledig("20090000");
    assert noDayAndMonth != null;
    assertEquals(2009, noDayAndMonth.getJaar());
    assertNull(noDayAndMonth.getMaand());
    assertNull(noDayAndMonth.getDag());
    assertNull(noDayAndMonth.getDatum());

    DatumOnvolledig noDay = Util.toDatumOnvolledig("20091100");
    assert noDay != null;
    assertEquals(2009, noDay.getJaar());
    assertEquals(11, noDay.getMaand());
    assertNull(noDay.getDag());
    assertNull(noDay.getDatum());
  }

  @Test
  void toLocalDate() {
    assertEquals(LocalDate.of(2009, 11, 12), Util.toLocalDate("20091112").get());
    assertEquals(Optional.empty(), Util.toLocalDate(""));
  }

  @Test
  void getInOnderzoek() {
    assertEquals(true, Util.getInOnderzoek(getAand("010000"), GBAElem.BSN));
    assertEquals(false, Util.getInOnderzoek(getAand("060000"), GBAElem.BSN));
    assertEquals(true, Util.getInOnderzoek(getAand("060800"), GBAElem.DATUM_OVERL));
    assertEquals(false, Util.getInOnderzoek(getAand("060100"), GBAElem.DATUM_OVERL));
    assertEquals(true, Util.getInOnderzoek(getAand("060810"), GBAElem.DATUM_OVERL));
    assertEquals(false, Util.getInOnderzoek(getAand("060820"), GBAElem.DATUM_OVERL));
  }

  private GbaWsPersonListRec getAand(String aand) {
    GbaWsPersonListRec rec = new GbaWsPersonListRec();
    rec.getElems().add(new GbaWsPersonListElem()
        .setCode(GBAElem.AAND_GEG_IN_ONDERZ.getCode())
        .setValue(new GbaWsPersonListVal().setVal(aand)));
    return rec;
  }

  @Test
  void toVoorletters() {
    assertEquals("K.J.P.", Util.toVoorletters("Klaas Jan Piet"));
    assertEquals("K.P.", Util.toVoorletters("Klaas-Jan Piet"));
    assertEquals("I.P.", Util.toVoorletters("IJsbrand Piet"));
  }
}
