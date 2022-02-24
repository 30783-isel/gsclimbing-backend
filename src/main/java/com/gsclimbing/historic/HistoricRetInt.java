package com.gsclimbing.historic;

import java.util.List;

public interface HistoricRetInt {

	//HistoricRet getHistoric(List<HistoricReport> listHistoricRecord, List<List<Alteration>> listAlterations);
	
	List<HistoricReport> getListHistoric(List<HistoricReport> listHistoricRecord);
	
	List<List<Alteration>> getListAlterations(List<List<Alteration>> listAlterations);
}
