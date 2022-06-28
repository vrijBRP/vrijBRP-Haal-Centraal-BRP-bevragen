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

package nl.procura.haalcentraal.brp.bevragen.converter.v1_3;

import static nl.procura.burgerzaken.gba.core.enums.GBAElem.*;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.LinkUtils.LinkType.*;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.PersonUtils.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import nl.procura.haalcentraal.brp.bevragen.model.Address;
import nl.procura.haalcentraal.brp.bevragen.model.exception.ExpandException;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.IngeschrevenPersoonEmbedded;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.IngeschrevenPersoonHal;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.IngeschrevenPersoonLinks;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PersoonConverterImplV1_3 extends BaseConverterImplV1_3<IngeschrevenPersoonHal, PersonSource>
    implements PersoonConverterV1_3 {

  private final KindConverterV1_3    kindConverter;
  private final OuderConverterV1_3   ouderConverter;
  private final PartnerConverterV1_3 partnerConverter;

  private final Map<String, Handler<IngeschrevenPersoonHal, PersonSource>> expandMap = new HashMap<>();

  public PersoonConverterImplV1_3(
      NaamPersoonConverterV1_3 naamPersoonConverter,
      KindConverterV1_3 kindConverter,
      OuderConverterV1_3 ouderConverter,
      PartnerConverterV1_3 partnerConverter,
      GeboorteConverterV1_3 geboorteConverter) {

    this.kindConverter = kindConverter;
    this.ouderConverter = ouderConverter;
    this.partnerConverter = partnerConverter;

    put("naam", (ip, source, fields, expand) -> ip
        .naam(naamPersoonConverter.convert(source, fields, expand)));
    put("burgerservicenummer", (persoon, source) -> persoon
        .burgerservicenummer(Util.toBsn(source.getRec())));
    put("anummer", (persoon, source) -> persoon
        .aNummer(Util.toAnr(source.getRec())));
    put("kiesrecht", (persoon, source) -> persoon
        .kiesrecht(toKiesrecht(source)));
    put("geboorte", (persoon, source, fields, expand) -> persoon
        .setGeboorte(geboorteConverter.convert(source.getRec(), fields, expand)));
    put("leeftijd", (persoon, source) -> persoon
        .leeftijd(Util.toLeeftijd(Util.toLocalDate(source.getRec().getElemValue(GEBOORTEDATUM)),
            LocalDate.now()).orElse(null)));
    put("geslachtsaanduiding", (persoon, source) -> persoon
        .geslachtsaanduiding(toGeslachtsAanduiding(source.getRec())));
    put("overlijden", (persoon, source, fields, expand) -> persoon
        .overlijden(toOverlijden(source)));
    put("verblijfsplaats", (persoon, source, fields, expand) -> persoon
        .verblijfplaats(Address.toAddress(source)));
    put("datumEersteInschrijvingGBA", (persoon, source, fields, expand) -> persoon
        .datumEersteInschrijvingGBA(toDatumEersteInschrijvingGBA(source)));
    put("inOnderzoek", (persoon, source) -> persoon
        .setInOnderzoek(OnderzoekUtils.toPersoonInOnderzoek(source.getRec())));
    put("nationaliteit", (persoon, source, fields, exand) -> persoon
        .nationaliteiten(getNationaliteiten(source)));
    put("ouders", (persoonHal, source, fields, expand) -> toFieldsOuders(persoonHal, source));
    put("kinderen", (persoonHal, source, fields, expand) -> toFieldsKinderen(persoonHal, source));
    put("partners", (persoonHal, source, fields, expand) -> toFieldsPartners(persoonHal, source));

    expandMap.put("kinderen", (persoonHal, source, fields, expand) -> toExpandKinderen(persoonHal, source, expand));
    expandMap.put("partners", (persoonHal, persoon, fields, expand) -> toExpandPartners(persoonHal, persoon, expand));
    expandMap.put("ouders", (persoonHal, source, fields, expand) -> toExpandOuders(persoonHal, source, expand));
  }



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

  @Override
  public IngeschrevenPersoonHal createTarget(final PersonSource source, final Set<String> fields) {
    final var persoonHal = new IngeschrevenPersoonHal();
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
    persoon.getLinks().self(LinkUtils.createIngeschrevenPersoonLink(source.getRec().getElemValue(BSN), fields, expand));

    persoon.geheimhoudingPersoonsgegevens(toGeheimhouding(source.getPl()));
    persoon.opschortingBijhouding(toOpschorting(source));
    persoon.gezagsverhouding(PersonUtils.toGezagsverhouding(source));

    return persoon;
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
              return s.substring(0, s.indexOf('.'));
            } else {
              return s;
            }
          })
          .distinct()
          .forEach(e -> expandMap.get(e)
              .handle(persoonHal, plSource, null, leaf(expand, e)));
    }
  }

  private static void toFieldsOuders(final IngeschrevenPersoonHal ip, final PersonSource source) {
    PersonUtils.getParents(source.getPl())
        .forEach(personSource -> ip.getLinks()
            .addOudersItem(LinkUtils.createLink(personSource, PARENT_LINK)));
  }

  private static void toFieldsKinderen(final IngeschrevenPersoonHal ip, final PersonSource source) {
    PersonUtils.getChildren(source.getPl())
        .forEach(personSource -> ip.getLinks()
            .addKinderenItem(LinkUtils.createLink(personSource, CHILD_LINK)));
  }

  private static void toFieldsPartners(final IngeschrevenPersoonHal ip, final PersonSource source) {
    PersonUtils.getPartners(source.getPl())
        .forEach(personSource -> ip.getLinks()
            .addPartnersItem(LinkUtils.createLink(personSource, PARTNER_LINK)));
  }

  private void toExpandOuders(final IngeschrevenPersoonHal persoonHal,
      final PersonSource source,
      final Set<String> expand) {

    if (persoonHal.getEmbedded() == null) {
      persoonHal.setEmbedded(new IngeschrevenPersoonEmbedded());
    }

    ouderConverter.validate(expand);

    PersonUtils.getParents(source.getPl())
        .forEach(personSource -> persoonHal.getEmbedded()
            .addOudersItem(ouderConverter.convert(personSource, expand, null)));
  }

  private void toExpandKinderen(final IngeschrevenPersoonHal persoonHal,
      final PersonSource source,
      final Set<String> expand) {

    if (persoonHal.getEmbedded() == null) {
      persoonHal.setEmbedded(new IngeschrevenPersoonEmbedded());
    }

    kindConverter.validate(expand);

    PersonUtils.getChildren(source.getPl())
        .forEach(personSource -> persoonHal.getEmbedded()
            .addKinderenItem(kindConverter.convert(personSource, expand, null)));
  }

  private void toExpandPartners(final IngeschrevenPersoonHal persoonHal,
      final PersonSource source,
      final Set<String> expand) {

    if (persoonHal.getEmbedded() == null) {
      persoonHal.setEmbedded(new IngeschrevenPersoonEmbedded());
    }

    partnerConverter.validate(expand);

    PersonUtils.getPartners(source.getPl())
        .forEach(personSource -> persoonHal.getEmbedded()
            .addPartnersItem(partnerConverter.convert(personSource, expand, null)));
  }
}
