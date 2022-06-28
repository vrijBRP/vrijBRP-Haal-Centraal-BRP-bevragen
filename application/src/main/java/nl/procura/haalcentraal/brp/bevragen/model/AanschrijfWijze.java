package nl.procura.haalcentraal.brp.bevragen.model;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.AdellijkeTitelPredikaat.*;
import static nl.procura.haalcentraal.brp.bevragen.model.FullNameParameters.HYPHEN;

import lombok.Data;
import nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums.AanduidingAanschrijving;
import java.util.Locale;

@Data
public class AanschrijfWijze {

  private FullNameParameters parameters;
  private Aanhef             aanhef;

  public AanschrijfWijze(FullNameParameters parameters) {
    this.parameters = parameters;
    aanhef = new Aanhef(parameters);
  }

  public String getAanschrijfwijze() {
    String aanschrijfwijze;
    if (isNull(parameters.getTitleNoble()) && isNull(parameters.getTitleNoblePartner())) {
      aanschrijfwijze = createAanschrijfwijzeWithoutTitle(parameters.getFullNameUsage());
    } else {
      if (parameters.getGender().isUnknown()) {
        aanschrijfwijze = createAanschrijfwijzeWithoutTitle(parameters.getFullNameUsage());
      } else {
        if (nonNull(parameters.getTitleNoble()) && nonNull(parameters.getTitleNoblePartner())) {
          aanschrijfwijze = createAanschrijfwijzeBothWithTitle(parameters.getFullNameUsage());
        } else if (nonNull(parameters.getTitleNoble())) {
          aanschrijfwijze = createAanschrijfwijzeWithTitle(parameters.getFullNameUsage());
        } else {
          aanschrijfwijze = createAanschrijfwijzeWithTitlePartner(parameters.getFullNameUsage());
        }
      }
    }
    return aanschrijfwijze;
  }

  private String createAanschrijfwijzeBothWithTitle(AanduidingAanschrijving fullNameUsage) {
    var aanschrijfwijze = new StringBuilder();
    boolean titlePartnerExceptR = parameters.getTitleNoblePartner().isTitel()
        && R != parameters.getTitleNoblePartner();

    switch (fullNameUsage) {
      default:
      case E:
        composeAfterCheckForTitleOrPredicate(aanschrijfwijze);
        break;
      case N:
        composeAfterCheckForTitleOrPredicate(aanschrijfwijze);
        aanschrijfwijze.append(HYPHEN);
        composeWithTitlePartner(aanschrijfwijze);
        break;
      case V:
        composeInitialsAndWithTitlePartner(aanschrijfwijze);
        aanschrijfwijze.append(HYPHEN);
        composeWithOwnTitle(aanschrijfwijze);
        break;
      case P:
        if (titlePartnerExceptR) {
          composeInitialsAndWithTitlePartner(aanschrijfwijze);
        } else {
          createAanschrijfwijzeWithoutTitle(fullNameUsage);
        }
    }
    return aanschrijfwijze.toString();
  }

  private void composeAfterCheckForTitleOrPredicate(StringBuilder aanschrijfwijze) {
    if (parameters.getTitleNoble().isTitel()) {
      composeIsTitleWithInitials(aanschrijfwijze);
    } else {
      composeIsPredicateWithInitials(aanschrijfwijze);
    }
  }

  private void composeIsTitleWithInitials(StringBuilder aanschrijfwijze) {
    aanschrijfwijze
        .append(parameters.getInitials())
        .append(" ")
        .append(parameters.getTitleNoble().getDescription().toLowerCase(Locale.ROOT))
        .append(" ");
    composeOwnName(aanschrijfwijze);
  }

  private void composeIsPredicateWithInitials(StringBuilder aanschrijfwijze) {
    aanschrijfwijze
        .append(parameters.getTitleNoble().getDescription())
        .append(" ")
        .append(parameters.getInitials())
        .append(" ");
    composeOwnName(aanschrijfwijze);
  }

  private void composeWithOwnTitle(StringBuilder aanschrijfwijze) {
    aanschrijfwijze.append(parameters.getTitleNoble().getDescription().toLowerCase(Locale.ROOT));
    composeOwnName(aanschrijfwijze);
  }

