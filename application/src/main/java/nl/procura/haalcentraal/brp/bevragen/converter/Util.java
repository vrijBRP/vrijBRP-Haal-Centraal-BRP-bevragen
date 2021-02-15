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

package nl.procura.haalcentraal.brp.bevragen.converter;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.StringUtils;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.burgerzaken.gba.core.enums.GBAGroup;
import nl.procura.burgerzaken.gba.core.enums.GBAGroupElements;
import nl.procura.commons.core.utils.ProNumberUtils;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListElem;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListVal;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.DatumOnvolledig;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.Waardetabel;

public final class Util {

  public static final DateTimeFormatter         DBFORMATTER = DateTimeFormatter.ofPattern("uuuuMMdd");
  private static final Function<String, String> formatBsn   = (
      bsn) -> bsn != null ? String.format("%09d", Long.valueOf(bsn)) : null;

  private Util() {
  }

  public static boolean isWaardeOnbekend(Optional<GbaWsPersonListElem> elem) {
    return elem.isPresent() && NumberUtils.toLong(elem.get().getValue().getVal(), -1) == 0L;
  }

  public static Optional<Integer> toLeeftijd(Optional<LocalDate> geboorteDatum, LocalDate currentDate) {
    return geboorteDatum.map((datum) -> Period.between(datum, currentDate).getYears());
  }

  public static String toBsn(String bsn) {
    return Util.formatBsn.apply(bsn);
  }

  public static boolean isBsnMatch(String bsn1, String bsn2) {
    return Objects.equals(toBsn(bsn1), toBsn(bsn2));
  }

  public static String toBsn(GbaWsPersonListRec rec) {
    return rec.getElem(GBAElem.BSN)
        .map(e -> toBsn(e.getValue().getVal()))
        .orElse(null);
  }

  public static DatumOnvolledig toDatumOnvolledig(final String dateString) {
    if (StringUtils.hasText(dateString) && dateString.length() == 8) {
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
        .collect(Collectors.toSet());
  }

  public static Waardetabel toWaarde(Optional<GbaWsPersonListElem> elem) {
    Waardetabel result = null;
    if (elem.isPresent()) {
      GbaWsPersonListVal value = elem.get().getValue();
      if (value != null && StringUtils.hasText(value.getVal())) {
        result = new Waardetabel();
        result.setCode(value.getVal());
        result.setOmschrijving(value.getDescr());
      }
    }
    return result;
  }

  public static Boolean getInOnderzoek(GbaWsPersonListRec rec, GBAElem... elems) {
    String aand = rec.getElemValue(GBAElem.AAND_GEG_IN_ONDERZ);
    if (StringUtils.hasText(aand) && aand.length() == 6) {
      if (aand.endsWith("0000")) {// Category under investigation
        GBACat cat = GBACat.getByCode(ProNumberUtils.toInt(aand.substring(0, 2)));
        return Arrays.stream(elems)
            .map(elem -> GBAGroupElements.getByCat(cat.getCode(), elem.getCode()))
            .anyMatch(Objects::nonNull);

      } else if (aand.endsWith("00")) { // Group in category under investigation
        GBACat cat = GBACat.getByCode(ProNumberUtils.toInt(aand.substring(0, 2)));
        GBAGroup group = GBAGroup.getByCode(ProNumberUtils.toInt(aand.substring(2, 4)));
        return Arrays.stream(elems)
            .map(elem -> GBAGroupElements.getByCat(cat.getCode(), elem.getCode()))
            .filter(Objects::nonNull)
            .filter(groupElem -> groupElem.getGroup() == group)
            .anyMatch(obj -> true);

      } else { // Element under investigation
        GBACat cat = GBACat.getByCode(ProNumberUtils.toInt(aand.substring(0, 2)));
        GBAGroup group = GBAGroup.getByCode(ProNumberUtils.toInt(aand.substring(2, 4)));
        GBAElem element = GBAElem.getByCode(ProNumberUtils.toInt(aand.substring(2, 6)));

        return Arrays.stream(elems)
            .map(elem -> GBAGroupElements.getByCat(cat.getCode(), elem.getCode()))
            .filter(Objects::nonNull)
            .filter(groupElem -> groupElem.getGroup() == group)
            .filter(groupElem -> groupElem.getElem() == element)
            .anyMatch(obj -> true);
      }
    }
    return null;
  }

  public static boolean inOnderzoek(GbaWsPersonListRec rec, GBAElem... elems) {
    Boolean inOnderzoek = getInOnderzoek(rec, elems);
    return inOnderzoek != null && inOnderzoek;
  }

  public static String toVoorletters(String voornamen) {
    if (StringUtils.hasText(voornamen)) {
      StringBuilder voorletters = new StringBuilder();
      String[] namen = voornamen.split(" ");
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
}
