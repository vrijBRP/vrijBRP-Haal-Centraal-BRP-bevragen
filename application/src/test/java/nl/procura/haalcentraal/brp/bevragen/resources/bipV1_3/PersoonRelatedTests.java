package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.*;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import spring.PersonRecordSource;

// TODO verder uitwerken
@Slf4j
@SpringBootTest
@ContextConfiguration(initializers = PersonRecordSource.class)
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
@AutoConfigureMockMvc
public class PersoonRelatedTests extends IngeschrevenPersonenResourceTest {

  @Test
  @SneakyThrows
  void testGetIngeschrevenPersonen() {
    var persoon = getIngeschrevenPersoon(999993069L);
    persoon.setLinks(new IngeschrevenPersoonLinks());
    assertEquals("999993069", persoon.getBurgerservicenummer());
    assertEquals("1421051595", persoon.getaNummer());
    assertEquals(GeslachtEnum.VROUW, persoon.getGeslachtsaanduiding());
    assertTrue(persoon.getLeeftijd() >= 50);
    assertEquals("1994-03-10", persoon.getDatumEersteInschrijvingGBA().getDatum().toString());
    assertNull(persoon.getInOnderzoek()); // Getest in InOnderzoekTests
  }

  @Test
  public void testMustReturnNaam() {
    var persoon = getIngeschrevenPersoon(999993069L);
    var naam = persoon.getNaam();
    assertEquals("Geachte mevrouw Van 't Wiel", naam.getAanhef());
    assertEquals("J. van 't Wiel", naam.getAanschrijfwijze());
    assertNull(naam.getAdellijkeTitelPredikaat());
    assertNull(naam.getRegelVoorafgaandAanAanschrijfwijze());
    assertEquals("mevrouw Van 't Wiel", naam.getGebruikInLopendeTekst());
    assertEquals("eigen", naam.getAanduidingNaamgebruik().toString());
    assertEquals("Wiel", naam.getGeslachtsnaam());
    assertEquals("J.", naam.getVoorletters());
    assertEquals("José", naam.getVoornamen());
    assertEquals("van 't", naam.getVoorvoegsel());
    assertNull(naam.getInOnderzoek()); // Getest in InOnderzoekTest. Geen situatie in DB.

    persoon = getIngeschrevenPersoon(999994669L);
    assertEquals("Jonkvrouw", persoon.getNaam()
        .getAdellijkeTitelPredikaat()
        .getOmschrijving());
    assertEquals("Hoogwelgeboren vrouwe", persoon.getNaam().getAanhef());
    assertEquals("Jonkvrouw Ż.Å.Đ. 's Streeveld-gravin te Schaars", persoon.getNaam().getAanschrijfwijze());
    assertNull(persoon.getNaam().getRegelVoorafgaandAanAanschrijfwijze());
    assertEquals("jonkvrouw 'S Streeveld-gravin te Schaars", persoon.getNaam().getGebruikInLopendeTekst());
  }

  @Test
  public void testMustReturnGeboorte() {
    var persoon = getIngeschrevenPersoon(999993069L);
    var geboorte = persoon.getGeboorte();
    assertEquals("1971-01-07", geboorte.getDatum().getDatum().toString());
    assertEquals("Utrecht", geboorte.getPlaats().getOmschrijving());
    assertEquals("0344", geboorte.getPlaats().getCode());
    assertEquals("Nederland", geboorte.getLand().getOmschrijving());
    assertEquals("6030", geboorte.getLand().getCode());
    assertNull(geboorte.getInOnderzoek()); // Getest in InOnderzoekTest. Geen situatie in DB
  }

  @Test
  public void testMustReturnGeheimhouding() {
    assertTrue(getIngeschrevenPersoon(999995169).getGeheimhoudingPersoonsgegevens());
  }

  @Test
  public void testMustReturnKiesrecht() {
    var kiesrecht = getIngeschrevenPersoon(999990524L).getKiesrecht();
    assertNull(kiesrecht.getEuropeesKiesrecht());
    assertTrue(kiesrecht.getUitgeslotenVanKiesrecht());
    assertNull(kiesrecht.getEinddatumUitsluitingKiesrecht());
    assertNull(kiesrecht.getEinddatumUitsluitingEuropeesKiesrecht());
  }

