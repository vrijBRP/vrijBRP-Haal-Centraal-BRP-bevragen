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

package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static java.lang.Long.parseLong;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.core.enums.GBARecStatus;
import nl.procura.gbaws.web.rest.v2.personlists.CustomGbaWsPersonList;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.PersonUtils;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.GeslachtEnum;
import lombok.SneakyThrows;

public class PartnerRelatedTests extends IngeschrevenPersonenResourceTest {

  /*
   * Scenario: er is geen partner
   * Gegeven de te raadplegen persoon heeft geen (actuele) partner
   * Als de partners worden geraadpleegd van de ingeschreven persoon met burgerservicenummer 999999011
   * Dan is het aantal gevonden partners 0
   */
  @Test
  @SneakyThrows
  public void testMustNotHaveAPartner() {
    var persoon = getIngeschrevenPersoon(999994062L);
    assertNull(persoon.getEmbedded().getPartners());
  }

  /**
   * Scenario: de partner is ingeschreven persoon
   *  Gegeven de te raadplegen persoon heeft een partner die zelf ingeschreven persoon is
   *  En de partner van de ingeschreven persoon heeft in de registratie burgerservicenummer 999999047,
   *  voornaam Franklin en geslachtsnaam Groenen
   *  Als de partners worden geraadpleegd van de ingeschreven persoon met burgerservicenummer 999999023
   *  Dan is het aantal gevonden partners 1
   *  En wordt de partner gevonden met burgerservicenummer=999999047
   *  En heeft deze partner naam.voornamen=Franklin
   *  En heeft deze partner naam.geslachtsnaam=Groenen
   *  En heeft de gevonden partner link ingeschrevenpersonen met /ingeschrevenpersonen/999999047
   */
  @Test
  @SneakyThrows
  public void testMustHaveAPartnerWithCertainAttributes() {
    var persoon = getIngeschrevenPersoon(999993756L);
    var partners = persoon.getEmbedded().getPartners();

    assertEquals(1, partners.size());
    assertEquals("999993756", persoon.getBurgerservicenummer());
    assertEquals("999992363", partners.get(0).getBurgerservicenummer());
    assertEquals("Gertrudis Maria", partners.get(0).getNaam().getVoornamen());
    assertEquals("Maassen", partners.get(0).getNaam().getGeslachtsnaam());
    assertTrue(partners.get(0).getLinks().getIngeschrevenPersoon().getHref()
        .contains("/ingeschrevenpersonen/999992363"));
  }

  /**
   * Scenario: de partner is geen ingeschreven persoon
   *  Gegeven de te raadplegen persoon heeft een partner die zelf geen ingeschreven persoon is
   *  En de partner van de ingeschreven persoon heeft volgens categorie 05/55 naam Dieter von Weit, geboren in Würzburg
   *  en geboortedatum 19 juni 1989
   *  Als de partners worden geraadpleegd van de ingeschreven persoon met burgerservicenummer 999999436
   *  Dan is het aantal gevonden partners 1
   *  En wordt de partner gevonden met burgerservicenummer=null
   *  En heeft deze partner naam.voornamen=Dieter
   *  En heeft deze partner naam.voorvoegsel=von
   *  En heeft deze partner naam.geslachtsnaam=Weit
   *  En heeft de gevonden partner een lege link ingeschrevenpersonen
   */
  @Test
  @SneakyThrows
  public void testMustHaveAPartnerWithoutBsn() {
    var persoon = getIngeschrevenPersoon(999992545L);
    var partner = persoon.getEmbedded().getPartners().get(0);

    assertEquals(1, persoon.getEmbedded().getPartners().size());
    assertEquals(1, persoon.getLinks().getPartners().size());
    assertNull(partner.getBurgerservicenummer());
    assertEquals("Jéan", partner.getNaam().getVoornamen());
    assertNull(partner.getNaam().getVoorvoegsel());
    assertEquals("Roussæx", partner.getNaam().getGeslachtsnaam());

    var linkedPartner = getPartner(999992545L, "1");
    assertNull(linkedPartner.getBurgerservicenummer());
    assertEquals("Jéan", linkedPartner.getNaam().getVoornamen());

    var error = "";
    try {
      getPartner(999992545L, "2");
    } catch (Exception e) {
      error = e.getMessage();
    }
    assertTrue(error.contains("Een of meerdere parameters zijn niet correct"));
  }

