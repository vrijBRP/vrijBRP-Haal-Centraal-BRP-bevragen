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
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.PersonUtils.toGeslachtsAanduiding;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.PersonUtils.toRelatedGeheimhouding;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.Util.*;

import java.util.Set;

import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.*;
import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.Verbintenis;
import nl.procura.haalcentraal.brp.bevragen.service.PersonWsService;

@Service
public class PartnerConverterImplV1_3 extends BaseConverterImplV1_3<PartnerHalBasis, PersonSource>
    implements PartnerConverterV1_3 {

  public PartnerConverterImplV1_3(
      NaamConverterV1_3<Naam> naamConverter,
      GeboorteConverterV1_3 geboorteConverter,
      PersonWsService personWsService) {

    put("burgerservicenummer", (partnerHal, source, fields, expand) -> partnerHal
        .setBurgerservicenummer(toBsn(source.getRec())));
    put("naam", (ouderHal, source, fields, expand) -> ouderHal
        .setNaam(naamConverter.convert(source, fields, expand)));
    put("geboorte", (partnerHal, source, fields, expand) -> partnerHal
        .setGeboorte(geboorteConverter.convert(source.getRec(), fields, expand)));
    put("geslachtsaanduiding", (persoon, source) -> persoon
        .geslachtsaanduiding(toGeslachtsAanduiding(source.getRec())));
    put("soortVerbintenis", (partnerHal, partnerschap) -> partnerHal
        .setSoortVerbintenis(toSoortVerbintenis(partnerschap)));
    put("aangaanHuwelijk_Partnerschap", (partnerHal, partnerschap) -> partnerHal
        .setAangaanHuwelijkPartnerschap(toAangaanHuwelijk(partnerschap)));
    put("geheimhouding", (ouderHal, source, fields, expand) -> ouderHal
        .setGeheimhoudingPersoonsgegevens(toRelatedGeheimhouding(personWsService,
            source.getRec().getElemValue(BSN))));
    put("ingeschrevenpersonen",
        (partnerHal, source, fields, expand) -> {
          if (source.getRec().getElem(GBAElem.BSN).isPresent()) {
            partnerHal.getLinks().setIngeschrevenPersoon(
                LinkUtils.createIngeschrevenPersoonLink(toBsn(source.getRec()), expand, null));
          }
        });
  }

  @Override
  public PartnerHalBasis createTarget(final PersonSource afstamming, final Set<String> fields) {
    return new PartnerHalBasis()
        .links(new PartnerLinks()
            .self(LinkUtils.createLink(afstamming, LinkUtils.LinkType.PARTNER_LINK)));
  }

  private SoortVerbintenisEnum toSoortVerbintenis(final PersonSource source) {
    return Verbintenis.fromCode(source.getRec().getElemValue(SOORT_VERBINTENIS));
  }

  private AangaanHuwelijkPartnerschap toAangaanHuwelijk(final PersonSource source) {
    return new AangaanHuwelijkPartnerschap()
        .datum(toDatumOnvolledig(source.getRec().getElemValue(DATUM_VERBINTENIS)))
        .plaats(toWaarde(source.getRec().getElem(PLAATS_VERBINTENIS)))
        .land(toWaarde(source.getRec().getElem(LAND_VERBINTENIS)))
        .inOnderzoek(OnderzoekUtils.toAangaanHuwPrtInOnderzoek(source.getRec()));
  }

  @Override
  public PartnerHalBasis postConvert(PersonSource source, PartnerHalBasis result, Set<String> fields) {
    result.setInOnderzoek(OnderzoekUtils.toPartnerInOnderzoek(source.getRec()));
    return super.postConvert(source, result, fields);
  }
}
