package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static java.lang.Long.parseLong;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.GeslachtEnum;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.OuderAanduidingEnum;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.OuderHalBasis;

import lombok.SneakyThrows;

public class OudersRelatedTests extends IngeschrevenPersonenResourceTest {

  /** Scenario: er is geen ouder
   *  Gegeven de te raadplegen persoon heeft geen geregistreerde ouders
   *  Als de ouders worden geraadpleegd van de ingeschreven persoon met burgerservicenummer 999999291
   *  Dan is het aantal gevonden ouders 0
   */
  @Test
  @SneakyThrows
  void testMustNotHaveRegisteredParents() {
    //deze persoon heeft 2 geregistreerde ouders
    var persoon = getIngeschrevenPersoon(999994001L);
    assertEquals(2, persoon.getEmbedded().getOuders().size());

    persoon = getIngeschrevenPersoon(999993835L);
    assertEquals("999993835", persoon.getBurgerservicenummer());
    assertEquals("Wesley", persoon.getNaam().getVoornamen());
    assertEquals(1, persoon.getEmbedded().getOuders().size());
    assertNull(persoon.getEmbedded().getOuders().get(0).getBurgerservicenummer());

    persoon = getIngeschrevenPersoon(999996940L);
    assertEquals("Wojciech", persoon.getNaam().getVoornamen());
    assertNull(persoon.getEmbedded().getOuders());
  }

  /* Functionaliteit: Ouders van een ingeschreven persoon raadplegen
     Van een inschreven persoon kunnen ouders worden opgevraagd als sub-resource van de ingeschreven persoon.
     De sub-resource ouders bevat de gegevens over de kind-ouderrelatie inclusief enkele eigenschappen van de betreffende ouder.
     Wanneer de ouder een ingeschreven persoon is, levert de sub-resource ouders de actuele gegevens van de ouder(s)
     zoals die op de PL van de betreffende ouder staan (dus NIET zoals ze in categorie 02/52 Ouder1 of 03/53 Ouder2 staan).
     Een ouder wordt alleen teruggegeven als minimaal geslachtsnaam OF voornamen (ivm naamketen) of geboortedatum van de ouder is gevuld.
  
     Abstract Scenario: een ouder is onjuist of onbekend
     Gegeven op de PL van een ingeschreven persoon is categorie ouder leeg met uitzondering van de
     <bij onjuist of onbekend ingevulde kenmerken> kenmerken
     Als de ouders worden geraadpleegd van de ingeschreven persoon met burgerservicenummer 999999291
     Dan is het aantal gevonden ouders 0
     En zijn er geen links naar de onbekende ouders
  
     Voorbeelden:
             | bij onjuist of onbekend ingevulde kenmerken |
             | registergemeenteAkte (81.10), aktenummer (81.20), datumIngangGeldigheid (85.10), datumVanOpneming (86.10) |
             | gemeenteDocument (82.10), datumDocument (82.20), beschrijvingDocument (82.30), datumIngangGeldigheid (85.10), datumVanOpneming (86.10) |
             | geboortedatum (03.10), datumIngangGeldigheid (85.10), datumVanOpneming (86.10) |
   */
  @Test
  @SneakyThrows
  public void testFindParentsBySubResource() {
    var persoon = getIngeschrevenPersoon(999996940L);

    assertNull(persoon.getEmbedded().getOuders());
    assertNull(persoon.getLinks().getOuders());
  }

