package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static nl.procura.burgerzaken.gba.core.enums.GBAElem.*;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.PersonUtils.toKiesrecht;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.core.enums.GBARecStatus;
import nl.procura.gbaws.web.rest.v2.personlists.CustomGbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.PersonSource;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.Kiesrecht;

/**
 * Functionaliteit: Kiesrecht
 * 	Het kiesrecht wordt afgeleid uit gegevens in de registratie.
 * 	Voor Europees kiesrecht kan in de registratie zijn vastgelegd of een persoon een oproep krijgt dan wel is uitgesloten.
 * 	Voor (niet-Europees) kiesrecht kan in de registratie zijn vastgelegd dat een persoon is uitgesloten.
 *
 * 	De actuele situatie wordt getoond. Als een uitsluiting in het verleden ligt, Dan wordt die uitsluiting niet
 * 	opgenomen in de API.
 *
 * 	Opname van (geheel of gedeeltelijk) onbekende datums gebeurt op dezelfde manier als andere (mogelijk onvolledige)
 * 	datums elders in de API.
 * 	Wanneer alleen het jaar van de einddatum uitsluiting bekend is, dan wordt de uitsluiting opgenomen tot en met dat jaar.
 * 	Wanneer het jaar en de maand van de einddatum uitsluiting bekend is, en de dag niet, dan wordt de uitsluiting
 * 	opgenomen tot en met die maand.
 * 	Een volledig onbekende datum wordt hetzelfde geïnterpreteerd en weergegeven als het niet aanwezig zijn van die datum.
 */
public class KiesrechtTest extends IngeschrevenPersonenResourceTest {

  /* Return no kiesrecht if category is empty or indication are empty
   */
  @Test
  public void mustReturnSuffrageAsNull() {

    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.KIESR)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(AANDUIDING_EURO_KIESR, "") // aanduiding europees kiesrecht
        .addElem(GBAElem.DATUM_VERZ_OF_MED_EURO_KIESR, "") // datum verzoek of mededeling europees kiesrecht
        .addElem(EINDDATUM_UITSL_EURO_KIESR, "") // einddatum uitsluiting europees kiesrecht
        .addElem(GBAElem.AAND_UITGESLOTEN_KIESR, "") // aanduiding uitgesloten kiesrecht
        .addElem(GBAElem.EINDDATUM_UITSLUIT_KIESR, "") // einddatum uitsluiting kiesrecht
        .toCat()
        .build();

    assertNull(toKiesrecht(PersonSource.of(pl)));

