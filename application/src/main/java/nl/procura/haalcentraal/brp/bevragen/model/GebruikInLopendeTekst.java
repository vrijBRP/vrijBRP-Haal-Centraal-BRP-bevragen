package nl.procura.haalcentraal.brp.bevragen.model;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.split;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.AdellijkeTitelPredikaat;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.GeslachtAanduiding;

import lombok.Data;

@Data
public class GebruikInLopendeTekst {

  private FullNameParameters parameters;
  private Aanhef             aanhef;
  private AanschrijfWijze    aanschrijfWijze;

  public GebruikInLopendeTekst(FullNameParameters parameters) {
    this.parameters = parameters;
    this.aanhef = new Aanhef(parameters);
    this.aanschrijfWijze = new AanschrijfWijze(parameters);
  }

  public String getUsageInText() {
    if (hasTitle(aanschrijfWijze.getAanschrijfwijze())) {
      return createUsageInTextWithTitle();
    } else {
      return createUsageInText(parameters.getGender());
    }
  }

  private boolean hasTitle(String aanschrijfwijze) {
    return Arrays.stream(AdellijkeTitelPredikaat.values())
        .anyMatch(tp -> aanschrijfwijze.contains(tp.getDescription()));
  }

  private String createUsageInTextWithTitle() {
    var aanschrijfwijze = aanschrijfWijze.getAanschrijfwijze();
    if (StringUtils.isNotBlank(aanschrijfwijze)) {
      List<String> aanschrijfwijzeElementen = new ArrayList<>(asList(split(aanschrijfwijze)));
      if (!aanschrijfwijzeElementen.isEmpty()) {
        for (String element : new ArrayList<>(aanschrijfwijzeElementen)) {
          if (element.contains(".")) {
            aanschrijfwijzeElementen.remove(element);
            break;
          }
        }
        return String.join(" ", aanschrijfwijzeElementen).trim();
      }
    }
    return aanschrijfwijze;
  }

  private String createUsageInText(GeslachtAanduiding gender) {
    var usageInTekst = new StringBuilder();
    if (gender.isMale()) {
      usageInTekst.append("de heer ");
    } else if (gender.isFemale()) {
      usageInTekst.append("mevrouw ");
    } else {
      if (nonNull(parameters.getInitials())) {
        usageInTekst.append(parameters.getInitials()).append(" ");
      }
    }
    return usageInTekst.append(aanhef.createCompositeName(parameters.getFullNameUsage(), true)).toString().trim();
  }
}
