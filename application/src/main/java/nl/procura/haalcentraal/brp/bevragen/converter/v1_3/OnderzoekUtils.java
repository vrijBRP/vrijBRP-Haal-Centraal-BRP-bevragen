package nl.procura.haalcentraal.brp.bevragen.converter.v1_3;

import static nl.procura.burgerzaken.gba.core.enums.GBAElem.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import org.springframework.util.StringUtils;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.core.enums.GBAGroup;
import nl.procura.burgerzaken.gba.core.enums.GBAGroupElements;
import nl.procura.commons.core.utils.ProNumberUtils;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListElem;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.*;

public class OnderzoekUtils {

  public static AangaanHuwelijkPartnerschapInOnderzoek toAangaanHuwPrtInOnderzoek(GbaWsPersonListRec rec) {
    if (isInOnderzoek(rec, GEBOORTEDATUM, GEBOORTEPLAATS, GEBOORTELAND)) {
      return new AangaanHuwelijkPartnerschapInOnderzoek()
          .datum(getInOnderzoek(rec, DATUM_VERBINTENIS))
          .plaats(getInOnderzoek(rec, PLAATS_VERBINTENIS))
          .land(getInOnderzoek(rec, LAND_VESTIGING))
          .datumIngangOnderzoek(Util.toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)));
    }
    return null;
  }

  public static GeboorteInOnderzoek toGeboorteInOnderzoek(GbaWsPersonListRec rec) {
    if (isInOnderzoek(rec, GEBOORTEDATUM, GEBOORTEPLAATS, GEBOORTELAND)) {
      return new GeboorteInOnderzoek()
          .datum(getInOnderzoek(rec, GEBOORTEDATUM))
          .plaats(getInOnderzoek(rec, GEBOORTEPLAATS))
          .land(getInOnderzoek(rec, GEBOORTELAND))
          .datumIngangOnderzoek(Util.toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)));
    }
    return null;
  }

  public static GezagsverhoudingInOnderzoek toGezagsverhoudingInOnderzoek(GbaWsPersonListRec rec) {
    if (isInOnderzoek(rec, IND_CURATELE_REG, IND_GEZAG_MINDERJ)) {
      return new GezagsverhoudingInOnderzoek()
          .indicatieCurateleRegister(getInOnderzoek(rec, IND_CURATELE_REG))
          .indicatieGezagMinderjarige(getInOnderzoek(rec, IND_GEZAG_MINDERJ))
          .datumIngangOnderzoek(Util.toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)));
    }
    return null;
  }

  public static KindInOnderzoek toKindInOnderzoek(GbaWsPersonListRec rec) {
    if (isInOnderzoek(rec, BSN)) {
      return new KindInOnderzoek()
          .burgerservicenummer(getInOnderzoek(rec, BSN))
          .datumIngangOnderzoek(Util.toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)));
    }
    return null;
  }

  public static NaamInOnderzoek toNaamInOnderzoek(GbaWsPersonListRec rec) {
    if (isInOnderzoek(rec, GESLACHTSNAAM, VOORNAMEN, VOORV_GESLACHTSNAAM, TITEL_PREDIKAAT)) {
      return new NaamInOnderzoek()
          .geslachtsnaam(getInOnderzoek(rec, GESLACHTSNAAM))
          .voornamen(getInOnderzoek(rec, VOORNAMEN))
          .voorvoegsel(getInOnderzoek(rec, VOORV_GESLACHTSNAAM))
          .adellijkeTitelPredikaat(getInOnderzoek(rec, TITEL_PREDIKAAT))
          .datumIngangOnderzoek(Util.toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)));
    }
    return null;
  }

  public static NaamPersoonInOnderzoek toNaamPersoonInOnderzoek(GbaWsPersonListRec rec) {
    if (isInOnderzoek(rec, AANDUIDING_NAAMGEBRUIK, GESLACHTSNAAM, VOORNAMEN, VOORV_GESLACHTSNAAM, TITEL_PREDIKAAT)) {
      return (NaamPersoonInOnderzoek) new NaamPersoonInOnderzoek()
          .aanduidingNaamgebruik(getInOnderzoek(rec, AANDUIDING_NAAMGEBRUIK))
          .geslachtsnaam(getInOnderzoek(rec, GESLACHTSNAAM))
          .voornamen(getInOnderzoek(rec, VOORNAMEN))
          .voorvoegsel(getInOnderzoek(rec, VOORV_GESLACHTSNAAM))
          .adellijkeTitelPredikaat(getInOnderzoek(rec, TITEL_PREDIKAAT))
          .datumIngangOnderzoek(Util.toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)));
    }
    return null;
  }

  // TODO: Nationaliteiten moet nog worden uitgewerkt
  public static NationaliteitInOnderzoek toNationaliteitInOnderzoek(GbaWsPersonListRec rec) {
    if (isInOnderzoek(rec, AAND_BIJZ_NL_SCHAP, NATIONALITEIT, REDEN_OPN_NATIO)) {
      return new NationaliteitInOnderzoek()
          .aanduidingBijzonderNederlanderschap(getInOnderzoek(rec, AAND_BIJZ_NL_SCHAP))
          .nationaliteit(getInOnderzoek(rec, NATIONALITEIT))
          .redenOpname(getInOnderzoek(rec, REDEN_OPN_NATIO))
          .datumIngangOnderzoek(Util.toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)));
    }
    return null;
  }

  public static OuderInOnderzoek toOuderInOnderzoek(GbaWsPersonListRec rec) {
    if (isInOnderzoek(rec, BSN, DATUM_INGANG_FAM_RECHT_BETREK, GESLACHTSAAND)) {
      return new OuderInOnderzoek()
          .burgerservicenummer(getInOnderzoek(rec, BSN))
          .datumIngangFamilierechtelijkeBetrekking(getInOnderzoek(rec, DATUM_INGANG_FAM_RECHT_BETREK))
          .geslachtsaanduiding(getInOnderzoek(rec, GESLACHTSAAND))
          .datumIngangOnderzoek(Util.toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)));
    }
    return null;
  }

  public static OverlijdenInOnderzoek toOverlijdenInOnderzoek(GbaWsPersonListRec rec) {
    if (isInOnderzoek(rec, DATUM_OVERL, PLAATS_OVERL, LAND_OVERL)) {
      return new OverlijdenInOnderzoek()
          .datumIngangOnderzoek(Util.toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)))
          .datum(getInOnderzoek(rec, DATUM_OVERL))
          .land(getInOnderzoek(rec, PLAATS_OVERL))
          .plaats(getInOnderzoek(rec, LAND_OVERL));
    }
    return null;
  }

  public static PartnerInOnderzoek toPartnerInOnderzoek(GbaWsPersonListRec rec) {
    if (isInOnderzoek(rec, BSN, GESLACHTSAAND, SOORT_VERBINTENIS)) {
      return new PartnerInOnderzoek()
          .burgerservicenummer(getInOnderzoek(rec, BSN))
          .geslachtsaanduiding(getInOnderzoek(rec, GESLACHTSAAND))
          .soortVerbintenis(getInOnderzoek(rec, SOORT_VERBINTENIS))
          .datumIngangOnderzoek(Util.toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)));
    }
    return null;
  }

  public static PersoonInOnderzoek toPersoonInOnderzoek(GbaWsPersonListRec rec) {
    if (isInOnderzoek(rec, BSN, GESLACHTSAAND)) {
      return new PersoonInOnderzoek()
          .burgerservicenummer(getInOnderzoek(rec, BSN))
          .geslachtsaanduiding(getInOnderzoek(rec, GESLACHTSAAND))
          .datumIngangOnderzoek(Util.toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)));
    }
    return null;
  }

  public static VerblijfplaatsInOnderzoek toVerblijfplaatsInOnderzoek(GbaWsPersonListRec rec) {
    if (isInOnderzoek(rec, HNR_A, DATUM_AANVANG_ADRESH, INGANGSDAT_GELDIG, DATUM_INSCHR, DATUM_VESTIGING_IN_NL,
        FUNCTIE_ADRES, GEM_INSCHR, HNR_L, HNR, HNR_T, IDCODE_NUMMERAAND, ID_VERBLIJFPLAATS, LAND_VESTIGING,
        LOCATIEBESCHR, OPENB_RUIMTE, POSTCODE, STRAATNAAM, WPL_NAAM, DATUM_VERTREK_UIT_NL, ADRES_BUITENL_1,
        ADRES_BUITENL_2, ADRES_BUITENL_3)) {
      return new VerblijfplaatsInOnderzoek()
          .datumIngangOnderzoek(Util.toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)))
          .aanduidingBijHuisnummer(getInOnderzoek(rec, HNR_A))
          .datumAanvangAdreshouding(getInOnderzoek(rec, DATUM_AANVANG_ADRESH))
          .datumIngangGeldigheid(getInOnderzoek(rec, INGANGSDAT_GELDIG))
          .datumInschrijvingInGemeente(getInOnderzoek(rec, DATUM_INSCHR))
          .datumVestigingInNederland(getInOnderzoek(rec, DATUM_VESTIGING_IN_NL))
          .functieAdres(getInOnderzoek(rec, FUNCTIE_ADRES))
          .gemeenteVanInschrijving(getInOnderzoek(rec, GEM_INSCHR))
          .huisletter(getInOnderzoek(rec, HNR_L))
          .huisnummer(getInOnderzoek(rec, HNR))
          .huisnummertoevoeging(getInOnderzoek(rec, HNR_T))
          .nummeraanduidingIdentificatie(getInOnderzoek(rec, IDCODE_NUMMERAAND))
          .adresseerbaarObjectIdentificatie(getInOnderzoek(rec, ID_VERBLIJFPLAATS))
          .landVanwaarIngeschreven(getInOnderzoek(rec, LAND_VESTIGING))
          .locatiebeschrijving(getInOnderzoek(rec, LOCATIEBESCHR))
          .straat(getInOnderzoek(rec, OPENB_RUIMTE))
          .postcode(getInOnderzoek(rec, POSTCODE))
          .korteNaam(getInOnderzoek(rec, STRAATNAAM))
          .woonplaats(getInOnderzoek(rec, WPL_NAAM))
          .verblijfBuitenland(getInOnderzoek(rec,
              DATUM_VERTREK_UIT_NL,
              LAND_VERTREK,
              ADRES_BUITENL_1,
              ADRES_BUITENL_2,
              ADRES_BUITENL_3));
    }
    return null;
  }

  // TODO: Verblijfstitel moet nog worden uitgewerkt
  public static VerblijfstitelInOnderzoek toVerblijfstitelInOnderzoek(GbaWsPersonListRec rec) {
    if (isInOnderzoek(rec, BSN, DATUM_EINDE_VBT, INGANGSDATUM_VBT)) {
      return new VerblijfstitelInOnderzoek()
          .aanduiding(getInOnderzoek(rec, BSN))
          .datumEinde(getInOnderzoek(rec, DATUM_EINDE_VBT))
          .datumIngang(getInOnderzoek(rec, INGANGSDATUM_VBT))
          .datumIngangOnderzoek(Util.toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)));
    }
    return null;
  }

  private static Boolean getInOnderzoek(GbaWsPersonListRec rec, GBAElem... elems) {
    return isInOnderzoek(rec, elems) ? true : null;
  }

  private static boolean isInOnderzoek(GbaWsPersonListRec rec, GBAElem... elems) {
    String aand = rec.getElemValue(GBAElem.AAND_GEG_IN_ONDERZ);
    Optional<GbaWsPersonListElem> eindeOnderz = rec.getElem(GBAElem.DATUM_EINDE_ONDERZ);
    if (StringUtils.hasText(aand) && aand.length() == 6 && eindeOnderz.isEmpty()) {
      if (aand.endsWith("0000")) {// Category under investigation
        GBACat cat = GBACat.getByCode(ProNumberUtils.toInt(aand.substring(0, 2)));
        return Arrays.stream(elems)
            .map(elem -> GBAGroupElements.getByCat(cat.getCode(), elem.getCode()))
            .anyMatch(Objects::nonNull);

      } else if (aand.endsWith("00")) { // Group in category under investigation
        GBACat cat = GBACat.getByCode(ProNumberUtils.toInt(aand.substring(0, 2)));
        GBAGroup group = GBAGroup.getByCode(ProNumberUtils.toInt(aand.substring(2, 4)));
        return Arrays.stream(elems)
            .map(elem -> GBAGroupElements.getByCat(cat.getCode(), elem.getCode()))
            .filter(Objects::nonNull)
            .filter(groupElem -> groupElem.getGroup() == group)
            .anyMatch(obj -> true);

      } else { // Element under investigation
        GBACat cat = GBACat.getByCode(ProNumberUtils.toInt(aand.substring(0, 2)));
        GBAGroup group = GBAGroup.getByCode(ProNumberUtils.toInt(aand.substring(2, 4)));
        GBAElem element = GBAElem.getByCode(ProNumberUtils.toInt(aand.substring(2, 6)));

        return Arrays.stream(elems)
            .map(elem -> GBAGroupElements.getByCat(cat.getCode(), elem.getCode()))
            .filter(Objects::nonNull)
            .filter(groupElem -> groupElem.getGroup() == group)
            .filter(groupElem -> groupElem.getElem() == element)
            .anyMatch(obj -> true);
      }
    }
    return false;
  }
}
