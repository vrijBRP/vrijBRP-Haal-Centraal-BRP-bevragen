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

package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.Util.toFields;
import static nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3.IngeschrevenPersonenParameters.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import nl.procura.burgerzaken.gba.StringUtils;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRequest;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.*;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.GeslachtAanduiding;
import nl.procura.haalcentraal.brp.bevragen.model.BsnParam;
import nl.procura.haalcentraal.brp.bevragen.model.exception.ParamException;
import nl.procura.haalcentraal.brp.bevragen.service.PersonWsService;
import nl.vng.realisatie.haalcentraal.rest.generated.api.bipv1_3.IngeschrevenpersonenApi;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.*;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Ingeschreven personen", description = "Opvragen ingeschreven personen uit de BRP")
@RequestMapping("/api/v1.3")
public class IngeschrevenPersonenResourceV1_3 implements IngeschrevenpersonenApi {

  @Autowired
  private HttpServletRequest request;
  private PersonWsService    wsService;

  private static final String        NON_SUPPORTED_PARAMETER = "Parameter wordt momenteel niet ondersteund";
  private final PersoonConverterV1_3 persoonConverter;
  private final PartnerConverterV1_3 partnerConverter;
  private final OuderConverterV1_3   ouderConverter;
  private final KindConverterV1_3    kindConverter;

  @Autowired
  public IngeschrevenPersonenResourceV1_3(PersonWsService wsService,
      PersoonConverterV1_3 persoonConverter,
      PartnerConverterV1_3 partnerConverter,
      OuderConverterV1_3 ouderConverter,
      KindConverterV1_3 kindConverter) {

    this.wsService = wsService;
    this.persoonConverter = persoonConverter;
    this.partnerConverter = partnerConverter;
    this.ouderConverter = ouderConverter;
    this.kindConverter = kindConverter;
  }

  @Override
  @RequestMapping(value = "/ingeschrevenpersonen",
      produces = { "application/hal+json", "application/problem+json" },
      method = RequestMethod.GET)
  public ResponseEntity<IngeschrevenPersoonHalCollectie> getIngeschrevenPersonen(
      @RequestParam(value = EXPAND, required = false) String expand,
      @RequestParam(value = FIELDS, required = false) String fields,
      @RequestParam(value = BURGERSERVICENUMMER, required = false) List<String> bsns,
      @RequestParam(value = GEBOORTE__DATUM, required = false) LocalDate geboorteDatum,
      @RequestParam(value = GEBOORTE__PLAATS, required = false) String geboortePlaats,
      @RequestParam(value = GESLACHTSAANDUIDING, required = false) GeslachtEnum geslachtsaanduiding,
      @RequestParam(value = INCLUSIEFOVERLEDENPERSONEN, required = false) Boolean incOverledenen,
      @RequestParam(value = NAAM__GESLACHTSNAAM, required = false) String naamGeslachtsnaam,
      @RequestParam(value = NAAM__VOORVOEGSEL, required = false) String naamVoorvoegsel,
      @RequestParam(value = NAAM__VOORNAMEN, required = false) String naamVoornamen,
      @RequestParam(value = VERBLIJFPLAATS__GEMEENTEVANINSCHRIJVING, required = false) String vbtGemInschr,
      @RequestParam(value = VERBLIJFPLAATS__HUISLETTER, required = false) String hnrL,
      @RequestParam(value = VERBLIJFPLAATS__HUISNUMMER, required = false) Integer hnr,
      @RequestParam(value = VERBLIJFPLAATS__HUISNUMMERTOEVOEGING, required = false) String hnrT,
      @RequestParam(value = VERBLIJFPLAATS__NUMMERAANDUIDINGIDENTIFICATIE, required = false) String ina,
      @RequestParam(value = VERBLIJFPLAATS__STRAAT, required = false) String straat,
      @RequestParam(value = VERBLIJFPLAATS__POSTCODE, required = false) String pc) {

    checkParameters(this.request.getParameterMap());

    GbaWsPersonListRequest request = new GbaWsPersonListRequest();

    request.setIds(new ArrayList<>());
    if (bsns != null) {
      request.setIds(bsns.stream().map(Long::parseLong).collect(Collectors.toList()));
    }

    if (geboorteDatum != null) {
      Util.fromLocalDate(geboorteDatum).ifPresent(request::setDateOfBirth);
    }
    if (geboortePlaats != null) {
      throw new ParamException("geboorte__plaats", geboortePlaats, NON_SUPPORTED_PARAMETER);
    }

    if (geslachtsaanduiding != null) {
      request.setGender(GeslachtAanduiding.fromCode(geslachtsaanduiding).getCode());
    }

    request.setShowSuspended(incOverledenen != null && incOverledenen);

    if (naamGeslachtsnaam != null) {
      request.setLastName(naamGeslachtsnaam);
    }

    if (naamVoorvoegsel != null) {
      request.setPrefix(naamVoorvoegsel);
    }

    if (naamVoornamen != null) {
      request.setFirstName(naamVoornamen);
    }

    if (vbtGemInschr != null) {
      throw new ParamException("verblijfplaats__gemeenteVanInschrijving", vbtGemInschr,
          NON_SUPPORTED_PARAMETER);
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
      throw new ParamException("verblijfplaats__nummeraanduidingIdentificatie", ina,
          NON_SUPPORTED_PARAMETER);
    }
    if (straat != null) {
      request.setStreet(straat);
    }

    if (pc != null) {
      request.setPostalCode(pc);
    }

    final URI selfLink = URI.create(linkTo(methodOn(IngeschrevenPersonenResourceV1_3.class)
        .getIngeschrevenPersonen(expand, fields, bsns, geboorteDatum, geboortePlaats,
            geslachtsaanduiding, incOverledenen, naamGeslachtsnaam, naamVoorvoegsel, naamVoornamen,
            vbtGemInschr, hnrL, hnr, hnrT, ina, straat, pc))
                .withSelfRel()
                .expand(new HashMap<>())
                .getHref());

    List<GbaWsPersonList> personLists = wsService.get(request);
    return ResponseEntity.ok(new IngeschrevenPersoonHalCollectie()
        .links(new HalCollectionLinks()
            .self(new HalLink().href(String.valueOf(selfLink))))
        .embedded(new IngeschrevenPersoonHalCollectieEmbedded()
            .ingeschrevenpersonen(personLists.stream()
                .map(pl -> convert(PersonSource.of(pl),
                    toFields(fields),
                    toFields(expand)))
                .collect(Collectors.toList()))));
  }

