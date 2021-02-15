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

import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.RedenOpschortingBijhoudingEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OpschortingBijhouding
 */
@AllArgsConstructor
@Getter
public enum OpschortingEnum {

  O(RedenOpschortingBijhoudingEnum.OVERLIJDEN),
  E(RedenOpschortingBijhoudingEnum.EMIGRATIE),
  M(RedenOpschortingBijhoudingEnum.MINISTERIEEL_BESLUIT),
  R(RedenOpschortingBijhoudingEnum.PL_AANGELEGD_IN_DE_RNI),
  F(RedenOpschortingBijhoudingEnum.FOUT);

  private RedenOpschortingBijhoudingEnum reden;

  public static RedenOpschortingBijhoudingEnum fromCode(final String code) {
    return Arrays.stream(values())
        .filter(i -> i.name().equals(code))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Onbekende code: " + code)).getReden();
  }
}
