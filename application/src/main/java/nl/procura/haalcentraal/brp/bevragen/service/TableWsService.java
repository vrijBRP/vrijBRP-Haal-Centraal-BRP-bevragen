package nl.procura.haalcentraal.brp.bevragen.service;

import nl.procura.gbaws.web.rest.v2.tables.GbaWsRestTable;
import nl.procura.gbaws.web.rest.v2.tables.GbaWsRestTableRecord;
import nl.procura.gbaws.web.rest.v2.tables.GbaWsRestTablesRequest;

import java.util.List;

public interface TableWsService {

  GbaWsRestTable getTable(GbaWsRestTablesRequest request);

  List<GbaWsRestTable> getTables(GbaWsRestTablesRequest request);

  GbaWsRestTableRecord getWaarde(GbaWsRestTablesRequest request);

  List<GbaWsRestTableRecord> getWaarden(GbaWsRestTablesRequest request);

}
