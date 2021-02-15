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

import static nl.procura.burgerzaken.gba.core.enums.GBAElem.*;
import static nl.procura.haalcentraal.brp.bevragen.converter.Util.toVoorletters;

import java.util.*;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import nl.procura.burgerzaken.gba.core.enums.GBACat;
import nl.procura.burgerzaken.gba.core.enums.GBAElem;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListCat;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListRec;
import nl.procura.gbaws.web.rest.v2.personlists.GbaWsPersonListSet;
import nl.procura.haalcentraal.brp.bevragen.converter.enums.AanschrijfwijzeEnum;
import nl.procura.haalcentraal.brp.bevragen.converter.enums.AdellijkeTitelPredikaat;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bip.NaamPersoon;

import lombok.Getter;

@Service
public class NaamPersoonConverterImpl extends BaseConverterImpl<NaamPersoon, PersonSource>
    implements NaamPersoonConverter {

  @Override
  public NaamPersoon createTarget(final PersonSource source, final Set<String> fields) {
    return new NaamPersoon();
  }

  @FunctionalInterface
  interface Mapper<S, T> {

    void apply(S source, T target);
  }

  private final Map<String, Mapper<AanschrijfWijze, StringBuilder>> aanschrijfMapping = new HashMap<>();
  private static final String                                       STREEPJE          = "-";

  public NaamPersoonConverterImpl() {
    put("voorletters", (naam, source) -> naam.setVoorletters(toVoorletters(source.getRec().getElemValue(VOORNAMEN))));
    put("voornamen", (naam, source) -> naam.setVoornamen(source.getRec().getElemValue(VOORNAMEN)));
    put("geslachtsnaam", (naam, source) -> naam.setGeslachtsnaam(source.getRec().getElemValue(GESLACHTSNAAM)));
    put("voorvoegsel", (naam, source) -> naam.setVoorvoegsel(source.getRec().getElemValue(VOORV_GESLACHTSNAAM)));
    put("aanhef", (naam, source) -> naam.setAanhef(toAanhef(source.getRec())));
    put("aanschrijfwijze", (naam, source) -> naam.setAanschrijfwijze(toAanschrijfwijze(source)));
  }

  private static String toAanhef(GbaWsPersonListRec rec) {
    if (StringUtils.hasText(rec.getElemValue(GBAElem.TITEL_PREDIKAAT))) {
      return AdellijkeTitelPredikaat.fromCode(rec.getElemValue(GBAElem.TITEL_PREDIKAAT)).getAanhef();
    }
    return null;
  }

  private String toAanschrijfwijze(final PersonSource source) {
    aanschrijfMapping.put("vl", (aanschrijf, sb) -> sb.append(aanschrijf.getVl()));
    aanschrijfMapping.put("vv", (aanschrijf, sb) -> sb.append(aanschrijf.getVv()));
    aanschrijfMapping.put("gn", (aanschrijf, sb) -> sb.append(aanschrijf.getGn()));
    aanschrijfMapping.put("at", (aanschrijf, sb) -> sb.append(toAdellijktitel(aanschrijf.getAt())));
    aanschrijfMapping.put("ap", (aanschrijf, sb) -> sb.append(toAdellijktitel(aanschrijf.getAp())));
    aanschrijfMapping.put("vp", (aanschrijf, sb) -> sb.append(aanschrijf.getVp()));
    aanschrijfMapping.put("gp", (aanschrijf, sb) -> sb.append(aanschrijf.getGp()));
    aanschrijfMapping.put("-", (aanschrijf, sb) -> sb.append(STREEPJE));

    GbaWsPersonListRec partner = new GbaWsPersonListRec();
    Optional<GbaWsPersonListCat> marriageCat = source.getPl().getCat(GBACat.HUW_GPS);
    if (marriageCat.isPresent()) {
      Optional<GbaWsPersonListSet> optionalPartner = marriageCat.get().getSets().stream()
          .filter(set -> set.getMostRecentMarriage() != null && set.getMostRecentMarriage())
          .findFirst();
      if (optionalPartner.isPresent()) {
        partner = optionalPartner.flatMap(GbaWsPersonListSet::getCurrentRec).get();
      }
    }

    AanschrijfWijze aanschrijfWijze = new AanschrijfWijze(source.getRec(), partner);
    List<AanschrijfwijzeEnum> samenstelling = AanschrijfwijzeEnum.toSamenstelling(source.getRec());
    if (partner.hasElems()
        && aanschrijfWijze.getAt() != null
        && samenstelling.contains(AanschrijfwijzeEnum.AT)
        && aanschrijfWijze.getAt().isPredikaat()) {
      samenstelling.remove(AanschrijfwijzeEnum.AT);
    }

    if (partner.hasElems()
        && aanschrijfWijze.getAp() != null
        && samenstelling.contains(AanschrijfwijzeEnum.AP)
        && aanschrijfWijze.getAp().isPredikaat()) {
      samenstelling.remove(AanschrijfwijzeEnum.AP);
    }

    if (!partner.hasElems()) {
      samenstelling.remove(AanschrijfwijzeEnum.STREEPJE);
    }

    if (samenstelling.isEmpty()) {
      return null;

    } else {
      StringBuilder stringBuilder = new StringBuilder();
      samenstelling
          .stream()
          .filter(s -> aanschrijfMapping.containsKey(s.getCode()))
          .map(AanschrijfwijzeEnum::getCode)
          .forEach(s -> aanschrijfMapping.get(s).apply(aanschrijfWijze, stringBuilder));
      return stringBuilder.toString().trim();
    }
  }

  private String toAdellijktitel(final AdellijkeTitelPredikaat adellijkeTitelPredikaat) {
    return adellijkeTitelPredikaat != null ? adellijkeTitelPredikaat.getOmschrijving().toLowerCase() + " " : "";
  }

  @Getter
  private static class AanschrijfWijze {

    private final GbaWsPersonListRec persoon;
    private final GbaWsPersonListRec partner;

    private final String                  vl; //persoon_voorletters
    private final String                  vv; //persoon_voorvoegsel
    private final String                  gn; //persoon_geslachtsnaam
    private final String                  vp; //partner_voorvoegsel
    private final String                  gp; //partner_geslachtsnaam
    private final AdellijkeTitelPredikaat at; //persoon_adellijk
    private final AdellijkeTitelPredikaat ap; //partner_adellijk

    public AanschrijfWijze(final GbaWsPersonListRec persoon, final GbaWsPersonListRec partner) {
      this.persoon = persoon;
      this.partner = partner;
      vl = getText(toVoorletters(persoon.getElemValue(VOORNAMEN)));
      vv = getText(persoon.getElemValue(VOORV_GESLACHTSNAAM));
      gn = getTextWithoutSpace(persoon.getElemValue(GESLACHTSNAAM));
      vp = getText(partner.getElemValue(VOORV_GESLACHTSNAAM));
      gp = getTextWithoutSpace(partner.getElemValue(GESLACHTSNAAM));
      at = getAdellijk(persoon.getElemValue(GBAElem.TITEL_PREDIKAAT));
      ap = getAdellijk(partner.getElemValue(GBAElem.TITEL_PREDIKAAT));
    }

    private String getText(final String str) {
      return StringUtils.hasText(str) ? str.concat(" ") : "";
    }

    private String getTextWithoutSpace(final String str) {
      return StringUtils.hasText(str) ? str : "";
    }

    private AdellijkeTitelPredikaat getAdellijk(final String code) {
      AdellijkeTitelPredikaat result = null;
      if (StringUtils.hasText(code)) {
        result = AdellijkeTitelPredikaat.fromCode(code);
      }
      return result;
    }
  }

}
