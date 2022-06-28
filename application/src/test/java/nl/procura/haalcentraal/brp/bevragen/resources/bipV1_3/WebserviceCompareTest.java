package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import static java.util.Collections.singletonList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import nl.procura.burgerzaken.gba.numbers.Bsn;
import nl.procura.gbaws.testdata.Testdata;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.*;

import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import spring.PersonRecordSource;

@Slf4j
public class WebserviceCompareTest extends IngeschrevenPersonenResourceTest {

  @Test
  @Disabled
  @SneakyThrows
  public void mustCompareMultiplePeople() {
    int numberOfBsns = 700;
    writeResults(getBsns(numberOfBsns).stream().map(this::getResult).collect(Collectors.toList()), false);
  }

  @Test
  @Disabled
  @SneakyThrows
  public void mustCompareSinglePerson() {
    writeResults(singletonList(getResult(999990019L)), false);
  }

  private List<Long> getBsns(int max) {
    Set<Long> bsns = Testdata.getGbaVBsns();
    return new ArrayList<>(bsns).subList(0, Math.min(max, bsns.size()));
  }

  private TestPersonResult getResult(Long bsn) {
    TestPersonResult result = new TestPersonResult();
    result.setBsn(new Bsn(bsn).toString());

    try {
      IngeschrevenPersoonHal procura;
      IngeschrevenPersoonHal vng;
      try {
        PersonRecordSource.emptyQueue();
        procura = getIngeschrevenPersoon(bsn);
        vng = getVngIngeschrevenPersoon(bsn);
        result.setProcuraPerson(procura);
        result.setVngPerson(vng);
      } catch (Exception e) {
        result.setSkipped(true);
        return result;
      }

      add(result, "bsn", IngeschrevenPersoon::getBurgerservicenummer);
      // add(result, "a-nummer", IngeschrevenPersoon::getaNummer); // Niet in VNG data
      add(result, "geheimhoudingpersoonsgegevens", IngeschrevenPersoon::getGeheimhoudingPersoonsgegevens);
      add(result, "geslachtsaanduiding", IngeschrevenPersoon::getGeslachtsaanduiding);
      add(result, "leeftijd", IngeschrevenPersoon::getLeeftijd);
      // add(result, "datum inschrijving gba", IngeschrevenPersoon::getDatumEersteInschrijvingGBA); // Niet in VNG data

      compareNaamPersoon(result);
      compareGeboorte(result);
      compareVerblijfplaats(result);

      if (add(result, "links", person -> person.getLinks() != null)) {
        if (add(result, "links.kinderen", person -> person.getLinks().getKinderen() != null)) {
          if (procura.getLinks().getKinderen() != null) {
            add(result, "links.kinderen.count", person -> person.getLinks().getKinderen().size());
          }
        }

        if (add(result, "links.kinderen", person -> person.getLinks().getKinderen() != null)) {
          if (procura.getLinks().getKinderen() != null) {
            add(result, "links.kinderen.count", person -> person.getLinks().getKinderen().size());
          }
        }

        if (add(result, "links.partners", person -> person.getLinks().getPartners() != null)) {
          if (procura.getLinks().getPartners() != null) {
            add(result, "links.partners.count", person -> person.getLinks().getPartners().size());
          }
        }
      }

      add(result, "kiesrecht", person -> person.getKiesrecht() != null);
      add(result, "inOnderzoek", IngeschrevenPersoon::getInOnderzoek);
      add(result, "opschorting", person -> person.getOpschortingBijhouding() != null);
      add(result, "overlijden", person -> person.getOverlijden() != null);
      add(result, "gezagsverhouding", person -> person.getGezagsverhouding() != null);

      // add(result, "nationaliteiten", person -> person.getNationaliteiten() != null); // Nog niet geimplementeerd
      // add(result, "verblijfstitel", person -> person.getVerblijfstitel() != null); // Nog niet geimplementeerd
      // add(result, "reisdocumentnummer", person -> person.getReisdocumentnummers() != null); // Nog niet geimplementeerd

      if (isNotNull(result, IngeschrevenPersoonHal::getOverlijden)) {
        compareOverlijden(result, IngeschrevenPersoon::getOverlijden);
      }

      if (isNotNull(result, IngeschrevenPersoonHal::getOpschortingBijhouding)) {
        compareOpschorting(result, IngeschrevenPersoon::getOpschortingBijhouding);
      }

      if (isNotNull(result, IngeschrevenPersoonHal::getGezagsverhouding)) {
        compareGezag(result, IngeschrevenPersoon::getGezagsverhouding);
      }

      if (isNotNull(result, IngeschrevenPersoonHal::getEmbedded)) {
        add(result, "kinderen", person -> person.getEmbedded().getKinderen() != null);
        add(result, "ouders", person -> person.getEmbedded().getOuders() != null);
        add(result, "partners", person -> person.getEmbedded().getPartners() != null);

        if (isNotNull(result, person -> person.getEmbedded().getKinderen())) {
          compareKind(result, person -> person.getEmbedded().getKinderen()
              .stream().min(Comparator.comparing(a -> a.getNaam().getVoornamen()))
              .orElse(null));
        }

        if (isNotNull(result, person -> person.getEmbedded().getOuders())) {
          compareNaam(result, "ouder", person -> person.getEmbedded()
              .getOuders().get(0).getNaam());
        }

        if (isNotNull(result, person -> person.getEmbedded().getPartners())) {
          compareNaam(result, "partner", person -> person.getEmbedded()
              .getPartners().get(0).getNaam());
        }
      }

      result.setDifferentValues(result.getDataList()
          .stream()
          .filter(info -> !info.isEquals)
          .map(ComparedData::getName)
          .collect(Collectors.toList()));

    } catch (Exception e) {
      e.printStackTrace();
      result.setException(e.getMessage());
    }
    return result;
  }

