package nl.procura.haalcentraal.brp.bevragen.service;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.List;

import org.springframework.stereotype.Service;

import nl.procura.brp.bevragen.client.GbaWsClient;
import nl.procura.gbaws.web.rest.v2.tables.GbaWsRestTable;
import nl.procura.gbaws.web.rest.v2.tables.GbaWsRestTableRecord;
import nl.procura.gbaws.web.rest.v2.tables.GbaWsRestTablesRequest;
import nl.procura.haalcentraal.brp.bevragen.model.exception.NotFoundException;
import nl.procura.haalcentraal.brp.bevragen.model.exception.ReturnException;

@Service
public class ProcuraTableWsService implements TableWsService {

  private final GbaWsClient client;

  public ProcuraTableWsService(GbaWsClient client) {
    this.client = client;
  }

  @Override
  public GbaWsRestTable getTable(GbaWsRestTablesRequest request) {
    var gbaWsRestTableList = getTables(request);

    if (gbaWsRestTableList.isEmpty()) {
      throw new NotFoundException();

    } else if (gbaWsRestTableList.size() > 1) {
      throw new ReturnException(BAD_REQUEST.value())
          .setTitle("Meerdere tabellen gevonden");
    }

    return gbaWsRestTableList.get(0);
  }

  @Override
  public List<GbaWsRestTable> getTables(GbaWsRestTablesRequest request) {
    List<GbaWsRestTable> tables = client.getTables(request).getTables();
    if (tables.isEmpty()) {
      throw new NotFoundException();
    }
    return tables;
  }

  @Override
  public GbaWsRestTableRecord getWaarde(GbaWsRestTablesRequest request) {
    var records = getWaarden(request);
    if (records.isEmpty()) {
      throw new NotFoundException();
    }
    if (records.size() > 1) {
      throw new ReturnException(BAD_REQUEST.value())
          .setTitle("Meerdere records (" + records.size() + ") gevonden");
    }

    return records.get(0);
  }

  @Override
  public List<GbaWsRestTableRecord> getWaarden(GbaWsRestTablesRequest request) {
    List<GbaWsRestTableRecord> records = getTable(request).getRecords();
    if (records.isEmpty()) {
      throw new NotFoundException();
    }
    return records;
  }
}
