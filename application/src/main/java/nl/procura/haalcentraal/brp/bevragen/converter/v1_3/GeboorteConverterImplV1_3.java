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

import java.util.Set;

import org.springframework.stereotype.Service;

import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.Geboorte;

@Service
public class GeboorteConverterImplV1_3 extends BaseConverterImplV1_3<Geboorte, GbaWsPersonListRec>
    implements GeboorteConverterV1_3 {

  public GeboorteConverterImplV1_3() {
    put("datum", (geb, rec) -> geb.setDatum(Util.toDatumOnvolledig(rec.getElemValue(GEBOORTEDATUM))));
    put("land", (geboorte, rec) -> geboorte.setLand(Util.toWaarde(rec.getElem(GEBOORTELAND))));
    put("plaats", (geboorte, rec) -> geboorte.setPlaats(Util.toWaarde(rec.getElem(GEBOORTEPLAATS))));
  }

  @Override
  public Geboorte createTarget(final GbaWsPersonListRec source, final Set<String> fields) {
    return new Geboorte();
  }

  @Override
  public Geboorte postConvert(GbaWsPersonListRec source, Geboorte result, Set<String> fields) {
    result.setInOnderzoek(OnderzoekUtils.toGeboorteInOnderzoek(source));
    return super.postConvert(source, result, fields);
  }
}
