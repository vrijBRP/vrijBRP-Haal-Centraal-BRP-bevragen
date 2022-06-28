package nl.procura.haalcentraal.brp.bevragen.converter.v1_3.enums;

import lombok.Getter;
import nl.vng.realisatie.haalcentraal.rest.generated.model.bipv1_3.AanduidingBijHuisnummerEnum;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Getter
public enum AanduidingBijHuisnummer {

  TO("TO", AanduidingBijHuisnummerEnum.TEGENOVER, "tegenover"),
  BY("BY", AanduidingBijHuisnummerEnum.BIJ, "bij");

  private final String                      code;
  private final AanduidingBijHuisnummerEnum type;
  private final String usageWithinAddress;

  AanduidingBijHuisnummer(String code, AanduidingBijHuisnummerEnum type, String usageWithinAddress) {
    this.code = code;
    this.type = type;
    this.usageWithinAddress = usageWithinAddress;
  }

  public static AanduidingBijHuisnummerEnum fromCode(final String code) {
    AanduidingBijHuisnummerEnum result = null;
    if (StringUtils.hasText(code)) {
      result = Arrays.stream(values())
          .filter(i -> i.getCode().equalsIgnoreCase(code))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Onbekende AanduidingBijHuisnummer code : " + code))
          .getType();
    }
    return result;
  }

}
