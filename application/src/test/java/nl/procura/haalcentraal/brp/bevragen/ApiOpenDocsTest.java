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

package nl.procura.haalcentraal.brp.bevragen;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
public class ApiOpenDocsTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  public void apiDocsCanBeDownloaded() throws Exception {
    saveToFile("bip-api-docs-v1_3_0.json", "bevraging-ingeschreven-personen-1.3");
    saveToFile("lt-api-docs-v1_0_0.json", "landelijke-tabellen-1.0");
  }

  private void saveToFile(String outputFile, String uri) throws Exception {
    File file = new File("target/" + outputFile);
    FileUtils.writeByteArrayToFile(file,
        mockMvc.perform(get("/public/v3/api-docs/" + uri))
            .andReturn()
            .getResponse()
            .getContentAsString(UTF_8).getBytes());

    log.info(String.format(uri + " written to %s.", file.getAbsolutePath()));
  }

  @Test
  void operationIdMustNotContainUnderscore() throws Exception {
    mockMvc.perform(get("/public/v3/api-docs"))
        .andExpect(jsonPath("$..operationId", hasSize(greaterThan(0))))
        .andExpect(jsonPath("$..operationId", not(hasItem(containsString("_")))));
  }
}
