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

package nl.procura.haalcentraal.brp.bevragen.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import nl.procura.haalcentraal.brp.bevragen.model.exception.ParamException;

class BsnParamTest {

  @Test
  void emptyStringMustThrowParamException() {
    assertThrows(ParamException.class, () -> new BsnParam(""));
  }

  @Test
  void nullMustThrowParamException() {
    assertThrows(ParamException.class, () -> new BsnParam(null));
  }

  @Test
  void notANumberMustThrowParamException() {
    assertThrows(ParamException.class, () -> new BsnParam("1a"));
  }

  @Test
  void numberMustReturnLong() {
    BsnParam bsn = new BsnParam("123456789");
    assertEquals(123456789, bsn.toLong());
  }
}
