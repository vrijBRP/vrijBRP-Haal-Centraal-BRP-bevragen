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
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.AanduidingBijzonderNederlanderschapEnum;
import org.springframework.util.StringUtils;

@Getter
public enum AanduidingBijzonderNederlanderschap {

  B("B",
      AanduidingBijzonderNederlanderschapEnum.BEHANDELD_ALS_NEDERLANDER),
  V("V",
      AanduidingBijzonderNederlanderschapEnum.VASTGESTELD_NIET_NEDERLANDER);

  private final String                                  code;
  private final AanduidingBijzonderNederlanderschapEnum type;

  AanduidingBijzonderNederlanderschap(String code, AanduidingBijzonderNederlanderschapEnum type) {
    this.code = code;
    this.type = type;
  }

  public static AanduidingBijzonderNederlanderschapEnum fromCode(String code) {
    AanduidingBijzonderNederlanderschapEnum result = null;
    if (StringUtils.hasText(code)) {
      result = Arrays.stream(values())
          .filter(i -> i.getCode().equals(code))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Onbekende AanduidingBijzonderNederlanderschap code" + code))
          .getType();
    }
    return result;
  }
}
