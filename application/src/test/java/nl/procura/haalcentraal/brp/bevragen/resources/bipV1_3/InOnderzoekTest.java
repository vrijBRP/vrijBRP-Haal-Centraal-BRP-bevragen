package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.OnderzoekUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.core.enums.GBARecStatus;
import nl.procura.gbaws.web.rest.v2.personlists.CustomGbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;

/**
 * Functionaliteit: in onderzoek
 *   Wanneer een attribuut in onderzoek is, krijgt in het antwoord het attribuut met dezelfde naam binnen inOnderzoek de waarde true.
 *
 *   Een categorie kan in zijn geheel in onderzoek zijn, maar er kunnen ook individuele groepen of attributen binnen de categorie in onderzoek zijn.
 *   Wanneer een categorie in zijn geheel in onderzoek is, krijgt in het antwoord in inOnderzoek elk attribuut van deze categorie de waarde true.
 *   Wanneer een categorie in zijn geheel in onderzoek is, krijgt in het antwoord in  inOnderzoek in elke groep van de categorie elk attribuut van deze categorie de waarde true.
 *   Voor de categorie 01 persoon betreft dit in het antwoord alleen de groepen naam en geboorte.
 *
 *   Een groep kan in zijn geheel in onderzoek zijn, maar er kunnen ook individuele attributen binnen de groep in onderzoek zijn.
 *   Wanneer een groep in zijn geheel in onderzoek is, krijgt in het antwoord in inOnderzoek elk attribuut van deze groep de waarde true.
 *
 *   Een attribuut dat niet in onderzoek is, wordt niet in het antwoord in inOnderzoek opgenomen, ook niet met de waarde false of null.
 */
public class InOnderzoekTest extends IngeschrevenPersonenResourceTest {

  /**
   *  Scenario: hele categorie persoon in onderzoek
   *     Gegeven de te raadplegen persoon heeft de hele persoon in onderzoek (01.83.10=010000)
   *     En geen enkele andere categorie, groep of attribuut is in onderzoek
   *     Als de ingeschreven persoon met burgerservicenummer 999991449 wordt geraadpleegd
   *     Dan is in het antwoord inOnderzoek.burgerservicenummer=true
   *     En is in het antwoord inOnderzoek.geslachtsaanduiding=true
   *     En is in het antwoord inOnderzoek.datumOpschortingBijhouding=true
   *     En is in het antwoord inOnderzoek.indicatieOpschortingBijhouding=true
   *     En is in het antwoord inOnderzoek.indicatieGeheim=true
   *     En is in het antwoord inOnderzoek.datumEersteInschrijvingGBA=true
   *     En is in het antwoord inOnderzoek.redenOpschortingBijhouding=true
   *     Dan is in het antwoord naam.inOnderzoek.geslachtsnaam=true
   *     En is in het antwoord naam.inOnderzoek.voornamen=true
   *     En is in het antwoord naam.inOnderzoek.voorvoegsel=true
   *     En is in het antwoord naam.inOnderzoek.adellijkeTitel_predikaat=true
   *     Dan is in het antwoord geboorte.inOnderzoek.plaats=true
   *     En is in het antwoord geboorte.inOnderzoek.datum=true
   *     En is in het antwoord geboorte.inOnderzoek.land=true
   *     En is in het antwoord attribuut nationaliteit.inOnderzoek null, leeg of afwezig
   *     En is in het antwoord attribuut overlijden.inOnderzoek null, leeg of afwezig
   *     En is in het antwoord attribuut verblijfplaats.inOnderzoek null, leeg of afwezig
   *     En is in het antwoord attribuut gezagsverhouding.inOnderzoek null, leeg of afwezig
   *     En is in het antwoord attribuut verblijfstitel.inOnderzoek null, leeg of afwezig
   */
  @Test
  public void mutReturnCategoryPersoonInOnderzoek() {
    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.PERSOON)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen")
        .addElem(GBAElem.AAND_GEG_IN_ONDERZ, "010000")
        .addElem(GBAElem.DATUM_INGANG_ONDERZ, "20210201")
        .toCat()
        .build();

    GbaWsPersonListRec rec = pl.getCurrentRec(GBACat.PERSOON).orElse(null);

    var persoonOnderzoek = toPersoonInOnderzoek(rec);
    assert persoonOnderzoek != null;
    assertTrue(persoonOnderzoek.getBurgerservicenummer());
    assertTrue(persoonOnderzoek.getGeslachtsaanduiding());
    assertEquals(LocalDate.of(2021, 2, 1), persoonOnderzoek.getDatumIngangOnderzoek().getDatum());

