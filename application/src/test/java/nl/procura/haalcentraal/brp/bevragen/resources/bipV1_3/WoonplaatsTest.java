package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import org.junit.jupiter.api.Test;
import lombok.SneakyThrows;
import static java.lang.Long.parseLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Functionaliteit: Woonplaats wordt altijd gevuld voor een Nederlandse verblijfplaats
 *   In LO GBA wordt voorgeschreven dat voor een verblijfplaats de woonplaats (samen met naam openbare ruimte en BAG identificaties) alleen wordt gevuld wanneer het een BAG adres is. Dus al deze gegevens zijn gevuld of geen van deze gegevens hebben een waarde.
 *
 *   In de API moet woonplaats voor een Nederlandse verblijfplaats altijd worden gevuld, zodat een gebruiker bij adressering maar op één plek hoeft te kijken voor de plaats in de adressering.
 *
 *   ALS verblijfplaats.woonplaats leeg is
 *   EN verblijfplaats.straatnaam heeft een waarde OF verblijfplaats.locatiebeschrijving heeft een waarde
 *   DAN wordt verblijfplaats.woonplaats gevuld met de omschrijving van verblijfplaats.gemeenteVanInschrijving
 *
 *   Consequentie is dat woonplaats niet wordt gevuld wanneer het een verblijfplaats in het buitenland betreft.
 */
public class WoonplaatsTest extends IngeschrevenPersonenResourceTest {

  /**
   *  Scenario: persoon heeft BAG adres met woonplaats gevuld
   *     Gegeven ingeschreven persoon met burgerservicenummer 999992934 heeft in de registratie element 08.11.70 met waarde "Scharwoude"
   *     Als ingeschreven persoon met burgerservicenummer 999993653 wordt geraadpleegd
   *     Dan is in het antwoord verblijfplaats.woonplaatsnaam gelijk aan "Scharwoude"
   */
  @Test
  @SneakyThrows
  public void testFindBAGaddressAndWoonplaats() {
    var persoon = getIngeschrevenPersoon(999992934);
    assertEquals("Scharwoude", persoon.getVerblijfplaats().getWoonplaats());
  }

  /**
   *   Scenario: persoon heeft niet-BAG adres in Nederland
   *     Gegeven ingeschreven persoon met burgerservicenummer 999990482 heeft in de registratie geen waarde voor element 08.11.70
   *     En element 08.11.10 heeft een waarde
   *     En element 09.09.10 heeft de waarde "1681"
   *     En in de landelijke tabel 33 (gemeententabel) komt code "1681" voor met omschrijving "Borger-Odoorn"
   *     Als ingeschreven persoon met burgerservicenummer 999990482 wordt geraadpleegd
   *     Dan is in het antwoord verblijfplaats.woonplaatsnaam gelijk aan "Borger-Odoorn"
   */
  @Test
  @SneakyThrows
  public void testFindNonBAGaddressAndWoonplaats() {
    var persoon = getIngeschrevenPersoon(999990482);
    assertEquals("1681200000000215", persoon.getVerblijfplaats().getNummeraanduidingIdentificatie());//het is dus wel een bag adres
    assertEquals("Borger-Odoorn", persoon.getVerblijfplaats().getWoonplaats());
    assertEquals("1e Exloërmond", persoon.getVerblijfplaats().getStraat());
    assertEquals("1e Exloërmond", persoon.getVerblijfplaats().getKorteNaam());
    assertEquals("1681", persoon.getVerblijfplaats().getGemeenteVanInschrijving().getCode());
    assertEquals("Borger-Odoorn", persoon.getVerblijfplaats().getGemeenteVanInschrijving().getOmschrijving());

    persoon = getIngeschrevenPersoon(999991802L);
    assertEquals(0, parseLong(persoon.getVerblijfplaats().getAdresseerbaarObjectIdentificatie()));//dit is geen bag adres
    assertEquals("Amsterdam", persoon.getVerblijfplaats().getWoonplaats());
    assertEquals("Zomerdijkstrtaat", persoon.getVerblijfplaats().getStraat());
    assertEquals("Zomerdijkstrtaat", persoon.getVerblijfplaats().getKorteNaam());
    assertEquals("0363", persoon.getVerblijfplaats().getGemeenteVanInschrijving().getCode());
    assertEquals("Amsterdam", persoon.getVerblijfplaats().getGemeenteVanInschrijving().getOmschrijving());
  }

  /**
   *   Scenario: persoon heeft een locatiebeschrijving voor de verblijfplaats
   *     Gegeven ingeschreven persoon met burgerservicenummer 000009921 heeft in de registratie geen waarde voor element 08.11.70
   *     En element 08.11.10 heeft geen waarde
   *     En element 08.12.10 heeft een waarde
   *     En element 09.09.10 heeft de waarde "0599"
   *     En in de landelijke tabel 33 (gemeententabel) komt code "0599" voor met omschrijving "Rotterdam"
   *     Als ingeschreven persoon met burgerservicenummer 000009921 wordt geraadpleegd
   *     Dan is in het antwoord verblijfplaats.woonplaatsnaam gelijk aan "Rotterdam"
   */
  @Test
  @SneakyThrows
  public void testFindAddressBasedOnLocationDescription() {
    var persoon = getIngeschrevenPersoon(9921);
    assertNull(persoon.getVerblijfplaats().getNummeraanduidingIdentificatie());
    assertEquals("Woonboot in de Grote Sloot", persoon.getVerblijfplaats().getLocatiebeschrijving());
    assertEquals("0518", persoon.getVerblijfplaats().getGemeenteVanInschrijving().getCode());
    assertEquals("'s-Gravenhage", persoon.getVerblijfplaats().getGemeenteVanInschrijving().getOmschrijving());
    assertEquals("'s-Gravenhage", persoon.getVerblijfplaats().getWoonplaats());
  }

  /**   
   * Scenario: persoon verblijft in het buitenland
   *     Gegeven ingeschreven persoon met burgerservicenummer 999993483 heeft in de registratie geen waarde voor element 08.11.70
   *     En element 08.11.10 heeft geen waarde
   *     En element 08.12.10 heeft geen waarde
   *     En element 08.13.10 heeft een waarde
   *     Als ingeschreven persoon met burgerservicenummer 000009921 wordt geraadpleegd
   *     Dan komt in het antwoord verblijfplaats.woonplaatsnaam niet voor met een waarde
   */
  @Test
  @SneakyThrows
  public void testFindForeignAddress() {
    var persoon = getIngeschrevenPersoon(999993483);
    assertNull(persoon.getVerblijfplaats().getWoonplaats());
    assertNull(persoon.getVerblijfplaats().getStraat());
    assertNull(persoon.getVerblijfplaats().getLocatiebeschrijving());
    assertEquals("België", persoon.getVerblijfplaats().getLand().getOmschrijving());
  }
}