  /**
   * Scenario: de ouders zijn ingeschreven personen
   * Gegeven de te raadplegen persoon heeft een ouder die zelf ingeschreven persoon is
   * En de Ouder1 van de ingeschreven persoon heeft in de registratie burgerservicenummer 999999382,
   * naam jonkheer Franciscus Theodorus in 't Groen
   * En de Ouder2 van de ingeschreven persoon heeft in de registratie burgerservicenummer 999999394, naam Philomena Blaauw
   * Als de ouders worden geraadpleegd van de ingeschreven persoon met burgerservicenummer 999999047
   * Dan wordt de ouder gevonden met ouder_aanduiding=1
   * En heeft deze ouder burgerservicenummer=999999382
   * En heeft deze ouder geslachtsaanduiding=M
   * En heeft deze ouder naam.voornamen=Franciscus Theodorus
   * En heeft deze ouder naam.voorvoegsel=in 't
   * En heeft deze ouder naam.geslachtsnaam=Groen
   * En heeft deze ouder naam.adellijkeTitel_predikaat.omschrijvingAdellijkeTitel_predikaat=jonkheer
   * En heeft deze gevonden ouder de ingeschrevenpersonen link met /ingeschrevenpersonen/999999382
   * En wordt de ouder gevonden met ouder_aanduiding=2
   * En heeft deze ouder burgerservicenummer=999999394
   * En heeft deze ouder geslachtsaanduiding=V
   * En heeft deze ouder naam.voornamen=Philomena
   * En heeft deze ouder naam.voorvoegsel=null
   * En heeft deze ouder naam.geslachtsnaam=Blaauw
   * En heeft deze ouder naam.adellijkeTitel_predikaat.omschrijvingAdellijkeTitel_predikaat=null
   * En heeft deze gevonden ouder de ingeschrevenpersonen link met /ingeschrevenpersonen/999999394
   **/
  @Test
  @SneakyThrows
  public void testFindOuder1AsFatherAndOuder2AsMother() {
    var persoon = getIngeschrevenPersoon(999996174L);
    var ouders = persoon.getEmbedded().getOuders();

    assertEquals("999996149", ouders.get(0).getBurgerservicenummer());
    assertEquals("man", ouders.get(0).getGeslachtsaanduiding().toString());
    assertEquals("Mark", ouders.get(0).getNaam().getVoornamen());
    assertEquals("de", ouders.get(0).getNaam().getVoorvoegsel());
    assertEquals("Boer", ouders.get(0).getNaam().getGeslachtsnaam());
    assertTrue(ouders.get(0).getLinks().getIngeschrevenPersoon().getHref().contains("/ingeschrevenpersonen/999996149"));

    assertEquals("999996150", ouders.get(1).getBurgerservicenummer());
    assertEquals("vrouw", ouders.get(1).getGeslachtsaanduiding().toString());
    assertEquals("Chantal", ouders.get(1).getNaam().getVoornamen());
    assertEquals("de", ouders.get(1).getNaam().getVoorvoegsel());
    assertEquals("Vries", ouders.get(1).getNaam().getGeslachtsnaam());
    assertTrue(ouders.get(1).getLinks().getIngeschrevenPersoon().getHref().contains("/ingeschrevenpersonen/999996150"));
  }

  @Test
  @SneakyThrows
  public void testFindOuder1AsFatherAndOuder2AsFather() {
    var persoon = getIngeschrevenPersoon(999992375L);
    var ouders = persoon.getEmbedded().getOuders();

    assertEquals("999992508", ouders.get(0).getBurgerservicenummer());
    assertEquals("man", ouders.get(0).getGeslachtsaanduiding().toString());
    assertEquals("Willem", ouders.get(0).getNaam().getVoornamen());
    assertNull(ouders.get(0).getNaam().getVoorvoegsel());
    assertEquals("Kierkegaard", ouders.get(0).getNaam().getGeslachtsnaam());
    assertTrue(ouders.get(0).getLinks().getIngeschrevenPersoon().getHref().contains("/ingeschrevenpersonen/999992508"));

    assertEquals("999994281", ouders.get(1).getBurgerservicenummer());
    assertEquals("man", ouders.get(1).getGeslachtsaanduiding().toString());
    assertEquals("Najih Walliyullah", ouders.get(1).getNaam().getVoornamen());
    assertNull(ouders.get(1).getNaam().getVoorvoegsel());
    assertEquals("Djibet", ouders.get(1).getNaam().getGeslachtsnaam());
    assertTrue(ouders.get(1).getLinks().getIngeschrevenPersoon().getHref().contains("/ingeschrevenpersonen/999994281"));
  }

