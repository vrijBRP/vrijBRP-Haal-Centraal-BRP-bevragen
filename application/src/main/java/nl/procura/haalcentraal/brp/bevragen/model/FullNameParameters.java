package nl.procura.haalcentraal.brp.bevragen.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.Util;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.AanduidingAanschrijving;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.AdellijkeTitelPredikaat;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.GeslachtAanduiding;
import org.springframework.util.StringUtils;
import java.util.Locale;
import static nl.procura.burgerzaken.gba.core.enums.GBAElem.*;
import static org.apache.commons.lang3.StringUtils.*;

@Data
@NoArgsConstructor
public class FullNameParameters {

  private String                  initials;
  private String                  firstNames;
  private String                  prefix;
  private String                  lastName;
  private String                  prefixPartner;
  private String                  lastNamePartner;
  private AdellijkeTitelPredikaat titleNoble;
  private AdellijkeTitelPredikaat titleNoblePartner;
  private GeslachtAanduiding      gender;           //gender M V O
  private AanduidingAanschrijving fullNameUsage;    // Usage E V P N

  public static final String HYPHEN = "-";

  public FullNameParameters(String initials, String firstNames, String prefix, String lastName, String prefixPartner,
      String lastNamePartner, AdellijkeTitelPredikaat titleNoble, AdellijkeTitelPredikaat titleNoblePartner,
      GeslachtAanduiding gender,
      AanduidingAanschrijving fullNameUsage) {
    this.initials = trimToEmpty(initials);
    this.firstNames = trimToEmpty(firstNames);
    this.prefix = trimToEmpty(prefix);
    this.lastName = trimToEmpty(lastName);
    this.prefixPartner = trimToEmpty(prefixPartner);
    this.lastNamePartner = trimToEmpty(lastNamePartner);
    this.titleNoble = titleNoble;
    this.titleNoblePartner = titleNoblePartner;
    this.gender = gender;
    this.fullNameUsage = fullNameUsage;
  }

  public FullNameParameters(GbaWsPersonListRec person, GbaWsPersonListRec partner) {
    this.initials = trimToEmpty(Util.toVoorletters(person.getElemValue(VOORNAMEN)));
    this.firstNames = trimToEmpty(person.getElemValue(VOORNAMEN));
    this.prefix = trimToEmpty(person.getElemValue(VOORV_GESLACHTSNAAM)).toLowerCase(Locale.ROOT);
    this.lastName = trimToEmpty(person.getElemValue(GESLACHTSNAAM));
    this.prefixPartner = trimToEmpty(partner.getElemValue(VOORV_GESLACHTSNAAM)).toLowerCase(Locale.ROOT);
    this.lastNamePartner = trimToEmpty(partner.getElemValue(GESLACHTSNAAM));
    this.titleNoble = getAdellijk(person.getElemValue(TITEL_PREDIKAAT));
    this.titleNoblePartner = getAdellijk(partner.getElemValue(TITEL_PREDIKAAT));
    this.gender = getGender(person.getElemValue(GESLACHTSAAND));
    this.fullNameUsage = getFullNameUsage(person.getElemValue(AANDUIDING_NAAMGEBRUIK));
  }

  //TODO: 3 duplicated methods refactor into generic()
  private static AdellijkeTitelPredikaat getAdellijk(final String code) {
    AdellijkeTitelPredikaat result = null;
    if (StringUtils.hasText(code)) {
      result = AdellijkeTitelPredikaat.fromCode(code);
    }
    return result;
  }

  private static GeslachtAanduiding getGender(final String code) {
    GeslachtAanduiding result = null;
    if (StringUtils.hasText(code)) {
      result = GeslachtAanduiding.fromCode(code);
    }
    return result;
  }

  private static AanduidingAanschrijving getFullNameUsage(final String code) {
    AanduidingAanschrijving result = AanduidingAanschrijving.E;
    if (StringUtils.hasText(code)) {
      result = AanduidingAanschrijving.fromCode(code);
    }
    return result;
  }

}
