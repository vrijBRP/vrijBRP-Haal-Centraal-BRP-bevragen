package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static java.lang.Long.parseLong;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.AanduidingBijHuisnummerEnum;
import org.junit.jupiter.api.Test;
import lombok.SneakyThrows;

/**
 * Functionaliteit:
 * Veld "straat" wordt gevuld met de naam openbare ruimte (11.15) wanneer die bekend is, en anders met straatnaam(11.10)
 * Veld "korteNaam" wordt gevuld met straatnaam (11.10).
 * Veld "woonplaats" wordt gevuld met de woonplaats (11.70) wanneer die bekend is, en anders met de omschrijving
 * van de gemeente van inschrijving (09.10 via tabel 33 Gemeententabel) als de verblijfplaats geen buitenlands adres
 * betreft.
 * Veld "land" wordt alleen opgenomen wanneer het een buitenlands adres betreft.
 */
public class AdresTest extends IngeschrevenPersonenResourceTest {

  @Test
  @SneakyThrows
  public void testAddresWithoutFieldsAdresRegel3AndLand() {
    var persons = getIngeschrevenPersonen(999991644L);
    var persoon = getIngeschrevenPersoon(999991644L);
    var verblijfplaats = persons.getEmbedded().getIngeschrevenpersonen().get(0).getVerblijfplaats();

    assertEquals("0518010000772703", verblijfplaats.getAdresseerbaarObjectIdentificatie());
    assertEquals("0518200000772702", verblijfplaats.getNummeraanduidingIdentificatie());
    assertEquals("0518", verblijfplaats.getGemeenteVanInschrijving().getCode());
    assertEquals("Ruychrocklaan", verblijfplaats.getStraat());
    assertNull(verblijfplaats.getLocatiebeschrijving());
    assertEquals(4, verblijfplaats.getHuisnummer());
    assertEquals("2597EN", verblijfplaats.getPostcode());
    assertEquals("'s-Gravenhage", verblijfplaats.getWoonplaats());
    assertEquals("'s-Gravenhage", verblijfplaats.getGemeenteVanInschrijving().getOmschrijving());
    assertEquals("Ruychrocklaan", verblijfplaats.getKorteNaam());
    assertEquals("Ruychrocklaan 4", verblijfplaats.getAdresregel1());
    assertEquals("Ruychrocklaan 4", persoon.getVerblijfplaats().getAdresregel1());
    assertEquals("2597EN 's-Gravenhage", verblijfplaats.getAdresregel2());
    assertNull(verblijfplaats.getAdresregel3());
    assertNull(verblijfplaats.getLand());
  }

  @Test
  @SneakyThrows
  public void testAddressWithNumberAddition() {
    var persons = getIngeschrevenPersonen(999993343L);
    var verblijfplaats = persons.getEmbedded().getIngeschrevenpersonen().get(0).getVerblijfplaats();
    assertNull(verblijfplaats.getNummeraanduidingIdentificatie());
    assertNull(verblijfplaats.getAdresseerbaarObjectIdentificatie());
    assertEquals("0363", verblijfplaats.getGemeenteVanInschrijving().getCode());
    assertEquals("Marga Klompélaan", verblijfplaats.getStraat());
    assertNull(verblijfplaats.getLocatiebeschrijving());
    assertEquals(88, verblijfplaats.getHuisnummer());
    assertEquals("2", verblijfplaats.getHuisnummertoevoeging());
    assertEquals("1067VA", verblijfplaats.getPostcode());
    assertEquals("Amsterdam", verblijfplaats.getWoonplaats());
    assertEquals("Amsterdam", verblijfplaats.getGemeenteVanInschrijving().getOmschrijving());
    assertEquals("Marga Klompélaan", verblijfplaats.getKorteNaam());
    assertEquals("Marga Klompélaan 88-2", verblijfplaats.getAdresregel1());
    assertEquals("1067VA Amsterdam", verblijfplaats.getAdresregel2());
    assertNull(verblijfplaats.getAdresregel3());
    assertNull(verblijfplaats.getLand());

    persons = getIngeschrevenPersonen(999994402L);
    verblijfplaats = persons.getEmbedded().getIngeschrevenpersonen().get(0).getVerblijfplaats();
    assertEquals("0363200000466090", verblijfplaats.getNummeraanduidingIdentificatie());
    assertEquals("0363010000990737", verblijfplaats.getAdresseerbaarObjectIdentificatie());
    assertEquals("0363", verblijfplaats.getGemeenteVanInschrijving().getCode());
    assertEquals("Vitternkade", verblijfplaats.getStraat());
    assertNull(verblijfplaats.getLocatiebeschrijving());
    assertEquals(101, verblijfplaats.getHuisnummer());
    assertEquals("II", verblijfplaats.getHuisnummertoevoeging());
    assertEquals("1060PK", verblijfplaats.getPostcode());
    assertEquals("Amsterdam", verblijfplaats.getWoonplaats());
    assertEquals("Amsterdam", verblijfplaats.getGemeenteVanInschrijving().getOmschrijving());
    assertEquals("Vitternkade", verblijfplaats.getKorteNaam());
    assertEquals("Vitternkade 101-II", verblijfplaats.getAdresregel1());
    assertEquals("1060PK Amsterdam", verblijfplaats.getAdresregel2());
    assertNull(verblijfplaats.getAdresregel3());
    assertNull(verblijfplaats.getLand());
  }