    // No category
    pl = CustomGbaWsPersonList.builder().build();
    assertNull(toKiesrecht(PersonSource.of(pl)));
  }

  /**
   * Scenario: de ingeschreven persoon is uitgesloten van Europees kiesrecht
   * Gegeven op de PL van een ingeschreven persoon is categorie 13, element 31.10 gelijk aan 1 (= persoon is uitgesloten)
   * En categorie 13, element 31.30 is niet aanwezig of heeft geen waarde
   * Als de ingeschreven persoon wordt geraadpleegd
   * Dan is europeesKiesrecht gelijk aan false
   * 	
   * Scenario: de ingeschreven persoon is niet uitgesloten van Europees kiesrecht
   * Gegeven op de PL van een ingeschreven persoon is categorie 13, element 31.10 gelijk aan 2 (= persoon ontvangt oproep)
   * Als de ingeschreven persoon wordt geraadpleegd
   * Dan is europeesKiesrecht gelijk aan true
   */
  @Test
  public void mustReturnEuropeanSuffrage() {

    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.KIESR)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(AANDUIDING_EURO_KIESR, "") // aanduiding europees kiesrecht
        .addElem(EINDDATUM_UITSL_EURO_KIESR, "") // aanduiding uitgesloten kiesrecht
        .toCat()
        .build();

    assertNull(toKiesrecht(PersonSource.of(pl)));

    // Future date
    pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.KIESR)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(AANDUIDING_EURO_KIESR, "1") // aanduiding europees kiesrecht
        .addElem(EINDDATUM_UITSL_EURO_KIESR, "20800101") // datum verzoek of mededeling europees kiesrecht
        .toCat()
        .build();

    Kiesrecht kiesrecht = toKiesrecht(PersonSource.of(pl));
    assertFalse(kiesrecht.getEuropeesKiesrecht());
    assertEquals("2080-01-01", kiesrecht.getEinddatumUitsluitingEuropeesKiesrecht().getDatum().toString());

    // No enddate
    pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.KIESR)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(AANDUIDING_EURO_KIESR, "1") // aanduiding europees kiesrecht
        .addElem(EINDDATUM_UITSL_EURO_KIESR, "") // datum verzoek of mededeling europees kiesrecht
        .toCat()
        .build();

    kiesrecht = toKiesrecht(PersonSource.of(pl));
    assertFalse(kiesrecht.getEuropeesKiesrecht());
    assertNull(kiesrecht.getEinddatumUitsluitingEuropeesKiesrecht());

    // Historic enddate
    pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.KIESR)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(AANDUIDING_EURO_KIESR, "1") // aanduiding europees kiesrecht
        .addElem(EINDDATUM_UITSL_EURO_KIESR, "20010101") // datum verzoek of mededeling europees kiesrecht
        .toCat()
        .build();

    kiesrecht = toKiesrecht(PersonSource.of(pl));
    assertNull(kiesrecht);

    // Not excluded, future
    pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.KIESR)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(AANDUIDING_EURO_KIESR, "2") // aanduiding europees kiesrecht
        .addElem(EINDDATUM_UITSL_EURO_KIESR, "20800101") // datum verzoek of mededeling europees kiesrecht
        .toCat()
        .build();

    kiesrecht = toKiesrecht(PersonSource.of(pl));
    assertTrue(kiesrecht.getEuropeesKiesrecht());
    assertEquals("2080-01-01", kiesrecht.getEinddatumUitsluitingEuropeesKiesrecht().getDatum().toString());

    // Not excluded, no end-date
    pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.KIESR)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(AANDUIDING_EURO_KIESR, "2") // aanduiding europees kiesrecht
        .addElem(EINDDATUM_UITSL_EURO_KIESR, "") // datum verzoek of mededeling europees kiesrecht
        .toCat()
        .build();

    kiesrecht = toKiesrecht(PersonSource.of(pl));
    assertTrue(kiesrecht.getEuropeesKiesrecht());
    assertNull(kiesrecht.getEinddatumUitsluitingEuropeesKiesrecht());
  }

  /* Scenario: bij de ingeschreven persoon is geen Europees kiesrecht vastgesteld
  * Gegeven op de PL van een ingeschreven persoon is categorie 13, element 31.10 is leeg
  * Als de ingeschreven persoon wordt geraadpleegd
  * Dan is europeesKiesrecht niet aanwezig
  *
  * Scenario: de ingeschreven persoon is uitgesloten van Europees kiesrecht en de einddatum uitsluiting Europees kiesrecht ligt in het verleden
  * Gegeven op de PL van een ingeschreven persoon is categorie 13, element 31.10 gelijk aan 1 (= persoon is uitgesloten)
  * En categorie 13, element 31.30 is een datum in het verleden
  * Dan is europeesKiesrecht niet aanwezig
  * En is einddatumUitsluitingEuropeesKiesrecht niet aanwezig
  *
  * Scenario: de ingeschreven persoon is uitgesloten van Europees kiesrecht en de einddatum uitsluiting Europees kiesrecht ligt in de toekomst
  * Gegeven op de PL van een ingeschreven persoon is categorie 13, element 31.10 gelijk aan 1 (= persoon is uitgesloten)
  * En categorie 13, element 31.30 is een datum in de toekomst
  * Dan is europeesKiesrecht gelijk aan false
  * En is einddatumUitsluitingEuropeesKiesrecht gelijk aan de waarde van element 31.10
  *
  * Abstract Scenario: de einddatum uitsluiting europees kiesrecht is geheel of deels onbekend
  * Gegeven op de PL van een ingeschreven persoon is categorie 13, element 31.10 gelijk aan 1 (= persoon is uitgesloten)
  * En op de PL van een ingeschreven persoon is categorie 13, element 31.30 gelijk aan <geheel of deels onbekende datum>
  * Als de ingeschreven persoon wordt geraadpleegd op 6 september 2019
  * Dan is europeesKiesrecht <europeesKiesrecht>
  * En is einddatumUitsluitingEuropeesKiesrecht <einddatumUitsluitingEuropeesKiesrecht>
  *
  * Voorbeelden:
  * | geheel of deels onbekende datum | europeesKiesrecht | einddatumUitsluitingEuropeesKiesrecht |
  * | 00000000                        | false             | niet aanwezig                         |
  * | 20190000                        | false             | jaar: 2019                            |
  * | 20180000                        | niet aanwezig     | niet aanwezig                         |
  * | 20191000                        | false             | jaar: 2019, maand: 10                 |
  * | 20190900                        | false             | jaar: 2019, maand: 09                 |
  * | 20190800                        | niet aanwezig     | niet aanwezig                         |
  *
  * Scenario: de ingeschreven persoon is uitgesloten van kiesrecht
  * Gegeven op de PL van een ingeschreven persoon is categorie 13, element 38.10 gelijk aan A (= persoon is uitgesloten)
  * En categorie 13, element 38.20 is niet aanwezig of heeft geen waarde
  * Als de ingeschreven persoon wordt geraadpleegd
  * Dan is uitgeslotenVanKiesrecht gelijk aan true
  *
  * Scenario: de ingeschreven persoon is niet uitgesloten van kiesrecht
  * Gegeven op de PL van een ingeschreven persoon is er geen categorie 13, element 38.10 (= persoon is niet uitgesloten)
  * Als de ingeschreven persoon wordt geraadpleegd
  * Dan is uitgeslotenVanKiesrecht niet aanwezig
  *
  * Scenario: de ingeschreven persoon was uitgesloten van kiesrecht en de einddatum uitsluiting kiesrecht ligt in het verleden
  * Gegeven op de PL van een ingeschreven persoon is categorie 13, element 38.10 gelijk aan A (= persoon is uitgesloten)
  * En categorie 13, element 38.20 is een datum in het verleden
  * Als de ingeschreven persoon wordt geraadpleegd
  * Dan is uitgeslotenVanKiesrecht niet aanwezig
  * En is einddatumUitsluitingKiesrecht niet aanwezig
  *
  * Scenario: de ingeschreven persoon is uitgesloten van kiesrecht en de einddatum uitsluiting kiesrecht ligt in de toekomst
  * Gegeven op de PL van een ingeschreven persoon is categorie 13, element 38.10 gelijk aan A (= persoon is uitgesloten)
  * En categorie 13, element 38.20 is een datum in de toekomst
  * Als de ingeschreven persoon wordt geraadpleegd
  * Dan is uitgeslotenVanKiesrecht gelijk aan true
  * En is einddatumUitsluitingKiesrecht gelijk aan de waarde van element 38.20
  *
  * Abstract Scenario: de einddatum uitsluiting kiesrecht is geheel of deels onbekend
  * Gegeven op de PL van een ingeschreven persoon is categorie 13, element 38.10 gelijk aan A (= persoon is uitgesloten)
  * En op de PL van een ingeschreven persoon is categorie 13, element 38.20 gelijk aan <geheel of deels onbekende datum>
  * Als de ingeschreven persoon wordt geraadpleegd op 6 september 2019
  * Dan is uitgeslotenVanKiesrecht <europeesKiesrecht>
  * En is einddatumUitsluitingEuropeesKiesrecht <einddatumUitsluitingEuropeesKiesrecht>
  *
  *	Voorbeelden:
  *	| geheel of deels onbekende datum | uitgeslotenVanKiesrecht | einddatumUitsluitingEuropeesKiesrecht |
  *	| 00000000                        | true                    | niet aanwezig                         |
  *	| 20190000                        | true                    | jaar: 2019                            |
  *	| 20180000                        | niet aanwezig           | niet aanwezig                         |
  *	| 20191000                        | true                    | jaar: 2019, maand: 10                 |
  *	| 20190900                        | true                    | jaar: 2019, maand: 09                 |
  *	| 20190800                        | niet aanwezig           | niet aanwezig
  */
  @Test
  public void mustReturnDutchSuffrage() {

    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.KIESR)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(AAND_UITGESLOTEN_KIESR, "")
        .addElem(EINDDATUM_UITSLUIT_KIESR, "")
        .toCat()
        .build();

    assertNull(toKiesrecht(PersonSource.of(pl)));

    // Future date
    pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.KIESR)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(AAND_UITGESLOTEN_KIESR, "A")
        .addElem(EINDDATUM_UITSLUIT_KIESR, "20800101")
        .toCat()
        .build();

    Kiesrecht kiesrecht = toKiesrecht(PersonSource.of(pl));
    assertTrue(kiesrecht.getUitgeslotenVanKiesrecht());
    assertEquals("2080-01-01", kiesrecht.getEinddatumUitsluitingKiesrecht().getDatum().toString());

    // No enddate
    pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.KIESR)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(AAND_UITGESLOTEN_KIESR, "A")
        .addElem(EINDDATUM_UITSLUIT_KIESR, "")
        .toCat()
        .build();

    kiesrecht = toKiesrecht(PersonSource.of(pl));
    assertTrue(kiesrecht.getUitgeslotenVanKiesrecht());
    assertNull(kiesrecht.getEinddatumUitsluitingKiesrecht());

    // Historic enddate
    pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.KIESR)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(AAND_UITGESLOTEN_KIESR, "A")
        .addElem(EINDDATUM_UITSLUIT_KIESR, "20010101")
        .toCat()
        .build();

    kiesrecht = toKiesrecht(PersonSource.of(pl));
    assertNull(kiesrecht);

    // Not excluded, no end-date
    pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.KIESR)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(AAND_UITGESLOTEN_KIESR, "A")
        .addElem(EINDDATUM_UITSLUIT_KIESR, "")
        .toCat()
        .build();

    kiesrecht = toKiesrecht(PersonSource.of(pl));
    assertTrue(kiesrecht.getUitgeslotenVanKiesrecht());
    assertNull(kiesrecht.getEinddatumUitsluitingKiesrecht());
  }
}
