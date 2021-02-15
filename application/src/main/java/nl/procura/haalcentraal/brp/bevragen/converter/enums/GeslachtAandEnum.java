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

import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.GeslachtEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum GeslachtAandEnum {

  M(GeslachtEnum.MAN),
  V(GeslachtEnum.VROUW),
  O(GeslachtEnum.ONBEKEND);

  private GeslachtEnum type;

  public static GeslachtAandEnum fromCode(GeslachtEnum geslachtEnum) {
    return Arrays.stream(values())
        .filter(i -> i.type == geslachtEnum)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Onbekende code : " + geslachtEnum));
  }

  public static GeslachtEnum fromCode(final String code) {
    return Arrays.stream(values())
        .filter(i -> i.name().equals(code))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Onbekende code : " + code))
        .getType();
  }

}
