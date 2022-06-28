package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.PersonUtils.toOverlijden;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.core.enums.GBARecStatus;
import nl.procura.gbaws.web.rest.v2.personlists.CustomGbaWsPersonList;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.PersonSource;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.Util;

import lombok.SneakyThrows;

/**
 * {@link features/onvolledige_datum.feature}
 */
public class OnvolledigeDatumTest extends IngeschrevenPersonenResourceTest {

  /*
  Scenario: Volledige datum bekend
  Gegeven de registratie ingeschreven persoon 999999047 kent met geboortedatum 26 mei 1983 (19830526)
  Als de ingeschreven persoon met burgerservicenummer 999999047 wordt geraadpleegd
  Dan heeft attribuut geboorte.datum.datum de waarde 1983-05-26
  En heeft attribuut geboorte.datum.jaar de waarde 1983
  En heeft attribuut geboorte.datum.maand de waarde 05
  En heeft attribuut geboorte.datum.dag de waarde 26
  */
  @Test
  public void mustReturnCompleteDate() {
    var datum = Util.toDatumOnvolledig("20200501");
    assert datum != null;
    assertEquals(LocalDate.of(2020, 5, 1), datum.getDatum());
    assertEquals(2020, datum.getJaar());
    assertEquals(5, datum.getMaand());
    assertEquals(1, datum.getDag());
  }

  /*
  Scenario: Jaar en maand van de datum zijn bekend
  Gegeven de registratie ingeschreven persoon 999999291 kent met geboortedatum januari 1975 (19750100)
  Als de ingeschreven persoon met burgerservicenummer 999999291 wordt geraadpleegd
  Dan is in het antwoord geboorte.datum.datum niet aanwezig
  En heeft attribuut geboorte.datum.jaar de waarde 1975
  En heeft attribuut geboorte.datum.maand de waarde 01
  En is in het antwoord geboorte.datum.dag niet aanwezig
  */
  @Test
  public void mustReturnOnlyYearAndMonth() {
    var datum = Util.toDatumOnvolledig("20200500");
    assert datum != null;
    assertNull(datum.getDatum());
    assertEquals(2020, datum.getJaar());
    assertEquals(5, datum.getMaand());
    assertNull(datum.getDag());
  }

  /*
  Scenario: Alleen jaar van de datum is bekend
  Gegeven de registratie ingeschreven persoon 999999308 kent met geboortedatum 1975 (19750000)
  Als de ingeschreven persoon met burgerservicenummer 999999308 wordt geraadpleegd
  Dan is in het antwoord geboorte.datum.datum niet aanwezig
  En heeft attribuut geboorte.datum.jaar de waarde 1975
  En is in het antwoord geboorte.datum.maand niet aanwezig
  En is in het antwoord geboorte.datum.dag niet aanwezig
  */
  @Test
  public void mustReturnOnlyYear() {
    var datum = Util.toDatumOnvolledig("20200000");
    assert datum != null;
    assertNull(datum.getDatum());
    assertEquals(2020, datum.getJaar());
    assertNull(datum.getMaand());
    assertNull(datum.getDag());
  }

  /*
  Scenario: Er is geen geboortedatum bekend
  Gegeven de registratie ingeschreven persoon 999999321 kent geen geboortedatum (00000000)
  Als de ingeschreven persoon met burgerservicenummer 999999321 wordt geraadpleegd
  Dan is in het antwoord geboorte.datum niet aanwezig
  */
  @Test
  public void mustReturnNoBirthdate() {
    assertNull(Util.toDatumOnvolledig("00000000"));
    assertNull(Util.toDatumOnvolledig("0"));
    assertNull(Util.toDatumOnvolledig(""));
    var persoon = getIngeschrevenPersoon(999995066L);
    // Heel geboorte kan null zijn als plaats en land ook onbekend zijn
    assertTrue(persoon.getGeboorte() == null || persoon.getGeboorte().getDatum() == null);
  }

  /*
  Scenario: Er is geen overlijdensdatum bekend
  Gegeven de registratie ingeschreven persoon 999999321 heeft een volledig onbekende overlijdensdatum (00000000)
  Als de ingeschreven persoon met burgerservicenummer 999999321 wordt geraadpleegd
  Dan is in het antwoord overlijden.datum niet aanwezig
  En heeft attribuut overlijden.indicatieOverleden de waarde true
  */
  @Test
  @SneakyThrows
  public void mustReturnIndicationOfDeathWithUnknownDate() {
    var pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.OVERL)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.DATUM_OVERL, "0")
        .addElem(GBAElem.PLAATS_OVERL, "0")
        .addElem(GBAElem.LAND_OVERL, "0")
        .toCat()
        .build();

    assertTrue(toOverlijden(PersonSource.of(pl)).getIndicatieOverleden());
  }

  /*
  Gegeven de registratie ingeschreven persoon 999992077 kent een overlijdensdatum (20151001)
  Als de ingeschreven persoon met burgerservicenummer 999992077 wordt geraadpleegd
  Dan heeft in het antwoord overlijden.datum.datum de waarde 2015-10-01
  En heeft attribuut overlijden.indicatieOverleden de waarde true
  */
  @Test
  public void mustReturnDateAndIndicationOfDeath() {
    var pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.OVERL)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.DATUM_OVERL, "20151001")
        .addElem(GBAElem.PLAATS_OVERL, "0")
        .addElem(GBAElem.LAND_OVERL, "0")
        .toCat()
        .build();

    assertTrue(toOverlijden(PersonSource.of(pl)).getIndicatieOverleden());
  }

  /*
  Gegeven de registratie ingeschreven persoon 999993653 kent geen overlijdensdatum
  Als de ingeschreven persoon met burgerservicenummer 999993653 wordt geraadpleegd
  Dan is in het antwoord overlijden niet aanwezig
  */
  @Test
  public void mustReturnNothingWithSpecificPerson() {
    var pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.OVERL)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.DATUM_OVERL, "")
        .addElem(GBAElem.PLAATS_OVERL, "0")
        .addElem(GBAElem.LAND_OVERL, "0")
        .toCat()
        .build();

    assertNull(toOverlijden(PersonSource.of(pl)));
  }
}
