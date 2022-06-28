package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static java.lang.Long.parseLong;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.GeslachtEnum;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.IngeschrevenPersoonHal;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.KindHalBasis;

import lombok.SneakyThrows;

/**Functionaliteit: Kinderen van een ingeschreven persoon raadplegen
 * Van een inschreven persoon kunnen kinderen worden opgevraagd als sub-resource van de ingeschreven persoon.
 * De sub-resource kinderen bevat de gegevens over de ouder-kindrelatie inclusief enkele eigenschappen van het betreffende kind.
 * Wanneer het kind een ingeschreven persoon is, levert de sub-resource kinderen de actuele gegevens van het kind
 * zoals die op de PL van het betreffende kind staan (dus NIET zoals ze in categorie 09/59 Kind staan).
 */
public class KinderenRelatedTests extends IngeschrevenPersonenResourceTest {

  /**
   * Scenario: de ingeschreven persoon heeft geen kind
   * Gegeven de te raadplegen persoon heeft geen (geregistreerde) kinderen
   * Als de kinderen worden geraadpleegd van de ingeschreven persoon met burgerservicenummer 999999011
   * Dan is het aantal gevonden kinderen 0
   */
  @Test
  @SneakyThrows
  public void testMustNotHaveAChild() {
    var persoon = getIngeschrevenPersoon(999994062L);
    assertNull(persoon.getEmbedded().getKinderen());
  }

  /**
   * Scenario: het kind is een ingeschreven personen
   * Gegeven de te raadplegen persoon heeft een kind dat zelf ingeschreven persoon is
   * En het kind van de ingeschreven persoon heeft in de registratie burgerservicenummer 999999047, naam Franklin	Groenen, geboren op 26 mei 1983
   * Als de kinderen worden geraadpleegd van de ingeschreven persoon met burgerservicenummer 999999382
   * Dan is het aantal gevonden kinderen 1
   * En wordt het kind gevonden met burgerservicenummer=999999047
   * En heeft dit kind naam.voornamen=Franklin
   * En heeft dit kind naam.voorvoegsel=null
   * En heeft dit kind naam.geslachtsnaam=Groenen
   * En heeft dit kind naam.adellijkeTitel_predikaat.omschrijvingAdellijkeTitel_predikaat=null
   * En heeft dit kind geboorte.datum.datum=1983-05-26
   * En heeft dit gevonden kind de ingeschrevenpersonen link met /ingeschrevenpersonen/999999047
   */
  @Test
  @SneakyThrows
  public void testMustHaveARegisteredChild() {
    var persoon = getIngeschrevenPersoon(999996769L);
    var kinderen = persoon.getEmbedded().getKinderen();

    assertEquals(1, kinderen.size());
    assertEquals(1, persoon.getLinks().getKinderen().size());
    assertEquals("999996526", kinderen.get(0).getBurgerservicenummer());
    assertEquals("Piet Junior", kinderen.get(0).getNaam().getVoornamen());
    assertNull(kinderen.get(0).getNaam().getVoorvoegsel());
    assertEquals("Streeveld", kinderen.get(0).getNaam().getGeslachtsnaam());
    assertNull(kinderen.get(0).getNaam().getAdellijkeTitelPredikaat());
    assertEquals("2019-07-27", kinderen.get(0).getGeboorte().getDatum().getDatum().toString());
    assertTrue(kinderen.get(0).getLinks().getIngeschrevenPersoon()
        .getHref().contains("/ingeschrevenpersonen/999996526"));

    //ouder1 wordt ook teruggevonden via het kind
    persoon = getIngeschrevenPersoon(999996526L);
    assertEquals("999996769", persoon.getEmbedded().getOuders().get(0).getBurgerservicenummer());
  }

