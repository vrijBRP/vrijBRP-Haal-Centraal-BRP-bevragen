package nl.procura.haalcentraal.brp.bevragen.model;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.core.enums.GBARecStatus;
import nl.procura.gbaws.web.rest.v2.personlists.CustomGbaWsPersonList;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FullNameParametersTest {

  @Test
  public void fullnameParametersTest1() {
    var pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.PERSOON)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.VOORNAMEN,"Frederique Herman Natashe")
        .addElem(GBAElem.VOORV_GESLACHTSNAAM,"van Der")
        .addElem(GBAElem.GESLACHTSNAAM,"Jansen")
        .addElem(GBAElem.AANDUIDING_NAAMGEBRUIK,"N")
        .addElem(GBAElem.TITEL_PREDIKAAT,"G")
        .addElem(GBAElem.GESLACHTSAAND,"M")
        .toCat()
        .toPL()
        .addCat(GBACat.HUW_GPS)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.VOORV_GESLACHTSNAAM,"In Het")
        .addElem(GBAElem.GESLACHTSNAAM," Veld")
        .addElem(GBAElem.TITEL_PREDIKAAT,"R")
        .addElem(GBAElem.AANDUIDING_NAAMGEBRUIK,"N")
        .addElem(GBAElem.GESLACHTSAAND,"M")
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
    assertEquals("graaf van der Jansen-in het Veld", gebruik.getUsageInText());
    assertEquals("De hooggeboren heer", regel.getRegelVoorafgaand(partner));
  }

  @Test
  public void fullnameParametersTest2() {
    var pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.PERSOON)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.VOORNAMEN,"Frederique Herman Natashe")
        .addElem(GBAElem.VOORV_GESLACHTSNAAM,"van Der")
        .addElem(GBAElem.GESLACHTSNAAM,"Jansen")
        .addElem(GBAElem.AANDUIDING_NAAMGEBRUIK,"N")
        .addElem(GBAElem.TITEL_PREDIKAAT,"")
        .addElem(GBAElem.GESLACHTSAAND,"M")
        .toCat()
        .toPL()
        .addCat(GBACat.HUW_GPS)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.VOORV_GESLACHTSNAAM,"In Het")
        .addElem(GBAElem.GESLACHTSNAAM," Veld")
        .addElem(GBAElem.TITEL_PREDIKAAT,"JV")
        .addElem(GBAElem.AANDUIDING_NAAMGEBRUIK,"N")
        .addElem(GBAElem.GESLACHTSAAND,"M")
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
    assertNull(parameters.getTitleNoble());
    assertEquals("JV", parameters.getTitleNoblePartner().getCode());
    assertEquals("in het", parameters.getPrefixPartner());
    assertEquals("Veld", parameters.getLastNamePartner());

    var aanhef = new Aanhef(parameters);
    var aanschrijfWijze = new AanschrijfWijze(parameters);
    var gebruik = new GebruikInLopendeTekst(parameters);
    var regel = new RegelVoorafGaandAanAanschrijfwijze(parameters);

    assertEquals("Hoogwelgeboren heer", aanhef.getAanhef());
    assertEquals("F.H.N. van der Jansen-in het Veld", aanschrijfWijze.getAanschrijfwijze());
    assertEquals("de heer Van der Jansen-in het Veld", gebruik.getUsageInText());
    assertNull(regel.getRegelVoorafgaand(partner));
  }

  @Test
  public void fullnameParametersTest3() {
    var pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.PERSOON)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.VOORNAMEN,"Frederique Herman Natashe")
        .addElem(GBAElem.VOORV_GESLACHTSNAAM,"van Der")
        .addElem(GBAElem.GESLACHTSNAAM,"Jansen")
        .addElem(GBAElem.AANDUIDING_NAAMGEBRUIK,"N")
        .addElem(GBAElem.TITEL_PREDIKAAT,"")
        .addElem(GBAElem.GESLACHTSAAND,"M")
        .toCat()
        .toPL()
        .addCat(GBACat.HUW_GPS)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.VOORV_GESLACHTSNAAM,"In Het")
        .addElem(GBAElem.GESLACHTSNAAM," Veld")
        .addElem(GBAElem.TITEL_PREDIKAAT,"")
        .addElem(GBAElem.AANDUIDING_NAAMGEBRUIK,"N")
        .addElem(GBAElem.GESLACHTSAAND,"M")
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
    assertNull(parameters.getTitleNoble());
    assertNull(parameters.getTitleNoblePartner());
    assertEquals("in het", parameters.getPrefixPartner());
    assertEquals("Veld", parameters.getLastNamePartner());

    var aanhef = new Aanhef(parameters);
    var aanschrijfWijze = new AanschrijfWijze(parameters);
    var gebruik = new GebruikInLopendeTekst(parameters);
    var regel = new RegelVoorafGaandAanAanschrijfwijze(parameters);

    assertEquals("Geachte heer Van der Jansen-in het Veld", aanhef.getAanhef());
    assertEquals("F.H.N. van der Jansen-in het Veld", aanschrijfWijze.getAanschrijfwijze());
    assertEquals("de heer Van der Jansen-in het Veld", gebruik.getUsageInText());
    assertNull(regel.getRegelVoorafgaand(partner));
  }

  @Test
  public void fullnameParametersTest4() {
    var pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.PERSOON)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.VOORNAMEN,"")
        .addElem(GBAElem.VOORV_GESLACHTSNAAM,"")
        .addElem(GBAElem.GESLACHTSNAAM,"Jansen")
        .addElem(GBAElem.AANDUIDING_NAAMGEBRUIK,"N")
        .addElem(GBAElem.TITEL_PREDIKAAT,"")
        .addElem(GBAElem.GESLACHTSAAND,"M")
        .toCat()
        .toPL()
        .addCat(GBACat.HUW_GPS)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.VOORV_GESLACHTSNAAM,"In Het")
        .addElem(GBAElem.GESLACHTSNAAM," Veld")
        .addElem(GBAElem.TITEL_PREDIKAAT,"")
        .addElem(GBAElem.AANDUIDING_NAAMGEBRUIK,"N")
        .addElem(GBAElem.GESLACHTSAAND,"M")
        .toCat()
        .build();

    var person = pl.getCurrentRec(GBACat.PERSOON).orElse(null);
    var partner = pl.getCurrentRec(GBACat.HUW_GPS).orElse(null);
    assert person != null;
    assert partner != null;
    var parameters = new FullNameParameters(person, partner);

    assertEquals("", parameters.getInitials());
    assertEquals("", parameters.getFirstNames());
    assertEquals("", parameters.getPrefix());
    assertEquals("Jansen", parameters.getLastName());
    assertEquals("MAN", parameters.getGender().toString());
    assertEquals("N", parameters.getFullNameUsage().toString());
    assertNull(parameters.getTitleNoble());
    assertNull(parameters.getTitleNoblePartner());
    assertEquals("in het", parameters.getPrefixPartner());
    assertEquals("Veld", parameters.getLastNamePartner());

    var aanhef = new Aanhef(parameters);
    var aanschrijfWijze = new AanschrijfWijze(parameters);
    var gebruik = new GebruikInLopendeTekst(parameters);
    var regel = new RegelVoorafGaandAanAanschrijfwijze(parameters);

    assertEquals("Geachte heer Jansen-in het Veld", aanhef.getAanhef());
    assertEquals("Jansen-in het Veld", aanschrijfWijze.getAanschrijfwijze());
    assertEquals("de heer Jansen-in het Veld", gebruik.getUsageInText());
    assertNull(regel.getRegelVoorafgaand(partner));
  }

  @Test
  public void fullnameParametersTest5() {
    var pl = CustomGbaWsPersonList.builder()
        // Current
        .addCat(GBACat.PERSOON)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.VOORNAMEN,"Suzanne")
        .addElem(GBAElem.VOORV_GESLACHTSNAAM,"In Het")
        .addElem(GBAElem.GESLACHTSNAAM," Veld")
        .addElem(GBAElem.TITEL_PREDIKAAT,"JV")
        .addElem(GBAElem.AANDUIDING_NAAMGEBRUIK,"V")
        .addElem(GBAElem.GESLACHTSAAND,"V")
        .toCat()
        .toPL()
        .addCat(GBACat.HUW_GPS)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.VOORNAMEN,"Pieter")
        .addElem(GBAElem.VOORV_GESLACHTSNAAM,"de")
        .addElem(GBAElem.GESLACHTSNAAM,"Jansen")
        .addElem(GBAElem.AANDUIDING_NAAMGEBRUIK,"E")
        .addElem(GBAElem.TITEL_PREDIKAAT,"")
        .addElem(GBAElem.GESLACHTSAAND,"M")
        .toCat()
        .build();

    var person = pl.getCurrentRec(GBACat.PERSOON).orElse(null);
    var partner = pl.getCurrentRec(GBACat.HUW_GPS).orElse(null);
    assert person != null;
    assert partner != null;
    var parameters = new FullNameParameters(person, partner);

    assertEquals("S.", parameters.getInitials());
    assertEquals("Suzanne", parameters.getFirstNames());
    assertEquals("in het", parameters.getPrefix());
    assertEquals("Veld", parameters.getLastName());
    assertEquals("VROUW", parameters.getGender().toString());
    assertEquals("V", parameters.getFullNameUsage().toString());
    assertEquals("jonkvrouw", parameters.getTitleNoble().getDescription());
    assertNull(parameters.getTitleNoblePartner());
    assertEquals("de", parameters.getPrefixPartner());
    assertEquals("Jansen", parameters.getLastNamePartner());

    var aanhef = new Aanhef(parameters);
    var aanschrijfWijze = new AanschrijfWijze(parameters);
    var gebruik = new GebruikInLopendeTekst(parameters);
    var regel = new RegelVoorafGaandAanAanschrijfwijze(parameters);

    assertEquals("Hoogwelgeboren vrouwe", aanhef.getAanhef());
    assertEquals("S. de Jansen-jonkvrouw in het Veld", aanschrijfWijze.getAanschrijfwijze());
    assertEquals("de Jansen-jonkvrouw in het Veld", gebruik.getUsageInText());
    assertNull(regel.getRegelVoorafgaand(partner));
  }
}