  /**  Scenario: de ingeschreven persoon heeft meerdere partners
   *  Gegeven de te raadplegen persoon heeft meerdere (twee) actuele partners (Ali en Iskander)
   *  Als de partners worden geraadpleegd van de ingeschreven persoon met burgerservicenummer 999999291
   *  Dan is het aantal gevonden partners 2
   *  En wordt de partner gevonden met naam.voornamen=Ali
   *  En wordt de partner gevonden met naam.voornamen=Iskander
   */
  @Test
  @SneakyThrows
  public void testMustHavePluralPartners() {

    var pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.HUW_GPS)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen")
        .toCat()
        // Current
        .addSet(2)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Vries")
        .toCat()
        // Incorrectly added
        .addSet(3)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "")
        .toCat()
        // Incorrect
        .addSet(4)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.IND_ONJUIST, "1234")
        .toCat()
        // Divorced
        .addSet(5)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.REDEN_ONTBINDING, "S")
        .toCat()
        .build();

    var currentPartners = PersonUtils.getPartners(pl);
    assertEquals(2, currentPartners.size());
    assertEquals("Jansen", currentPartners.get(0).getRec().getElemValue(GBAElem.GESLACHTSNAAM));
    assertEquals("Vries", currentPartners.get(1).getRec().getElemValue(GBAElem.GESLACHTSNAAM));
  }

  /**Scenario: de ingeschreven persoon heeft een beëindigde relatie
   *  Gegeven de te raadplegen persoon heeft een beëindigde relatie
   *  En de te raadplegen persoon heeft geen andere of nieuwe relatie
   *  Als de partners worden geraadpleegd van de ingeschreven persoon met burgerservicenummer 999999321
   *  Dan is het aantal gevonden partners 0
   */
  @Test
  @SneakyThrows
  public void testMustHaveExPartner() {
    // zijn vrouw is overleden en is niet hertrouwd
    var persoon = getIngeschrevenPersoon(999994001L);
    assertNull(persoon.getEmbedded().getPartners());
    assertNull(persoon.getLinks().getPartners());

    // Heeft nooit een partner gehad
    persoon = getIngeschrevenPersoon(999995984L);
    assertNull(persoon.getEmbedded().getPartners());
    assertNull(persoon.getLinks().getPartners());

    // 2 keer gescheiden beide ex-partners worden niet vermeld
    persoon = getIngeschrevenPersoon(999992910L);
    assertNull(persoon.getEmbedded().getPartners());
    assertNull(persoon.getLinks().getPartners());
    assertNull(persoon.getLinks().getPartners());
  }

  /**Scenario: partner ophalen vanuit links van ingeschreven persoon via sub-resource partners
   *  Gegeven de te raadplegen persoon heeft een partner die zelf ingeschreven persoon is
   *  En de partner van de ingeschreven persoon heeft in de registratie burgerservicenummer 999999047, voornaam Franklin en geslachtsnaam Groenen
   *  Als de ingeschreven persoon met burgerservicenummer 999999023 wordt geraadpleegd
   *  En de link partners wordt gevolgd
   *  Dan is in het antwoord burgerservicenummer=999999047
   *  En is in het antwoord naam.voornamen=Franklin
   *  En is in het antwoord naam.voorvoegsel=null
   *  En is in het antwoord naam.geslachtsnaam=Groenen
   *  En is in het antwoord naam.adellijkeTitel_predikaat.omschrijvingAdellijkeTitel_predikaat=null
   *  En is in het antwoord geboorte.datum.datum=1983-05-26
   *  Als de link ingeschrevenpersonen wordt gevolgd
   *  Dan is in het antwoord burgerservicenummer=999999047
   *  En is in het antwoord naam.voornamen=Franklin
   *  En is in het antwoord geslachtsaanduiding=M
   */
  @Test
  @SneakyThrows
  public void testMustFindPartnerThroughLinks() {
    var persoon = getIngeschrevenPersoon(999991516L);
    var partner = persoon.getEmbedded().getPartners().get(0);

    assertEquals(1, persoon.getEmbedded().getPartners().size());
    assertEquals("999995170", partner.getBurgerservicenummer());
    assertEquals("Marjolein", partner.getNaam().getVoornamen());
    assertEquals("de", partner.getNaam().getVoorvoegsel());
    assertEquals("Goede", partner.getNaam().getGeslachtsnaam());
    assertNull(partner.getNaam().getAdellijkeTitelPredikaat());
    assertEquals("1990-06-07", partner.getGeboorte().getDatum().getDatum().toString());
    assertTrue(partner.getLinks().getSelf().getHref().contains("999991516/partners/1"));
    assertTrue(partner.getLinks().getIngeschrevenPersoon().getHref().contains("999995170"));

    var linkedPartner = getPartner(999991516L, "1");
    assertEquals("999995170", linkedPartner.getBurgerservicenummer());
    assertEquals("Marjolein", linkedPartner.getNaam().getVoornamen());
    assertEquals(GeslachtEnum.VROUW, linkedPartner.getGeslachtsaanduiding());

    var partnerIngeschrevenPersoon = getIngeschrevenPersoon(
        parseLong(partner.getBurgerservicenummer()));
    assertEquals(partner.getBurgerservicenummer(), partnerIngeschrevenPersoon.getBurgerservicenummer());
    assertEquals(GeslachtEnum.VROUW, partnerIngeschrevenPersoon.getGeslachtsaanduiding());
  }
}
