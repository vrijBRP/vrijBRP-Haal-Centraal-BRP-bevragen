package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import lombok.SneakyThrows;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.IngeschrevenPersoonHal;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Functionaliteit: Als gemeente wil ik de juiste en consistente briefaanhef in communicatie naar burgers
 * Attribuut aanhef bij een ingeschreven persoon wordt gevuld door de provider om op deze wijze op eenduidige
 * wijze een persoon te kunnen aanschrijven.
 * De briefaanhef wordt gebruikt bovenaan een brief.
 *
 * Voor een persoon zonder adellijke titel of predikaat begint de briefaanhef met “geachte mevrouw” (vrouw) of
 * “geachte heer” (man) of "geachte " plus de voorletters (onbekend), afhankelijk van het geslacht van de persoon.
 * Hierop volgt de samengestelde naam.
 * De waarde van aanduidingNaamgebruik bepaalt hoe de aanhef wordt samengesteld uit de naam van de persoon en de
 * naam van de partner.
 *
 * Wanneer geslachtsaanduiding is niet leeg of "onbekend", dan wordt het voorvoegsel van de eerste geslachtsnaam in de
 * briefaanhef met een hoofdletter geschreven.
 *
 * E = Eigen
 * P = Partner
 * V = Partner - Eigen
 * N = Eigen - Partner
 *
 */
public class AanhefTest extends IngeschrevenPersonenResourceTest {

  @Test
  @SneakyThrows
  public void testSalutationForPersonBasedOnGenderDesignationStatus() {
    //gender "V" with name designation "E" and status "H" or "P"
    IngeschrevenPersoonHal persoon = getIngeschrevenPersoon(999996630);
    assertEquals("Charbon", persoon.getNaam().getGeslachtsnaam());
    assertEquals(1, persoon.getEmbedded().getPartners().size());
    assertEquals("Boer", persoon.getEmbedded().getPartners().get(0).getNaam().getGeslachtsnaam());
    assertEquals("O.G. Charbon", persoon.getNaam().getAanschrijfwijze());
    assertEquals("Geachte mevrouw Charbon", persoon.getNaam().getAanhef());
    assertEquals("mevrouw Charbon", persoon.getNaam().getGebruikInLopendeTekst());

    //gender "V" with name designation "E" and status "O" or "S"
    persoon = getIngeschrevenPersoon(999997622);
    assertEquals("Nolles", persoon.getNaam().getGeslachtsnaam());
    assertNull(persoon.getEmbedded().getPartners());
    assertEquals("Geachte mevrouw Nolles", persoon.getNaam().getAanhef());
    assertEquals("mevrouw Nolles", persoon.getNaam().getGebruikInLopendeTekst());

    //gender "V" with name designation "V"  and status "H" or "P"
    persoon = getIngeschrevenPersoon(999990950);
    assertEquals("Maassen", persoon.getNaam().getGeslachtsnaam());
    assertEquals(1, persoon.getEmbedded().getPartners().size());
    assertEquals("Altena", persoon.getEmbedded().getPartners().get(0).getNaam().getGeslachtsnaam());
    assertEquals("Geachte mevrouw Altena-Maassen", persoon.getNaam().getAanhef());
    assertEquals("mevrouw Altena-Maassen", persoon.getNaam().getGebruikInLopendeTekst());

    //gender "V" with name designation "V" and status "H" or "P" and partner's name contains a prefix
    persoon = getIngeschrevenPersoon(999993392);
    assertEquals("Holthuizen", persoon.getNaam().getGeslachtsnaam());
    assertEquals(1, persoon.getEmbedded().getPartners().size());
    assertEquals("Burck", persoon.getEmbedded().getPartners().get(0).getNaam().getGeslachtsnaam());
    assertEquals("Geachte mevrouw Du Burck-Holthuizen", persoon.getNaam().getAanhef());//attention prefix starts with Uppercase
    assertEquals("mevrouw Du Burck-Holthuizen", persoon.getNaam().getGebruikInLopendeTekst());

    //gender "V with name designation "P" and status "H" or "P"
    persoon = getIngeschrevenPersoon(999993409);
    assertEquals("Kouwenhoven", persoon.getNaam().getGeslachtsnaam());
    assertEquals(1, persoon.getEmbedded().getPartners().size());
    assertEquals("Groeman", persoon.getEmbedded().getPartners().get(0).getNaam().getGeslachtsnaam());
    assertEquals("Geachte mevrouw Groeman", persoon.getNaam().getAanhef());
    assertEquals("mevrouw Groeman", persoon.getNaam().getGebruikInLopendeTekst());

    //gender "M" with name designation "E" name constains a prefix
    persoon = getIngeschrevenPersoon(999990329);
    assertEquals("Hoop", persoon.getNaam().getGeslachtsnaam());
    assertEquals("Geachte heer De Hoop", persoon.getNaam().getAanhef());
    assertEquals("de heer De Hoop", persoon.getNaam().getGebruikInLopendeTekst());

    //gender "M" with name designation "P" and status "H" or "P"
    persoon = getIngeschrevenPersoon(999996538);
    assertEquals("Boer", persoon.getNaam().getGeslachtsnaam());
    assertEquals(1, persoon.getEmbedded().getPartners().size());
    assertEquals("Charbon", persoon.getEmbedded().getPartners().get(0).getNaam().getGeslachtsnaam());
    assertEquals("Geachte heer Charbon", persoon.getNaam().getAanhef());
    assertEquals("de heer Charbon", persoon.getNaam().getGebruikInLopendeTekst());

    //gender "O" with name designation "E" no first name nor prefix
    persoon = getIngeschrevenPersoon(999991310);
    assertEquals("Debrabandere", persoon.getNaam().getGeslachtsnaam());
    assertEquals("Geachte Debrabandere", persoon.getNaam().getAanhef());
    assertEquals("Debrabandere", persoon.getNaam().getGebruikInLopendeTekst());
  }
}
