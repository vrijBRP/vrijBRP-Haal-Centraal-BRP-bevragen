package nl.procura.haalcentraal.brp.bevragen.model;

import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.AanduidingAanschrijving;
import java.util.Locale;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class RegelVoorafGaandAanAanschrijfwijze {

  private final FullNameParameters parameters;
  private final Aanhef             aanhef;

  public RegelVoorafGaandAanAanschrijfwijze(FullNameParameters parameters) {
    this.parameters = parameters;
    aanhef = new Aanhef(parameters);
  }

  public String getRegelVoorafgaand(GbaWsPersonListRec partner) {
    if (parameters.getGender().isUnknown()
        || AanduidingAanschrijving.P.equals(parameters.getFullNameUsage())
        || isNull(parameters.getTitleNoble())) {
      return null;
    } else if ((parameters.getTitleNoble().isPredikaat()
        && parameters.getGender().isFemale()
        && nonNull(partner))
        || AanduidingAanschrijving.V.equals(parameters.getFullNameUsage())) {
      return null;
    } else {
      return "De " + aanhef.getAanhef().toLowerCase(Locale.ROOT);
    }
  }
}
