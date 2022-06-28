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

import static nl.procura.burgerzaken.gba.core.enums.GBACat.PERSOON;

import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;

import lombok.Getter;

/**
 * Class contains specific record that needs to be convert and
 * the rest of the personlist if the convertor needs data from another record in the personlist
 */
@Getter
public class PersonSource {

  protected GbaWsPersonList    pl;
  protected GbaWsPersonListRec rec;
  protected int                id = -1;

  public static PersonSource of(GbaWsPersonList pl) {
    return of(pl, pl.getCurrentRec(PERSOON).orElse(new GbaWsPersonListRec()), -1);
  }

  public static PersonSource of(GbaWsPersonList pl, GbaWsPersonListRec rec, int size) {
    PersonSource ps = new PersonSource();
    ps.pl = pl;
    ps.rec = rec;
    ps.id = size;
    return ps;
  }

  public boolean isId(String id) {
    return String.valueOf(getId()).equals(id);
  }
}
