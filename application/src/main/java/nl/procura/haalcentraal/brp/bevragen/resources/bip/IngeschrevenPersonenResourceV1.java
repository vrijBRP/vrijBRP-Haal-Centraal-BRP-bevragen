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

package nl.procura.haalcentraal.brp.bevragen.resources.bip;

import static nl.procura.burgerzaken.gba.core.enums.GBACat.*;
import static nl.procura.haalcentraal.brp.bevragen.converter.Util.toBsn;
import static nl.procura.haalcentraal.brp.bevragen.converter.Util.toFields;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRequest;
import nl.procura.haalcentraal.brp.bevragen.converter.*;
import nl.procura.haalcentraal.brp.bevragen.converter.enums.GeslachtAandEnum;
import nl.procura.haalcentraal.brp.bevragen.model.BsnParam;
import nl.procura.haalcentraal.brp.bevragen.model.exception.ExpandException;
import nl.procura.haalcentraal.brp.bevragen.model.exception.FieldsException;
import nl.procura.haalcentraal.brp.bevragen.model.exception.ParamException;
import nl.procura.haalcentraal.brp.bevragen.service.ProcuraWsService;
import nl.vng.realisatie.haalcentraal.rest.generated.api.bip.IngeschrevenpersonenApi;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Ingeschreven personen", description = "Opvragen ingeschreven personen uit de BRP")
@RequestMapping("/api/v1")
public class IngeschrevenPersonenResourceV1 implements IngeschrevenpersonenApi {

  private ProcuraWsService personWsService;

  private final PartnerConverter partnerConverter;
  private final PersoonConverter persoonConverter;
  private final KindConverter    kindConverter;
  private final OuderConverter   ouderConverter;

  @Autowired
  public IngeschrevenPersonenResourceV1(ProcuraWsService personWsService,
      PartnerConverter partnerConverter,
      PersoonConverter persoonConverter,
      KindConverter kindConverter,
      OuderConverter ouderConverter) {

    this.personWsService = personWsService;
    this.partnerConverter = partnerConverter;
    this.persoonConverter = persoonConverter;
    this.kindConverter = kindConverter;
    this.ouderConverter = ouderConverter;
  }

  @Override
  @RequestMapping(value = "/ingeschrevenpersonen",
      produces = { "application/hal+json", "application/problem+json" },
      method = RequestMethod.GET)
  public ResponseEntity<IngeschrevenPersoonHalCollectie> ingeschrevenNatuurlijkPersonen(
      @RequestHeader(value = "api-version", required = false) String apiVersion,
      @RequestParam(value = "expand", required = false) String expand,
      @RequestParam(value = "burgerservicenummer", required = false) String bsn,
      @RequestParam(value = "fields", required = false) String fields,
      @RequestParam(value = "geboorte__datum", required = false) LocalDate geboorteDatum,
      @RequestParam(value = "geboorte__plaats", required = false) String geboortePlaats,
      @RequestParam(value = "geslachtsaanduiding", required = false) GeslachtEnum geslachtsaanduiding,
      @RequestParam(value = "inclusiefoverledenpersonen", required = false) Boolean incOverledenen,
      @RequestParam(value = "naam__geslachtsnaam", required = false) String naamGeslachtsnaam,
      @RequestParam(value = "naam__voornamen", required = false) String naamVoornamen,
      @RequestParam(value = "verblijfplaats__gemeentevaninschrijving", required = false) String vbtGemInschr,
      @RequestParam(value = "verblijfplaats__huisletter", required = false) String hnrL,
      @RequestParam(value = "verblijfplaats__huisnummer", required = false) Integer hnr,
      @RequestParam(value = "verblijfplaats__huisnummertoevoeging", required = false) String hnrT,
      @RequestParam(value = "verblijfplaats__identificatiecodenummeraanduiding", required = false) String ina,
      @RequestParam(value = "verblijfplaats__naamopenbareruimte", required = false) String openbareRuimte,
      @RequestParam(value = "verblijfplaats__postcode", required = false) String pc,
      @RequestParam(value = "naam__voorvoegsel", required = false) String naamVoorvoegsel) {

    GbaWsPersonListRequest request = new GbaWsPersonListRequest();

    request.setIds(new ArrayList<>());
    if (StringUtils.hasText(bsn)) {
      request.setIds(Collections.singletonList(Long.valueOf(bsn)));
    }

    if (geboorteDatum != null) {
      Util.fromLocalDate(geboorteDatum).ifPresent(request::setDateOfBirth);
    }

    if (geboortePlaats != null) {
      throw new ParamException("geboorte__plaats", geboortePlaats, "Parameter wordt momenteel niet ondersteund");
    }

    if (geslachtsaanduiding != null) {
      request.setGender(GeslachtAandEnum.fromCode(geslachtsaanduiding).name());
    }

    request.setShowSuspended(incOverledenen == null || incOverledenen);

    if (naamGeslachtsnaam != null) {
      request.setLastName(naamGeslachtsnaam);
    }

    if (naamVoornamen != null) {
      request.setFirstName(naamVoornamen);
    }

    if (vbtGemInschr != null) {
      throw new ParamException("verblijfplaats__gemeentevaninschrijving", vbtGemInschr,
          "Parameter wordt momenteel niet ondersteund");
    }

    if (hnrL != null) {
      request.setHnrL(hnrL);
    }

    if (hnr != null) {
      request.setHnr(Integer.toString(hnr));
    }

    if (hnrT != null) {
      request.setHnrL(hnrT);
    }

    if (ina != null) {
      throw new ParamException("verblijfplaats__identificatiecodenummeraanduiding", ina,
          "Parameter wordt momenteel niet ondersteund");
    }

    if (openbareRuimte != null) {
      throw new ParamException("verblijfplaats__naamopenbareruimte", openbareRuimte,
          "Parameter wordt momenteel niet ondersteund");
    }

    if (pc != null) {
      request.setPostalCode(pc);
    }

    if (naamVoorvoegsel != null) {
      request.setPrefix(naamVoorvoegsel);
    }

    final URI selfLink = URI.create(linkTo(methodOn(IngeschrevenPersonenResourceV1.class)
        .ingeschrevenNatuurlijkPersonen(apiVersion, expand, bsn, fields, geboorteDatum, geboortePlaats,
            geslachtsaanduiding, incOverledenen, naamGeslachtsnaam, naamVoornamen,
            vbtGemInschr,
            hnrL, hnr, hnrT,
            ina, openbareRuimte, pc,
            naamVoorvoegsel))
                .withSelfRel()
                .expand(new HashMap<>())
                .getHref());

    List<GbaWsPersonList> personLists = personWsService.get(request);
    return ResponseEntity.ok(new IngeschrevenPersoonHalCollectie()
        .links(new HalCollectionLinks()
            .self(new HalLink().href(selfLink)))
        .embedded(new IngeschrevenPersoonHalCollectieEmbedded()
            .ingeschrevenpersonen(personLists.stream()
                .map(pl -> convert(new PersonSource(pl), toFields(fields), toFields(expand)))
                .collect(Collectors.toList()))));
  }

