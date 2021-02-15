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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import nl.procura.haalcentraal.brp.bevragen.model.exception.ExpandException;
import nl.procura.haalcentraal.brp.bevragen.model.exception.FieldsException;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseConverterImpl<T, S> implements BaseConverter<T, S> {

  protected static final Handler<?, ?> noop = (target, source, fields, expand) -> {};

  @FunctionalInterface
  interface Handler<T, S> {

    void handle(T target, S source, Set<String> fields, Set<String> expand);
  }

  @FunctionalInterface
  interface ValueHandler<T, S> {

    void handle(T target, S source);
  }

  @Getter
  private final Map<String, Handler<T, S>> map = new HashMap<>();

  public void put(String field, Handler<T, S> handler) {
    getMap().put(field, handler);
  }

  public void put(String field, ValueHandler<T, S> handler) {
    getMap().put(field, (target, source, fields, expand) -> handler.handle(target, source));
  }

  @Override
  public T convert(final S source, T result, final Set<String> fields, final Set<String> expand) {
    log.debug("fields3=" + fields + ", expand=" + expand);
    validate(fields);
    (fields == null || fields.isEmpty() ? getMap().keySet() : fields).stream() //
        .map(s -> {
          if (s.contains(".")) {
            return s.substring(0, s.indexOf("."));
          } else {
            return s;
          }
        }).distinct()
        .forEach(e -> getMap().get(e)
            .handle(result, source, PersoonConverterImpl.leaf(fields, e), expand));

    return postConvert(source, result, fields);

  }

  @Override
  public void validate(final Set<String> fields) {
    if (fields != null) {
      fields.stream()
          .filter(field -> !getMap().containsKey(field) &&
              getMap().keySet().stream().noneMatch(i -> field.startsWith(i + ".")))
          //          .peek(c -> log.debug("checking: " + c + ", keys=" + getMap().keySet()))
          .findAny().ifPresent(entry -> {
            if (this instanceof PersoonConverterImpl) {
              throw new FieldsException(entry);
            } else {
              throw new ExpandException(entry);
            }
          });
    }
  }

  public T postConvert(final S source, T result, final Set<String> fields) {
    return result;
  }

}
