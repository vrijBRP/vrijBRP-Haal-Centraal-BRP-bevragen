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

import static nl.procura.burgerzaken.gba.core.enums.GBAElem.AANDUIDING_NAAMGEBRUIK;
import java.util.Set;
import nl.procura.haalcentraal.brp.bevragen.model.*;
import org.springframework.stereotype.Service;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.NaamPersoon;

@Service
public class NaamPersoonConverterImplV1_3 extends NaamConverterImplV1_3<NaamPersoon>
    implements NaamPersoonConverterV1_3 {

  public NaamPersoonConverterImplV1_3() {
    put("aanhef", (naamPersoon, source) -> naamPersoon.setAanhef(toAanhef(source)));
    put("aanschrijfwijze", (naamPersoon, source) -> naamPersoon.setAanschrijfwijze(toAanschrijfwijze(source)));
    put("regelVoorafgaandAanAanschrijfwijze",
        (naamPersoon, source) -> naamPersoon.setRegelVoorafgaandAanAanschrijfwijze(toRegelVoorafgaand(source)));
    put("gebruikInLopendeTekst",
        (naamPersoon, source) -> naamPersoon.setGebruikInLopendeTekst(toGebruikInLopendeTekst(source)));
    put("aanduidingNaamgebruik", (naamPersoon, source) -> naamPersoon
        .aanduidingNaamgebruik(Util.toNaamgebruikEnum(source.getRec().getElemValue(AANDUIDING_NAAMGEBRUIK))));
  }

  @Override
  public NaamPersoon createTarget(final PersonSource source, final Set<String> fields) {
    return new NaamPersoon();
  }

  private static String toAanhef(final PersonSource source) {
    var parameters = new FullNameParameters(source.getRec(), getPartner(source));
    return new Aanhef(parameters).getAanhef();
  }

  private static String toAanschrijfwijze(final PersonSource source) {
    var parameters = new FullNameParameters(source.getRec(), getPartner(source));
    return new AanschrijfWijze(parameters).getAanschrijfwijze();
  }

  private static GbaWsPersonListRec getPartner(PersonSource source) {
    return PersonUtils.getMostRecentPartner(source.getPl())
        .map(PersonSource::getRec)
        .orElse(new GbaWsPersonListRec());
  }

  private static String toRegelVoorafgaand(PersonSource source) {
    var parameters = new FullNameParameters(source.getRec(), getPartner(source));
    return new RegelVoorafGaandAanAanschrijfwijze(parameters).getRegelVoorafgaand(getPartner(source));
  }

  private static String toGebruikInLopendeTekst(PersonSource source) {
    var parameters = new FullNameParameters(source.getRec(), getPartner(source));
    return new GebruikInLopendeTekst(parameters).getUsageInText();
  }
}
