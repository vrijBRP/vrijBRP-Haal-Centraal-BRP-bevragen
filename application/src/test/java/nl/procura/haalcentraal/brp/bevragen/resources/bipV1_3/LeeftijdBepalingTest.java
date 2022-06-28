package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.Util.toLeeftijd;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Locale;

import org.junit.jupiter.api.Test;

/*
  Functionaliteit: LeeftijdBepaling
*/
public class LeeftijdBepalingTest extends IngeschrevenPersonenResourceTest {

  /*
  Abstract Scenario: Volledig geboortedatum
  Gegeven een ingeschreven persoon met geboortedatum 26 mei 1983
  Als de ingeschreven persoon op <raadpleeg datum> wordt geraadpleegd
  Dan heeft attribuut leeftijd de waarde <leeftijd>
  
  Voorbeelden:
  | raadpleeg datum  | leeftijd |
  | 26 mei 2019      | 36       |
  | 30 november 2019 | 36       |
  | 1 januari 2019   | 35       |
  */
  @Test
  public void testMustReturnAgeWithCompleteBirthdate() {
    var formatter = ofPattern("d MM yyyy", Locale.getDefault());
    var date = "26 05 1983";
    var geboortedatum = ofNullable(parse(date, formatter));

    var testdatum = parse("26 05 2019", formatter);
    assertEquals(of(36), toLeeftijd(geboortedatum, testdatum));

    testdatum = parse("30 11 2019", formatter);
    assertEquals(of(36), toLeeftijd(geboortedatum, testdatum));

    testdatum = parse("1 01 2019", formatter);
    assertEquals(of(35), toLeeftijd(geboortedatum, testdatum));

    var persoon = getIngeschrevenPersoon(999990020);
    assertEquals("1931-07-06", persoon.getGeboorte().getDatum().getDatum().toString());
    final var testdag = parse("12 07 2021", formatter);
    var geboortedag = ofNullable(persoon.getGeboorte().getDatum().getDatum());
    var leeftijd = (geboortedag
        .flatMap(verjaarsdag -> toLeeftijd(geboortedag, testdag))
        .orElse(0));
    assertEquals(90, leeftijd);
  }

  /*
  Scenario: Volledig onbekend geboortedatum
  Gegeven een ingeschreven persoon kent geen geboortedatum
  Als de ingeschreven persoon wordt geraadpleegd
  Dan is attribuut leeftijd niet aanwezig
  */
  @Test
  public void testMustNotReturnAgeWithUnknownBirthdate() {
    var persoon = getIngeschrevenPersoon(999995066);
    assertNull(persoon.getGeboorte().getDatum());
  }

  /*
  Abstract Scenario: Jaar en maand van geboorte datum zijn bekend
  Gegeven een ingeschreven persoon met geboortedatum mei 1983
  Als de ingeschreven persoon op <raadpleeg datum> wordt geraadpleegd
  Dan heeft attribuut leeftijd de waarde <leeftijd>
  
  Voorbeelden: 
  | raadpleeg datum | leeftijd          | omschrijving                                                                           |
  | 31 mei 2019     | <niet aanwezig>   | In de geboorte maand weten we niet wanneer de persoon jarig is                         |
  | 01 juni 2019    | 36                | Na de geboorte maand weten we zeker dat de persoon 1 jaar ouder is geworden            |
  | 30 april 2019   | 35                | Voor de geboorte maand weten we zeker dat de persoon nog niet 1 jaar ouder is geworden |
  */
  @Test
  public void testMustReturnAgeWithOnlyYearAndMonth() {
    var geboortedatum = "05 1983";
    var formatterGeboortedatum = ofPattern("MM yyyy", Locale.getDefault());

    var ym = YearMonth.parse(geboortedatum, formatterGeboortedatum);
    var ld = ym.atDay(1);//Aanname: als alleen geboortemaand bekend is wordt er van de 1e van de maand uitgegaan.
    var formatter = ofPattern("d MM yyyy", Locale.getDefault());
    var testdatum = parse("1 05 2019", formatter);
    assertEquals(of(36), toLeeftijd(of(ld), testdatum));

    testdatum = parse("31 05 2019", formatter);
    assertEquals(of(36), toLeeftijd(of(ld), testdatum));

    testdatum = parse("1 06 2019", formatter);
    assertEquals(of(36), toLeeftijd(of(ld), testdatum));

    testdatum = parse("30 04 2019", formatter);
    assertEquals(of(35), toLeeftijd(of(ld), testdatum));
  }

  /*
  Scenario: Alleen jaar van geboorte datum is bekend
  Gegeven een ingeschreven persoon met geboortedatum 1983
  Als de ingeschreven persoon op <raadpleeg datum> wordt geraadpleegd
  Dan is attribuut leeftijd niet aanwezig
  */
  @Test
  public void testMustReturnAgeWithOnlyYear() {//Aanname: als alleen geboortejaar bekend is wordt er van 1 januari uitgegaan
    var geboortejaar = "1983";
    var formatterGeboortedatum = ofPattern("yyyy", Locale.getDefault());
    var jaar = Year.parse(geboortejaar, formatterGeboortedatum);

    var formatter = ofPattern("d MM yyyy", Locale.getDefault());
    var testdatum = parse("1 04 2019", formatter);
    LocalDate januari1 = jaar.atDay(1);
    assertEquals(of(36), toLeeftijd(of(januari1), testdatum));

  }

  /*
  Abstract Scenario: Geboren op 29 februari in een schrikkeljaar
  Gegeven een ingeschreven persoon met 29 februari 1996
  Als de ingeschreven persoon op <raadpleeg datum> wordt geraadpleegd
  Dan heeft attribuut leeftijd de waarde <leeftijd>
  
  Voorbeelden:
  | raadpleeg datum  | leeftijd |
  | 29 februari 2016 | 20       |
  | 28 februari 2017 | 20       |
  | 01 maart 2017    | 21       |
  */
  @Test
  public void testMustReturnAgeWithLeapYear() {
    var formatter = ofPattern("d MM yyyy", Locale.getDefault());
    var date = "29 02 1996";
    var geboortedatum = ofNullable(parse(date, formatter));

    var testdatum = parse("29 02 2016", formatter);
    assertEquals(of(20), toLeeftijd(geboortedatum, testdatum));

    testdatum = parse("28 02 2017", formatter);
    assertEquals(of(20), toLeeftijd(geboortedatum, testdatum));

    testdatum = parse("01 03 2017", formatter);
    assertEquals(of(21), toLeeftijd(geboortedatum, testdatum));
  }
}
