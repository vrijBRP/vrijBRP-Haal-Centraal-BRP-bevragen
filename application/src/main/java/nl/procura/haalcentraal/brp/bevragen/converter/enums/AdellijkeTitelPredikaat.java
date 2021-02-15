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

import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * AdellijkeTitelPredikaat
 */
@AllArgsConstructor
@Getter
public enum AdellijkeTitelPredikaat {

  B("B", "Baron", "titel", "Hoogwelgeboren heer"),
  BS("BS", "Barones", "titel", "Hoogwelgeboren vrouw"),
  G("G", "Graaf", "titel", "Hoogwelgeboren heer"),
  GI("GI", "Gravin", "titel", "Hoogwelgeboren vrouw"),
  H("H", "Hertog", "titel", "Hoogwelgeboren heer"),
  HI("HI", "Hertogin", "titel", "Hoogwelgeboren vrouw"),
  JH("JH", "Jonkheer", "predikaat", "Hoogwelgeboren heer"),
  JV("JV", "Jonkvrouw", "predikaat", "Hoogwelgeboren vrouw"),
  M("M", "Markies", "titel", "Hoogwelgeboren heer"),
  MI("MI", "Markiezin", "titel", "Hoogwelgeboren vrouw"),
  P("P", "Prins", "titel", "Hoogheid"),
  PS("PS", "Prinses", "titel", "Hoogheid"),
  R("R", "Ridder", "titel", "Hoogwelgeboren heer");

  private String code;
  private String omschrijving;
  private String soort;
  private String aanhef;

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
    return getSoort().equals("titel");
  }

  public boolean isPredikaat() {
    return getSoort().equals("predikaat");
  }
}
