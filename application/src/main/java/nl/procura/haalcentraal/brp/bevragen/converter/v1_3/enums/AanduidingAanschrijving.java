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
import lombok.Getter;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.NaamgebruikEnum;
import org.springframework.util.StringUtils;

@Getter
public enum AanduidingAanschrijving {

  E("E", NaamgebruikEnum.EIGEN),
  P("P", NaamgebruikEnum.PARTNER),
  V("V", NaamgebruikEnum.PARTNER_EIGEN),
  N("N", NaamgebruikEnum.EIGEN_PARTNER);

  private final String          initialValue;
  private final NaamgebruikEnum type;

  AanduidingAanschrijving(String initialValue, NaamgebruikEnum type) {
    this.initialValue = initialValue;
    this.type = type;
  }

  public static AanduidingAanschrijving fromCode(final String code) {
    AanduidingAanschrijving result = E;
    if (StringUtils.hasText(code)) {
      result = Arrays.stream(values())
          .filter(i -> i.getInitialValue().equals(code))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Onbekende AanduidingAanschrijving code" + code));
    }
    return result;
  }

}
