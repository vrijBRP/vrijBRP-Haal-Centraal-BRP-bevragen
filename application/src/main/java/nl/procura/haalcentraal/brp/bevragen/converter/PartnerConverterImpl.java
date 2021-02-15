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

import static nl.procura.burgerzaken.gba.core.enums.GBAElem.*;
import static nl.procura.haalcentraal.brp.bevragen.converter.PersoonConverterImpl.toGeslachtsAanduiding;
import static nl.procura.haalcentraal.brp.bevragen.converter.Util.*;

import java.util.Set;

import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.haalcentraal.brp.bevragen.converter.enums.VerbintenisEnum;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.AangaanHuwelijkPartnerschap;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.PartnerHal;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.PartnerLinks;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.SoortVerbintenisEnum;

@Service
public class PartnerConverterImpl extends BaseConverterImpl<PartnerHal, PersonSource> implements PartnerConverter {

  public PartnerConverterImpl(
      NaamPersoonConverter naamConverter,
      GeboorteConverter geboorteConverter) {

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
    put("ingeschrevenpersonen",
        (partnerHal, source, fields, expand) -> {
          if (source.getRec().getElem(GBAElem.BSN).isPresent()) {
            partnerHal.getLinks().setIngeschrevenPersoon(
                PersoonConverterImpl.createIngeschrevenPersoonLink(toBsn(source.getRec()), expand, null));
          }
        });
  }

  @Override
  public PartnerHal createTarget(final PersonSource afstamming, final Set<String> fields) {
    return new PartnerHal()
        .links(new PartnerLinks()
            .self(PersoonConverterImpl.createPartnerLink(afstamming, afstamming.getRec())));
  }

  private SoortVerbintenisEnum toSoortVerbintenis(final PersonSource source) {
    return VerbintenisEnum.fromCode(source.getRec().getElemValue(SOORT_VERBINTENIS));
  }

  private AangaanHuwelijkPartnerschap toAangaanHuwelijk(final PersonSource source) {
    return new AangaanHuwelijkPartnerschap()
        .datum(toDatumOnvolledig(source.getRec().getElemValue(DATUM_VERBINTENIS)))
        .plaats(toWaarde(source.getRec().getElem(PLAATS_VERBINTENIS)))
        .land(toWaarde(source.getRec().getElem(LAND_VERBINTENIS)));
  }
}