  /**
   * Scenario: het kind is geen ingeschreven persoon
   * Gegeven de te raadplegen persoon heeft een kind die zelf geen ingeschreven persoon is
   * En het kind van de ingeschreven persoon heeft volgens categorie 09/59 naam Chantal Groenen, geboren in Istanbul
   * en geboortedatum 2 januari 2002
   * Als de kinderen worden geraadpleegd van de ingeschreven persoon met burgerservicenummer 999999291
   * Dan wordt het kind gevonden met naam.voornamen=Chantal
   * En heeft dit kind burgerservicenummer=null
   * En heeft dit kind naam.voorvoegsel=null
   * En heeft dit kind naam.geslachtsnaam=Groenen
   * En heeft dit kind naam.adellijkeTitel_predikaat.omschrijvingAdellijkeTitel_predikaat=null
   * En heeft dit kind geboorte.datum.datum=2002-01-02
   * En heeft dit kind geboorte.datum.dag=02
   * En heeft dit kind geboorte.datum.maand=01
   * En heeft dit kind geboorte.plaats=Istanbul
   * En heeft dit kind geboorte.land.landnaam=Turkije
   * En heeft dit gevonden kind een lege link ingeschrevenpersonen
   */
  @Test
  @SneakyThrows
  public void testMustHaveANonRegisteredChild() {
    var persoon = getIngeschrevenPersoon(999992545L);
    var kind = persoon.getEmbedded().getKinderen().get(0);

    assertEquals(1, persoon.getLinks().getKinderen().size());
    assertEquals("Hélène", kind.getNaam().getVoornamen());
    assertNull(kind.getBurgerservicenummer());
    assertNull(kind.getNaam().getVoorvoegsel());
    assertEquals("Dumas", kind.getNaam().getGeslachtsnaam());
    assertNull(kind.getNaam().getAdellijkeTitelPredikaat());
    assertEquals("1950-07-23", kind.getGeboorte().getDatum().getDatum().toString());
    assertEquals("7", kind.getGeboorte().getDatum().getMaand().toString());
    assertEquals("23", kind.getGeboorte().getDatum().getDag().toString());
    assertEquals("Narbonne", kind.getGeboorte().getPlaats().getOmschrijving());
    assertEquals("Frankrijk", kind.getGeboorte().getLand().getOmschrijving());
    assertNull(kind.getLinks().getIngeschrevenPersoon());
  }

  /**
   * Scenario: de ingeschreven persoon twee kinderen
   * Gegeven de te raadplegen persoon heeft meerdere (twee) kinderen (Lisa en Daan)
   * Als de kinderen worden geraadpleegd van de ingeschreven persoon met burgerservicenummer 999999047
   * Dan is het aantal gevonden kinderen 2
   * En wordt het kind gevonden met naam.voornamen=Lisa
   * En wordt het kind gevonden met naam.voornamen=Daan
   */
  @Test
  @SneakyThrows
  public void testMustHaveTwoRegisteredChildren() {
    var persoon = getIngeschrevenPersoon(999993677L);
    var kinderen = persoon.getEmbedded().getKinderen();

    assertEquals(2, kinderen.size());
    assertEquals("Sandra", kinderen.get(0).getNaam().getVoornamen());
    assertEquals("Mihailović", kinderen.get(1).getNaam().getVoornamen());
  }

