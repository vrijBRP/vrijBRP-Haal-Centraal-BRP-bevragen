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
import lombok.Getter;

import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.GeslachtAanduiding.MAN;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.GeslachtAanduiding.VROUW;

@Getter
public enum AdellijkeTitelPredikaat {

  B("B", "baron", MAN, "BS", "titel", "hoogwelgeboren heer"),
  BS("BS", "barones", VROUW, "B", "titel", "hoogwelgeboren vrouwe"),
  G("G", "graaf", MAN, "GI", "titel", "hooggeboren heer"),
  GI("GI", "gravin", VROUW, "G", "titel", "hooggeboren vrouwe"),
  H("H", "hertog", MAN, "HI", "titel", "hoogwelgeboren heer"),
  HI("HI", "hertogin", VROUW, "H", "titel", "hoogwelgeboren vrouwe"),
  JH("JH", "jonkheer", MAN, "JV", "predikaat", "hoogwelgeboren heer"),
  JV("JV", "jonkvrouw", VROUW, "JH", "predikaat", "hoogwelgeboren vrouwe"),
  M("M", "markies", MAN, "MI", "titel", "hoogwelgeboren heer"),
  MI("MI", "markiezin", VROUW, "M", "titel", "hoogwelgeboren vrouwe"),
  P("P", "prins", MAN, "PS", "titel", "hoogheid"),
  PS("PS", "prinses", VROUW, "P", "titel", "hoogheid"),
  R("R", "ridder", MAN, null, "titel", "hoogwelgeboren heer");

  private final String             code;
  private final String             description;
  private final String             type;
  private final String             aanhef;
  private final GeslachtAanduiding gender;
  private final String             switchGender;

  AdellijkeTitelPredikaat(String code, String description, GeslachtAanduiding gender, String switchGender,
      String type, String aanhef) {
    this.code = code;
    this.description = description;
    this.type = type;
    this.aanhef = aanhef;
    this.switchGender = switchGender;
    this.gender = gender;
  }

  public static AdellijkeTitelPredikaat fromCode(final String code) {
    AdellijkeTitelPredikaat result = null;
    if (StringUtils.hasText(code)) {
      result = Arrays.stream(values())
          .filter(v -> v.code.equals(code))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("onbekende adellijkeTitelPredikaat code:" + code));
    }
    return result;
  }

  public boolean isTitel() {
    return "titel".equals(getType());
  }

  public boolean isPredikaat() {
    return "predikaat".equals(getType());
  }

  public AdellijkeTitelPredikaat getSwitchGender() {
    if (StringUtils.hasText(switchGender)) {
      return AdellijkeTitelPredikaat.fromCode(switchGender);
    }
    return null;
  }
}
