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

package nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums;

import java.util.Arrays;

import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.RedenOpschortingBijhoudingEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

@AllArgsConstructor
@Getter
public enum Opschorting {

  O("O", RedenOpschortingBijhoudingEnum.OVERLIJDEN),
  E("E", RedenOpschortingBijhoudingEnum.EMIGRATIE),
  M("M", RedenOpschortingBijhoudingEnum.MINISTERIEEL_BESLUIT),
  R("R", RedenOpschortingBijhoudingEnum.PL_AANGELEGD_IN_DE_RNI),
  F("F", RedenOpschortingBijhoudingEnum.FOUT);

  private final String                         code;
  private final RedenOpschortingBijhoudingEnum type;

  public static RedenOpschortingBijhoudingEnum fromCode(final String code) {
    RedenOpschortingBijhoudingEnum result = null;
    if (StringUtils.hasText(code)) {
      result = Arrays.stream(values())
          .filter(i -> i.name().equals(code))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Onbekende Opschorting code: " + code))
          .getType();
    }
    return result;
  }
}
