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

import java.util.Set;

import nl.procura.haalcentraal.brp.bevragen.model.exception.ReturnException;

public interface BaseConverter<T, S> {

  default T convert(final S source) throws ReturnException {
    return convert(source, null, null);
  }

  default T convert(final S source, final Set<String> fields, final Set<String> expand) throws ReturnException {
    return convert(source, createTarget(source, fields), fields, expand);
  }

  T convert(final S source, final T target, final Set<String> fields, final Set<String> expand) throws ReturnException;

  T createTarget(final S source, final Set<String> fields);

  void validate(final Set<String> fields);

}
