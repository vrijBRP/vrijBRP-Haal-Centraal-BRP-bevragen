/*
 * Copyright 2021 - 2022 Procura B.V.
 *
 * In licentie gegeven krachtens de EUPL, versie 1.2
 * U mag dit werk niet gebruiken, behalve onder de voorwaarden van de licentie.
 * U kunt een kopie van de licentie vinden op:
 *
 *   https://github.com/vrijBRP/vrijBRP/blob/master/LICENSE.md
 *
 * Deze bevat zowel de Nederlandse als de Engelse tekst
 *
 * Tenzij dit op grond van toepasselijk recht vereist is of schriftelijk
 * is overeengekomen, wordt software krachtens deze licentie verspreid
 * "zoals deze is", ZONDER ENIGE GARANTIES OF VOORWAARDEN, noch expliciet
 * noch impliciet.
 * Zie de licentie voor de specifieke bepalingen voor toestemmingen en
 * beperkingen op grond van de licentie.
 */

package nl.procura.haalcentraal.brp.bevragen.converter;

import static nl.procura.burgerzaken.gba.core.enums.GBACat.*;
import static nl.procura.burgerzaken.gba.core.enums.GBAElem.*;
import static nl.procura.haalcentraal.brp.bevragen.converter.Util.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.util.StringUtils.hasText;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListCat;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListElem;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListSet;
import nl.procura.haalcentraal.brp.bevragen.converter.enums.GeslachtAandEnum;
import nl.procura.haalcentraal.brp.bevragen.converter.enums.IndicatieGezagEnum;
import nl.procura.haalcentraal.brp.bevragen.converter.enums.OpschortingEnum;
import nl.procura.haalcentraal.brp.bevragen.converter.enums.WoonAdresEnum;
import nl.procura.haalcentraal.brp.bevragen.model.exception.ExpandException;
import nl.procura.haalcentraal.brp.bevragen.model.exception.FieldsException;
import nl.procura.haalcentraal.brp.bevragen.resources.bip.IngeschrevenPersonenResourceV1;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PersoonConverterImpl
    extends BaseConverterImpl<IngeschrevenPersoonHal, PersonSource>
    implements PersoonConverter {

  private final KindConverter    kindConverter;
  private final OuderConverter   ouderConverter;
  private final PartnerConverter partnerConverter;

  public static Set<String> leaf(final Set<String> fields, String field) {

    if (fields == null) {
      return null;
    }

    return fields.stream() //
        .filter(s -> s.startsWith(field + ".")) //
        .map(s -> s.substring(field.length() + 1)) //
        .filter(StringUtils::hasText) //
        .collect(Collectors.toSet());
  }

  private final Map<String, Handler<IngeschrevenPersoonHal, PersonSource>> expandMap = new HashMap<>();

  public PersoonConverterImpl(
      NaamPersoonConverter naamPersoonConverter,
      KindConverter kindConverter,
      OuderConverter ouderConverter,
      PartnerConverter partnerConverter,
      GeboorteConverter geboorteConverter) {

    this.kindConverter = kindConverter;
    this.ouderConverter = ouderConverter;
    this.partnerConverter = partnerConverter;

    put("naam", (ip, source, fields, expand) -> ip
        .naam(naamPersoonConverter.convert(source, fields, expand)));

    put("burgerservicenummer", (persoon, source) -> persoon
        .burgerservicenummer(toBsn(source.getRec())));

    put("kiesrecht", (persoon, source) -> persoon.kiesrecht(toKiesrecht(source)));

    put("geboorte", (persoon, source, fields, expand) -> persoon
        .setGeboorte(geboorteConverter.convert(source.getRec(), fields, expand)));

    put("leeftijd", (persoon, source) -> persoon
        .leeftijd(Util.toLeeftijd(toLocalDate(source.getRec().getElemValue(GEBOORTEDATUM)),
            LocalDate.now()).orElse(null)));

    put("geslachtsaanduiding", (persoon, source) -> persoon
        .geslachtsaanduiding(toGeslachtsAanduiding(source.getRec())));

    put("overlijden", (persoon, source, fields, expand) -> persoon
        .overlijden(toOverlijden(source)));

    put("verblijfsplaats", (persoon, source, fields, expand) -> persoon
        .verblijfplaats(toVerblijfplaats(source)));

    put("datumEersteInschrijvingGBA", (persoon, source, fields, expand) -> persoon
        .datumEersteInschrijvingGBA(toDatumEersteInschrijvingGBA(source)));

    put("ouders", (persoonHal, source, fields, expand) -> toFieldsOuders(persoonHal, source, fields));
    put("kinderen", (persoonHal, source, fields, expand) -> toFieldsKinderen(persoonHal, source, fields));
    put("partners", (persoonHal, source, fields, expand) -> toFieldsPartners(persoonHal, source, fields));
    //
    expandMap.put("kinderen", (persoonHal, source, fields, expand) -> toExpandKinderen(persoonHal, source, expand));
    expandMap.put("partners", (persoonHal, persoon, fields, expand) -> toExpandPartners(persoonHal, persoon, expand));
    expandMap.put("ouders", (persoonHal, source, fields, expand) -> toExpandOuders(persoonHal, source, expand));
  }

  @Override
  public IngeschrevenPersoonHal createTarget(final PersonSource source, final Set<String> fields) {
    final IngeschrevenPersoonHal persoonHal = new IngeschrevenPersoonHal();
    persoonHal.setLinks(new IngeschrevenPersoonLinks());
    return persoonHal;
  }

  @Override
  public IngeschrevenPersoonHal convert(final PersonSource source, final IngeschrevenPersoonHal persoon,
      final Set<String> fields, final Set<String> expand) {

    // show all fields when no fields are requested
    super.convert(source, persoon, fields == null || fields.isEmpty()
        ? getMap().keySet()
        : fields,
        null);

    log.debug("fields=" + fields + ", expand=" + expand);
    persoon.getLinks().self(createIngeschrevenPersoonLink(source.getRec().getElemValue(BSN), fields, expand));

    persoon.geheimhoudingPersoonsgegevens(toGeheimhouding(source));
    persoon.opschortingBijhouding(toOpschorting(source));
    persoon.gezagsverhouding(toGezagsverhouding(source));

    return persoon;
  }

  private Gezagsverhouding toGezagsverhouding(PersonSource source) {
    return source.getPl().getCurrentRec(GEZAG)
        .map(rec -> new Gezagsverhouding()
            .indicatieCurateleRegister(rec.getElem(IND_CURATELE_REG).isPresent())
            .indicatieGezagMinderjarige(rec.getElem(IND_GEZAG_MINDERJ)
                .map(e -> IndicatieGezagEnum.getIndicatieGezagMinderjarige(e.getValue().getVal()))
                .orElse(null))
            .inOnderzoek(toGezagsverhoudingInOnderzoek(rec)))
        .orElse(null);
  }

  private OpschortingBijhouding toOpschorting(PersonSource source) {
    return source.getPl().getCurrentRec(GBACat.INSCHR)
        .filter(rec -> rec.getElem(DATUM_OPSCH_BIJHOUD).isPresent())
        .map(rec -> new OpschortingBijhouding()
            .datum(toDatumOnvolledig(rec.getElemValue(DATUM_OPSCH_BIJHOUD)))
            .reden(OpschortingEnum.fromCode(rec.getElemValue(OMSCHR_REDEN_OPSCH_BIJHOUD))))
        .orElse(null);
  }

  @Override
  public void handleExpand(final IngeschrevenPersoonHal persoonHal,
      final PersonSource plSource, final Set<String> expand) {
    log.debug("expat2=" + expand + ", keyset=" + expandMap.keySet());
    if (expand != null) {
      expand.stream() //
          .filter(expandField -> !expandMap.containsKey(expandField)
              && expandMap.keySet().stream().noneMatch(i -> expandField.startsWith(i + "."))) //
          .findAny() //
          .ifPresent(entry -> {
            throw new ExpandException(entry);
          });

      expand.stream() //
          .map(s -> {
            if (s.contains(".")) {
              return s.substring(0, s.indexOf("."));
            } else {
              return s;
            }
          })
          .distinct()
          .forEach(e -> expandMap.get(e)
              .handle(persoonHal, plSource, null, leaf(expand, e)));
    }
  }

  private void toFieldsOuders(final IngeschrevenPersoonHal ip,
      final PersonSource source,
      final Set<String> fields) {

    // no leftovers
    if (fields != null && !fields.isEmpty()) {
      throw new FieldsException(String.join(",", fields));
    }

    Stream.of(source.getPl().getCurrentRec(OUDER_1), source.getPl().getCurrentRec(OUDER_2))
        .filter(rec -> rec.isPresent() && rec.get().getElem(BSN).isPresent())
        .forEach(rec -> ip.getLinks().addOudersItem(createOuderLink(source, rec.get())));
  }

  private void toFieldsKinderen(final IngeschrevenPersoonHal ip,
      final PersonSource source, final Set<String> fields) {

    // no leftovers
    if (fields != null && !fields.isEmpty()) {
      throw new FieldsException(String.join(",", fields));
    }

    source.getPl().getCat(KINDEREN).map(GbaWsPersonListCat::getSets)
        .ifPresent(set -> set.stream().map(GbaWsPersonListSet::getCurrentRec)
            .filter(rec -> rec.isPresent() && rec.get().getElem(BSN).isPresent())
            .forEach(rec -> ip.getLinks().addKinderenItem(createKindLink(source, rec.get()))));
  }

  private void toFieldsPartners(final IngeschrevenPersoonHal ip,
      final PersonSource source,
      final Set<String> fields) {

    // no leftovers
    if (fields != null && !fields.isEmpty()) {
      throw new FieldsException(String.join(",", fields));
    }

    source.getPl().getCat(HUW_GPS).map(GbaWsPersonListCat::getSets)
        .ifPresent(set -> set.stream()
            .filter(s -> s.getMostRecentMarriage() != null && s.getMostRecentMarriage())
            .map(GbaWsPersonListSet::getCurrentRec)
            .filter(rec -> rec.isPresent() && rec.get().getElem(BSN).isPresent())
            .forEach(rec -> ip.getLinks().addPartnersItem(createPartnerLink(source, rec.get()))));
  }

  private void toExpandOuders(final IngeschrevenPersoonHal persoonHal,
      final PersonSource source,
      final Set<String> expand) {

    if (persoonHal.getEmbedded() == null) {
      persoonHal.setEmbedded(new IngeschrevenPersoonEmbedded());
    }

    ouderConverter.validate(expand);

    source.getPl().getCats()
        .stream()
        .filter(c -> EnumSet.of(OUDER_1, OUDER_2).contains(GBACat.getByCode(c.getCode())))
        .forEach(cat -> {
          Optional<GbaWsPersonListRec> rec = cat.getCurrentRec();
          if (rec.isPresent()) {
            ParentSource parentSource = new ParentSource(source.getPl(), rec.get(),
                GBACat.getByCode(cat.getCode()));
            persoonHal.getEmbedded().addOudersItem(ouderConverter.convert(parentSource, expand, null));
          }
        });
  }

  private void toExpandKinderen(final IngeschrevenPersoonHal persoonHal,
      final PersonSource source,
      final Set<String> expand) {

    if (persoonHal.getEmbedded() == null) {
      persoonHal.setEmbedded(new IngeschrevenPersoonEmbedded());
    }

    kindConverter.validate(expand);

    source.getPl().getCat(KINDEREN)
        .map(GbaWsPersonListCat::getSets)
        .ifPresent(set -> set.stream()
            .map(GbaWsPersonListSet::getCurrentRec)
            .filter(Optional::isPresent)
            .forEach(rec -> {
              PersonSource personSource = new PersonSource(source.getPl(), rec.get());
              persoonHal.getEmbedded().addKinderenItem(kindConverter.convert(personSource, expand, null));
            }));
  }

  private void toExpandPartners(final IngeschrevenPersoonHal persoonHal,
      final PersonSource source,
      final Set<String> expand) {

    if (persoonHal.getEmbedded() == null) {
      persoonHal.setEmbedded(new IngeschrevenPersoonEmbedded());
    }

    partnerConverter.validate(expand);

    Optional<GbaWsPersonListCat> cat = source.getPl().getCat(HUW_GPS);
    if (cat.isPresent()) {
      Optional<GbaWsPersonListRec> rec = cat.get().getCurrentRec();
      if (rec.isPresent()) {
        PersonSource personSource = new PersonSource(source.getPl(), rec.get());
        persoonHal.getEmbedded().addPartnersItem(partnerConverter.convert(personSource, expand, null));
      }
    }
  }

  public static HalLink createIngeschrevenPersoonLink(final String bsn, final Set<String> fields,
      final Set<String> expand) {
    return new HalLink().href(linkTo(methodOn(IngeschrevenPersonenResourceV1.class, bsn) //
        .ingeschrevenNatuurlijkPersoon(bsn,
            null,
            expand != null ? String.join(",", expand) : null,
            fields != null ? String.join(",", fields) : null))
                .toUri());
  }

  public static HalLink createOuderLink(PersonSource a, GbaWsPersonListRec rec) {
    Optional<GbaWsPersonListRec> currentRec = a.getPl().getCurrentRec(PERSOON);
    if (currentRec.isPresent()) {
      String bsn = toBsn(currentRec.get());
      String bsnParent = toBsn(rec);
      if (hasText(bsnParent) && hasText(bsn)) {
        return new HalLink().href(linkTo(methodOn(IngeschrevenPersonenResourceV1.class, bsn, bsnParent)
            .ingeschrevenpersonenBurgerservicenummeroudersUuid(bsn, bsnParent, null)).toUri());
      }
    }
    return null;
  }

  public static HalLink createKindLink(PersonSource a, GbaWsPersonListRec rec) {
    Optional<GbaWsPersonListRec> currentRec = a.getPl().getCurrentRec(PERSOON);
    if (currentRec.isPresent()) {
      String bsn = toBsn(currentRec.get());
      String bsnParent = toBsn(rec);
      if (hasText(bsnParent) && hasText(bsn)) {
        return new HalLink().href(linkTo(methodOn(IngeschrevenPersonenResourceV1.class, bsn, bsnParent)
            .ingeschrevenpersonenBurgerservicenummerkinderenUuid(bsn, bsnParent,
                null)).toUri());
      }
    }
    return null;
  }

  public static HalLink createPartnerLink(PersonSource a, GbaWsPersonListRec rec) {
    Optional<GbaWsPersonListRec> currentRec = a.getPl().getCurrentRec(PERSOON);
    if (currentRec.isPresent()) {
      String bsn = toBsn(currentRec.get());
      String bsnParent = toBsn(rec);
      if (hasText(bsnParent) && hasText(bsn)) {
        return new HalLink().href(linkTo(methodOn(IngeschrevenPersonenResourceV1.class, bsn, bsnParent)
            .ingeschrevenpersonenBurgerservicenummerpartnersUuid(bsn, bsnParent,
                null)).toUri());
      }
    }
    return null;
  }

  public static Overlijden toOverlijden(final PersonSource source) {
    Overlijden overlijden = new Overlijden()
        .indicatieOverleden(false);

    source.getPl().getCurrentRec(GBACat.OVERL)
        .ifPresent(rec -> overlijden.indicatieOverleden(rec.getElem(DATUM_OVERL).isPresent())
            .datum(toDatumOnvolledig(rec.getElemValue(DATUM_OVERL)))
            .plaats(toWaarde(rec.getElem(PLAATS_OVERL)))
            .land(toWaarde(rec.getElem(LAND_OVERL)))
            .inOnderzoek(toOverlijdenInOnderzoek(rec)));
    return overlijden;
  }

  public static Verblijfplaats toVerblijfplaats(final PersonSource source) {
    Verblijfplaats verblijfplaats = null;
    Optional<GbaWsPersonListRec> optRec = source.getPl().getCurrentRec(GBACat.VB);
    if (optRec.isPresent()) {
      GbaWsPersonListRec rec = optRec.get();
      verblijfplaats = new Verblijfplaats()
          .identificatiecodeVerblijfplaats(rec.getElemValue(ID_VERBLIJFPLAATS))
          .indicatieVestigingVanuitBuitenland(rec.getElem(DATUM_VESTIGING_IN_NL).isPresent())
          .locatiebeschrijving(rec.getElemDescr(LOCATIEBESCHR))
          .straatnaam(rec.getElemDescr(STRAATNAAM))
          .datumAanvangAdreshouding(toDatumOnvolledig(rec.getElemValue(DATUM_AANVANG_ADRESH)))
          .datumIngangGeldigheid(toDatumOnvolledig(rec.getElemValue(INGANGSDAT_GELDIG)))
          .datumVestigingInNederland(toDatumOnvolledig(rec.getElemValue(DATUM_VESTIGING_IN_NL)))
          .gemeenteVanInschrijving(toWaarde(rec.getElem(GEM_INSCHR)))
          .landVanwaarIngeschreven(toWaarde(rec.getElem(LAND_VESTIGING)))
          .inOnderzoek(toVerblijfplaatsInOnderzoek(rec));

      Optional<GbaWsPersonListElem> landVertrek = rec.getElem(LAND_VERTREK);

      if (landVertrek.isPresent()) {
        verblijfplaats.vanuitVertrokkenOnbekendWaarheen(isWaardeOnbekend(landVertrek))
            .verblijfBuitenland(new VerblijfBuitenland()
                .vertrokkenOnbekendWaarheen(false)
                .land(toWaarde(landVertrek))
                .adresRegel1(rec.getElemValue(ADRES_BUITENL_1))
                .adresRegel2(rec.getElemValue(ADRES_BUITENL_2))
                .adresRegel3(rec.getElemValue(ADRES_BUITENL_3)));
      }

      verblijfplaats.functieAdres(WoonAdresEnum.fromCode(rec.getElemValue(FUNCTIE_ADRES)))
          .huisletter(rec.getElemValue(HNR_L))
          .huisnummer(Integer.valueOf(rec.getElemValue(HNR)))
          .huisnummertoevoeging(rec.getElemValue(HNR_T))
          .aanduidingBijHuisnummer(toAandHnr(rec.getElemValue(HNR_A)))
          .naamOpenbareRuimte(rec.getElemValue(OPENB_RUIMTE))
          .postcode(rec.getElemValue(POSTCODE))
          .identificatiecodeNummeraanduiding(rec.getElemValue(IDCODE_NUMMERAAND))
          .woonplaatsnaam(rec.getElemDescr(WPL_NAAM));

    }
    return verblijfplaats;
  }

  private GezagsverhoudingInOnderzoek toGezagsverhoudingInOnderzoek(GbaWsPersonListRec rec) {
    Optional<GbaWsPersonListElem> onderzoek = rec.getElem(AAND_GEG_IN_ONDERZ);
    return onderzoek.map(e -> new GezagsverhoudingInOnderzoek()
        .datumIngangOnderzoek(toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)))
        .indicatieCurateleRegister(getInOnderzoek(rec, IND_CURATELE_REG))
        .indicatieGezagMinderjarige(getInOnderzoek(rec, IND_GEZAG_MINDERJ)))
        .orElse(null);
  }

  private static OverlijdenInOnderzoek toOverlijdenInOnderzoek(GbaWsPersonListRec rec) {
    Optional<GbaWsPersonListElem> onderzoek = rec.getElem(AAND_GEG_IN_ONDERZ);
    return onderzoek.map(e -> new OverlijdenInOnderzoek()
        .datumIngangOnderzoek(toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)))
        .datum(getInOnderzoek(rec, DATUM_OVERL))
        .land(getInOnderzoek(rec, PLAATS_OVERL))
        .plaats(getInOnderzoek(rec, LAND_OVERL)))
        .orElse(null);
  }

  private static VerblijfplaatsInOnderzoek toVerblijfplaatsInOnderzoek(GbaWsPersonListRec rec) {
    Optional<GbaWsPersonListElem> onderzoek = rec.getElem(AAND_GEG_IN_ONDERZ);
    return onderzoek.map(e -> new VerblijfplaatsInOnderzoek()
        .datumIngangOnderzoek(toDatumOnvolledig(rec.getElemValue(DATUM_INGANG_ONDERZ)))
        .aanduidingBijHuisnummer(getInOnderzoek(rec, HNR_A))
        .datumAanvangAdreshouding(getInOnderzoek(rec, DATUM_AANVANG_ADRESH))
        .datumIngangGeldigheid(getInOnderzoek(rec, INGANGSDAT_GELDIG))
        .datumInschrijvingInGemeente(getInOnderzoek(rec, GEM_INSCHR))
        .datumVestigingInNederland(getInOnderzoek(rec, DATUM_VESTIGING_IN_NL))
        .functieAdres(getInOnderzoek(rec, FUNCTIE_ADRES))
        .gemeenteVanInschrijving(getInOnderzoek(rec, GEM_INSCHR))
        .huisletter(getInOnderzoek(rec, HNR_L))
        .huisnummer(getInOnderzoek(rec, HNR))
        .huisnummertoevoeging(getInOnderzoek(rec, HNR_T))
        .identificatiecodeNummeraanduiding(getInOnderzoek(rec, IDCODE_NUMMERAAND))
        .identificatiecodeVerblijfplaats(getInOnderzoek(rec, ID_VERBLIJFPLAATS))
        .landVanwaarIngeschreven(getInOnderzoek(rec, LAND_VESTIGING))
        .locatiebeschrijving(getInOnderzoek(rec, LOCATIEBESCHR))
        .naamOpenbareRuimte(getInOnderzoek(rec, OPENB_RUIMTE))
        .postcode(getInOnderzoek(rec, POSTCODE))
        .straatnaam(getInOnderzoek(rec, STRAATNAAM))
        .woonplaatsnaam(getInOnderzoek(rec, WPL_NAAM))
        .verblijfBuitenland(getInOnderzoek(rec,
            DATUM_VERTREK_UIT_NL,
            LAND_VERTREK,
            ADRES_BUITENL_1,
            ADRES_BUITENL_1,
            ADRES_BUITENL_1)))
        .orElse(null);
  }

  private static Kiesrecht toKiesrecht(PersonSource source) {
    Optional<GbaWsPersonListRec> optRec = source.getPl().getCurrentRec(KIESR);
    return optRec.map(rec -> new Kiesrecht()
        .europeesKiesrecht(rec.getElem(AANDUIDING_EURO_KIESR).isPresent())
        .uitgeslotenVanKiesrecht(rec.getElem(AAND_UITGESLOTEN_KIESR).isPresent())
        .einddatumUitsluitingEuropeesKiesrecht(toDatumOnvolledig(rec.getElemValue(EINDDATUM_UITSL_EURO_KIESR)))
        .einddatumUitsluitingKiesrecht(toDatumOnvolledig(rec.getElemValue(EINDDATUM_UITSLUIT_KIESR))))
        .orElse(null);
  }

  private static Boolean toGeheimhouding(PersonSource source) {
    Optional<GbaWsPersonListRec> optRec = source.getPl().getCurrentRec(GBACat.INSCHR);
    return optRec.isPresent() && optRec
        .filter(rec -> rec.getElem(IND_GEHEIM).isPresent())
        .map(rec -> NumberUtils.toInt(rec.getElemValue(IND_GEHEIM), -1))
        .orElse(-1) > 0;
  }

  private static DatumOnvolledig toDatumEersteInschrijvingGBA(PersonSource source) {
    Optional<GbaWsPersonListRec> optRec = source.getPl().getCurrentRec(GBACat.INSCHR);
    return optRec.map(rec -> toDatumOnvolledig(rec.getElemValue(DATUM_EERSTE_INSCHR_GBA))).orElse(null);
  }

  private static AanduidingBijHuisnummerEnum toAandHnr(final String value) {
    AanduidingBijHuisnummerEnum result = null;
    if (hasText(value)) {
      switch (value.toLowerCase()) {
        case "to":
          result = AanduidingBijHuisnummerEnum.TEGENOVER;
          break;
        case "by":
          result = AanduidingBijHuisnummerEnum.BIJ;
          break;
        default:
          result = null;
      }
    }
    return result;
  }

  public static GeslachtEnum toGeslachtsAanduiding(final GbaWsPersonListRec rec) {
    return GeslachtAandEnum.fromCode(rec.getElemValue(GESLACHTSAAND));
  }
}