  @Test
  public void testMustReturnNationaliteiten() {
    var persoon = getIngeschrevenPersoon(999993069L);
    var nationaliteit = persoon.getNationaliteiten().get(0);
    assertNull(nationaliteit.getAanduidingBijzonderNederlanderschap());
    assertEquals("Nederlandse", nationaliteit.getNationaliteit().getOmschrijving());
    assertEquals("0001", nationaliteit.getNationaliteit().getCode());
    assertEquals("1971-01-07", nationaliteit.getDatumIngangGeldigheid().getDatum().toString());
  }

  @Test
  public void testMustReturnOpschortingBijhouding() {
    var opsch = getIngeschrevenPersoon(999990147L).getOpschortingBijhouding();
    assertEquals("2018-05-26", opsch.getDatum().getDatum().toString());
    assertEquals(RedenOpschortingBijhoudingEnum.OVERLIJDEN, opsch.getReden());
  }

  @Test
  public void testMustReturnOverlijden() {
    var overl = getIngeschrevenPersoon(999990147L).getOverlijden();
    assertTrue(overl.getIndicatieOverleden());
    assertEquals("2018-05-26", overl.getDatum().getDatum().toString());
    assertEquals("Rotterdam", overl.getPlaats().getOmschrijving());
    assertEquals("Nederland", overl.getLand().getOmschrijving());
    assertNull(overl.getInOnderzoek()); // Getest in inOnderzoekTests. Geen situatie in DB.
  }

  @Test
  public void testMustReturnVerblijfplaats() {
    var vb = getIngeschrevenPersoon(999993069L).getVerblijfplaats();
    assertEquals("0518200270021001", vb.getNummeraanduidingIdentificatie());
    assertEquals(SoortAdresEnum.WOONADRES, vb.getFunctieAdres());
    assertEquals("Marktweg", vb.getStraat());
    assertEquals("Marktweg", vb.getKorteNaam());
    assertEquals("1989-10-01", vb.getDatumAanvangAdreshouding().getDatum().toString());
    assertEquals("1989-10-01", vb.getDatumInschrijvingInGemeente().getDatum().toString());
    assertEquals("'s-Gravenhage", vb.getGemeenteVanInschrijving().getOmschrijving());
    assertEquals("Marktweg 21", vb.getAdresregel1());
    assertEquals("2525JA 's-Gravenhage", vb.getAdresregel2());
    assertNull(vb.getInOnderzoek()); // Getest in InOnderzoekTests
    assertEquals(21, vb.getHuisnummer());
    assertEquals("2525JA", vb.getPostcode());
    assertEquals("'s-Gravenhage", vb.getWoonplaats());

    assertEquals("0518010270021001", vb.getAdresseerbaarObjectIdentificatie());
    assertNull(vb.getAanduidingBijHuisnummer());
    assertEquals("0518200270021001", vb.getNummeraanduidingIdentificatie());
    assertEquals("woonadres", vb.getFunctieAdres().toString());
    assertNull(vb.getIndicatieVestigingVanuitBuitenland());
    assertNull(vb.getLocatiebeschrijving());
    assertEquals("Marktweg", vb.getKorteNaam());
    assertNull(vb.getVanuitVertrokkenOnbekendWaarheen());
    assertEquals("1989-10-01", vb.getDatumAanvangAdreshouding().getDatum().toString());
    assertEquals("2009-11-01", vb.getDatumIngangGeldigheid().getDatum().toString());
    assertEquals("1989-10-01", vb.getDatumInschrijvingInGemeente().getDatum().toString());
    assertNull(vb.getDatumVestigingInNederland());
    assertEquals("'s-Gravenhage", vb.getGemeenteVanInschrijving().getOmschrijving());
    assertNull(vb.getLandVanwaarIngeschreven());
    assertEquals("Marktweg 21", vb.getAdresregel1());
    assertEquals("2525JA 's-Gravenhage", vb.getAdresregel2());
    assertNull(vb.getAdresregel3());
    assertNull(vb.getVanuitVertrokkenOnbekendWaarheen());
    assertNull(vb.getLand());
    //    assertEquals("", vb.getInOnderzoek());//FIXME
    assertEquals("Marktweg", vb.getStraat());
    assertEquals(21, vb.getHuisnummer());
    assertNull(vb.getHuisletter());
    assertNull(vb.getHuisnummertoevoeging());
    assertNull(vb.getAanduidingBijHuisnummer());
    assertEquals("2525JA", vb.getPostcode());
    assertEquals("'s-Gravenhage", vb.getWoonplaats());

    vb = getIngeschrevenPersoon(999992326L).getVerblijfplaats();
    assertNull(vb.getAdresseerbaarObjectIdentificatie());
    assertNull(vb.getAanduidingBijHuisnummer());
    assertNull(vb.getNummeraanduidingIdentificatie());
    assertNull(vb.getFunctieAdres());
    assertNull(vb.getIndicatieVestigingVanuitBuitenland());
    assertNull(vb.getLocatiebeschrijving());
    assertNull(vb.getKorteNaam());
    assertNull(vb.getVanuitVertrokkenOnbekendWaarheen());
    assertNull(vb.getDatumAanvangAdreshouding());
    assertEquals("2010-01-01", vb.getDatumIngangGeldigheid().getDatum().toString());
    assertEquals("1990-04-01", vb.getDatumInschrijvingInGemeente().getDatum().toString());
    assertNull(vb.getDatumVestigingInNederland());
    assertNull(vb.getGemeenteVanInschrijving());
    assertNull(vb.getLandVanwaarIngeschreven());
    assertEquals("Olympos 387A", vb.getAdresregel1());
    assertEquals("85700", vb.getAdresregel2());
    assertEquals("Dodekanesos", vb.getAdresregel3());
    assertNull(vb.getVanuitVertrokkenOnbekendWaarheen());
    assertEquals("Griekenland", vb.getLand().getOmschrijving());

  }

