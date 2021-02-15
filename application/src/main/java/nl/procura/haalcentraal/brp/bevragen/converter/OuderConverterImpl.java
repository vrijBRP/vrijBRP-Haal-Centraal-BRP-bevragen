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

import static nl.procura.haalcentraal.brp.bevragen.converter.Util.toBsn;

import java.util.Set;

import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.DatumOnvolledig;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.OuderAanduidingEnum;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.OuderHal;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.OuderLinks;

@Service
public class OuderConverterImpl extends BaseConverterImpl<OuderHal, ParentSource> implements OuderConverter {

  public OuderConverterImpl(
      NaamPersoonConverter naamConverter,
      GeboorteConverter geboorteConverter) {

    put("burgerservicenummer", (ouderHal, source, fields, expand) -> ouderHal.setBurgerservicenummer(
        source.getRec().getElem(GBAElem.BSN).map(e -> toBsn(source.getRec())).orElse(null)));
    put("geboorte", (ouderHal, source, fields, expand) -> ouderHal
        .setGeboorte(geboorteConverter.convert(source.getRec(), fields, expand)));
    put("geslachtsaanduiding", (ouderHal, source, fields, expand) -> ouderHal
        .setGeslachtsaanduiding(PersoonConverterImpl.toGeslachtsAanduiding(source.getRec())));
    put("ouder_aanduiding", (ouderHal, source, fields, expand) -> ouderHal
        .setOuderAanduiding(toOuderAanduiding(source)));
    put("datumIngangFamilierechtelijkeBetrekking", (ouderHal, afstamming, fields, expand) -> ouderHal
        .setDatumIngangFamilierechtelijkeBetrekking(toDatumIngangFamilierechtelijkeBetrekking(afstamming)));
    put("naam", (ouderHal, source, fields, expand) -> ouderHal
        .setNaam(naamConverter.convert(source, fields, expand)));
    put("ingeschrevenpersonen",
        (ouderHal, source, fields, expand) -> {
          if (source.getRec().getElem(GBAElem.BSN).isPresent()) {
            ouderHal.getLinks().setIngeschrevenPersoon(
                PersoonConverterImpl.createIngeschrevenPersoonLink(toBsn(source.getRec()), expand, null));
          }
        });
  }

  private OuderAanduidingEnum toOuderAanduiding(ParentSource source) {
    return source.getParentType() == GBACat.OUDER_1 ? OuderAanduidingEnum.OUDER1 : OuderAanduidingEnum.OUDER2;
  }

  public static DatumOnvolledig toDatumIngangFamilierechtelijkeBetrekking(final ParentSource source) {
    return Util.toDatumOnvolledig(source.getRec().getElemValue(GBAElem.DATUM_INGANG_FAM_RECHT_BETREK));
  }

  @Override
  public OuderHal createTarget(final ParentSource source, final Set<String> fields) {
    return new OuderHal()
        .links(new OuderLinks()
            .self(PersoonConverterImpl.createOuderLink(source, source.getRec())));
  }
}