  private void composeOwnName(StringBuilder aanschrijfwijze) {
    if (nonNull(parameters.getPrefix())) {
      aanschrijfwijze
          .append(parameters.getPrefix())
          .append(" ");
    }
    aanschrijfwijze.append(parameters.getLastName());
  }

  private void composeInitialsAndWithTitlePartner(StringBuilder aanschrijfwijze) {
    aanschrijfwijze
        .append(parameters.getInitials())
        .append(" ");
    composeWithTitlePartner(aanschrijfwijze);
  }

  private void composeWithTitlePartner(StringBuilder aanschrijfwijze) {
    var titleNoblePartner = parameters.getTitleNoblePartner();

    if (parameters.getGender().equals(titleNoblePartner.getGender()) &&
        !R.equals(titleNoblePartner)) {
      aanschrijfwijze
          .append(titleNoblePartner.getDescription().toLowerCase(Locale.ROOT))
          .append(" ");
    } else {
      if (nonNull(titleNoblePartner.getSwitchGender())) {
        aanschrijfwijze
            .append(titleNoblePartner.getSwitchGender().getDescription().toLowerCase(Locale.ROOT))
            .append(" ");
      }
    }
    composeNamePartner(aanschrijfwijze);
  }

  private String createAanschrijfwijzeWithTitle(AanduidingAanschrijving fullNameUsage) {
    var aanschrijfwijze = new StringBuilder();

    switch (fullNameUsage) {
      default:
      case E:
        composeAfterCheckForTitleOrPredicate(aanschrijfwijze);
        break;
      case V:
        composeInitialsAndNamePartner(aanschrijfwijze);
        aanschrijfwijze.append(HYPHEN);
        composeWithOwnTitle(aanschrijfwijze);
        break;
      case N:
        composeAfterCheckForTitleOrPredicate(aanschrijfwijze);
        aanschrijfwijze.append(HYPHEN);
        composeNamePartner(aanschrijfwijze);
        break;
      case P:
        createAanschrijfwijzeWithoutTitle(fullNameUsage);
        break;
    }
    return aanschrijfwijze.toString();
  }

  private void composeNamePartner(StringBuilder aanschrijfwijze) {
    if (nonNull(parameters.getPrefixPartner())) {
      aanschrijfwijze
          .append(parameters.getPrefixPartner())
          .append(" ");
    }
    aanschrijfwijze.append(parameters.getLastNamePartner());
  }

  private void composeInitialsAndNamePartner(StringBuilder aanschrijfwijze) {
    aanschrijfwijze
        .append(parameters.getInitials())
        .append(" ");
    composeNamePartner(aanschrijfwijze);
  }

  private String createAanschrijfwijzeWithTitlePartner(AanduidingAanschrijving fullNameUsage) {
    var aanschrijfwijze = new StringBuilder();
    boolean titlePartnerExceptR = parameters.getTitleNoblePartner().isTitel()
        && R != parameters.getTitleNoblePartner();

    switch (fullNameUsage) {
      default:
      case E:
        createAanschrijfwijzeWithoutTitle(fullNameUsage);
        break;
      case N:
        if (titlePartnerExceptR) {
          composeInitialsAndName(aanschrijfwijze);
          aanschrijfwijze.append(HYPHEN);
          composeWithTitlePartner(aanschrijfwijze);
        } else {
          createAanschrijfwijzeWithoutTitle(fullNameUsage);
        }
        break;
      case V:
        if (titlePartnerExceptR) {
          composeInitialsAndWithTitlePartner(aanschrijfwijze);
          aanschrijfwijze.append(HYPHEN);
          composeInitialsAndName(aanschrijfwijze);
        } else {
          createAanschrijfwijzeWithoutTitle(fullNameUsage);
        }
        break;
      case P:
        if (titlePartnerExceptR) {
          composeInitialsAndWithTitlePartner(aanschrijfwijze);
        } else {
          createAanschrijfwijzeWithoutTitle(fullNameUsage);
        }
        break;
    }
    return aanschrijfwijze.toString();
  }

  private void composeInitialsAndName(StringBuilder aanschrijfwijze) {
    aanschrijfwijze
        .append(parameters.getInitials())
        .append(" ");
    composeOwnName(aanschrijfwijze);
  }

  private String createAanschrijfwijzeWithoutTitle(AanduidingAanschrijving fullNameUsage) {
    return parameters.getInitials() +
        " " +
        aanhef.createCompositeName(fullNameUsage, false);
  }
}
