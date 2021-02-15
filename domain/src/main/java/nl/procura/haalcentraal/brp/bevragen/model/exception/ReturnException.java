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

package nl.procura.haalcentraal.brp.bevragen.model.exception;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * ReturnException
 */
@Getter
@Setter
@RequiredArgsConstructor
@Accessors(chain = true)
public class ReturnException extends RuntimeException {

  @Getter
  @Accessors(fluent = true)
  @RequiredArgsConstructor
  public static class Param {

    private final String name;
    private final String code;
    private final String reason;
  }

  private final List<Param> params = new ArrayList<>();

  private String title;

  private int statusCode = 400;

  private String code;

  public ReturnException(int statusCode) {
    this.statusCode = statusCode;
  }

  public ReturnException addParam(final Param param) {
    this.params.add(param);
    return this;
  }

}
