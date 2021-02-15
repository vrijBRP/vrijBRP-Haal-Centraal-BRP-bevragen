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

package nl.procura.haalcentraal.brp.bevragen.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String SCOPE_API = "SCOPE_api";

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // TODO implement security

    //    http.authorizeRequests();
    //     .antMatchers("/*", "/public/**", "/oauth/token").permitAll();
    //     .antMatchers("/api/**").hasAuthority(SCOPE_API);
    //     should we disable CSRF on all endpoints?
    http.csrf().disable();
    // manually enables Jwt-encoded bearer token support as OAuth2ResourceServerJwtConfiguration
    // doesn't do it when WebSecurityConfigurerAdapter has been defined
    //http.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
  }

}
