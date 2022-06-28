package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;

/**
 * Functionaliteit: Voor ingeschreven_personen worden alleen actuele gegevens getoond
 * Voor diverse groepen gegevens is het mogelijk dat de gegevens die voor een persoon geregistreerd zijn een einddatum
 * kennen. Als bij een dergelijke groep de einddatum daadwerkelijk is ingevuld en in het verleden ligt, dan worden deze
 * gegevens niet getoond.
 * Voorbeelden van deze groepen gegevens zijn : Nationaliteit, Verblijfstitel, Huwelijk/geregistreerd partnerschap,
 * Verblijfplaats, Kiesrecht.
 * Voor Huwelijk/geregistreerd partnerschap beschrijft de [partners feature](./partners.feature) de exacte
 * functionaliteit.
 * Voor kiesrecht beschrijft de [kiesrecht feature](./kiesrecht.feature) de exacte functionaliteit.
 * Voor nationaliteit beschrijft de [nationaliteit feature](./nationaliteit.feature) de exacte functionaliteit.
 * Alleen gegevens uit de actuele LO GBA categorieën worden opgenomen (Categorieën 01, 02, 03, 04, 05, 06, 07, 08, 09,
 * 10, 12, 13)
 */
public class ActueleGegevensIngeschrevenPersoonTest extends IngeschrevenPersonenResourceTest {

  /**
   *  Scenario: de ingeschreven persoon heeft een beëindigde verblijfstitel
   *  Gegeven de te raadplegen persoon heeft een verblijfstitel waarvan de einddatum in het verleden ligt
   *  En de te raadplegen persoon heeft geen andere of nieuwe verblijfstitel
   *  Als de ingeschreven persoon met burgerservicenummer 999999321 wordt geraadpleegd
   *  Dan is de verblijfstitel van de betreffende ingeschreven persoon leeg
   */
  @Test
  @SneakyThrows
  public void mustHaveEmptyStatus() {
    //        Long bsnL = 0L;
    //        IngeschrevenPersoonHalCollectie persons = getIngeschrevenPersonen(bsnL);
    //
    //        assertTrue(persons.getEmbedded().getIngeschrevenpersonen().get(0).getVerblijfstitel().getAanduiding().getOmschrijving().isEmpty());

  }
}
