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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.util.StringUtils;

import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;

import lombok.Getter;

/**
 * AanschrijfwijzeEnum
 */
@Getter
public enum AanschrijfwijzeEnum {

  VL("vl"), //voorletters
  VV("vv"), //voorvoegselGeslachtsnaam
  GN("gn"), //geslachtsnaam
  VP("vp"), //voorvoegselGeslachtsnaam partner
  GP("gp"), //geslachtsnaam partner
  AT("at"), //adelijke titel
  AP("ap"), //adelijke titel partner
  PK("pk"), //predikaat
  STREEPJE("-");//streepje

  private final String code;

  AanschrijfwijzeEnum(final String code) {
    this.code = code;
  }

  public static List<AanschrijfwijzeEnum> toSamenstelling(final GbaWsPersonListRec rec) {
    List<AanschrijfwijzeEnum> list = Collections.emptyList();
    String aanduiding = rec.getElemValue(GBAElem.AANDUIDING_NAAMGEBRUIK);
    if (StringUtils.hasText(aanduiding)) {
      switch (aanduiding) {
        case "E":
          if (StringUtils.hasText(rec.getElemValue(GBAElem.TITEL_PREDIKAAT)) && AdellijkeTitelPredikaat
              .fromCode(rec.getElemValue(GBAElem.TITEL_PREDIKAAT)).isPredikaat()) {
            list = new LinkedList<>(Arrays.asList(AT, VL, VV, GN));
          } else {
            list = new LinkedList<>(Arrays.asList(VL, AT, VV, GN));
          }
          break;
        case "N":
          list = new LinkedList<>(Arrays.asList(VL, AT, VV, GN, STREEPJE, AP, VP, GP));
          break;
        case "P":
          list = new LinkedList<>(Arrays.asList(VL, AP, VP, GP));
          break;
        case "V":
          list = new LinkedList<>(Arrays.asList(VL, AP, VP, GP, STREEPJE, AT, VV, GN));
          break;
        default:
      }
    }
    return list;
  }
}
