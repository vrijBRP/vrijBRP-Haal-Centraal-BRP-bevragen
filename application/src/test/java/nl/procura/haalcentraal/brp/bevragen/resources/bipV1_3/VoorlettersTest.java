package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.Util;
import nl.procura.haalcentraal.brp.bevragen.model.FullNameParameters;
import lombok.SneakyThrows;

/**
 * Functionaliteit: Bepalen van voorletters uit de voornamen van een persoon
 *
 * Als klant van gemeenten
 * wil ik dat mijn voorletters in plaats van mijn voornamen automatisch worden ingevuld in e-formulieren en gepersonaliseerde "mijn" omgevingen
 * zodat mijn naam overzichtelijk wordt weergegeven
 *
 * De voorletters worden opgenomen als één (1) voorletter per voornaam, gevolgd door een punt (.).
 * Als een voornaam een samengestelde naam is gescheiden door een koppelteken (-), Dan wordt deze voornaam (ook) afgekort tot één voorletter.
 * Als een voornaam  begint met een dubbelklank (Th, Ph, Ch, IJ, enz.), Dan wordt deze voornaam (ook) afgekort tot één voorletter.
 * Als één of meerdere voornamen uit één letter bestaan, dan volgt er na de letter geen .
 * Wanneer na een voorletter zonder punt (voornaam had één letter) nog een andere voorletter volgt, wordt daartussen een spatie gezet.
 * Als de rubriek Voornamen is gevuld met de standaardwaarde '.' (punt), Dan wordt geen extra (scheidings)punt toegevoegd; de inhoud van de attribuut voorletters is na afleiding Dan '.'
 *
 *
 * Abstract Scenario: Voorletters wordt samengesteld uit de eerste letter van de voornamen gescheiden door een punt
 * Gegeven een ingeschreven persoon met voornamen <voornamen>
 * Als de persoon wordt geraadpleegd
 * Dan zijn de voorletters van de ingeschreven persoon gelijk aan <voorletters>
 *
 * Voorbeelden:
 * | voornamen            | voorletters |
 * | Henk                 | H.          |
 * | Anna Cornelia        | A.C.        |
 * | Johan Frank Robert   | J.F.R.      |
 * | Theo Philip IJsbrand | T.P.I.      |
 * | Anne-Fleur Belle     | A.B.        |
 * | Suzie Q              | S.Q         |
 * | J P                  | J P         |
 * | A                    | A           |
 * | .                    | .           |
 */
public class VoorlettersTest extends IngeschrevenPersonenResourceTest {

  @Test
  @SneakyThrows
  public void testFindInitials() {
    var persoon = getIngeschrevenPersoon(999990536L);

    assertEquals("Hendrika Johanna Theodora", persoon.getNaam().getVoornamen());
    assertEquals("H.J.T.", persoon.getNaam().getVoorletters());

    persoon = getIngeschrevenPersoon(999990901L);
    assertEquals("Ria", persoon.getNaam().getVoornamen());
    assertEquals("R.", persoon.getNaam().getVoorletters());

    persoon = getIngeschrevenPersoon(999992491L);
    assertEquals("Frank N J M", persoon.getNaam().getVoornamen());
    assertEquals("F.N J M", persoon.getNaam().getVoorletters());

    persoon = getIngeschrevenPersoon(999991498L);
    assertEquals("Maria Christina", persoon.getNaam().getVoornamen());
    assertEquals("M.C.", persoon.getNaam().getVoorletters());

    persoon = getIngeschrevenPersoon(999996216L);
    assertEquals("Christiaan", persoon.getNaam().getVoornamen());
    assertEquals("C.", persoon.getNaam().getVoorletters());

    persoon = getIngeschrevenPersoon(999995169L);
    assertEquals("Özlem", persoon.getNaam().getVoornamen());
    assertEquals("Ö.", persoon.getNaam().getVoorletters());

    persoon = getIngeschrevenPersoon(999993422L);
    assertEquals("Marcelius Gerardus Franciscus Michaelis Vincentius", persoon.getNaam().getVoornamen());
    assertEquals("M.G.F.M.V.", persoon.getNaam().getVoorletters());

    persoon = getIngeschrevenPersoon(999990743L);
    assertEquals("Ŗî Ãō Øū Ŋÿ Ği ŢžŰŲ ŜŞőĠĪ Ŷŵ Ĉŷ", persoon.getNaam().getVoornamen());
    assertEquals("Ŗ.Ã.Ø.Ŋ.Ğ.Ţ.Ŝ.Ŷ.Ĉ.", persoon.getNaam().getVoorletters());

    persoon = getIngeschrevenPersoon(999991279L);
    assertEquals("Anne-Marie", persoon.getNaam().getVoornamen());
    assertEquals("A.", persoon.getNaam().getVoorletters());

    persoon = getIngeschrevenPersoon(999991310L);
    assertNull(persoon.getNaam().getVoornamen());
    assertNull(persoon.getNaam().getVoorletters());

    persoon = getIngeschrevenPersoon(999990603L);
    assertEquals("Aişe Çağati", persoon.getNaam().getVoornamen());
    assertEquals("A.Ç.", persoon.getNaam().getVoorletters());

    persoon = getIngeschrevenPersoon(999990913L);
    assertEquals(
        "Jérôme G J P R C 17 Mai Joannes Jantje KarelTheo Jindrich IV Willem Tikan dICKsen Roeland Jan P@r Jan Peter",
        persoon.getNaam().getVoornamen());
    assertEquals("J.G J P R C 1.M.J.J.K.J.I.W.T.D.R.J.P.J.P.", persoon.getNaam().getVoorletters());

    persoon = getIngeschrevenPersoon(999990743L);
    assertEquals(
        "Żáïŀëñøŕ Åłéèça Đëļŧå Źêŗôřėņà Çēěĺāăęśţe-Ëlīĩşâ-Èŋűğķıį ?gđä Üĝħöý Ŧæňœß ĤĦòńã Œċčąťù  Žóŝd' Ķćõšũů ĒĚźżûü ÏĨŭųġĥì Ŭĸľo ÝY'ĵĉė ŁíŔÖÕú",
        persoon.getEmbedded().getOuders().get(0).getNaam().getVoornamen());
    assertEquals("Ż.Å.Đ.Ź.Ç.?.Ü.Ŧ.Ĥ.Œ.Ž.Ķ.Ē.Ï.Ŭ.Ý.Ł.",
        persoon.getEmbedded().getOuders().get(0).getNaam().getVoorletters());

    var parameters = new FullNameParameters();
    parameters.setFirstNames("Theo    Philip  IJsbrand");
    assertEquals("T.P.I.", Util.toVoorletters(parameters.getFirstNames()));

    parameters.setFirstNames("Anne-Fleur Belle");
    assertEquals("A.B.", Util.toVoorletters(parameters.getFirstNames()));

    parameters.setFirstNames("Suzie Q");
    assertEquals("S.Q", Util.toVoorletters(parameters.getFirstNames()));

    parameters.setFirstNames("J P");
    assertEquals("J P", Util.toVoorletters(parameters.getFirstNames()));

    parameters.setFirstNames("A");
    assertEquals("A", Util.toVoorletters(parameters.getFirstNames()));

    parameters.setFirstNames(".");
    assertEquals(".", Util.toVoorletters(parameters.getFirstNames()));
  }
}
