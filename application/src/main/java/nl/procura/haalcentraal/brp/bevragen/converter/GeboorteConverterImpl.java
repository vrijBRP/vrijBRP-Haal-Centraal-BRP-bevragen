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
import static nl.procura.haalcentraal.brp.bevragen.converter.Util.*;

import java.util.Set;

import org.springframework.stereotype.Service;

import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.Geboorte;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.GeboorteInOnderzoek;

@Service
public class GeboorteConverterImpl extends BaseConverterImpl<Geboorte, GbaWsPersonListRec>
    implements GeboorteConverter {

  public GeboorteConverterImpl() {
    put("datum", (geb, rec) -> geb.setDatum(Util.toDatumOnvolledig(rec.getElemValue(GEBOORTEDATUM))));
    put("land", (geboorte, rec) -> geboorte.setLand(toWaarde(rec.getElem(GEBOORTELAND))));
    put("plaats", (geboorte, rec) -> geboorte.setPlaats(toWaarde(rec.getElem(GEBOORTEPLAATS))));
  }

  @Override
  public Geboorte createTarget(final GbaWsPersonListRec source, final Set<String> fields) {
    return new Geboorte();
  }

  @Override
  public Geboorte postConvert(GbaWsPersonListRec source, Geboorte result, Set<String> fields) {
    if (inOnderzoek(source, GEBOORTEDATUM, GEBOORTEPLAATS, GEBOORTELAND)) {
      result.setInOnderzoek(new GeboorteInOnderzoek());
      result.getInOnderzoek().setDatum(getInOnderzoek(source, GEBOORTEDATUM));
      result.getInOnderzoek().setPlaats(getInOnderzoek(source, GEBOORTEPLAATS));
      result.getInOnderzoek().setLand(getInOnderzoek(source, GEBOORTELAND));
    }
    return result;
  }
}
