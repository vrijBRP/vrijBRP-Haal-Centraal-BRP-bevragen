package nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3;

import java.util.*;
import java.util.stream.Collectors;

import org.junit.platform.commons.util.StringUtils;

import nl.procura.gbaws.testdata.Testdata;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public class IngeschrevenPersonenTestParams {

  private Map<Long, Testdata.DataSet> bsns   = new LinkedHashMap<>();
  private Map<String, String>         params = new HashMap<>();
  private List<String>                expand = new ArrayList(Arrays.asList("ouders", "partners", "kinderen"));
  private List<String>                fields = new ArrayList<>();

  public IngeschrevenPersonenTestParams bsn(Long bsn, Testdata.DataSet dataSet) {
    this.bsns.put(bsn, dataSet);
    return this;
  }

  public IngeschrevenPersonenTestParams bsns(Long... bsns) {
    Arrays.asList(bsns).forEach(bsn -> this.bsns.put(bsn, Testdata.DataSet.GBAV));
    return this;
  }

  public IngeschrevenPersonenTestParams fields(String... fields) {
    this.fields = new ArrayList<>(Arrays.asList(fields));
    return this;
  }

  public IngeschrevenPersonenTestParams expand(String... expand) {
    this.expand = new ArrayList<>(Arrays.asList(expand));
    return this;
  }

  public IngeschrevenPersonenTestParams param(String key, String value) {
    params.put(key, value);
    return this;
  }

  public String toQueryString() {
    ArrayList<String> elements = new ArrayList<>();
    if (!expand.stream().allMatch(StringUtils::isBlank)) {
      elements.add("expand=" + String.join(",", expand));
    }
    if (!fields.stream().allMatch(StringUtils::isBlank)) {
      elements.add("fields=" + String.join(",", fields));
    }
    elements.addAll(params.entrySet().stream()
        .map(entry -> entry.getKey() + "=" + entry.getValue())
        .collect(Collectors.toList()));

    return ("?" + elements.stream()
        .filter(StringUtils::isNotBlank)
        .collect(Collectors.joining("&")));
  }
}
