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

import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.IndicatieGezagMinderjarigeEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

/**
 * IndicatieGezagMinderjarigeEnum
 */
@AllArgsConstructor
@Getter
public enum IndicatieGezagEnum {

  _1("1", IndicatieGezagMinderjarigeEnum.OUDER1),
  _2("2", IndicatieGezagMinderjarigeEnum.OUDER2),
  _D("D", IndicatieGezagMinderjarigeEnum.DERDEN),
  _1D("1D", IndicatieGezagMinderjarigeEnum.OUDER1_EN_DERDE),
  _2D("2D", IndicatieGezagMinderjarigeEnum.OUDER2_EN_DERDE),
  _12("12", IndicatieGezagMinderjarigeEnum.OUDER1_EN_OUDER2);

  private String                         code;
  private IndicatieGezagMinderjarigeEnum type;

  public static IndicatieGezagMinderjarigeEnum getType(final String code) {
    IndicatieGezagMinderjarigeEnum result = null;
    if (StringUtils.hasText(code)) {
      result = Arrays.stream(values())
          .filter(i -> i.getCode().equals(code))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Onbekende code: " + code))
          .getType();
    }
    return result;
  }
}