  private void compareOverlijden(TestPersonResult result, Function<IngeschrevenPersoonHal, Overlijden> function) {
    add(result, "overlijden.indicatie", person -> function.apply(person).getIndicatieOverleden());
    add(result, "overlijden.datum", person -> function.apply(person).getDatum());
    add(result, "overlijden.plaats", person -> function.apply(person).getPlaats());
    add(result, "overlijden.land", person -> function.apply(person).getLand());
    add(result, "overlijden.onderzoek", person -> function.apply(person).getInOnderzoek());
  }

  private void compareOpschorting(TestPersonResult result,
      Function<IngeschrevenPersoonHal, OpschortingBijhouding> function) {
    add(result, "opschorting.datum", person -> function.apply(person).getDatum());
    add(result, "opschorting.reden", person -> function.apply(person).getReden());
  }

  private void compareGezag(TestPersonResult result,
      Function<IngeschrevenPersoonHal, Gezagsverhouding> function) {
    add(result, "gezag.minderjarige", person -> function.apply(person).getIndicatieGezagMinderjarige());
    add(result, "gezag.curatele", person -> function.apply(person).getIndicatieCurateleRegister());
    add(result, "gezag.onderzoek", person -> function.apply(person).getInOnderzoek());
  }

  private void compareKind(TestPersonResult result, Function<IngeschrevenPersoonHal, KindHalBasis> function) {
    add(result, "kind.leeftijd", person -> function.apply(person).getLeeftijd());
    add(result, "kind.geheimhouding", person -> function.apply(person).getGeheimhoudingPersoonsgegevens());
    add(result, "kind.bsn", person -> function.apply(person).getBurgerservicenummer());
    add(result, "kind.onderzoek", person -> function.apply(person).getInOnderzoek());
    compareNaam(result, "kind", person -> person.getEmbedded().getKinderen()
        .stream().min(Comparator.comparing(a -> a.getNaam().getVoornamen()))
        .map(Kind::getNaam)
        .orElse(null));
  }

  private void compareNaam(TestPersonResult result, String type, Function<IngeschrevenPersoonHal, Naam> function) {
    add(result, type + ".naam.geslachtsnaam", person -> function.apply(person).getGeslachtsnaam());
    add(result, type + ".naam.titelPredikaat", person -> function.apply(person).getAdellijkeTitelPredikaat());
    add(result, type + ".naam.voorletters", person -> function.apply(person).getVoorletters());
    add(result, type + ".naam.voornamen", person -> function.apply(person).getVoornamen());
    add(result, type + ".naam.voorvoegsel", person -> function.apply(person).getVoorvoegsel());
    add(result, type + ".naam.inOnderzoek", person -> function.apply(person).getInOnderzoek());
  }