  /**
   * Scenario: kind ophalen vanuit links van ingeschreven persoon via sub-resource kinderen
   * Gegeven de te raadplegen persoon heeft een kind dat zelf ingeschreven persoon is
   * En het kind van de ingeschreven persoon heeft in de registratie burgerservicenummer 999999047,
   * naam Franklin Groenen, geboren op 26 mei 1983
   * Als de ingeschreven persoon met burgerservicenummer 999999382 wordt geraadpleegd
   * En de link kinderen wordt gevolgd
   * Dan is in het antwoord burgerservicenummer=999999047
   * En is in het antwoord naam.voornamen=Franklin
   * En is in het antwoord naam.voorvoegsel=null
   * En is in het antwoord naam.geslachtsnaam=Groenen
   * En is in het antwoord naam.adellijkeTitel_predikaat.omschrijvingAdellijkeTitel_predikaat=null
   * En is in het antwoord geboorte.datum.datum=1983-05-26
   * Als de link ingeschrevenpersonen wordt gevolgd
   * Dan is in het antwoord burgerservicenummer=999999047
   * En is in het antwoord naam.voornamen=Franklin
   * En is in het antwoord geslachtsaanduiding=M
   */
  @Test
  @SneakyThrows
  public void testMustHaveThreeRegisteredChildren() {
    var persoon = getIngeschrevenPersoon(999996551L);
    var kinderen = persoon.getEmbedded().getKinderen();
    var kinderenLinks = persoon.getLinks().getKinderen();

    assertEquals(3, kinderen.size());
    assertEquals(3, kinderenLinks.size());

    KindHalBasis child1 = kinderen.get(0);
    assertEquals("Babette", child1.getNaam().getVoornamen());
    assertEquals("999996599", child1.getBurgerservicenummer());
    assertTrue(child1.getLinks().getSelf().getHref().contains("999996551/kinderen/1"));
    assertTrue(kinderenLinks.get(0).getHref().contains("999996551/kinderen/1"));
    KindHalBasis linkChild1 = getKind(parseLong(persoon.getBurgerservicenummer()), "1");
    assertEquals("999996599", linkChild1.getBurgerservicenummer());
    assertEquals(child1.getBurgerservicenummer(), linkChild1.getBurgerservicenummer());
    IngeschrevenPersoonHal child1IngeschrevenPersoon = getIngeschrevenPersoon(
        parseLong(child1.getBurgerservicenummer()));
    assertEquals(child1.getBurgerservicenummer(), child1IngeschrevenPersoon.getBurgerservicenummer());
    assertEquals(GeslachtEnum.VROUW, child1IngeschrevenPersoon.getGeslachtsaanduiding());

    KindHalBasis child2 = kinderen.get(1);
    assertEquals("Anoeska", child2.getNaam().getVoornamen());
    assertTrue(child2.getLinks().getSelf().getHref().contains("999996551/kinderen/2"));
    assertTrue(kinderenLinks.get(1).getHref().contains("999996551/kinderen/2"));
    KindHalBasis linkChild2 = getKind(parseLong(persoon.getBurgerservicenummer()), "2");
    assertEquals("999996587", linkChild2.getBurgerservicenummer());
    String child2Bsn = child2.getBurgerservicenummer();
    assertEquals(child2Bsn, linkChild2.getBurgerservicenummer());
    IngeschrevenPersoonHal child2IngeschrevenPersoon = getIngeschrevenPersoon(parseLong(child2Bsn));
    assertEquals(child2.getBurgerservicenummer(), child2IngeschrevenPersoon.getBurgerservicenummer());
    assertEquals(GeslachtEnum.VROUW, child2IngeschrevenPersoon.getGeslachtsaanduiding());
    assertEquals("Anoeska", child2IngeschrevenPersoon.getNaam().getVoornamen());

    KindHalBasis child3 = kinderen.get(2);
    assertEquals("Carola Bernadette", child3.getNaam().getVoornamen());
    assertTrue(child3.getLinks().getSelf().getHref().contains("999996551/kinderen/3"));
    assertTrue(kinderenLinks.get(2).getHref().contains("999996551/kinderen/3"));
    KindHalBasis linkChild3 = getKind(parseLong(persoon.getBurgerservicenummer()), "3");
    assertEquals("999995984", linkChild3.getBurgerservicenummer());
    String child3Bsn = child3.getBurgerservicenummer();
    assertEquals(child3Bsn, linkChild3.getBurgerservicenummer());
    IngeschrevenPersoonHal child3IngeschrevenPersoon = getIngeschrevenPersoon(parseLong(child3Bsn));
    assertEquals(child3.getBurgerservicenummer(), child3IngeschrevenPersoon.getBurgerservicenummer());
    assertEquals(GeslachtEnum.VROUW, child3IngeschrevenPersoon.getGeslachtsaanduiding());
    assertEquals("Carola Bernadette", child3IngeschrevenPersoon.getNaam().getVoornamen());
  }
}
