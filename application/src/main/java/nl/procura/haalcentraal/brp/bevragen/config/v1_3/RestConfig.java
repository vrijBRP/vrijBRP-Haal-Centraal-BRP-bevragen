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

package nl.procura.haalcentraal.brp.bevragen.config.v1_3;

import static java.util.stream.Collectors.toMap;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import nl.procura.haalcentraal.brp.bevragen.resources.bipV1_3.IngeschrevenPersonenResourceV1_3;
import nl.procura.haalcentraal.brp.bevragen.resources.lt.LandelijkeTabellenResourceV1;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
@EnableAutoConfiguration
public class RestConfig {

  private static final String  OAUTH_SECURITY_SCHEME      = "oAuth2ClientCredentials";
  private static final String  BASIC_AUTH_SECURITY_SCHEME = "basicAuth";
  private final ServletContext servletContext;

  @Value("${openapi.server.url:}")
  private String openApiServerUrl;

  @Value("${develop.rest.show.null:false}")
  private boolean developRestShowNull;

  public RestConfig(ServletContext servletContext) {
    this.servletContext = servletContext;
  }

  @Bean
  public ObjectMapper customObjectMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new Jdk8Module()); // (de)serialization of Optional
    mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    mapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);

    if (!developRestShowNull) {
      // Useful during dev to show null values. Default is false
      mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    JavaTimeModule module = new JavaTimeModule();
    module.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_DATE));
    module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_DATE_TIME));
    module.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_DATE));
    module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_DATE_TIME));
    mapper.registerModule(module);

    mapper.setVisibility(mapper.getSerializationConfig()
        .getDefaultVisibilityChecker()
        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
        .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

    return mapper;
  }

  @Bean
  public GroupedOpenApi openApiGroupLt() {
    return GroupedOpenApi.builder()
        .setGroup("landelijke-tabellen-1.0")
        .packagesToScan(LandelijkeTabellenResourceV1.class.getPackageName())
        .addOpenApiCustomiser(openApi -> openApi
            .paths(sortPaths(openApi.getPaths()))
            .setInfo(new Info().title("Haal Centraal Landelijke tabellen")
                .version("1.0.0")
                .title("Haal Centraal Landelijke tabellen")
                .description("API voor het ontsluiten van landelijke tabellen die op de website van BZK " +
                    "worden gepubliceerd.")
                .contact(new Contact().name("Procura BV")
                    .email("burgerzaken@procura.nl")
                    .url("https://www.procura.nl"))))
        .build();
  }

  @Bean
  public GroupedOpenApi openApiGroupBipV1_3() {
    return GroupedOpenApi.builder()
        .setGroup("bevraging-ingeschreven-personen-1.3")
        .packagesToScan(IngeschrevenPersonenResourceV1_3.class.getPackageName())
        .addOpenApiCustomiser(openApi -> openApi
            .paths(sortPaths(openApi.getPaths()))
            .setInfo(new Info().title("Haal Centraal BRP bevragen")
                .version("1.3.0")
                .title("Bevragingen ingeschreven personen")
                .description("API voor het ontsluiten van gegevens van ingeschreven personen en aanverwante gegevens " +
                    "uit de GBA en RNI. Met deze API worden de actuele gegevens van ingeschreven personen, " +
                    "hun kinderen, partners en ouders ontsloten. Ook de gegevens over reisdocumenten worden via " +
                    "deze API ontsloten. Zie de [Functionele documentatie]" +
                    "(https://github.com/VNG-Realisatie/Bevragingen-ingeschreven-personen/tree/master/features) " +
                    "voor nadere toelichting.")
                .contact(new Contact().name("Procura BV")
                    .email("burgerzaken@procura.nl")
                    .url("https://www.procura.nl"))))
        .build();
  }

  @Bean
  public OpenAPI customOpenAPI() {
    OpenAPI info = new OpenAPI()
        .openapi("3.0.0")
        .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH_SECURITY_SCHEME))
        .components(new Components()
            .addSecuritySchemes(BASIC_AUTH_SECURITY_SCHEME,
                new SecurityScheme().type(SecurityScheme.Type.HTTP)
                    .description("This API uses basic authentication.")
                    .scheme("basic")));

    if (StringUtils.isNotBlank(openApiServerUrl)) {
      info.addServersItem(new Server().url(openApiServerUrl));
    }

    return info;
  }

  private Paths sortPaths(Paths paths) {
    return paths.entrySet().stream()
        .sorted(Map.Entry.comparingByKey())
        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, Paths::new));
  }
}
