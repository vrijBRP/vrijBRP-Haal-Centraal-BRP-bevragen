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

/**
 * AanduidingBijzonderNederlanderschapEnum
 */
@AllArgsConstructor
public enum AanduidingBijzonderNederlanderschapEnum {

  BEHANDELD_ALS_NEDERLANDER("behandeld_als_nederlander"),
  VASTGESTELD_NIET_NEDERLANDER("vastgesteld_niet_nederlander");

  private String value;

  public AanduidingBijzonderNederlanderschapEnum withValue(final String value) {
    return Arrays.stream(values())
        .filter(i -> i.value.startsWith(value))
        .findFirst().orElse(null);
  }
}
