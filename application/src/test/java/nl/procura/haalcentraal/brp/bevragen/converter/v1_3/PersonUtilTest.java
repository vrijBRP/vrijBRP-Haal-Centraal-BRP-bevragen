package nl.procura.haalcentraal.brp.bevragen.converter.v1_3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.core.enums.GBARecStatus;
import nl.procura.gbaws.web.rest.v2.personlists.CustomGbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;

public class PersonUtilTest {

  @Test
  public void mustHaveTwoParents() {

    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.OUDER_1)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen")
        .toPL()
        .addCat(GBACat.OUDER_2)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Vries")
        .build();

    assertEquals(2, PersonUtils.getParents(pl).size());
  }

  @Test
  public void mustNotAllowIncorrect() {

    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.OUDER_1)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen")
        .toPL()
        .addCat(GBACat.OUDER_2)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Vries")
        .addElem(GBAElem.IND_ONJUIST, "030000")
        .build();

    assertEquals(1, PersonUtils.getParents(pl).size());
  }

  @Test
  public void mustNotAllowUnknownRelative() {

    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.OUDER_1)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen")
        .toPL()
        //
        .addCat(GBACat.OUDER_2)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, ".")
        .build();

    assertEquals(1, PersonUtils.getParents(pl).size());
  }

  @Test
  public void mustHaveTwoChildren() {

    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.KINDEREN)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen")
        .toCat()
        //
        .addSet(2)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen")
        .toCat()
        //
        .addSet(3)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen")
        .addElem(GBAElem.REG_BETREKK, "L")
        .toCat()
        //
        .addSet(4)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, ".")
        //
        .toCat()
        .addSet(5)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen")
        .addElem(GBAElem.IND_ONJUIST, "123")
        .build();

    assertEquals(2, PersonUtils.getChildren(pl).size());
  }

  @Test
  public void mustHaveOnePartner() {

    GbaWsPersonList pl = CustomGbaWsPersonList.builder()
        .addCat(GBACat.HUW_GPS)
        .addSet(1)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Jansen")
        .addElem(GBAElem.REDEN_ONTBINDING, "S")
        .toCat()
        //
        .addSet(2)
        .addRecord(1, GBARecStatus.CURRENT)
        .addElem(GBAElem.GESLACHTSNAAM, "Vries")
        .addElem(GBAElem.REDEN_ONTBINDING, "S")
        .toCat()
        .build();

    assertEquals("Jansen", PersonUtils.getMostRecentPartner(pl)
        .map(s -> s.getRec().getElemValue(GBAElem.GESLACHTSNAAM))
        .orElse(null));
    assertEquals(0, PersonUtils.getPartners(pl).size());
  }
}