  @Override
  @RequestMapping(value = "/ingeschrevenpersonen/{burgerservicenummer}",
      produces = { "application/hal+json", "application/problem+json" },
      method = RequestMethod.GET)
  public ResponseEntity<IngeschrevenPersoonHal> ingeschrevenNatuurlijkPersoon(
      @PathVariable("burgerservicenummer") String burgerservicenummer,
      @RequestHeader(value = "api-version", required = false) String apiVersion,
      @RequestParam(value = "expand", required = false) String expand,
      @RequestParam(value = "fields", required = false) String fields) {

    GbaWsPersonList pl = personWsService.get(new BsnParam(burgerservicenummer));
    return ResponseEntity.ok(convert(new PersonSource(pl),
        toFields(fields), toFields(expand)));
  }

  @Override
  public ResponseEntity<KindHal> ingeschrevenpersonenBurgerservicenummerkinderenUuid(
      String ouderBsn,
      String kindBsn,
      String apiVersion) {

    GbaWsPersonList pl = personWsService.get(new BsnParam(ouderBsn));
    return ResponseEntity.ok(pl.getCurrentRec(KINDEREN, rec -> Util.isBsnMatch(kindBsn, toBsn(rec)))
        .map(kind -> kindConverter.convert(new PersonSource(pl, kind)))
        .orElseThrow(() -> new ParamException("kindBsn", kindBsn)));
  }

  @Override
  public ResponseEntity<OuderHal> ingeschrevenpersonenBurgerservicenummeroudersUuid(
      String kindBsn,
      String ouderBsn,
      String apiVersion) {

    GbaWsPersonList pl = personWsService.get(new BsnParam(kindBsn));
    return getOuder(ouderBsn, pl, OUDER_1)
        .orElseGet(() -> getOuder(ouderBsn, pl, OUDER_2)
            .orElseThrow(() -> new ParamException("ouderBsn", ouderBsn)));
  }

  private Optional<ResponseEntity<OuderHal>> getOuder(String ouderBsn, GbaWsPersonList pl, GBACat cat) {
    return pl.getCurrentRec(cat, rec1 -> Util.isBsnMatch(ouderBsn, toBsn(rec1)))
        .map(rec -> ResponseEntity.ok(ouderConverter.convert(new ParentSource(pl, rec, cat))));
  }

