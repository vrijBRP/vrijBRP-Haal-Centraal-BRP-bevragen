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

package nl.procura.haalcentraal.brp.bevragen.converter.enums;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * RedenOpnemen
 */

@Getter
@AllArgsConstructor
public enum RedenOpnemen {

  A000("000", "ONBEKEND"),
  A001("001", "Wet op het Nederlanderschap 1892,art.1, lid 1a");

  private String code;
  private String omschrijving;

  public static RedenOpnemen fromCode(final String code) {
    return Arrays.stream(values())
        .filter(i -> i.code.equals(code))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

}