    var naamOnderzoek = toNaamInOnderzoek(rec);
    assert naamOnderzoek != null;
    assertTrue(naamOnderzoek.getGeslachtsnaam());
    assertTrue(naamOnderzoek.getVoornamen());
    assertTrue(naamOnderzoek.getVoorvoegsel());
    assertTrue(naamOnderzoek.getAdellijkeTitelPredikaat());
    assertEquals(LocalDate.of(2021, 2, 1), naamOnderzoek.getDatumIngangOnderzoek().getDatum());

    var geboorteOnderzoek = toGeboorteInOnderzoek(rec);
    assert geboorteOnderzoek != null;
    assertTrue(geboorteOnderzoek.getDatum());
    assertTrue(geboorteOnderzoek.getPlaats());
    assertTrue(geboorteOnderzoek.getLand());
    assertEquals(LocalDate.of(2021, 2, 1), geboorteOnderzoek.getDatumIngangOnderzoek().getDatum());
  }

  /*
   *   Scenario: hele categorie in onderzoek
   *     Gegeven de te raadplegen persoon heeft categorie nationaliteit in onderzoek
   *     Als de ingeschreven persoon met burgerservicenummer 999999102 wordt geraadpleegd
   *     Dan is in het antwoord nationaliteit.inOnderzoek.nationaliteit=true
   *     En is in het antwoord nationaliteit.inOnderzoek.redenOpname=true
   *     En is in het antwoord nationaliteit.inOnderzoek.redenBeindigen=true
   *     En is in het antwoord nationaliteit.inOnderzoek.aanduidingBijzonderNederlanderschap=true
   */
  @Test
  public void mustReturnCategoryOverlijdenInOnderzoek() {
    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.OVERL)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.DATUM_OVERL, "20150501")
        .addElem(GBAElem.AAND_GEG_IN_ONDERZ, "060000")
        .addElem(GBAElem.DATUM_INGANG_ONDERZ, "20210201")
        .toCat()
        .build();

    var onderzoek = toOverlijdenInOnderzoek(pl.getCurrentRec(GBACat.OVERL).orElse(null));
    assert onderzoek != null;
    assertTrue(onderzoek.getDatum());
    assertTrue(onderzoek.getPlaats());
    assertTrue(onderzoek.getLand());
    assertEquals(LocalDate.of(2021, 2, 1), onderzoek.getDatumIngangOnderzoek().getDatum());
  }

  @Test
  public void mustReturnCategoryKindInOnderzoek() {
    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.KINDEREN)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen")
        .addElem(GBAElem.AAND_GEG_IN_ONDERZ, "090000")
        .addElem(GBAElem.DATUM_INGANG_ONDERZ, "20210201")
        .toCat()
        .build();

    var onderzoek = toKindInOnderzoek(pl.getCurrentRec(GBACat.KINDEREN).orElse(null));
    assert onderzoek != null;
    assertTrue(onderzoek.getBurgerservicenummer());
    assertEquals(LocalDate.of(2021, 2, 1), onderzoek.getDatumIngangOnderzoek().getDatum());
  }

  @Test
  public void mustReturnCategoryOuderInOnderzoek() {
    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.OUDER_1)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen")
        .addElem(GBAElem.AAND_GEG_IN_ONDERZ, "020000")
        .addElem(GBAElem.DATUM_INGANG_ONDERZ, "20210201")
        .toCat()
        .build();

    var onderzoek = toOuderInOnderzoek(pl.getCurrentRec(GBACat.OUDER_1).orElse(null));
    assert onderzoek != null;
    assertTrue(onderzoek.getBurgerservicenummer());
    assertTrue(onderzoek.getGeslachtsaanduiding());
    assertTrue(onderzoek.getDatumIngangFamilierechtelijkeBetrekking());
    assertEquals(LocalDate.of(2021, 2, 1), onderzoek.getDatumIngangOnderzoek().getDatum());
  }

  @Test
  public void mustReturnCategoryGezagInOnderzoek() {
    var pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.GEZAG)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.IND_GEZAG_MINDERJ, "12")
        .addElem(GBAElem.AAND_GEG_IN_ONDERZ, "110000")
        .addElem(GBAElem.DATUM_INGANG_ONDERZ, "20210201")
        .toCat()
        .build();

    var onderzoek = toGezagsverhoudingInOnderzoek(pl.getCurrentRec(GBACat.GEZAG).orElse(null));
    assert onderzoek != null;
    assertTrue(onderzoek.getIndicatieCurateleRegister());
    assertTrue(onderzoek.getIndicatieGezagMinderjarige());
    assertEquals(LocalDate.of(2021, 2, 1), onderzoek.getDatumIngangOnderzoek().getDatum());
  }

  /*
   *   Scenario: Hele groep in onderzoek
   *     Gegeven de te raadplegen persoon heeft groep naam in onderzoek
   *     Als de ingeschreven persoon met burgerservicenummer 999999151 wordt geraadpleegd
   *     Dan is in het antwoord inOnderzoek.naam.geslachtsnaam=true
   *     En is in het antwoord inOnderzoek.naam.voornamen=true
   *     En is in het antwoord inOnderzoek.naam.voorvoegsel=true
   */
  @Test
  public void mustReturnGroupInOnderzoek() {
    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.PERSOON)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen")
        .addElem(GBAElem.AAND_GEG_IN_ONDERZ, "010200")
        .addElem(GBAElem.DATUM_INGANG_ONDERZ, "20210201")
        .toCat()
        .build();

    GbaWsPersonListRec rec = pl.getCurrentRec(GBACat.PERSOON).orElse(null);
    var naamOnderzoek = toNaamInOnderzoek(rec);
    assert naamOnderzoek != null;
    assertTrue(naamOnderzoek.getGeslachtsnaam());
    assertTrue(naamOnderzoek.getVoorvoegsel());
    assertTrue(naamOnderzoek.getVoornamen());
    assertNull(toGeboorteInOnderzoek(rec));
  }

  /*   Scenario: Een attribuut is in inOnderzoek
  *     Gegeven de te raadplegen persoon heeft attribuut naam.voornamen in onderzoek
  *     Als de ingeschreven persoon met burgerservicenummer 999999163 wordt geraadpleegd
  *     Dan is in het antwoord inOnderzoek.naam.geslachtsnaam niet aanwezig of null
  *     En is in het antwoord inOnderzoek.naam.voornamen=true
  *     En is in het antwoord inOnderzoek.naam.voorvoegsel niet aanwezig of null
  */
  @Test
  public void mustReturnAttributeInOnderzoek() {
    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.PERSOON)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen")
        .addElem(GBAElem.AAND_GEG_IN_ONDERZ, "010240") // Element geslachtsnaam in categorie 1
        .addElem(GBAElem.DATUM_INGANG_ONDERZ, "20210201")
        .toCat()
        .build();

    GbaWsPersonListRec rec = pl.getCurrentRec(GBACat.PERSOON).orElse(null);
    var naamOnderzoek = toNaamInOnderzoek(rec);
    assert naamOnderzoek != null;
    assertTrue(naamOnderzoek.getGeslachtsnaam());
    assertNull(naamOnderzoek.getVoorvoegsel());
    assertNull(naamOnderzoek.getVoornamen());
    assertNull(toGeboorteInOnderzoek(rec));
  }

  /*
  *   Scenario: Het onderzoek is beëindigd
  *     Gegeven de te raadplegen persoon heeft in onderzoek gevuld (010000), met datum einde in onderzoek ook gevuld
  *     Als de ingeschreven persoon met burgerservicenummer 999994888 wordt geraadpleegd
  *     Dan is in het antwoord inOnderzoek niet aanwezig of null
  */
  @Test
  public void mustReturnNoEndedOnderzoek() {
    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.PERSOON)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen")
        .addElem(GBAElem.AAND_GEG_IN_ONDERZ, "010000")
        .addElem(GBAElem.DATUM_INGANG_ONDERZ, "20210201")
        .addElem(GBAElem.DATUM_EINDE_ONDERZ, "20210202")
        .toCat()
        .build();

    GbaWsPersonListRec rec = pl.getCurrentRec(GBACat.PERSOON).orElse(null);
    assertNull(toPersoonInOnderzoek(rec));
    assertNull(toNaamInOnderzoek(rec));
    assertNull(toGeboorteInOnderzoek(rec));
  }

  /*   Scenario: Een attribuut is in onderzoek dat niet opgenomen is in de API
  *     Gegeven de te raadplegen persoon heeft in onderzoek gevuld op attribuut (048510)
  *     Als de ingeschreven persoon wordt geraadpleegd
  *     Dan is in het antwoord nationaliteit..inOnderzoek niet aanwezig of null
  */
  @Test
  public void mustReturnNoInOnderzoekInvalidAttribute() {
    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.OVERL)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.DATUM_OVERL, "20150501")
        .addElem(GBAElem.AAND_GEG_IN_ONDERZ, "068510") // Element 'ingangsdatum geldigheid' niet in API
        .addElem(GBAElem.DATUM_INGANG_ONDERZ, "20210201")
        .toCat()
        .build();

    assertNull(toOverlijdenInOnderzoek(pl.getCurrentRec(GBACat.OVERL).orElse(null)));
  }
}
