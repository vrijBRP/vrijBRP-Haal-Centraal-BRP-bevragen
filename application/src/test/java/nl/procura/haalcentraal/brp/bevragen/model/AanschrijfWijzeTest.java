package nl.procura.haalcentraal.brp.bevragen.model;

import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.AanduidingAanschrijving;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.GeslachtAanduiding;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

// Voorbeelden: geen adellijke titel of predikaat
//      | aanduidingNaamgebruik | geslachtsaanduiding |samenstelling gebruikInLopendeTekst | aanschrijfwijze | gebruikInLopendeTekst |
//      | Eigen                 | man                 | GA VV GN        | H. in het Veld            | de heer In het Veld            |
//      | Eigen                 | man                 | GA VV GN        | F. Groenen                | de heer Groenen                |
//      | Eigen                 | Onbekend            | VL VV GN        | P.T. Groenink             | P.T. Groenink                  |
//      | Partner na eigen      | vrouw               | GA VV GN-VP GP  | I. van Velzen-in het Veld | mevrouw Van Velzen-in het Veld |
//      | Partner na eigen      | vrouw               | GA VV GN-VP GP  | F. Groenen-Groenink       | mevrouw Groenen-Groenink       |
//      | Partner               | vrouw               | GA VP GP        | S. van Velzen             | mevrouw Van Velzen             |
//      | Partner               | vrouw               | GA VP GP        | J.F.R. Groenen            | mevrouw Groenen                |
//      | Partner               | Onbekend            | VL VP GP        | I. van Velzen             | I. van Velzen                  |
//      | Partner voor eigen    | man                 | GA VP GP-VV GN  | F. in het Veld-van Velzen | de heer In het Veld-van Velzen |
//      | Partner voor eigen    | man                 | GA VP GP-VV GN  | F. Groenen-Groenink       | de heer Groenen-Groenink       |

public class AanschrijfWijzeTest {

  @Test
  public void testAanschrijfwijzeEAndPrefix() {
    var parameters = new FullNameParameters("H.", null, "in het", "Veld", "van", "Velzen", null, null,
        GeslachtAanduiding.MAN, AanduidingAanschrijving.E);
    var aanschrijfWijze = new AanschrijfWijze(parameters);
    var gebruikinText = new GebruikInLopendeTekst(parameters);

    assertEquals("H. in het Veld", aanschrijfWijze.getAanschrijfwijze());
    assertEquals("de heer In het Veld", gebruikinText.getUsageInText());
  }

  @Test
  public void testAanschrijfwijzeENoPrefix() {
    var parameters = new FullNameParameters("F.", null, null, "Groenen", "van ", "Velzen", null, null,
        GeslachtAanduiding.MAN, AanduidingAanschrijving.E);
    var aanschrijfWijze = new AanschrijfWijze(parameters);
    var gebruikinText = new GebruikInLopendeTekst(parameters);

    assertEquals("F. Groenen", aanschrijfWijze.getAanschrijfwijze());
    assertEquals("de heer Groenen", gebruikinText.getUsageInText());

  }

  @Test
  public void testAanschrijfwijzeNAndPrefix() {
    var parameters = new FullNameParameters("I.", null, "van", "Velzen", "in het", "Veld", null, null,
        GeslachtAanduiding.VROUW, AanduidingAanschrijving.N);
    var aanschrijfWijze = new AanschrijfWijze(parameters);
    var gebruikinText = new GebruikInLopendeTekst(parameters);

    assertEquals("I. van Velzen-in het Veld", aanschrijfWijze.getAanschrijfwijze());
    assertEquals("mevrouw Van Velzen-in het Veld", gebruikinText.getUsageInText());
  }

  @Test
  public void testAanschrijfwijzeNoPrefix() {
    var parameters = new FullNameParameters("H.", null, "", "Veld", null, "Velzen", null, null,
        GeslachtAanduiding.MAN, AanduidingAanschrijving.N);
    var aanschrijfWijze = new AanschrijfWijze(parameters);
    var gebruikinText = new GebruikInLopendeTekst(parameters);

    assertEquals("H. Veld-Velzen", aanschrijfWijze.getAanschrijfwijze());
    assertEquals("de heer Veld-Velzen", gebruikinText.getUsageInText());
  }

  @Test
  public void testAanschrijfwijzePAndPrefix() {
    var parameters = new FullNameParameters("H.F.G.", null, "in het", "Veld", "van", "Velzen", null, null,
        GeslachtAanduiding.MAN, AanduidingAanschrijving.P);
    var aanschrijfWijze = new AanschrijfWijze(parameters);
    var gebruikinText = new GebruikInLopendeTekst(parameters);

    assertEquals("H.F.G. van Velzen", aanschrijfWijze.getAanschrijfwijze());
    assertEquals("de heer Van Velzen", gebruikinText.getUsageInText());
  }

  @Test
  public void testAanschrijfwijzePNoPrefix() {
    var parameters = new FullNameParameters("H.", null, "in het", "Veld", "", "Velzen", null, null,
        GeslachtAanduiding.VROUW, AanduidingAanschrijving.P);
    var aanschrijfWijze = new AanschrijfWijze(parameters);
    var gebruikinText = new GebruikInLopendeTekst(parameters);

    assertEquals("H. Velzen", aanschrijfWijze.getAanschrijfwijze());
    assertEquals("mevrouw Velzen", gebruikinText.getUsageInText());
  }

  @Test
  public void testAanschrijfwijzeVAndPrefix() {
    var parameters = new FullNameParameters("H.", null, "in het", "Veld", "van", "Velzen", null, null,
        GeslachtAanduiding.MAN, AanduidingAanschrijving.V);
    var aanschrijfWijze = new AanschrijfWijze(parameters);
    var gebruikinText = new GebruikInLopendeTekst(parameters);

    assertEquals("H. van Velzen-in het Veld", aanschrijfWijze.getAanschrijfwijze());
    assertEquals("de heer Van Velzen-in het Veld", gebruikinText.getUsageInText());
  }

  @Test
  public void testAanschrijfwijzeVNoPrefix() {
    var parameters = new FullNameParameters("H.", null, "", "Veld", "", "Velzen", null, null,
        GeslachtAanduiding.MAN, AanduidingAanschrijving.V);
    var aanschrijfWijze = new AanschrijfWijze(parameters);
    var gebruikinText = new GebruikInLopendeTekst(parameters);

    assertEquals("H. Velzen-Veld", aanschrijfWijze.getAanschrijfwijze());
    assertEquals("de heer Velzen-Veld", gebruikinText.getUsageInText());
  }

}
