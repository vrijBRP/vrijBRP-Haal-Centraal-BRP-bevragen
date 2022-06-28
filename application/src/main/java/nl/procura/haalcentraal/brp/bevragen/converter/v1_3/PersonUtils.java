package nl.procura.haalcentraal.brp.bevragen.converter.v1_3;

import static nl.procura.burgerzaken.gba.core.enums.GBACat.*;
import static nl.procura.burgerzaken.gba.core.enums.GBAElem.*;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.OnderzoekUtils.toGezagsverhoudingInOnderzoek;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.OnderzoekUtils.toNationaliteitInOnderzoek;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.Util.*;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListCat;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.AanduidingBijzonderNederlanderschap;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.GeslachtAanduiding;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.IndicatieGezagEnum;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.Opschorting;
import nl.procura.haalcentraal.brp.bevragen.model.BsnParam;
import nl.procura.haalcentraal.brp.bevragen.service.PersonWsService;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.*;

public class PersonUtils {

  /**
   * Returns the parent. This includes deceased parents
   */
  public static List<ParentSource> getParents(GbaWsPersonList pl) {
    return pl.getCats().stream()
        .filter(cat -> GBACat.getByCode(cat.getCode()).is(OUDER_1, OUDER_2))
        .map(cat -> cat.getCurrentRec()
            .filter(PersonUtils::isCorrect)
            .filter(PersonUtils::isValidName)
            .map(rec -> ParentSource.of(pl, rec, GBACat.getByCode(cat.getCode()))))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  public static Optional<ParentSource> getParentById(GbaWsPersonList pl, String id) {
    return getParents(pl).stream().filter(p -> p.isId(id)).findFirst();
  }

  /**
  * Returns the children. This includes deceased children, but not stillborn children
  */
  public static List<PersonSource> getChildren(GbaWsPersonList pl) {
    List<PersonSource> sources = new ArrayList<>();
    pl.getCat(KINDEREN)
        .map(GbaWsPersonListCat::getSets)
        .ifPresent(setList -> setList.forEach(set -> set.getCurrentRec()
            .filter(PersonUtils::isCorrect)
            .filter(PersonUtils::isValidName)
            .filter(PersonUtils::isNotStillborn)
            .ifPresent(rec -> sources.add(PersonSource.of(pl, rec, sources.size() + 1)))));
    return sources;
  }

  /**
   * If a child is stillborn then element 8910 is filled with 'L'
   */
  private static boolean isNotStillborn(GbaWsPersonListRec rec) {
    return rec.getElem(REG_BETREKK).isEmpty();
  }

  public static Optional<PersonSource> getChildById(GbaWsPersonList pl, String id) {
    return getChildren(pl).stream().filter(p -> p.isId(id)).findFirst();
  }

  /**
   * The most recent partner. This could be a former partner after a death or divorce.
   */
  public static Optional<PersonSource> getMostRecentPartner(GbaWsPersonList pl) {
    return pl.getCat(HUW_GPS)
        .flatMap(cat -> cat.getSets()
            .stream()
            .filter(set -> isTrue(set.getMostRecentMarriage()))
            .findFirst()
            .flatMap(set -> set.getCurrentRec()
                .filter(PersonUtils::isCorrect)
                .filter(PersonUtils::isValidName)
                .map(rec -> PersonSource.of(pl, rec, 1))));
  }

  public static Optional<PersonSource> getPartnerById(GbaWsPersonList pl, String id) {
    return getPartners(pl).stream().filter(p -> p.isId(id)).findFirst();
  }

  public static List<PersonSource> getPartners(GbaWsPersonList pl) {
    List<PersonSource> sources = new ArrayList<>();
    pl.getCat(HUW_GPS)
        .map(GbaWsPersonListCat::getSets)
        .ifPresent(setList -> setList.forEach(set -> set.getCurrentRec()
            .filter(PersonUtils::isCorrect)
            .filter(PersonUtils::isValidName)
            .filter(PersonUtils::isCommitmentNotEnded)
            .ifPresent(rec -> sources.add(PersonSource.of(pl, rec, sources.size() + 1)))));
    return sources;
  }

  public static boolean isDeceased(GbaWsPersonList pl) {
    return pl.getCurrentRec(GBACat.OVERL)
        .filter(PersonUtils::isCorrect)
        .filter(rec -> rec.getElem(DATUM_OVERL).isPresent())
        .isPresent();
  }

  public static Boolean toRelatedGeheimhouding(PersonWsService personWsService, String bsn) {
    if (isBlank(bsn)) {
      return null;
    }
    return Optional.ofNullable(personWsService.getInternal(new BsnParam(bsn)))
        .map(PersonUtils::toGeheimhouding)
        .orElse(null);
  }

  public static Boolean toGeheimhouding(GbaWsPersonList pl) {
    return pl.getCurrentRec(GBACat.INSCHR)
        .filter(PersonUtils::isCorrect)
        .filter(rec -> rec.getElem(IND_GEHEIM).isPresent())
        .map(rec -> NumberUtils.toInt(rec.getElemValue(IND_GEHEIM), -1) > 0 ? true : null)
        .orElse(null);
  }

  public static boolean isCorrect(GbaWsPersonListRec rec) {
    return rec.getElem(IND_ONJUIST).isEmpty();
  }

  private static boolean isCommitmentNotEnded(GbaWsPersonListRec rec) {
    return rec.getElem(REDEN_ONTBINDING).isEmpty();
  }

  /**
   * Name has to be filled. Value "." means unknown, but the person does exists.
   */
  private static boolean isValidName(GbaWsPersonListRec rec) {
    return rec.getElem(GESLACHTSNAAM).isPresent() && !rec.getElemValue(GESLACHTSNAAM).equals(".");
  }

  public static DatumOnvolledig toDatumEersteInschrijvingGBA(PersonSource source) {
    return source.getPl().getCurrentRec(GBACat.INSCHR)
        .filter(PersonUtils::isCorrect)
        .map(rec -> toDatumOnvolledig(rec.getElemValue(DATUM_EERSTE_INSCHR_GBA))).orElse(null);
  }

  public static GeslachtEnum toGeslachtsAanduiding(final GbaWsPersonListRec rec) {
    return Optional.ofNullable(GeslachtAanduiding.fromCode(rec.getElemValue(GESLACHTSAAND)))
        .map(GeslachtAanduiding::getType)
        .orElse(null);
  }

  public static OpschortingBijhouding toOpschorting(PersonSource source) {
    return source.getPl().getCurrentRec(GBACat.INSCHR)
        .filter(PersonUtils::isCorrect)
        .filter(rec -> rec.getElem(DATUM_OPSCH_BIJHOUD).isPresent())
        .map(rec -> new OpschortingBijhouding()
            .datum(toDatumOnvolledig(rec.getElemValue(DATUM_OPSCH_BIJHOUD)))
            .reden(Opschorting.fromCode(rec.getElemValue(OMSCHR_REDEN_OPSCH_BIJHOUD))))
        .orElse(null);
  }

  public static Kiesrecht toKiesrecht(PersonSource source) {
    return source.getPl().getCurrentRec(KIESR)
        .filter(PersonUtils::isCorrect)
        .filter(rec -> getEuroKiesrecht(rec).isPresent() || getKiesrecht(rec).isPresent())
        .map(rec -> new Kiesrecht()
            .europeesKiesrecht(getEuroKiesrecht(rec).orElse(null))
            .uitgeslotenVanKiesrecht(getKiesrecht(rec).orElse(null))
            .einddatumUitsluitingEuropeesKiesrecht(filterHistorischeDatum(toDatumOnvolledig(
                rec.getElemValue(EINDDATUM_UITSL_EURO_KIESR))))
            .einddatumUitsluitingKiesrecht(filterHistorischeDatum(toDatumOnvolledig(
                rec.getElemValue(EINDDATUM_UITSLUIT_KIESR)))))
        .orElse(null);
  }

  private static Optional<Boolean> getEuroKiesrecht(GbaWsPersonListRec rec) {
    var aand = rec.getElem(AANDUIDING_EURO_KIESR);
    var eind = toDatumOnvolledig(rec.getElemValue(EINDDATUM_UITSL_EURO_KIESR));
    if (aand.isPresent()) {
      if (Util.isHistoricDate(eind)) {
        return Optional.empty();
      }
      return Optional.of(aand.get().getValue().getVal().equals("2"));
    }
    return Optional.empty();
  }

  private static Optional<Boolean> getKiesrecht(GbaWsPersonListRec rec) {
    var aand = rec.getElem(AAND_UITGESLOTEN_KIESR);
    var eind = toDatumOnvolledig(rec.getElemValue(EINDDATUM_UITSLUIT_KIESR));
    if (aand.isPresent()) {
      if (Util.isHistoricDate(eind)) {
        return Optional.empty();
      }
      return Optional.of(true);
    }
    return Optional.empty();
  }

  public static Overlijden toOverlijden(final PersonSource source) {
    return source.getPl().getCurrentRec(GBACat.OVERL)
        .filter(PersonUtils::isCorrect)
        .filter(rec -> rec.getElem(DATUM_OVERL).isPresent())
        .map(rec -> new Overlijden()
            .indicatieOverleden(rec.getElem(DATUM_OVERL).isPresent())
            .datum(toDatumOnvolledig(rec.getElemValue(DATUM_OVERL)))
            .plaats(Util.toWaarde(rec.getElem(PLAATS_OVERL)))
            .land(Util.toWaarde(rec.getElem(LAND_OVERL)))
            .inOnderzoek(OnderzoekUtils.toOverlijdenInOnderzoek(rec)))
        .orElse(null);
  }

  public static Gezagsverhouding toGezagsverhouding(PersonSource source) {
    return source.getPl().getCurrentRec(GEZAG)
        .filter(rec -> rec.getElem(IND_CURATELE_REG).isPresent()
            || rec.getElem(IND_GEZAG_MINDERJ).isPresent())
        .filter(PersonUtils::isCorrect)
        .map(rec -> new Gezagsverhouding()
            .indicatieCurateleRegister(Util.toBooleanNull(rec.getElem(IND_CURATELE_REG)))
            .indicatieGezagMinderjarige(IndicatieGezagEnum.getType(rec.getElemValue(IND_GEZAG_MINDERJ)))
            .inOnderzoek(toGezagsverhoudingInOnderzoek(rec)))
        .orElse(null);
  }

  public static List<Nationaliteit> getNationaliteiten(PersonSource source) {
    List<Nationaliteit> nationaliteitList = new ArrayList<>();
    source.getPl().getCat(NATIO)
        .map(GbaWsPersonListCat::getSets)
        .ifPresent(setList -> setList.forEach(set -> set.getCurrentRec()
            .filter(PersonUtils::isCorrect)
            .filter(rec -> rec.getElem(REDEN_EINDE_NATIO).isEmpty())
            .map(rec -> nationaliteitList.add(new Nationaliteit()
                .nationaliteit(toWaarde(rec.getElem(NATIONALITEIT)))
                .aanduidingBijzonderNederlanderschap(AanduidingBijzonderNederlanderschap
                    .fromCode(rec.getElemValue(AAND_BIJZ_NL_SCHAP)))
                .datumIngangGeldigheid(Util.toDatumOnvolledig(rec.getElemValue(INGANGSDAT_GELDIG)))
                .redenOpname(toWaarde(rec.getElem(REDEN_OPN_NATIO)))
                .inOnderzoek(toNationaliteitInOnderzoek(rec))))));
    return nationaliteitList;
  }
}