  @Test
  public void testMustReturnGezagsverhouding() {
    var gv = getIngeschrevenPersoon(999992144L).getGezagsverhouding();
    assertNull(gv.getInOnderzoek()); // Getest in inOnderzoekTests. Geen situatie in DB.
    //        assertFalse(gv.getIndicatieCurateleRegister()); //FIXME: nullpointer return true or null? but not false?
    assertNull(gv.getIndicatieCurateleRegister());
    assertEquals(IndicatieGezagMinderjarigeEnum.OUDER1_EN_OUDER2, gv.getIndicatieGezagMinderjarige());

    gv = getIngeschrevenPersoon(999991061L).getGezagsverhouding();
    assertTrue(gv.getIndicatieCurateleRegister());
  }

  //TODO Verblijfstitel not yet implemented
  public void mustReturnVerblijfstitel() {
  }

  //TODO Reisdocumenten not yet implemented
  public void mustReturnReisdocumenten() {
  }

  @Test
  public void testMustReturnRelatives() {
    var persoon = getIngeschrevenPersoon(999993069L);
    assertEquals(2, persoon.getEmbedded().getKinderen().size());
    assertEquals(2, persoon.getLinks().getKinderen().size());

    assertEquals(2, persoon.getEmbedded().getOuders().size());
    assertEquals(2, persoon.getLinks().getOuders().size());

    assertEquals(1, persoon.getEmbedded().getPartners().size());
    assertEquals(1, persoon.getLinks().getPartners().size());
  }

