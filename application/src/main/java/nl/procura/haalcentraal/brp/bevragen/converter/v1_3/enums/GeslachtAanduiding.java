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

import org.springframework.util.StringUtils;

import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.GeslachtEnum;

import lombok.Getter;

@Getter
public enum GeslachtAanduiding {

  MAN("M", GeslachtEnum.MAN),
  VROUW("V", GeslachtEnum.VROUW),
  ONBEKEND("O", GeslachtEnum.ONBEKEND);

  private final String       code;
  private final GeslachtEnum type;

  GeslachtAanduiding(String code, GeslachtEnum type) {
    this.code = code;
    this.type = type;
  }

  public static GeslachtAanduiding fromCode(GeslachtEnum code) {
    return Arrays.stream(values())
        .filter(i -> i.getType().equals(code))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Onbekende Geslachtaanduiding code : " + code));
  }

  public static GeslachtAanduiding fromCode(final String code) {
    GeslachtAanduiding result = null;
    if (StringUtils.hasText(code)) {
      result = Arrays.stream(values())
          .filter(i -> i.getCode().equals(code))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Onbekende Geslachtaanduiding code : " + code));
    }
    return result;
  }

  public boolean isMale() {
    return MAN.getCode().equals(code);
  }

  public boolean isFemale() {
    return VROUW.getCode().equals(code);
  }

  public boolean isUnknown() {
    return ONBEKEND.getCode().equals(code);
  }

}
