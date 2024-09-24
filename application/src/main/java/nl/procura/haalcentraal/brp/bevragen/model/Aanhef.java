package nl.procura.haalcentraal.brp.bevragen.model;

import lombok.Data;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.AanduidingAanschrijving;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.AdellijkeTitelPredikaat;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.GeslachtAanduiding;
import java.util.Locale;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static nl.procura.haalcentraal.brp.bevragen.model.FullNameParameters.HYPHEN;
import static org.apache.commons.lang3.StringUtils.*;

/**
 * Voor een persoon zonder adellijke titel of predikaat begint de briefaanhef met “geachte mevrouw” (vrouw) of
 * “geachte heer” (man) of "geachte " plus de voorletters (onbekend), afhankelijk van het geslacht van de persoon.
 * Hierop volgt de samengestelde naam.
 *   De waarde van aanduidingNaamgebruik bepaalt hoe de aanhef wordt samengesteld uit de naam van de persoon en de
 *   naam van de partner.
 *
 *   Wanneer geslachtsaanduiding is niet leeg of "onbekend", dan wordt het voorvoegsel van de eerste geslachtsnaam
 *   in de briefaanhef met een hoofdletter geschreven.
 */
@Data
public class Aanhef {

  private FullNameParameters parameters;

  public Aanhef(FullNameParameters parameters) {
    this.parameters = parameters;
  }

  public String getAanhef() {
    String aanhef;
    if (isNull(parameters.getTitleNoble()) && isNull(parameters.getTitleNoblePartner())) {
      aanhef = createAanhefWithoutTitle().toString();
    } else {
      if (parameters.getGender().isUnknown() && !"hoogheid".equalsIgnoreCase(parameters.getTitleNoble().getAanhef())) {
        aanhef = createAanhefWithoutTitle().toString();
      } else {
        if (nonNull(parameters.getTitleNoble()) && nonNull(parameters.getTitleNoblePartner())) {
          aanhef = createAanhefWithBothATitle();
        } else if (nonNull(parameters.getTitleNoble())) {
          aanhef = createAanhefWithTitle();
        } else {
          aanhef = createAanhefWithTitlePartner();
        }
      }
    }
    return capitalize(aanhef);
  }

  private StringBuilder createAanhefWithoutTitle() {
    StringBuilder completeAanhef = new StringBuilder();
    completeAanhef
        .append(createDear(parameters.getGender()))
        .append(createCompositeName(parameters.getFullNameUsage(), true));
    return completeAanhef;
  }

  private String createDear(GeslachtAanduiding gender) {
    String result = "geachte ";
    if (gender == null || gender.isUnknown()) {
      if (isNotBlank(parameters.getInitials())) {
        result += parameters.getInitials() + " ";
      }
    } else if (gender.isMale()) {
      result += "heer ";
    } else {
      result += "mevrouw ";
    }
    return result;
  }

  public String createCompositeName(AanduidingAanschrijving fullNameUsage, boolean isAanhef) {
    StringBuilder fullName = new StringBuilder();

    switch (fullNameUsage) {
      default:
      case E:
        fullName.append(
            getPrefix(fullNameUsage, parameters.getPrefix(), parameters.getGender(), false, isAanhef));
        fullName.append(parameters.getLastName());
        break;

      case P:
        fullName.append(
            getPrefix(fullNameUsage, parameters.getPrefixPartner(), parameters.getGender(), true, isAanhef))
            .append(parameters.getLastNamePartner());
        break;

      case V:
        fullName
            .append(getPrefix(fullNameUsage, parameters.getPrefixPartner(), parameters.getGender(),
                true, isAanhef))
            .append(parameters.getLastNamePartner())
            .append(HYPHEN)
            .append(getPrefix(fullNameUsage, parameters.getPrefix(), parameters.getGender(),
                false, isAanhef))
            .append(parameters.getLastName());
        break;

      case N:
        fullName
            .append(getPrefix(fullNameUsage, parameters.getPrefix(), parameters.getGender(),
                false, isAanhef))
            .append(parameters.getLastName())
            .append(HYPHEN)
            .append(getPrefix(fullNameUsage, parameters.getPrefixPartner(), parameters.getGender(),
                true, isAanhef))
            .append(parameters.getLastNamePartner());
        break;
    }
    return fullName.toString();
  }

  private static String getPrefix(AanduidingAanschrijving fullNameUsage, String prefix, GeslachtAanduiding gender,
      boolean partner,
      boolean isAanhef) {
    String result = "";

    if (isNotBlank(prefix)) {
      if (GeslachtAanduiding.ONBEKEND == gender ||
          (AanduidingAanschrijving.N == fullNameUsage && partner) ||
          (AanduidingAanschrijving.V == fullNameUsage && !partner) ||
          !isAanhef) {
        result = prefix.toLowerCase(Locale.ROOT);
      } else {
        result = capitalize(prefix.toLowerCase(Locale.ROOT));
      }
      result += " ";
    }
    return result;
  }

  private String createAanhefWithTitle() {
    switch (parameters.getFullNameUsage().toString()) {
      case "E":
      case "V":
      case "N":
        return parameters.getTitleNoble().getAanhef();
      case "P":
        return createAanhefWithoutTitle().toString();
      default:
        return null;
    }
  }

  private String createAanhefWithTitlePartner() {
    switch (parameters.getFullNameUsage().toString()) {
      case "E":
        return createAanhefWithoutTitle().toString();
      case "P":
      case "V":
      case "N":
        return changeAanhefByGender(parameters.getGender(), parameters.getTitleNoblePartner());
      default:
        return null;
    }
  }

  private String createAanhefWithBothATitle() {
    switch (parameters.getFullNameUsage()) {
      case E:
      case V:
      case N:
        return parameters.getTitleNoble().getAanhef();
      case P:
        return changeAanhefByGender(parameters.getGender(), parameters.getTitleNoblePartner());
      default:
        return null;
    }
  }

  private static String changeAanhefByGender(GeslachtAanduiding gender, AdellijkeTitelPredikaat titleNoble) {
    if (gender.isFemale()) {
      return titleNoble.getAanhef().replace("heer", "vrouwe");
    }
    return titleNoble.getAanhef().replace("vrouwe", "heer");
  }
}