  @Test
  @SneakyThrows
  public void testAddressNotBAG() {
    var persons = getIngeschrevenPersonen(999991802L);
    var verblijfplaats = persons.getEmbedded().getIngeschrevenpersonen().get(0).getVerblijfplaats();
    assertEquals(0, parseLong(verblijfplaats.getNummeraanduidingIdentificatie()));
    assertEquals("0363", verblijfplaats.getGemeenteVanInschrijving().getCode());
    assertEquals("Zomerdijkstrtaat", verblijfplaats.getKorteNaam());
    assertEquals(17, verblijfplaats.getHuisnummer());
    assertEquals("1079WZ", verblijfplaats.getPostcode());
    assertEquals("Amsterdam", verblijfplaats.getWoonplaats());
    assertEquals("Zomerdijkstrtaat", verblijfplaats.getStraat());
    assertEquals("Zomerdijkstrtaat 17", verblijfplaats.getAdresregel1());
    assertEquals("1079WZ Amsterdam", verblijfplaats.getAdresregel2());
    assertNull(verblijfplaats.getAdresregel3());
    assertNull(verblijfplaats.getLand());
  }

  @Test
  @SneakyThrows
  public void testAddressWithAanduidingBijHuisnummer() {
    var persons = getIngeschrevenPersonen(999990482L);
    var verblijfplaats = persons.getEmbedded().getIngeschrevenpersonen().get(0).getVerblijfplaats();
    assertEquals("1681200000000215", verblijfplaats.getNummeraanduidingIdentificatie());
    assertEquals("1681010000000215", verblijfplaats.getAdresseerbaarObjectIdentificatie());
    assertEquals("1681", verblijfplaats.getGemeenteVanInschrijving().getCode());
    assertEquals("Borger-Odoorn", verblijfplaats.getGemeenteVanInschrijving().getOmschrijving());
    assertEquals("1e Exloërmond", verblijfplaats.getKorteNaam());
    assertEquals(3, verblijfplaats.getHuisnummer());
    assertNull(verblijfplaats.getAanduidingBijHuisnummer());
    assertEquals("9573PA", verblijfplaats.getPostcode());
    assertEquals("1e Exloërmond", verblijfplaats.getStraat());
    assertEquals("1e Exloërmond", verblijfplaats.getKorteNaam());
    assertEquals("Borger-Odoorn", verblijfplaats.getWoonplaats());
    assertEquals("Borger-Odoorn", verblijfplaats.getGemeenteVanInschrijving().getOmschrijving());
    assertEquals("1e Exloërmond 3", verblijfplaats.getAdresregel1());
    assertEquals("9573PA Borger-Odoorn", verblijfplaats.getAdresregel2());

    persons = getIngeschrevenPersonen(999992624L);
    verblijfplaats = persons.getEmbedded().getIngeschrevenpersonen().get(0).getVerblijfplaats();
    assertNull(verblijfplaats.getNummeraanduidingIdentificatie());
    assertEquals(AanduidingBijHuisnummerEnum.TEGENOVER, verblijfplaats.getAanduidingBijHuisnummer());
    assertEquals("tegenover Weerterbeekweg 4", verblijfplaats.getAdresregel1());
    assertEquals("6001VH Weert", verblijfplaats.getAdresregel2());
    assertNull(verblijfplaats.getAdresregel3());

    persons = getIngeschrevenPersonen(999990913L);
    verblijfplaats = persons.getEmbedded().getIngeschrevenpersonen().get(0).getVerblijfplaats();
    assertNull(verblijfplaats.getNummeraanduidingIdentificatie());
    assertEquals(AanduidingBijHuisnummerEnum.BIJ, verblijfplaats.getAanduidingBijHuisnummer());
    assertEquals(15201, verblijfplaats.getHuisnummer());
    assertEquals("2132EA", verblijfplaats.getPostcode());
    assertEquals("bij Graan voor Visch 15201", verblijfplaats.getAdresregel1());
    assertEquals("2132EA Haarlemmermeer", verblijfplaats.getAdresregel2());
    assertNull(verblijfplaats.getAdresregel3());
  }