  private void compareNaamPersoon(TestPersonResult result) {
    add(result, "naam.aanhef",
        person -> person.getNaam().getAanhef());
    add(result, "naam.aanschrijfwijze",
        person -> person.getNaam().getAanschrijfwijze());
    add(result, "naam.regelVoorafgaandAanAanschrijfwijze",
        person -> person.getNaam().getRegelVoorafgaandAanAanschrijfwijze());
    add(result, "naam.gebruikInLopendeTekst",
        person -> person.getNaam().getGebruikInLopendeTekst());
    add(result, "naam.aanduidingNaamgebruik",
        person -> person.getNaam().getAanduidingNaamgebruik());
    add(result, "naam.inOnderzoek",
        person -> person.getNaam().getInOnderzoek());
    add(result, "naam.geslachtsnaam",
        person -> person.getNaam().getGeslachtsnaam());
    add(result, "naam.voorletters",
        person -> person.getNaam().getVoorletters());
    add(result, "naam.voornamen",
        person -> person.getNaam().getVoornamen());
    add(result, "naam.voorvoegsel",
        person -> person.getNaam().getVoorvoegsel());
    add(result, "naam.adellijkeTitelPredikaat",
        person -> person.getNaam().getAdellijkeTitelPredikaat());
  }

  private void compareGeboorte(TestPersonResult testInfo) {
    add(testInfo, "geboorte.datum",
        person -> person.getGeboorte().getDatum());
    add(testInfo, "geboorte.plaats",
        person -> person.getGeboorte().getPlaats());
    add(testInfo, "geboorte.datum",
        person -> person.getGeboorte().getDatum());
    add(testInfo, "geboorte.inonderzoek",
        person -> person.getGeboorte().getInOnderzoek());
  }

  private void compareVerblijfplaats(TestPersonResult testInfo) {
    add(testInfo, "verblijfplaats.adresseerbaarObjectIdentificatie",
        person -> person.getVerblijfplaats().getAdresseerbaarObjectIdentificatie());
    add(testInfo, "verblijfplaats.aanduidingBijHuisnummer",
        person -> person.getVerblijfplaats().getAanduidingBijHuisnummer());
    add(testInfo, "verblijfplaats.nummeraanduidingIdentificatie",
        person -> person.getVerblijfplaats().getNummeraanduidingIdentificatie());
    add(testInfo, "verblijfplaats.functieAdres",
        person -> person.getVerblijfplaats().getFunctieAdres());
    add(testInfo, "verblijfplaats.indicatieVestigingVanuitBuitenland",
        person -> person.getVerblijfplaats().getIndicatieVestigingVanuitBuitenland());
    add(testInfo, "verblijfplaats.locatiebeschrijving",
        person -> person.getVerblijfplaats().getLocatiebeschrijving());
    add(testInfo, "verblijfplaats.korteNaam",
        person -> person.getVerblijfplaats().getKorteNaam());
    add(testInfo, "verblijfplaats.vanuitVertrokkenOnbekendWaarheen",
        person -> person.getVerblijfplaats().getVanuitVertrokkenOnbekendWaarheen());
    add(testInfo, "verblijfplaats.datumAanvangAdreshouding",
        person -> person.getVerblijfplaats().getDatumAanvangAdreshouding());
    add(testInfo, "verblijfplaats.datumIngangGeldigheid",
        person -> person.getVerblijfplaats().getDatumIngangGeldigheid());
    add(testInfo, "verblijfplaats.datumInschrijvingInGemeente",
        person -> person.getVerblijfplaats().getDatumInschrijvingInGemeente());
    add(testInfo, "verblijfplaats.datumVestigingInNederland",
        person -> person.getVerblijfplaats().getDatumVestigingInNederland());
    add(testInfo, "verblijfplaats.gemeenteVanInschrijving",
        person -> person.getVerblijfplaats().getGemeenteVanInschrijving());
    add(testInfo, "verblijfplaats.landVanwaarIngeschreven",
        person -> person.getVerblijfplaats().getLandVanwaarIngeschreven());
    add(testInfo, "verblijfplaats.adresregel1",
        person -> person.getVerblijfplaats().getAdresregel1());
    add(testInfo, "verblijfplaats.adresregel3",
        person -> person.getVerblijfplaats().getAdresregel2());
    add(testInfo, "verblijfplaats.vertrokkenOnbekendWaarheen",
        person -> person.getVerblijfplaats().getAdresregel3());
    add(testInfo, "verblijfplaats.vertrokkenOnbekendWaarheen",
        person -> person.getVerblijfplaats().getVertrokkenOnbekendWaarheen());
    add(testInfo, "verblijfplaats.land",
        person -> person.getVerblijfplaats().getLand());
    add(testInfo, "verblijfplaats.inOnderzoek",
        person -> person.getVerblijfplaats().getInOnderzoek());
    add(testInfo, "verblijfplaats.straat",
        person -> person.getVerblijfplaats().getStraat());
    add(testInfo, "verblijfplaats.huisnummer",
        person -> person.getVerblijfplaats().getHuisnummer());
    add(testInfo, "verblijfplaats.huisletter",
        person -> person.getVerblijfplaats().getHuisletter());
    add(testInfo, "verblijfplaats.huisnummertoevoeging",
        person -> person.getVerblijfplaats().getHuisnummertoevoeging());
    add(testInfo, "verblijfplaats.postcode",
        person -> person.getVerblijfplaats().getPostcode());
    add(testInfo, "verblijfplaats.woonplaats",
        person -> person.getVerblijfplaats().getWoonplaats());
  }

