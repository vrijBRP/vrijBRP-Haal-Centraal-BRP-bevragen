package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
* Als de persoon een adellijke titel of predikaat heeft, wordt de aanhef bepaald op basis van adellijkeTitel_predikaat
 * en de geslachtsaanduiding volgens de volgende tabel:
        *   | adellijkeTitel_predikaat | geslachtsaanduiding | Aanhef                |
        *   | baron, barones           | man                 | hoogwelgeboren heer   |
        *   | baron, barones           | vrouw               | hoogwelgeboren vrouwe |
        *   | graaf, gravin            | man                 | hooggeboren heer      |
        *   | graaf, gravin            | vrouw               | hooggeboren vrouwe    |
        *   | hertog, hertogin         | man                 | hoogwelgeboren heer   |
        *   | hertog, hertogin         | vrouw               | hoogwelgeboren vrouwe |
        *   | jonkheer, jonkvrouw      | man                 | hoogwelgeboren heer   |
        *   | jonkheer, jonkvrouw      | vrouw               | hoogwelgeboren vrouwe |
        *   | markies, markiezin       | man                 | hoogwelgeboren heer   |
        *   | markies, markiezin       | vrouw               | hoogwelgeboren vrouwe |
        *   | prins, prinses           | man                 | hoogheid              |
        *   | prins, prinses           | vrouw               | hoogheid              |
        *   | prins, prinses           | onbekend            | hoogheid              |
        *   | ridder                   | man                 | hoogwelgeboren heer   |
        *   | ridder                   | vrouw               | hoogwelgeboren vrouwe |
   */

public class AdelijkeTitelGebruikTests extends IngeschrevenPersonenResourceTest {

  @Test
  @SneakyThrows
  public void testSalutationAndAddressingForPersonWithTitleOrPredicate() {

    var persoon = getIngeschrevenPersoon(999991553L);
    assertEquals("Sigurðardóttir", persoon.getNaam().getGeslachtsnaam());
    assertEquals("vrouw", persoon.getGeslachtsaanduiding().toString());
    assertEquals("partner", persoon.getNaam().getAanduidingNaamgebruik().toString());
    assertNull(persoon.getNaam().getAdellijkeTitelPredikaat());
    assertEquals("Graaf",
        persoon.getEmbedded().getPartners().get(0).getNaam().getAdellijkeTitelPredikaat().getOmschrijving());
    assertEquals("Hooggeboren vrouwe", persoon.getNaam().getAanhef());
    assertNull(persoon.getNaam().getRegelVoorafgaandAanAanschrijfwijze());
    assertEquals("G.S. gravin de Marchant et d'Ansembourg", persoon.getNaam().getAanschrijfwijze());
    assertEquals("gravin de Marchant et d'Ansembourg", persoon.getNaam().getGebruikInLopendeTekst());

    persoon = getIngeschrevenPersoon(999994669L);
    assertEquals("eigen_partner", persoon.getNaam().getAanduidingNaamgebruik().toString());
    assertEquals("vrouw", persoon.getGeslachtsaanduiding().toString());
    assertEquals("Jonkvrouw", persoon.getNaam().getAdellijkeTitelPredikaat().getOmschrijving());
    assertEquals("Gravin",
        persoon.getEmbedded().getPartners().get(0).getNaam().getAdellijkeTitelPredikaat().getOmschrijving());
    assertEquals("Hoogwelgeboren vrouwe", persoon.getNaam().getAanhef());
    assertNull(persoon.getNaam().getRegelVoorafgaandAanAanschrijfwijze());
    assertEquals("jonkvrouw Ż.Å.Đ. 's Streeveld-gravin te Schaars", persoon.getNaam().getAanschrijfwijze());
    assertEquals("jonkvrouw 's Streeveld-gravin te Schaars", persoon.getNaam().getGebruikInLopendeTekst());

    persoon = getIngeschrevenPersoon(999990305L);
    assertEquals("eigen_partner", persoon.getNaam().getAanduidingNaamgebruik().toString());
    assertEquals("vrouw", persoon.getGeslachtsaanduiding().toString());
    assertEquals("Jonkvrouw", persoon.getNaam().getAdellijkeTitelPredikaat().getOmschrijving());
    assertEquals("Gravin",
        persoon.getEmbedded().getPartners().get(0).getNaam().getAdellijkeTitelPredikaat().getOmschrijving());
    assertEquals("Hoogwelgeboren vrouwe", persoon.getNaam().getAanhef());
    assertNull(persoon.getNaam().getRegelVoorafgaandAanAanschrijfwijze());
    assertEquals("jonkvrouw Ż.Å.Đ. 's Streeveld-gravin te Schaars", persoon.getNaam().getAanschrijfwijze());
    assertEquals("jonkvrouw 's Streeveld-gravin te Schaars", persoon.getNaam().getGebruikInLopendeTekst());

    persoon = getIngeschrevenPersoon(999994827L);
    assertEquals("eigen", persoon.getNaam().getAanduidingNaamgebruik().toString());
    assertEquals("vrouw", persoon.getGeslachtsaanduiding().toString());
    assertEquals("Jonkvrouw", persoon.getNaam().getAdellijkeTitelPredikaat().getOmschrijving());
    assertEquals("Hoogwelgeboren vrouwe", persoon.getNaam().getAanhef());
    assertNull(persoon.getNaam().getRegelVoorafgaandAanAanschrijfwijze());
    assertEquals("jonkvrouw Ż.Å.Đ. s Slechte", persoon.getNaam().getAanschrijfwijze());
    assertEquals("jonkvrouw s Slechte", persoon.getNaam().getGebruikInLopendeTekst());

    persoon = getIngeschrevenPersoon(999994037L);
    assertEquals("Hom", persoon.getNaam().getGeslachtsnaam());
    assertEquals("vrouw", persoon.getGeslachtsaanduiding().toString());
    assertEquals("partner", persoon.getNaam().getAanduidingNaamgebruik().toString());
    assertEquals("Baron",
        persoon.getEmbedded().getPartners().get(0).getNaam().getAdellijkeTitelPredikaat().getOmschrijving());
    assertEquals("Barones", persoon.getNaam().getAdellijkeTitelPredikaat().getOmschrijving());
    assertEquals("Hoogwelgeboren vrouwe", persoon.getNaam().getAanhef());
    assertEquals("C. barones van Brest naar Kempen", persoon.getNaam().getAanschrijfwijze());
    assertNull(persoon.getNaam().getRegelVoorafgaandAanAanschrijfwijze());
    assertEquals("barones van Brest naar Kempen", persoon.getNaam().getGebruikInLopendeTekst());

    persoon = getIngeschrevenPersoon(999993422L);
    assertEquals("eigen", persoon.getNaam().getAanduidingNaamgebruik().toString());
    assertEquals("man", persoon.getGeslachtsaanduiding().toString());
    assertEquals("Baron", persoon.getNaam().getAdellijkeTitelPredikaat().getOmschrijving());
    assertEquals("Hoogwelgeboren heer", persoon.getNaam().getAanhef());
    assertEquals("De hoogwelgeboren heer", persoon.getNaam().getRegelVoorafgaandAanAanschrijfwijze());
    assertEquals("M.G.F.M.V. baron van Brest naar Kempen", persoon.getNaam().getAanschrijfwijze());
    assertEquals("baron van Brest naar Kempen", persoon.getNaam().getGebruikInLopendeTekst());

  }
}