  /**
   * Scenario: de ouder is geen ingeschreven persoon
   * Gegeven de te raadplegen persoon heeft een ouder die zelf geen ingeschreven persoon is
   * En de Ouder2 van de ingeschreven persoon heeft volgens categorie 05/55 naam markiezin Marie du Partenaire,
   * geboren in Saintt-Quentin-en-Tourmont en geboortedatum 2 november 1979
   * Als de ouders worden geraadpleegd van de ingeschreven persoon met burgerservicenummer 999999011
   * Dan wordt de ouder gevonden met ouder_aanduiding=2
   * En heeft deze ouder burgerservicenummer=null
   * En heeft deze ouder naam.voornamen=Marie
   * En heeft deze ouder naam.voorvoegsel=du
   * En heeft deze ouder naam.geslachtsnaam=Partenaire
   * En heeft deze ouder naam.adellijkeTitel_predikaat.omschrijvingAdellijkeTitel_predikaat=markiezin
   * En heeft deze ouder geboorte.datum.datum=1972-08-31
   * En heeft deze ouder geboorte.datum.dag=31
   * En heeft deze ouder geboorte.datum.maand=08
   * En heeft deze ouder geboorte.plaats=Saintt-Quentin-en-Tourmont
   * En heeft deze ouder geboorte.land.landnaam=Frankrijk
   * En heeft deze gevonden ouder een lege link ingeschrevenpersonen
   */
  @Test
  @SneakyThrows
  public void testFindOuder2NoBsn() {
    var persoon = getIngeschrevenPersoon(999990512L);
    var ouder2 = persoon.getEmbedded().getOuders().get(1);

    assertNull(ouder2.getBurgerservicenummer());
    assertEquals("Arnold", ouder2.getNaam().getVoornamen());
    assertNull(ouder2.getNaam().getVoorvoegsel());
    assertEquals("Goulouse", ouder2.getNaam().getGeslachtsnaam());
    assertEquals("1960-03-09", ouder2.getGeboorte().getDatum().getDatum().toString());
    assertEquals(9, ouder2.getGeboorte().getDatum().getDag());
    assertEquals(3, ouder2.getGeboorte().getDatum().getMaand());
    assertEquals("Rotterdam", ouder2.getGeboorte().getPlaats().getOmschrijving());
    assertEquals("Nederland", ouder2.getGeboorte().getLand().getOmschrijving());
    assertNull(ouder2.getLinks().getIngeschrevenPersoon());
  }

  /**
   * Scenario: de ingeschreven persoon heeft twee ouders
   * Gegeven de te raadplegen persoon heeft meerdere (twee) ouders (Marie en Cornelis Petrus Johannus)
   * Als de ouders worden geraadpleegd van de ingeschreven persoon met burgerservicenummer 999999011
   * Dan is het aantal gevonden ouders 2
   * En wordt de ouder gevonden met naam.voornamen=Marie
   * En wordt de ouder gevonden met naam.voornamen=Cornelis Petrus Johannus
   */
  @Test
  @SneakyThrows
  public void testFindTwoParents() {
    var persoon = getIngeschrevenPersoon(999990159L);
    var moeder = persoon.getEmbedded().getOuders().get(0);
    var vader = persoon.getEmbedded().getOuders().get(1);

    assertNull(moeder.getBurgerservicenummer());
    assertEquals("Ida", moeder.getNaam().getVoornamen());
    assertNull(moeder.getNaam().getVoorvoegsel());
    assertEquals("Froelke", moeder.getNaam().getGeslachtsnaam());
    assertNull(moeder.getGeboorte().getDatum());
    assertEquals("Bondsrepubliek Duitsland", moeder.getGeboorte().getLand().getOmschrijving());

    assertEquals(1930, vader.getGeboorte().getDatum().getJaar());
    assertEquals(7, vader.getGeboorte().getDatum().getMaand());
    assertEquals(3, vader.getGeboorte().getDatum().getDag());

    assertEquals("vrouw", moeder.getGeslachtsaanduiding().toString());
    assertEquals("man", vader.getGeslachtsaanduiding().toString());
  }

