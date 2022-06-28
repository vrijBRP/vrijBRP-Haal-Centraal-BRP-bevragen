package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

/**
 * Functionaliteit: Als gemeente wil ik de juiste en consistent naamgebruik in een lopende tekst
 * Attribuut gebruikInLopendeTekst bij een ingeschreven persoon wordt gevuld door de provider om op deze wijze op
 * eenduidige wijze een persoon te kunnen benoemen.
 * Dit attribuut kan worden gebruikt in bijvoorbeeld een zin in een brief als "In uw brief van 12 mei jongstleden,
 * geeft u het overlijden van uw vader, de heer in het Veld, aan.", waarbij "de heer in het Veld" gehaald is uit
 * attribuut gebruikInLopendeTekst.
 */
public class GebruikInLopendeTekstTest extends IngeschrevenPersonenResourceTest {

  /**
   *  Scenario: meerdere actuele relaties
   *     Gegeven de ingeschreven persoon de heer F.C. Groen is getrouwd in 1958 met Geel
   *     En de ingeschreven persoon is getrouwd in 1961 met Roodt
   *     En geen van beide relaties is beëindigd
   *     En de ingeschreven persoon heeft aanduidingAanschrijving='V'
   *     Als de ingeschreven persoon wordt geraadpleegd
   *     Dan is in het antwoord naam.gebruikInLopendeTekst=de heer Geel-Groen
   */
  @Test
  @SneakyThrows
  public void checkUsageInTextWithPluralRelations() {
    //        Long bsnL = 0L;
    //        IngeschrevenPersoonHalCollectie persons = getIngeschrevenPersonen(bsnL);

  }

  /**
   *   Scenario: meerdere ontbonden relaties
   *     Gegeven de ingeschreven persoon de heer J. Wit is getrouwd in 1958 met Geel
   *     En de ingeschreven persoon is getrouwd in 1961 met Roodt
   *     En het huwelijk met Geel is ontbonden in 1960
   *     En het huwelijk met Roodt is ontbonden in 2006
   *     En de ingeschreven persoon heeft aanduidingAanschrijving='V'
   *     Als de ingeschreven persoon wordt geraadpleegd
   *     Dan is in het antwoord naam.aanschrijfwijze=de heer Roodt-Wit
   */
  @Test
  @SneakyThrows
  public void checkUsageInTextWithPluralFinishedRelationsI() {
    //        Long bsnL = 0L;
    //        IngeschrevenPersoonHalCollectie persons = getIngeschrevenPersonen(bsnL);

  }

  /**
   *     Gegeven de ingeschreven persoon de heer J. Wit is getrouwd in 1958 met Zwart
   *     En de ingeschreven persoon is getrouwd in 1961 met Blaauw
   *     En het huwelijk met Blaauw is ontbonden in 1983
   *     En het huwelijk met Zwart is ontbonden in 2006
   *     En de ingeschreven persoon heeft aanduidingAanschrijving='V'
   *     Als de ingeschreven persoon wordt geraadpleegd
   *     Dan is in het antwoord naam.aanhef=de heer Zwart-Wit
   */
  @Test
  @SneakyThrows
  public void checkUsageInTextWithPluralFinishedRelationsII() {
    //        Long bsnL = 0L;
    //        IngeschrevenPersoonHalCollectie persons = getIngeschrevenPersonen(bsnL);

  }
}
