package nl.procura.haalcentraal.brp.bevragen.model;

import static java.util.Optional.ofNullable;
import static nl.procura.burgerzaken.gba.core.enums.GBACat.VB;
import static nl.procura.burgerzaken.gba.core.enums.GBAElem.*;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.Util.*;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.AanduidingBijHuisnummer.fromCode;
import static nl.procura.haalcentraal.brp.bevragen.model.FullNameParameters.HYPHEN;
import static org.apache.commons.lang3.StringUtils.*;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListElem;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.PersonSource;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.Util;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.WoonAdres;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.Verblijfplaats;

import lombok.Data;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Address {

  private static String         adresregel1;
  private static String         adresregel2;
  private static String         adresregel3;
  private static Verblijfplaats verblijfplaats;

  public static Verblijfplaats toAddress(final PersonSource source) {
    verblijfplaats = new Verblijfplaats();
    var optRec = source.getPl().getCurrentRec(VB);

    if (optRec.isPresent()) {
      var rec = optRec.get();

      verblijfplaats.indicatieVestigingVanuitBuitenland(Util.toBooleanNull(rec.getElem(DATUM_VESTIGING_IN_NL)))
          .datumAanvangAdreshouding(Util.toDatumOnvolledig(rec.getElemValue(DATUM_AANVANG_ADRESH)))
          .datumIngangGeldigheid(Util.toDatumOnvolledig(rec.getElemValue(INGANGSDAT_GELDIG)))
          .datumVestigingInNederland(Util.toDatumOnvolledig(rec.getElemValue(DATUM_VESTIGING_IN_NL)))
          .landVanwaarIngeschreven(Util.toWaarde(rec.getElem(LAND_VESTIGING)))
          .datumInschrijvingInGemeente(Util.toDatumOnvolledig(rec.getElemValue(DATUM_INSCHR)));

      var foreignAddress = rec.getElem(LAND_VERTREK);
      if (foreignAddress.isPresent()) {
        getForeignAddress(rec);
      } else {
        getLocalAddress(rec);
      }
    }
    return verblijfplaats;
  }

  private static void getForeignAddress(GbaWsPersonListRec rec) {
    verblijfplaats.land(toWaarde(rec.getElem(LAND_VERTREK)));
    if (isWaardeOnbekend(rec.getElem(LAND_VERTREK))) {
      createUnknownAddress();
    } else {
      createForeignAddress(rec);
    }
  }

  private static void getLocalAddress(GbaWsPersonListRec rec) {
    verblijfplaats
        .functieAdres(WoonAdres.fromCode(rec.getElemValue(FUNCTIE_ADRES)))
        .gemeenteVanInschrijving(toWaarde(rec.getElem(GEM_INSCHR)))
        .woonplaats(ofNullable(rec.getElemDescr(WPL_NAAM)).orElse(rec.getElemDescr(GEM_INSCHR)));

    Optional<GbaWsPersonListElem> locationDescription = rec.getElem(LOCATIEBESCHR);
    if (locationDescription.isPresent()) {
      createAddressWithLocation(rec);
    } else {
      var bagAddress = rec.getElem(ID_VERBLIJFPLAATS);
      var addressWithIndication = rec.getElem(HNR_A);
      verblijfplaats.korteNaam(rec.getElemValue(STRAATNAAM));
      boolean puntAdres = (".".equals(rec.getElemValue(STRAATNAAM)));
      if (puntAdres) {
        createUnknownAddress();
      } else {
        verblijfplaats.straat(ofNullable(rec.getElemDescr(OPENB_RUIMTE)).orElse(rec.getElemDescr(STRAATNAAM)))
            .huisnummer(Integer.valueOf(rec.getElemValue(HNR)))
            .huisletter(rec.getElemDescr(HNR_L))
            .huisnummertoevoeging(rec.getElemValue(HNR_T))
            .postcode(rec.getElemValue(POSTCODE));
        if (bagAddress.isPresent()) {
          verblijfplaats.adresseerbaarObjectIdentificatie(rec.getElemValue(ID_VERBLIJFPLAATS))
              .nummeraanduidingIdentificatie(rec.getElemValue(IDCODE_NUMMERAAND));
          createLocalAddress(rec);
        } else if (addressWithIndication.isPresent()) {
          verblijfplaats.aanduidingBijHuisnummer(fromCode(rec.getElemValue(HNR_A)));
          createAddressWithIndication(rec);
        } else {
          createLocalAddress(rec);
        }
      }
    }
  }

  private static void createAddressWithLocation(GbaWsPersonListRec rec) {
    verblijfplaats.locatiebeschrijving(rec.getElemDescr(LOCATIEBESCHR))
        .adresregel1(verblijfplaats.getLocatiebeschrijving())
        .adresregel2(verblijfplaats.getWoonplaats());
  }

  private static void createForeignAddress(GbaWsPersonListRec rec) {
    verblijfplaats.vanuitVertrokkenOnbekendWaarheen(null)
        .adresregel1(rec.getElemValue(ADRES_BUITENL_1))
        .adresregel2(rec.getElemValue(ADRES_BUITENL_2))
        .adresregel3(rec.getElemValue(ADRES_BUITENL_3));
  }

  private static void createLocalAddress(GbaWsPersonListRec rec) {
    var addressLine1 = new StringBuilder();
    var addressLine2 = new StringBuilder();
    var huisnummertoevoeging = new StringBuilder();
    if (isNotBlank(verblijfplaats.getHuisnummertoevoeging())) {
      huisnummertoevoeging.append(HYPHEN)
          .append(trimToEmpty(verblijfplaats.getHuisnummertoevoeging()));
    }
    getAddressLine1(rec, addressLine1, huisnummertoevoeging.toString());
    getAddressLine2(rec, addressLine2);
  }

  private static void createAddressWithIndication(GbaWsPersonListRec rec) {
    var addressLine1 = new StringBuilder();
    var addressLine2 = new StringBuilder();
    if (isNotBlank(verblijfplaats.getAanduidingBijHuisnummer().toString())) {
      addressLine1
          .append(trimToEmpty(verblijfplaats.getAanduidingBijHuisnummer().toString()))
          .append(" ");
    }
    getAddressLine1(rec, addressLine1, "");
    getAddressLine2(rec, addressLine2);
  }

  private static void getAddressLine1(GbaWsPersonListRec rec, StringBuilder addressLine1, String s) {
    addressLine1.append(rec.getElemValue(STRAATNAAM))
        .append(" ")
        .append(trimToEmpty(rec.getElemValue(HNR)))
        .append(trimToEmpty(verblijfplaats.getHuisletter()))
        .append(s);
    verblijfplaats.adresregel1(addressLine1.toString().trim());
  }

  private static void getAddressLine2(GbaWsPersonListRec rec, StringBuilder addressLine2) {
    addressLine2.append(rec.getElemValue(POSTCODE))
        .append(" ")
        .append(verblijfplaats.getWoonplaats());
    verblijfplaats.adresregel2(addressLine2.toString().trim());
  }

  private static void createUnknownAddress() {
    verblijfplaats.vertrokkenOnbekendWaarheen(true)
        .adresregel1("Onbekend")
        .adresregel2("Onbekend")
        .adresregel3(null);
  }

}
