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
import static nl.procura.burgerzaken.gba.core.enums.GBAElem.GEBOORTEDATUM;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.LinkUtils.LinkType.CHILD_LINK;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.PersonUtils.toRelatedGeheimhouding;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.Util.toBsn;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.Util.toLocalDate;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.haalcentraal.brp.bevragen.service.PersonWsService;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.KindHalBasis;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.KindLinks;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.Naam;

@Service
public class KindConverterImplV1_3 extends BaseConverterImplV1_3<KindHalBasis, PersonSource>
    implements KindConverterV1_3 {

  public KindConverterImplV1_3(
      NaamConverterV1_3<Naam> naamConverter,
      GeboorteConverterV1_3 geboorteConverter,
      PersonWsService personWsService) {

    put("burgerservicenummer", (kindHal, source, fields, expand) -> kindHal
        .setBurgerservicenummer(toBsn(source.getRec())));
    put("geboorte", (kindHal, source, fields, expand) -> kindHal
        .setGeboorte(geboorteConverter.convert(source.getRec(), fields, expand)));
    put("leeftijd", (persoon, source) -> persoon
        .leeftijd(Util.toLeeftijd(toLocalDate(source.getRec().getElemValue(GEBOORTEDATUM)),
            LocalDate.now()).orElse(null)));
    put("naam", (ouderHal, source, fields, expand) -> ouderHal
        .setNaam(naamConverter.convert(source, fields, expand)));
    put("geheimhouding", (ouderHal, source, fields, expand) -> ouderHal
        .setGeheimhoudingPersoonsgegevens(toRelatedGeheimhouding(personWsService,
            source.getRec().getElemValue(BSN))));
    put("ingeschrevenpersonen",
        (kindHal, source, fields, expand) -> {
          if (source.getRec().getElem(GBAElem.BSN).isPresent()) {
            kindHal.getLinks().setIngeschrevenPersoon(
                LinkUtils.createIngeschrevenPersoonLink(toBsn(source.getRec()), expand, null));
          }
        });
  }

  @Override
  public KindHalBasis createTarget(final PersonSource source, final Set<String> fields) {
    return new KindHalBasis()
        .links(new KindLinks()
            .self(LinkUtils.createLink(source, CHILD_LINK)));
  }

  @Override
  public KindHalBasis postConvert(PersonSource source, KindHalBasis result, Set<String> fields) {
    result.setInOnderzoek(OnderzoekUtils.toKindInOnderzoek(source.getRec()));
    return super.postConvert(source, result, fields);
  }
}