  @Test
  @SneakyThrows
  public void testAddressAsLocatiebeschrijving() {
    var persons = getIngeschrevenPersonen(999997002L);
    var verblijfplaats = persons.getEmbedded().getIngeschrevenpersonen().get(0).getVerblijfplaats();
    assertNull(verblijfplaats.getAdresseerbaarObjectIdentificatie());
    assertEquals("Amsterdam", verblijfplaats.getGemeenteVanInschrijving().getOmschrijving());
    assertEquals("woonaak De Kabouter in Ruigoord", verblijfplaats.getLocatiebeschrijving());
    assertEquals("Amsterdam", verblijfplaats.getWoonplaats());
    assertEquals("woonaak De Kabouter in Ruigoord", verblijfplaats.getAdresregel1());
    assertEquals("Amsterdam", verblijfplaats.getAdresregel2());
    assertNull(verblijfplaats.getStraat());
    assertNull(verblijfplaats.getKorteNaam());
  }

  @Test
  @SneakyThrows
  public void testAddressInForeignCountry() {
    var persons = getIngeschrevenPersonen(999992326L);
    var verblijfplaats = persons.getEmbedded().getIngeschrevenpersonen().get(0).getVerblijfplaats();
    assertNull(verblijfplaats.getAdresseerbaarObjectIdentificatie());
    assertEquals("Olympos 387A", verblijfplaats.getAdresregel1());
    assertEquals("85700", verblijfplaats.getAdresregel2());
    assertEquals("Dodekanesos", verblijfplaats.getAdresregel3());
    assertEquals("Griekenland", verblijfplaats.getLand().getOmschrijving());
    assertNull(verblijfplaats.getStraat());
    assertNull(verblijfplaats.getKorteNaam());
    assertNull(verblijfplaats.getWoonplaats());

    var persoon = getIngeschrevenPersoon(999993896L);
    assertEquals("Street # 38 & House # 10", persoon.getVerblijfplaats().getAdresregel1());
    assertEquals("Baghdad", persoon.getVerblijfplaats().getAdresregel2());
    assertEquals("Park Al-Sadoum, Hay Al-Nidhal 103", persoon.getVerblijfplaats().getAdresregel3());
    assertEquals("5043", persoon.getVerblijfplaats().getLand().getCode());
    assertEquals("Irak", persoon.getVerblijfplaats().getLand().getOmschrijving());
    assertNull(persoon.getVerblijfplaats().getStraat());
    assertNull(persoon.getVerblijfplaats().getKorteNaam());
    assertNull(persoon.getVerblijfplaats().getWoonplaats());
  }

  @SneakyThrows
  @Test
  public void testAddressUnknown() {
    var persons = getIngeschrevenPersonen(999993586L);
    var verblijfplaats = persons.getEmbedded().getIngeschrevenpersonen().get(0).getVerblijfplaats();
    assert verblijfplaats.getVertrokkenOnbekendWaarheen();
    assertNull(verblijfplaats.getAdresseerbaarObjectIdentificatie());
    assertEquals("Onbekend", verblijfplaats.getAdresregel1());
    assertEquals("Onbekend", verblijfplaats.getAdresregel2());
    assertNull(verblijfplaats.getAdresregel3());
    assertEquals("0000", verblijfplaats.getLand().getCode());
    assertEquals("Onbekend", verblijfplaats.getLand().getOmschrijving());

    persons = getIngeschrevenPersonen(999990822);//puntadres
    verblijfplaats = persons.getEmbedded().getIngeschrevenpersonen().get(0).getVerblijfplaats();
    assert verblijfplaats.getVertrokkenOnbekendWaarheen();
    assertEquals("'s-Gravenhage", verblijfplaats.getWoonplaats());
    assertEquals("Onbekend", verblijfplaats.getAdresregel1());
    assertEquals("Onbekend", verblijfplaats.getAdresregel2());
    assertNull(verblijfplaats.getAdresregel3());
    assertNull(verblijfplaats.getLand());
  }
}