  @Override
  public ResponseEntity<PartnerHal> ingeschrevenpersonenBurgerservicenummerpartnersUuid(
      String partner1Bsn,
      String partner2Bsn,
      String apiVersion) {

    GbaWsPersonList pl = personWsService.get(new BsnParam(partner1Bsn));
    return ResponseEntity.ok(pl.getCurrentRec(HUW_GPS,
        rec -> Util.isBsnMatch(partner2Bsn, toBsn(rec)))
        .map(kind -> partnerConverter.convert(new PersonSource(pl, kind)))
        .orElseThrow(() -> new ParamException("partner2Bsn", partner2Bsn)));
  }

  @Override
  public ResponseEntity<KindHalCollectie> ingeschrevenpersonenBurgerservicenummerkinderen(
      String burgerservicenummer,
      String apiVersion) {

    final URI selfLink = URI.create(linkTo(methodOn(IngeschrevenPersonenResourceV1.class)
        .ingeschrevenpersonenBurgerservicenummerkinderen(burgerservicenummer, apiVersion))
            .withSelfRel().expand(burgerservicenummer).getHref());

    KindHalCollectie halCollectie = new KindHalCollectie().links(new KindHalCollectie()
        .links(new HalCollectionLinks().self(new HalLink().href(selfLink))).getLinks());

    GbaWsPersonList pl = personWsService.get(new BsnParam(burgerservicenummer));
    List<GbaWsPersonListRec> records = pl.getCurrentRecords(KINDEREN);
    if (!records.isEmpty()) {
      halCollectie.embedded(new KindHalCollectieEmbedded()
          .kinderen(records.stream()
              .map(rec -> kindConverter.convert(new PersonSource(pl, rec)))
              .collect(Collectors.toList())));
    }

    return ResponseEntity.ok(halCollectie);
  }

  @Override
  public ResponseEntity<OuderHalCollectie> ingeschrevenpersonenBurgerservicenummerouders(
      String burgerservicenummer,
      String apiVersion) {

    final URI selfLink = URI.create(linkTo(methodOn(IngeschrevenPersonenResourceV1.class)
        .ingeschrevenpersonenBurgerservicenummerouders(burgerservicenummer, apiVersion))
            .withSelfRel().expand(burgerservicenummer).getHref());

    OuderHalCollectie halCollectie = new OuderHalCollectie().links(new OuderHalCollectie()
        .links(new HalCollectionLinks().self(new HalLink().href(selfLink))).getLinks());

    GbaWsPersonList pl = personWsService.get(new BsnParam(burgerservicenummer));
    Map<GBACat, Optional<GbaWsPersonListRec>> map = new HashMap<>();
    map.put(OUDER_1, pl.getCurrentRec(OUDER_1));
    map.put(OUDER_2, pl.getCurrentRec(OUDER_2));

    if (map.values().stream().anyMatch(Optional::isPresent)) {
      OuderHalCollectieEmbedded embedded = new OuderHalCollectieEmbedded();
      halCollectie.embedded(embedded);
      map.forEach((cat, rec) -> rec.ifPresent(gbaWsPersonListRec -> embedded
          .addOudersItem(ouderConverter.convert(new ParentSource(pl, gbaWsPersonListRec, cat)))));
    }

    return ResponseEntity.ok(halCollectie);
  }

  @Override
  public ResponseEntity<PartnerHalCollectie> ingeschrevenpersonenBurgerservicenummerpartners(
      String burgerservicenummer,
      String apiVersion) {

    final URI selfLink = URI.create(linkTo(methodOn(IngeschrevenPersonenResourceV1.class)
        .ingeschrevenpersonenBurgerservicenummerpartners(burgerservicenummer, apiVersion))
            .withSelfRel().expand(burgerservicenummer).getHref());

    PartnerHalCollectie halCollectie = new PartnerHalCollectie().links(new PartnerHalCollectie()
        .links(new HalCollectionLinks().self(new HalLink().href(selfLink))).getLinks());

    GbaWsPersonList pl = personWsService.get(new BsnParam(burgerservicenummer));
    List<GbaWsPersonListRec> records = pl.getCurrentRecords(HUW_GPS);
    if (!records.isEmpty()) {
      halCollectie.embedded(new PartnerHalCollectieEmbedded()
          .partners(records.stream()
              .map(rec -> partnerConverter.convert(new PersonSource(pl, rec)))
              .collect(Collectors.toList())));
    }

    return ResponseEntity.ok(halCollectie);
  }

  private IngeschrevenPersoonHal convert(final PersonSource source,
      final Set<String> fields,
      final Set<String> expand) {

    log.debug("fields=" + fields + ", expand=" + expand);

    if (fields != null && fields.isEmpty()) {
      throw new FieldsException("null");
    }

    if (expand != null && expand.isEmpty()) {
      throw new ExpandException("null");
    }

    final IngeschrevenPersoonHal ingeschrevenPersoonHal = persoonConverter.convert(source, fields, expand);
    persoonConverter.handleExpand(ingeschrevenPersoonHal, source, expand);
    return ingeschrevenPersoonHal;
  }

}