  @Override
  @RequestMapping(value = "/ingeschrevenpersonen/{burgerservicenummer}",
      produces = { "application/hal+json", "application/problem+json" },
      method = RequestMethod.GET)
  public ResponseEntity<IngeschrevenPersoonHal> getIngeschrevenPersoon(
      @PathVariable(BURGERSERVICENUMMER) String burgerservicenummer,
      @RequestParam(value = EXPAND, required = false) String expand,
      @RequestParam(value = FIELDS, required = false) String fields) {

    GbaWsPersonList pl = wsService.get(new BsnParam(burgerservicenummer));
    return ResponseEntity.ok(convert(PersonSource.of(pl),
        toFields(fields),
        toFields(expand)));
  }

  @Override
  public ResponseEntity<KindHalBasis> getKind(String bsn, String id) {
    GbaWsPersonList pl = wsService.get(new BsnParam(bsn));
    return ResponseEntity.ok(PersonUtils.getChildById(pl, id)
        .map(kindConverter::convert)
        .orElseThrow(() -> new ParamException("kindId", id)));
  }

  @Override
  public ResponseEntity<PartnerHalBasis> getPartner(String bsn, String id) {
    GbaWsPersonList pl = wsService.get(new BsnParam(bsn));
    return ResponseEntity.ok(PersonUtils.getPartnerById(pl, id)
        .map(partnerConverter::convert)
        .orElseThrow(() -> new ParamException("partnerId", id)));
  }

  @Override
  public ResponseEntity<OuderHalBasis> getOuder(String bsn, String id) {
    GbaWsPersonList pl = wsService.get(new BsnParam(bsn));
    return ResponseEntity.ok(PersonUtils.getParentById(pl, id)
        .map(ouderConverter::convert)
        .orElseThrow(() -> new ParamException("id", id)));
  }

  @Hidden
  @Deprecated
  @Override
  public ResponseEntity<KindHalCollectie> getKinderen(String burgerservicenummer) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @Hidden
  @Deprecated
  @Override
  public ResponseEntity<OuderHalCollectie> getOuders(String burgerservicenummer) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  @Hidden
  @Deprecated
  @Override
  public ResponseEntity<PartnerHalCollectie> getPartners(String burgerservicenummer) {
    return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
  }

  private IngeschrevenPersoonHal convert(final PersonSource source,
      final Set<String> fields,
      final Set<String> expand) {

    IngeschrevenPersoonHal ingeschrevenPersoonHal = persoonConverter.convert(source, fields, expand);
    persoonConverter.handleExpand(ingeschrevenPersoonHal, source, expand);
    return ingeschrevenPersoonHal;
  }

  private void checkParameters(Map<String, String[]> parameters) {
    for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
      if (!isExistingParameter(entry.getKey())) {
        throw new ParamException(entry.getKey(), "unknownParam", "Parameter is niet verwacht");
      }
      String value = entry.getValue()[0];
      if (StringUtils.isBlank(value) || value.equals("null")) {
        throw new ParamException(entry.getKey(), "blankParam", "Parameter heeft geen waarde");
      }
    }
    if (parameters.isEmpty()) {
      throw new ParamException(null, "paramsRequired", "Geef tenminste één parameter op");
    }
  }
}
