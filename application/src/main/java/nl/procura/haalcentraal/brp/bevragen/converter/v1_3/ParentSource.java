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

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonList;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;

import lombok.Getter;

@Getter
public class ParentSource extends PersonSource {

  private GBACat parentType;

  public static ParentSource of(GbaWsPersonList pl, GbaWsPersonListRec rec, GBACat parentType) {
    ParentSource parentSource = new ParentSource();
    parentSource.pl = pl;
    parentSource.rec = rec;
    parentSource.parentType = parentType;
    parentSource.id = parentType.is(GBACat.OUDER_1) ? 1 : 2;
    return parentSource;
  }
}
