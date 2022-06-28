package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.AanduidingBijzonderNederlanderschap;
import org.junit.jupiter.api.Test;
import static nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.AanduidingBijzonderNederlanderschapEnum.BEHANDELD_ALS_NEDERLANDER;
import static nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.AanduidingBijzonderNederlanderschapEnum.VASTGESTELD_NIET_NEDERLANDER;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Functionaliteit: Bepalen van de actuele nationaliteit van een ingeschreven persoon
 *   Niet-beëindigde nationaliteiten van de ingeschreven persoon wordt opgenomen.
 *
 *   In het antwoord voor ingeschrevenpersonen worden alleen nationaliteiten opgenomen waarbij in
 *   categorie 04 nationaliteit (05.10) of aanduiding bijzonder Nederlanderschap (65.10) is opgenomen,
 *   en in categorie 04 GEEN reden beëindigen nationaliteit (64.10) is opgenomen.
 *
 *   Voor een nationaliteit wordt de datumIngangGeldigheid gevuld met de datum geldigheid (85.10) uit de oudste
 *   bijbehorende categorie (04 of 54) waarin er een waarde is voor 05.10 of voor 65.10.
 *   De overige gegevens over een nationaliteit worden gehaald uit categorie 04.
 *
 *   Een onjuiste nationaliteit wordt niet opgenomen. Een nationaliteit waarbij indicatie onjuist (84.10) is gevuld,
 *   wordt niet opgenomen in het antwoord.
 *   Voor een actuele nationaliteit met een bijbehorende historische categorie 54 met indicatie onjuist,
 *   worden de gegevens in de onjuiste categorie (incl. datum ingang) genegeerd.
 */

public class NationaliteitTest extends IngeschrevenPersonenResourceTest {

  @Test
  public void testFindNationality() {
    var persoon = getIngeschrevenPersoon(999996630);
    assertEquals("Nederlandse", persoon.getNationaliteiten().get(0).getNationaliteit().getOmschrijving());

    persoon = getIngeschrevenPersoon(999992466);
    assertEquals("Franse", persoon.getNationaliteiten().get(0).getNationaliteit().getOmschrijving());

  }

  @Test
  public void testFindReasonForAdmission() {
    var persoon = getIngeschrevenPersoon(999992466);
    var nationaliteit = persoon.getNationaliteiten().get(0);
    assertEquals("0057", nationaliteit.getNationaliteit().getCode());
    assertEquals("301", nationaliteit.getRedenOpname().getCode());
    assertEquals("Vaststelling bezit vreemde nationaliteit",
        nationaliteit.getRedenOpname().getOmschrijving());

    persoon = getIngeschrevenPersoon(999996630);
    nationaliteit = persoon.getNationaliteiten().get(0);

    assertEquals("0001", nationaliteit.getNationaliteit().getCode());
    assertEquals("000", nationaliteit.getRedenOpname().getCode());
    assertEquals("Onbekend", nationaliteit.getRedenOpname().getOmschrijving());

    persoon = getIngeschrevenPersoon(999994268);
    nationaliteit = persoon.getNationaliteiten().get(0);
    assertEquals("0001", nationaliteit.getNationaliteit().getCode());
    assertEquals("001", nationaliteit.getRedenOpname().getCode());
    assertEquals("Wet op het Nederlanderschap 1892, art.1, lid 1a",
        nationaliteit.getRedenOpname().getOmschrijving());
  }

  @Test
  public void testFindStartdateValidity() {
    var persoon = getIngeschrevenPersoon(999992466);
    assertEquals("2016-06-01", persoon.getNationaliteiten().get(0).getDatumIngangGeldigheid().getDatum().toString());

    persoon = getIngeschrevenPersoon(999994268);
    assertEquals("1939-11-29", persoon.getNationaliteiten().get(0).getDatumIngangGeldigheid().getDatum().toString());

    persoon = getIngeschrevenPersoon(999996630);
    assertNull(persoon.getNationaliteiten().get(0).getDatumIngangGeldigheid());

  }

  @Test
  public void testCheckIndicationDutchCitizenship() {
    var persoon = getIngeschrevenPersoon(999990160);
    var nationaliteit = persoon.getNationaliteiten().get(0);
    assertEquals(BEHANDELD_ALS_NEDERLANDER, nationaliteit.getAanduidingBijzonderNederlanderschap());

    nationaliteit.setAanduidingBijzonderNederlanderschap(AanduidingBijzonderNederlanderschap.fromCode("V"));
    assertEquals("vastgesteld_niet_nederlander", nationaliteit.getAanduidingBijzonderNederlanderschap().toString());
    assertEquals(VASTGESTELD_NIET_NEDERLANDER, nationaliteit.getAanduidingBijzonderNederlanderschap());

  }

  @Test
  public void testCheckForPluralNationality() {
    var persoon = getIngeschrevenPersoon(999994220);
    var nationaliteiten = persoon.getNationaliteiten();
    assertEquals(3, nationaliteiten.size());

    persoon = getIngeschrevenPersoon(999990160);
    nationaliteiten = persoon.getNationaliteiten();
    assertEquals(1, nationaliteiten.size());

  }

}