  @Test
  public void testMustReturnParentValues() {
    var ouders = getIngeschrevenPersoon(999991139L).getEmbedded().getOuders();
    var ouder = ouders.get(0);
    assertNull(ouder.getInOnderzoek()); // Getest in InOnderzoekTests
    assertEquals("1911-11-11", ouder.getDatumIngangFamilierechtelijkeBetrekking().getDatum().toString());
    assertEquals(GeslachtEnum.VROUW, ouder.getGeslachtsaanduiding());

    var naam = ouder.getNaam();
    assertEquals("Prins de Lignac", naam.getGeslachtsnaam());
    assertEquals("H.U.G.", naam.getVoorletters());
    assertEquals("Héloïse Uriëlle Germaîne", naam.getVoornamen());

    var geboorte = ouder.getGeboorte();
    assertEquals("1891-11-11", geboorte.getDatum().getDatum().toString());
    assertEquals("Bayenghem-Lès-Eperlecques", geboorte.getPlaats().getOmschrijving());
    assertEquals("Frankrijk", geboorte.getLand().getOmschrijving());

    ouder = ouders.get(1);
    assertEquals(OuderAanduidingEnum.OUDER2, ouder.getOuderAanduiding());
    assertEquals(GeslachtEnum.MAN, ouder.getGeslachtsaanduiding());

    naam = ouder.getNaam();
    assertEquals("de", naam.getVoorvoegsel());

    ouder = getIngeschrevenPersoon(999995170).getEmbedded().getOuders().get(0);
    assertEquals("999993069", ouder.getBurgerservicenummer());

    ouder = getIngeschrevenPersoon(999994669).getEmbedded().getOuders().get(1);
    var tp = ouder.getNaam().getAdellijkeTitelPredikaat();
    assertEquals("Jonkheer", tp.getOmschrijving());

    ouders = getIngeschrevenPersoon(999994906L).getEmbedded().getOuders();
    ouder = ouders.get(0);
    assertNull(ouder.getGeheimhoudingPersoonsgegevens());
    assertEquals("999993677", ouder.getBurgerservicenummer());

    ouder = ouders.get(1);
    assertTrue(ouder.getGeheimhoudingPersoonsgegevens());
    assertEquals("999995169", ouder.getBurgerservicenummer());
  }

  @Test
  public void testMustReturnPartnerValues() {
    var partner = getIngeschrevenPersoon(999995169L).getEmbedded().getPartners().get(0);
    assertNull(partner.getGeheimhoudingPersoonsgegevens());
    assertEquals("999993677", partner.getBurgerservicenummer());
    assertNull(partner.getInOnderzoek()); // Getest in InOnderzoekTests
    assertEquals(GeslachtEnum.VROUW, partner.getGeslachtsaanduiding());

    var naam = partner.getNaam();
    assertEquals("Wiel", naam.getGeslachtsnaam());
    assertEquals("T.", naam.getVoorletters());
    assertEquals("Truus", naam.getVoornamen());
    assertNull(naam.getInOnderzoek()); // Getest in InOnderzoekTests

    var geboorte = partner.getGeboorte();
    assertEquals("1960-09-12", geboorte.getDatum().getDatum().toString());
    assertEquals("Utrecht", geboorte.getPlaats().getOmschrijving());
    assertEquals("Nederland", geboorte.getLand().getOmschrijving());

    assertEquals(SoortVerbintenisEnum.HUWELIJK, partner.getSoortVerbintenis());
    var verbintenis = partner.getAangaanHuwelijkPartnerschap();
    assertEquals("1982-11-20", verbintenis.getDatum().getDatum().toString());
    assertEquals("Rotterdam", verbintenis.getPlaats().getOmschrijving());
    assertEquals("Nederland", verbintenis.getLand().getOmschrijving());
    assertNull(verbintenis.getInOnderzoek()); // Getest in InOnderzoekTests

    partner = getIngeschrevenPersoon(999993677L).getEmbedded().getPartners().get(0);
    assertTrue(partner.getGeheimhoudingPersoonsgegevens());
  }

  @Test
  public void testMustReturnChildValues() {
    var kind = getIngeschrevenPersoon(999991188L).getEmbedded().getKinderen().get(0);
    assertEquals("999995169", kind.getBurgerservicenummer());
    assertTrue(kind.getLeeftijd() >= 62);
    assertNull(kind.getInOnderzoek()); // Getest in InOnderzoekTests
    assertTrue(kind.getGeheimhoudingPersoonsgegevens());

    var naam = kind.getNaam();
    assertEquals("Bilgiç", naam.getGeslachtsnaam());
    assertEquals("Ö.", naam.getVoorletters());
    assertEquals("Özlem", naam.getVoornamen());
    assertNull(naam.getInOnderzoek()); // Getest in InOnderzoekTests

    var geboorte = kind.getGeboorte();
    assertEquals("1959-01-13", geboorte.getDatum().getDatum().toString());
    assertEquals("Belgrado", geboorte.getPlaats().getOmschrijving());
    assertEquals("Joegoslavië", geboorte.getLand().getOmschrijving());
    assertNull(geboorte.getInOnderzoek()); // Getest in InOnderzoekTests
  }
}
