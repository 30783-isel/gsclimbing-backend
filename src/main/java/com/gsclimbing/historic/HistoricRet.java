package com.gsclimbing.historic;

import java.util.List;

public class HistoricRet implements Comparable<HistoricRet>{

	HistoricReport historicRecord = null;
	
	List<Alteration> listAlterations = null;

	public HistoricReport getHistoricRecord() {
		return historicRecord;
	}

	public void setHistoricRecord(HistoricReport historicRecord) {
		this.historicRecord = historicRecord;
	}

	public List<Alteration> getListAlterations() {
		return listAlterations;
	}

	public void setListAlterations(List<Alteration> listAlterations) {
		this.listAlterations = listAlterations;
	}

	@Override
	public int compareTo(HistoricRet o) {
		return this.getHistoricRecord().getLocalDateTime().compareTo( o.getHistoricRecord().getLocalDateTime() );
	}
	
	
	
}
