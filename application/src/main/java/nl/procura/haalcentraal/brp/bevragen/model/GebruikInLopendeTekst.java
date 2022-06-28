package nl.procura.haalcentraal.brp.bevragen.model;

import lombok.Data;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.GeslachtAanduiding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.tomcat.util.IntrospectionUtils.capitalize;

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
    if (nonNull(parameters.getTitleNoble()) || nonNull(parameters.getTitleNoblePartner())) {
      return createUsageInTextWithTitle();
    } else {
      return createUsageInText(parameters.getGender());
    }
  }

  private String createUsageInTextWithTitle() {
    var aanschrijfwijze = aanschrijfWijze.getAanschrijfwijze();
    List<String> aanschrijfwijzeElementen = new ArrayList<>(asList(split(aanschrijfwijze)));
    if (!aanschrijfwijze.contains("Jonk")) {
      aanschrijfwijzeElementen.remove(0);
      aanschrijfwijzeElementen.add(1, capitalize(aanschrijfwijzeElementen.get(1)));
      aanschrijfwijzeElementen.remove(2);
    } else {
      aanschrijfwijzeElementen.add(0, aanschrijfwijzeElementen.get(0).toLowerCase(Locale.ROOT));
      aanschrijfwijzeElementen.remove(1);
      aanschrijfwijzeElementen.remove(1);
      if (aanschrijfwijzeElementen.get(1).startsWith("'")) {
        aanschrijfwijzeElementen.add(1, aanschrijfwijzeElementen.get(1).toUpperCase(Locale.ROOT));
      } else {
        aanschrijfwijzeElementen.add(1, capitalize(aanschrijfwijzeElementen.get(1)));
      }
      aanschrijfwijzeElementen.remove(2);
    }

    String gebruikInTekst = String.join(" ", aanschrijfwijzeElementen);
    return gebruikInTekst.trim();
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
