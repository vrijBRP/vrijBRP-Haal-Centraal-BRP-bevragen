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

  B("B", "Baron", MAN, "BS", "titel", "Hoogwelgeboren heer"),
  BS("BS", "Barones", VROUW, "B", "titel", "Hoogwelgeboren vrouwe"),
  G("G", "Graaf", MAN, "GI", "titel", "Hooggeboren heer"),
  GI("GI", "Gravin", VROUW, "G", "titel", "Hooggeboren vrouwe"),
  H("H", "Hertog", MAN, "HI", "titel", "Hoogwelgeboren heer"),
  HI("HI", "Hertogin", VROUW, "H", "titel", "Hoogwelgeboren vrouwe"),
  JH("JH", "Jonkheer", MAN, "JV", "predikaat", "Hoogwelgeboren heer"),
  JV("JV", "Jonkvrouw", VROUW, "JH", "predikaat", "Hoogwelgeboren vrouwe"),
  M("M", "Markies", MAN, "MI", "titel", "Hoogwelgeboren heer"),
  MI("MI", "Markiezin", VROUW, "M", "titel", "Hoogwelgeboren vrouwe"),
  P("P", "Prins", MAN, "PS", "titel", "Hoogheid"),
  PS("PS", "Prinses", VROUW, "P", "titel", "Hoogheid"),
  R("R", "Ridder", MAN, null, "titel", "Hoogwelgeboren heer");

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
