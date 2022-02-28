package com.gsclimbing.historic;

import java.util.List;
import com.gsclimbing.database.entity.Alteration;
import com.gsclimbing.database.entity.HistoricReport;

public class Historic implements Comparable<Historic> {

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
	public int compareTo(Historic o) {
		return this.getHistoricRecord().getLocalDateTime().compareTo(o.getHistoricRecord().getLocalDateTime());
	}

}
