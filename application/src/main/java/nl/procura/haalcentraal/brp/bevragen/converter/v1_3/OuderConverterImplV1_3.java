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

import static nl.procura.burgerzaken.gba.core.enums.GBAElem.BSN;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.LinkUtils.LinkType.PARENT_LINK;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.PersonUtils.toGeslachtsAanduiding;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.PersonUtils.toRelatedGeheimhouding;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.Util.toBsn;

import java.util.Set;

import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.haalcentraal.brp.bevragen.service.PersonWsService;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.*;

@Service
public class OuderConverterImplV1_3 extends BaseConverterImplV1_3<OuderHalBasis, ParentSource>
    implements OuderConverterV1_3 {

  public OuderConverterImplV1_3(
      NaamConverterV1_3<Naam> naamConverter,
      GeboorteConverterV1_3 geboorteConverter,
      PersonWsService personWsService) {

    put("burgerservicenummer", (ouderHal, source, fields, expand) -> ouderHal.setBurgerservicenummer(
        source.getRec().getElem(GBAElem.BSN).map(e -> toBsn(source.getRec())).orElse(null)));
    put("geboorte", (ouderHal, source, fields, expand) -> ouderHal
        .setGeboorte(geboorteConverter.convert(source.getRec(), fields, expand)));
    put("geslachtsaanduiding", (ouderHal, source, fields, expand) -> ouderHal
        .setGeslachtsaanduiding(toGeslachtsAanduiding(source.getRec())));
    put("ouder_aanduiding", (ouderHal, source, fields, expand) -> ouderHal
        .setOuderAanduiding(toOuderAanduiding(source)));
    put("datumIngangFamilierechtelijkeBetrekking", (ouderHal, afstamming, fields, expand) -> ouderHal
        .setDatumIngangFamilierechtelijkeBetrekking(toDatumIngangFamilierechtelijkeBetrekking(afstamming)));
    put("naam", (ouderHal, source, fields, expand) -> ouderHal
        .setNaam(naamConverter.convert(source, fields, expand)));
    put("geheimhouding", (ouderHal, source, fields, expand) -> ouderHal
        .setGeheimhoudingPersoonsgegevens(toRelatedGeheimhouding(personWsService,
            source.getRec().getElemValue(BSN))));
    put("ingeschrevenpersonen",
        (ouderHal, source, fields, expand) -> {
          if (source.getRec().getElem(GBAElem.BSN).isPresent()) {
            ouderHal.getLinks().setIngeschrevenPersoon(
                LinkUtils.createIngeschrevenPersoonLink(toBsn(source.getRec()), expand, null));
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
  public OuderHalBasis createTarget(final ParentSource source, final Set<String> fields) {
    return new OuderHalBasis()
        .links(new OuderLinks()
            .self(LinkUtils.createLink(source, PARENT_LINK)));
  }

  @Override
  public OuderHalBasis postConvert(ParentSource source, OuderHalBasis result, Set<String> fields) {
    result.setInOnderzoek(OnderzoekUtils.toOuderInOnderzoek(source.getRec()));
    return super.postConvert(source, result, fields);
  }
}
