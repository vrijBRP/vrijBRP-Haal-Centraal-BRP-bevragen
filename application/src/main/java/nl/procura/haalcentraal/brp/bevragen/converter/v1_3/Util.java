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

package nl.procura.haalcentraal.brp.bevragen.converter.v1_3;

import static java.util.stream.Collectors.toCollection;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.AanduidingAanschrijving;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.NaamgebruikEnum;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.StringUtils;

import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.numbers.Anr;
import nl.procura.burgerzaken.gba.numbers.Bsn;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListElem;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListVal;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.DatumOnvolledig;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.Waardetabel;

public final class Util {

  public static final DateTimeFormatter DBFORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd");

  private Util() {
  }

  public static boolean isWaardeOnbekend(Optional<GbaWsPersonListElem> elem) {
    return elem.isPresent() && NumberUtils.toLong(elem.get().getValue().getVal(), -1) == 0L;
  }

  public static Optional<Integer> toLeeftijd(Optional<LocalDate> geboorteDatum, LocalDate currentDate) {
    return geboorteDatum.map((datum) -> Period.between(datum, currentDate).getYears());
  }

  public static String toBsn(GbaWsPersonListRec rec) {
    return rec.getElem(GBAElem.BSN)
        .map(e -> new Bsn(e.getValue().getVal()).toString())
        .orElse(null);
  }

  public static String toAnr(GbaWsPersonListRec rec) {
    return rec.getElem(GBAElem.ANR)
        .map(e -> new Anr(e.getValue().getVal()).toString())
        .orElse(null);
  }

  public static boolean isHistoricDate(DatumOnvolledig datumOnvolledig) {
    if (datumOnvolledig != null) {
      StringBuilder date = new StringBuilder();
      date.append(datumOnvolledig.getJaar());
      date.append(String.format("%02d", Optional.ofNullable(datumOnvolledig.getMaand()).orElse(0)));
      date.append(String.format("%02d", Optional.ofNullable(datumOnvolledig.getDag()).orElse(0)));
      String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
      return Integer.parseInt(date.toString()) < Integer.parseInt(today);
    }
    return false;
  }

  public static DatumOnvolledig filterHistorischeDatum(DatumOnvolledig datumOnvolledig) {
    return isHistoricDate(datumOnvolledig) ? null : datumOnvolledig;
  }

  public static DatumOnvolledig toDatumOnvolledig(final String dateString) {
    boolean isCorrectLength = StringUtils.hasText(dateString) && dateString.length() == 8;
    boolean isNotUnknown = NumberUtils.toLong(dateString) > 0;
    if (isCorrectLength && isNotUnknown) {
      final int jaar = Integer.parseInt(dateString.substring(0, 4));
      final int maand = Integer.parseInt(dateString.substring(4, 6));
      final int dag = Integer.parseInt(dateString.substring(6, 8));
      return new DatumOnvolledig()
          .datum(toLocalDate(dateString).orElse(null))
          .jaar(jaar != 0 ? jaar : null)
          .maand(maand != 0 ? maand : null)
          .dag(dag != 0 ? dag : null);
    }
    return null;
  }

  public static Optional<LocalDate> toLocalDate(final String dateString) {
    try {
      if (StringUtils.hasText(dateString)) {
        return Optional.of(LocalDate.from(Util.DBFORMATTER.parse(dateString)));
      } else {
        return Optional.empty();
      }
    } catch (java.time.DateTimeException e) {
      return Optional.empty();
    }
  }

  public static Optional<String> fromLocalDate(final LocalDate localDate) {
    try {
      if (localDate != null) {
        return Optional.of(Util.DBFORMATTER.format(localDate));
      } else {
        return Optional.empty();
      }
    } catch (java.time.DateTimeException e) {
      return Optional.empty();
    }
  }

  public static Set<String> toFields(final String fields) {

    if (fields == null) {
      return null;
    }

    return Arrays.stream(fields.split(","))
        .filter(Objects::nonNull)
        .map(String::trim)
        .filter(StringUtils::hasText)
        .collect(toCollection(LinkedHashSet::new));
  }

  public static Waardetabel toWaarde(Optional<GbaWsPersonListElem> elem) {
    Waardetabel result = null;
    if (elem.isPresent()) {
      GbaWsPersonListVal value = elem.get().getValue();
      if (value != null && StringUtils.hasText(value.getVal())) {
        result = new Waardetabel();
        if (NumberUtils.isDigits(value.getVal())) {
          result.setCode(value.getVal());
        }
        result.setOmschrijving(value.getDescr());
      }
    }
    return result;
  }

  public static String toVoorletters(String voornamen) {
    if (StringUtils.hasText(voornamen)) {
      StringBuilder voorletters = new StringBuilder();
      String[] namen = voornamen.split("\\s+");
      for (String naam : namen) {
        if (naam.length() > 1) {
          voorletters.append(Character.toUpperCase(naam.charAt(0)))
              .append(".");
        } else {
          voorletters.append(naam.toUpperCase())
              .append(" ");
        }
      }
      return voorletters.toString().trim();
    } else {
      return null;
    }
  }

  public static Boolean toBooleanNull(Optional<GbaWsPersonListElem> elem) {
    if (elem.isPresent()) {
      return true;
    }
    return null;
  }

  public static NaamgebruikEnum toNaamgebruikEnum(String value) {
    AanduidingAanschrijving aanduiding = AanduidingAanschrijving.fromCode(value);
    switch (aanduiding) {
      default:
      case E:
        return NaamgebruikEnum.EIGEN;
      case P:
        return NaamgebruikEnum.PARTNER;
      case V:
        return NaamgebruikEnum.PARTNER_EIGEN;
      case N:
        return NaamgebruikEnum.EIGEN_PARTNER;
    }

  }
}