  private boolean isNotNull(TestPersonResult testInfo, Function<IngeschrevenPersoonHal, Object> function) {
    Object procuraValue = function.apply(testInfo.getProcuraPerson());
    Object vngValue = function.apply(testInfo.getVngPerson());
    return procuraValue != null && vngValue != null;
  }

  private boolean add(TestPersonResult testInfo, String value, Function<IngeschrevenPersoonHal, Object> function) {
    Object procuraValue = function.apply(testInfo.getProcuraPerson());
    Object vngValue = function.apply(testInfo.getVngPerson());
    ComparedData data = new ComparedData(value, procuraValue, vngValue);
    testInfo.getDataList().add(data);
    return data.isEquals();
  }

  private IngeschrevenPersoonHal getVngIngeschrevenPersoon(long bsn) throws IOException {
    InputStream resource = Testdata.class.getClassLoader().getResourceAsStream("vng-api-data.zip");
    assert resource != null;
    ZipInputStream zipInputStream = new ZipInputStream(resource, StandardCharsets.UTF_8);
    ZipEntry nextEntry;
    while ((nextEntry = zipInputStream.getNextEntry()) != null) {
      boolean isBsn = nextEntry.getName().equals(new Bsn(bsn) + ".json");
      if (isBsn) {
        return objectMapper.readValue(zipInputStream, IngeschrevenPersoonHal.class);
      }
    }

    throw new IllegalStateException("Personal data of " + bsn + " has not been found");
  }

  private void writeResults(List<TestPersonResult> results, boolean writeAllValues) throws IOException {
    File resultsFolder = new File("target/comparison-data");
    FileUtils.deleteDirectory(resultsFolder);
    for (TestPersonResult result : results) {
      File folder;
      if (result.isSkipped()) {
        folder = new File(resultsFolder, "skipped");
      } else if (result.getException() != null) {
        folder = new File(resultsFolder, "exception");
      } else if (result.matchesAll()) {
        folder = new File(resultsFolder, "matches");
      } else {
        folder = new File(resultsFolder, "different");
      }
      if (folder.mkdirs()) {
        log.debug(folder + " created!");
      }
      FileUtils.writeByteArrayToFile(new File(folder, result.getBsn() + ".json"),
          objectMapper.writerWithDefaultPrettyPrinter()
              .writeValueAsString(getFilteredResult(result, writeAllValues))
              .getBytes());
    }
  }

  private TestPersonResult getFilteredResult(TestPersonResult result, boolean writeAllValues) {
    return new TestPersonResult()
        .setBsn(result.getBsn())
        .setException(result.getException())
        .setDifferentValues(result.getDifferentValues())
        .setDataList(result.getDataList().stream()
            .filter(data -> writeAllValues || !data.isEquals)
            .collect(Collectors.toList()));
  }

  @Data
  @Accessors(chain = true)
  public static class TestPersonResult {

    private String             bsn;
    private boolean            skipped;
    private String             exception;
    private List<String>       differentValues = new ArrayList<>();
    private List<ComparedData> dataList        = new ArrayList<>();

    private IngeschrevenPersoonHal procuraPerson;
    private IngeschrevenPersoonHal vngPerson;

    public boolean matchesAll() {
      return dataList.stream().filter(data -> !data.isEquals()).findFirst().isEmpty();
    }
  }

  @Getter
  @Accessors(chain = true)
  public static class ComparedData {

    private final String  name;
    private final Object  procura;
    private final Object  vng;
    private final boolean isEquals;

    public ComparedData(String name, Object procura, Object vng) {
      this.name = name;
      this.procura = procura == null ? "null" : procura;
      this.vng = vng == null ? "null" : vng;
      if (procura instanceof String && vng instanceof String) {
        this.isEquals = Objects.equals(((String) procura).trim(), ((String) vng).trim());
      } else {
        this.isEquals = Objects.equals(procura, vng);
      }
    }
  }
}
