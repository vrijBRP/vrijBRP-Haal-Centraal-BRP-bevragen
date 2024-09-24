package nl.procura.haalcentraal.brp.bevragen.model.info;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(name = "Info")
public class ApiInfo {

  private String version;
  private String buildTime;
}