  /**
   * Scenario: ouder ophalen vanuit links van ingeschreven persoon via sub-resource ouders
   * Gegeven de te raadplegen persoon heeft één ouder dat zelf ingeschreven persoon is
   * En de ouder van de ingeschreven persoon heeft in de registratie burgerservicenummer 999999291,
   *   naam Çelik	Groenen, geboren in januari 1975 (geboortedag is onbekend)
   * Als de ingeschreven persoon met burgerservicenummer 999999424 wordt geraadpleegd
   * En de link ouders wordt gevolgd
   * Dan is in het antwoord burgerservicenummer=999999291
   * En is in het antwoord naam.voornamen=Çelik
   * En is in het antwoord naam.voorvoegsel=null
   * En is in het antwoord naam.geslachtsnaam=Groenen
   * En is in het antwoord naam.adellijkeTitel_predikaat.omschrijvingAdellijkeTitel_predikaat=null
   * En is in het antwoord geboorte.datum.datum=null
   * En is in het antwoord geboorte.datum.jaar=1975
   * En is in het antwoord geboorte.datum.maand=01
   * En is in het antwoord geboorte.datum.dag niet aanwezig of null
   * En is in het antwoord geslachtsaanduiding=V
   * Als de link ingeschrevenpersonen wordt gevolgd
   * Dan is in het antwoord burgerservicenummer=999999291
   * En is in het antwoord naam.voornamen=Çelik
   * En is in het antwoord geslachtsaanduiding=V
   * En is in het antwoord datumVestigingInNederland=2002-08-12
   */
  @Test
  @SneakyThrows
  public void testFindParentWithBirthdayUnknown() {
    long bsn = 999991991L;
    var persoon = getIngeschrevenPersoon(bsn);
    var ouders = persoon.getEmbedded().getOuders();

    assertEquals(2, ouders.size());

    assertEquals("Denise Gerarda", ouders.get(0).getNaam().getVoornamen());
    assertEquals(OuderAanduidingEnum.OUDER1, ouders.get(0).getOuderAanduiding());
    OuderHalBasis linkedParent1 = getOuder(bsn, "1");
    String linkBsn1 = linkedParent1.getBurgerservicenummer();
    assertEquals("999990950", linkBsn1);
    assertEquals("Denise Gerarda", linkedParent1.getNaam().getVoornamen());
    assertEquals(GeslachtEnum.VROUW, linkedParent1.getGeslachtsaanduiding());
    assertEquals(linkBsn1, ouders.get(0).getBurgerservicenummer());
    assertEquals(linkBsn1, getIngeschrevenPersoon(parseLong(linkBsn1)).getBurgerservicenummer());

    assertEquals("Floris", ouders.get(1).getNaam().getVoornamen());
    assertEquals(OuderAanduidingEnum.OUDER2, ouders.get(1).getOuderAanduiding());
    OuderHalBasis linkedParent2 = getOuder(bsn, "2");
    String linkBsn2 = linkedParent2.getBurgerservicenummer();
    assertEquals("999993689", linkBsn2);
    assertEquals("Floris", linkedParent2.getNaam().getVoornamen());
    assertEquals(GeslachtEnum.MAN, linkedParent2.getGeslachtsaanduiding());
    assertEquals(linkBsn2, ouders.get(1).getBurgerservicenummer());
    assertEquals(linkBsn2, getIngeschrevenPersoon(parseLong(linkBsn2)).getBurgerservicenummer());
  }
}
