package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3.IngeschrevenPersonenParameters.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/*
  Functionaliteit: Een zoekvraag levert alleen personen op die nog in leven zijn
  De gebruiker kan ook overlede personen vinden door gebruik van de parameter inclusiefoverledenpersonen.

  Achtergrond:
  Gegeven er is een persoon met geslachtsnaam "Groenen", gebooortedatum "1983-05-26", postcode "2595AK" en huisnummer 21 en nummeraanduidingIdentificatie "689047857696734"
  En deze persoon is overleden op de datum "2018-01-23"
  En er is ten minste één andere ingeschreven persoon met geslachtsnaam "Groenen" en geboortedatum "1983-05-26" die niet overleden is
  En er is ten minste één andere ingeschreven persoon met postcode "2595AK" en huisnummer 21 die niet overleden is
  En er is ten minste één andere ingeschreven persoon met nummeraanduidingIdentificatie "689047857696734" die niet overleden is
 */
public class OverledenPersonenTest extends IngeschrevenPersonenResourceTest {

  /*
  * Is deceased
  */
  @Test
  public void mustReturnIndicationDeceased() {
    var persoon = getIngeschrevenPersoon(999991139);
    assertTrue(persoon.getOverlijden().getIndicatieOverleden());
    assertNull(persoon.getOverlijden().getDatum().getDatum());
    assertEquals(2008, persoon.getOverlijden().getDatum().getJaar());
    assertEquals("7028", persoon.getOverlijden().getLand().getCode());
    assertEquals("Polen", persoon.getOverlijden().getLand().getOmschrijving());
    assertNull(persoon.getOverlijden().getPlaats().getCode());
    assertEquals("Łódź", persoon.getOverlijden().getPlaats().getOmschrijving());
  }

  /**
  * Not Death indication
  */
  @Test
  public void mustNotReturnIndicationDeceased() {
    // Death is corrected
    var persoon = getIngeschrevenPersoon(999994669);
    assertNull(persoon.getOverlijden());

    // Alive
    persoon = getIngeschrevenPersoon(999992545);
    assertNull(persoon.getOverlijden());
  }

  /**
  * Scenario: Default levert een zoekvraag alleen personen op die nog in leven zijn
  * Als ingeschreven personen gezocht worden met ?naam__geslachtsnaam=groenen&geboorte__datum=1983-05-26
  * Dan is in elke van de gevonden ingeschrevenpersonen attribuut overlijden niet aanwezig
  * Als ingeschreven personen gezocht worden met ?verblijfplaats__postcode=2595AK&verblijfplaats__huisnummer=21
  * Dan is in elke van de gevonden ingeschrevenpersonen attribuut overlijden niet aanwezig
  * Als ingeschreven personen gezocht worden met ?veblijfplaats__nummeraanduidingIdentificatie=689047857696734
  * Dan is in elke van de gevonden ingeschrevenpersonen attribuut overlijden niet aanwezig
  */
  @Test
  public void mustReturnOnlyLivingPeople() {
    var params = new IngeschrevenPersonenTestParams()
        .bsns(999994839L)
        .param(NAAM__GESLACHTSNAAM, "Homeros")
        .param(NAAM__VOORNAMEN, "Menelaos");

    var collectie = getIngeschrevenPersonen(params);
    assertTrue(collectie.getEmbedded().getIngeschrevenpersonen().isEmpty());
  }

  /**
  * Scenario: Met parameter inclusiefoverledenpersonen=true worden ook overleden personen gezocht
  * Als ingeschreven personen gezocht worden met ?naam__geslachtsnaam=groenen&geboorte__datum=1983-05-26&inclusiefoverledenpersonen=true
  * Dan wordt de ingeschreven persoon gevonden met overlijden.datum.datum=2018-01-23
  * En wordt de ingeschreven persoon gevonden zonder veld overlijden
  * Als ingeschreven personen gezocht worden met ?verblijfplaats__postcode=2595AK&verblijfplaats__huisnummer=21&inclusiefoverledenpersonen=true
  * Dan wordt de ingeschreven persoon gevonden met overlijden.datum.datum=2018-01-23
  * En wordt de ingeschreven persoon gevonden veld overlijden
  * Als ingeschreven personen gezocht worden met ?veblijfplaats__nummeraanduidingIdentificatie=689047857696734&inclusiefoverledenpersonen=true
  * Dan wordt de ingeschreven persoon gevonden met overlijden.datum.datum=2018-01-23
  * En wordt de ingeschreven persoon gevonden veld overlijden
  * Als ingeschreven personen gezocht worden met ?naam__geslachtsnaam=groenen&geboorte__datum=1983-05-26&inclusiefoverledenpersonen=false
  * Dan is in elke van de gevonden ingeschrevenpersonen attribuut overlijden niet aanwezig
  */
  @Test
  public void mustReturnAlsoDeceasedPeople() {
    var params = new IngeschrevenPersonenTestParams()
        .bsns(999994839L)
        .param(NAAM__GESLACHTSNAAM, "Homeros")
        .param(NAAM__VOORNAMEN, "Menelaos")
        .param(INCLUSIEFOVERLEDENPERSONEN, "True");

    var collectie = getIngeschrevenPersonen(params);
    assertEquals(1, collectie.getEmbedded().getIngeschrevenpersonen().size());
  }
}
