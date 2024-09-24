package nl.procura.haalcentraal.brp.bevragen.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.AanduidingAanschrijving;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.GeslachtAanduiding;

//    Voorbeelden: geen adellijke titel of predikaat
//            | aanduidingNaamgebruik | geslachtsaanduiding |samenstelling aanhef | aanschrijfwijze           | aanhef                                 |
//      | Eigen  (E)               | man                 | GA VV GN            | H. in het Veld            | geachte heer In het Veld               |
//      | Eigen  (E)               | vrouw               | GA VV GN            | F. Groenen                | geachte mevrouw Groenen                |
//      | Eigen    (E)             | onbekend            | GA VL VV GN         | C.F.H. van Velzen         | geachte C.F.H. van Velzen              |
//      | Partner na eigen  (N)    | vrouw               | GA VV GN-VP GP      | I. van Velzen-in het Veld | geachte mevrouw Van Velzen-in het Veld |
//      | Partner na eigen   (N)   | onbekend            | GA VV GN-VP GP      | F. Groenen-Groenink       | geachte F. Groenen-Groenink            |
//      | Partner    (P)           | vrouw               | GA VP GP            | S. van Velzen             | geachte mevrouw Van Velzen             |
//      | Partner   (P)            | man                 | GA VP GP            | J.F.R. Groenen            | geachte heer Groenen                   |
//      | Partner voor eigen  (V)  | man                 | GA VP GP-VV GN      | F. in het Veld-van Velzen | geachte heer In het Veld-van Velzen    |
//      | Partner voor eigen  (V)  | vrouw               | GA VP GP-VV GN      | M. Groenen-Groenink       | geachte mevrouw Groenen-Groenink       |
//      | Partner voor eigen   (V) | onbekend            | GA VL. VP GP-VV GN  | J.P. van Velzen-Groenen   | geachte J.P. van Velzen-Groenen        |

public class AanhefTest {

  @Test
  public void testAanhefMAndE() {
    var parameters = new FullNameParameters("H.", "Hendrik", "in het ", "Veld", "van", "Velzen",
        null, null, GeslachtAanduiding.MAN, AanduidingAanschrijving.E);
    var aanhef = new Aanhef(parameters);
    assertEquals("Geachte heer In het Veld", aanhef.getAanhef());
  }

  @Test
  public void testAanhefVAndE() {
    var parameters = new FullNameParameters("F.", "Fransje", "", "Groenen", " ",
        "Groenink", null, null, GeslachtAanduiding.VROUW, AanduidingAanschrijving.E);
    var aanhef = new Aanhef(parameters);
    assertEquals("Geachte mevrouw Groenen", aanhef.getAanhef());
  }

  @Test
  public void testAanhefOAndE() {
    var parameters = new FullNameParameters("C.F.H.", null, "van", "Velzen", "", "Groenink", null, null,
        GeslachtAanduiding.ONBEKEND, AanduidingAanschrijving.E);
    var aanhef = new Aanhef(parameters);
    assertEquals("Geachte C.F.H. van Velzen", aanhef.getAanhef());
  }

  @Test
  public void testAanhefVAndV() {
    var parameters = new FullNameParameters(null, null, "in het", "Veld", "van", "Velzen", null, null,
        GeslachtAanduiding.VROUW, AanduidingAanschrijving.V);
    var aanhef = new Aanhef(parameters);
    assertEquals("Geachte mevrouw Van Velzen-in het Veld", aanhef.getAanhef());
  }

  @Test
  public void testAanhefOAndV() {
    var parameters = new FullNameParameters("F.", null, "van", "Groenink", "in de", "Groenen", null, null,
        GeslachtAanduiding.ONBEKEND, AanduidingAanschrijving.V);
    var aanhef = new Aanhef(parameters);
    assertEquals("Geachte F. in de Groenen-van Groenink", aanhef.getAanhef());
  }

  @Test
  public void testAanhefMAndV() {
    var parameters = new FullNameParameters("F.", null, "van", "Groenink", "in de", "Groenen", null, null,
        GeslachtAanduiding.MAN, AanduidingAanschrijving.V);
    var aanhef = new Aanhef(parameters);
    assertEquals("Geachte heer In de Groenen-van Groenink", aanhef.getAanhef());
  }

  @Test
  public void testAanhefVAndP() {
    var parameters = new FullNameParameters("H.", null, "in het", "Veld", "van", "Velzen", null, null,
        GeslachtAanduiding.VROUW, AanduidingAanschrijving.P);
    var aanhef = new Aanhef(parameters);
    assertEquals("Geachte mevrouw Van Velzen", aanhef.getAanhef());
  }

  @Test
  public void testAanhefMAndP() {
    var parameters = new FullNameParameters("J.F.R.", null, null, "Groenink", "van", "Groenen", null, null,
        GeslachtAanduiding.MAN, AanduidingAanschrijving.P);
    var aanhef = new Aanhef(parameters);
    assertEquals("Geachte heer Van Groenen", aanhef.getAanhef());
  }

  @Test
  public void testAanhefOAndP() {
    var parameters = new FullNameParameters("J.F.R.", null, null, "Groenink", "van", "Groenen", null, null,
        GeslachtAanduiding.ONBEKEND, AanduidingAanschrijving.P);
    var aanhef = new Aanhef(parameters);
    assertEquals("Geachte J.F.R. van Groenen", aanhef.getAanhef());
  }

  @Test
  public void testAanhefMAndN() {
    var parameters = new FullNameParameters("J.F.R.", null, "in de", "Groenink", "van", "Groenen", null, null,
        GeslachtAanduiding.MAN, AanduidingAanschrijving.N);
    var aanhef = new Aanhef(parameters);
    assertEquals("Geachte heer In de Groenink-van Groenen", aanhef.getAanhef());
  }

  @Test
  public void testAanhefVAndN() {
    var parameters = new FullNameParameters("J.F.R.", null, "van de ", " Groenink", "van", "Groenen", null, null,
        GeslachtAanduiding.VROUW, AanduidingAanschrijving.N);
    var aanhef = new Aanhef(parameters);
    assertEquals("Geachte mevrouw Van de Groenink-van Groenen", aanhef.getAanhef());
  }

  @Test
  public void testAanhefOAndN() {
    var parameters = new FullNameParameters("J.F.R.", null, "Naar De", "Groenink", "van", "Groenen", null, null,
        GeslachtAanduiding.ONBEKEND, AanduidingAanschrijving.N);
    var aanhef = new Aanhef(parameters);
    assertEquals("Geachte J.F.R. naar de Groenink-van Groenen", aanhef.getAanhef());
  }

}
