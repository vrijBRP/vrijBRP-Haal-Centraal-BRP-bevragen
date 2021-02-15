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

import static nl.procura.burgerzaken.gba.core.enums.GBAElem.GEBOORTEDATUM;
import static nl.procura.haalcentraal.brp.bevragen.converter.Util.toBsn;
import static nl.procura.haalcentraal.brp.bevragen.converter.Util.toLocalDate;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.stereotype.Service;

import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.KindHal;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.KindLinks;

@Service
public class KindConverterImpl extends BaseConverterImpl<KindHal, PersonSource> implements KindConverter {

  public KindConverterImpl(
      NaamPersoonConverter naamConverter,
      GeboorteConverter geboorteConverter) {

    put("burgerservicenummer", (kindHal, source, fields, expand) -> kindHal
        .setBurgerservicenummer(toBsn(source.getRec())));
    put("geboorte", (kindHal, source, fields, expand) -> kindHal
        .setGeboorte(geboorteConverter.convert(source.getRec(), fields, expand)));
    put("leeftijd", (persoon, source) -> persoon
        .leeftijd(Util.toLeeftijd(toLocalDate(source.getRec().getElemValue(GEBOORTEDATUM)),
            LocalDate.now()).orElse(null)));
    put("naam", (ouderHal, source, fields, expand) -> ouderHal
        .setNaam(naamConverter.convert(source, fields, expand)));
    put("ingeschrevenpersonen",
        (kindHal, source, fields, expand) -> {
          if (source.getRec().getElem(GBAElem.BSN).isPresent()) {
            kindHal.getLinks().setIngeschrevenPersoon(
                PersoonConverterImpl.createIngeschrevenPersoonLink(toBsn(source.getRec()), expand, null));
          }
        });
  }

  @Override
  public KindHal createTarget(final PersonSource source, final Set<String> fields) {
    return new KindHal()
        .links(new KindLinks()
            .self(PersoonConverterImpl.createKindLink(source, source.getRec())));
  }
}
