package nl.procura.haalcentraal.brp.bevragen.model;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.core.enums.GBARecStatus;
import nl.procura.gbaws.web.rest.v2.personlists.CustomGbaWsPersonList;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class FullNameParametersTest {

  @Test
  public void fullnameParametersTest() {
    var pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.PERSOON)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.VOORNAMEN, "Frederique Herman Natashe")
        .addElem(GBAElem.VOORV_GESLACHTSNAAM, "van Der ")
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen ")
        .addElem(GBAElem.AANDUIDING_NAAMGEBRUIK, "N")
        .addElem(GBAElem.TITEL_PREDIKAAT, "G")
        .addElem(GBAElem.GESLACHTSAAND, "M")
        .toCat()
        .toPL()
        .addCat(GBACat.HUW_GPS)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.VOORV_GESLACHTSNAAM, "In Het  ")
        .addElem(GBAElem.GESLACHTSNAAM, " Veld")
        .addElem(GBAElem.TITEL_PREDIKAAT, "R")
        .addElem(GBAElem.AANDUIDING_NAAMGEBRUIK, "N")
        .addElem(GBAElem.GESLACHTSAAND, "M")
        .toCat()
        .build();

    var person = pl.getCurrentRec(GBACat.PERSOON).orElse(null);
    var partner = pl.getCurrentRec(GBACat.HUW_GPS).orElse(null);
    assert person != null;
    assert partner != null;
    var parameters = new FullNameParameters(person, partner);

    assertEquals("F.H.N.", parameters.getInitials());
    assertEquals("Frederique Herman Natashe", parameters.getFirstNames());
    assertEquals("van der", parameters.getPrefix());
    assertEquals("Jansen", parameters.getLastName());
    assertEquals("MAN", parameters.getGender().toString());
    assertEquals("N", parameters.getFullNameUsage().toString());
    assertEquals("G", parameters.getTitleNoble().getCode());
    assertEquals("R", parameters.getTitleNoblePartner().getCode());
    assertEquals("in het", parameters.getPrefixPartner());
    assertEquals("Veld", parameters.getLastNamePartner());

    var aanhef = new Aanhef(parameters);
    var aanschrijfWijze = new AanschrijfWijze(parameters);
    var gebruik = new GebruikInLopendeTekst(parameters);
    var regel = new RegelVoorafGaandAanAanschrijfwijze(parameters);

    assertEquals("Hooggeboren heer", aanhef.getAanhef());
    assertEquals("F.H.N. graaf van der Jansen-in het Veld", aanschrijfWijze.getAanschrijfwijze());
    assertEquals("graaf Van der Jansen-in het Veld", gebruik.getUsageInText());
    assertEquals("De hooggeboren heer", regel.getRegelVoorafgaand(partner));
  }
}
