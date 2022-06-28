package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

/**
 * Functionaliteit: Als gemeente wil ik gegevens met indicatie onjuist niet tonen.
 *   Geen enkele GBA-categorie waarin de 'indicatie onjuist' is gevuld wordt geleverd. Dit geldt voor alle groepen,  ook historische gegevens over partner, verblijfplaats, en verblijfstitel.
 *   Alleen voor de categorie 08.58 Verblijfplaats wordt de indicatie onjuist opgenomen.
 *
 *   Maar bv. ook een onjuist opgenomen categorie 06 Overlijden.
 */
public class IndicatieOnjuistTest extends IngeschrevenPersonenResourceTest {

  /**
   *  Scenario: de persoon heeft een indicatie onjuist bij de gegevensgroep Overlijden
   *       Gegeven: de te raadplegen persoon met burgerservicenummer 999999023 heeft een indicatie onjuist bij de
   *       gegevensgroep Overlijden
   *       Als de persoon wordt geraadpleegd met burgerservicenummer 999999023
   *       Dan wordt de gegevensgroep overlijden niet getoond.
   */
  @Test
  @SneakyThrows
  public void showsIncorrectDeceased() {
    //        Long bsnL = 0L;
    //        IngeschrevenPersoonHalCollectie persons = getIngeschrevenPersonen(bsnL);

  }

  /**
   * # Functionaliteit voor het tonen van onjuist verblijfsadres (alleen bij historische adressen).
   *
   *   Scenario: de persoon heeft een indicatie onjuist bij de gegevensgroep Verblijfsplaats
   *       Gegeven: de te raadplegen persoon met burgerservicenummer 999999023 heeft een indicatie onjuist bij de gegevensgroep Verblijfsplaats
   *       Als de persoon wordt geraadpleegd met burgerservicenummer 999999023
   *       Dan wordt de gegevensgroep verblijfsplaats inclusief indicatie onjuist getoond.
   */
  @Test
  @SneakyThrows
  public void showsIncorrectAddress() {
    //        Long bsnL = 0L;
    //        IngeschrevenPersoonHalCollectie persons = getIngeschrevenPersonen(bsnL);

  }
}
