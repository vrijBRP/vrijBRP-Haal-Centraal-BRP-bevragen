package nl.procura.haalcentraal.brp.bevragen.converter.v1_3;

import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.Naam;
import org.springframework.stereotype.Service;
import java.util.Set;

import static nl.procura.burgerzaken.gba.core.enums.GBAElem.*;
import static nl.procura.burgerzaken.gba.core.enums.GBAElem.TITEL_PREDIKAAT;

@Service
public class NaamConverterImplV1_3<T extends Naam> extends BaseConverterImplV1_3<T, PersonSource>
    implements NaamConverterV1_3<T> {

  public NaamConverterImplV1_3() {
    put("geslachtsnaam", (naam, source) -> naam.setGeslachtsnaam(source.getRec().getElemValue(GESLACHTSNAAM)));
    put("voorletters",
        (naam, source) -> naam.setVoorletters(Util.toVoorletters(source.getRec().getElemValue(VOORNAMEN))));
    put("voornamen", (naam, source) -> naam.setVoornamen(source.getRec().getElemValue(VOORNAMEN)));
    put("voorvoegsel", (naam, source) -> naam.setVoorvoegsel(source.getRec().getElemValue(VOORV_GESLACHTSNAAM)));
    put("adellijkeTitelPredikaat",
        (naam, source) -> naam.adellijkeTitelPredikaat(Util.toWaarde(source.getRec().getElem(TITEL_PREDIKAAT))));
  }

  @Override
  public T createTarget(final PersonSource source, final Set<String> fields) {
    return (T) new Naam();
  }

  @Override
  public T postConvert(PersonSource source, T result, Set<String> fields) {
    result.setInOnderzoek(OnderzoekUtils.toNaamInOnderzoek(source.getRec()));
    return super.postConvert(source, result, fields);
  }
}
