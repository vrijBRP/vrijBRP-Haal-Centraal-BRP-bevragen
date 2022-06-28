package nl.procura.haalcentraal.brp.bevragen.converter.v1_3;

import static java.lang.String.valueOf;
import static nl.procura.burgerzaken.gba.core.enums.GBACat.PERSOON;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.util.StringUtils.hasText;

import java.util.Optional;
import java.util.Set;

import org.springframework.http.ResponseEntity;

import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3.IngeschrevenPersonenResourceV1_3;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.HalLink;

public class LinkUtils {

  public enum LinkType {
    PARTNER_LINK,
    CHILD_LINK,
    PARENT_LINK
  }

  public static HalLink createIngeschrevenPersoonLink(final String bsn, final Set<String> fields,
      final Set<String> expand) {
    return new HalLink().href(valueOf(linkTo(methodOn(IngeschrevenPersonenResourceV1_3.class, bsn)
        .getIngeschrevenPersoon(bsn,
            expand != null ? String.join(",", expand) : null,
            fields != null ? String.join(",", fields) : null))
                .toUri()));
  }

  public static HalLink createLink(PersonSource source, LinkType linkType) {
    Optional<GbaWsPersonListRec> currentRec = source.getPl().getCurrentRec(PERSOON);
    if (currentRec.isPresent()) {
      String bsn = Util.toBsn(currentRec.get());
      if (hasText(bsn)) {
        var resource = methodOn(IngeschrevenPersonenResourceV1_3.class, bsn, source.getId());
        var link = createLink(source, linkType, bsn, resource);
        return new HalLink().href(valueOf(linkTo(link).toUri()));
      }
    }
    return null;
  }

  private static ResponseEntity<?> createLink(PersonSource source, LinkType linkType,
      String bsn, IngeschrevenPersonenResourceV1_3 resource) {
    switch (linkType) {
      case PARTNER_LINK:
        return resource.getPartner(bsn, valueOf(source.getId()));
      case CHILD_LINK:
        return resource.getKind(bsn, valueOf(source.getId()));
      case PARENT_LINK:
        return resource.getOuder(bsn, valueOf(source.getId()));
      default:
        throw new IllegalArgumentException("Unknown link type: " + linkType);
    }
  }
}
