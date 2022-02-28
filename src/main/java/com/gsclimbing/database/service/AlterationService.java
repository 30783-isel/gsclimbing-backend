package com.gsclimbing.database.service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.Alteration;
import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.HistoricReport;
import com.gsclimbing.database.repository.AlterationRepository;

@Service
public class AlterationService {

	Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private AlterationRepository alterationRepository;
	
	@Autowired
	private HistoricReportService historicReportService;
	
	public List<Alteration> getListAlterationsByIdHistoricReport(int idHr){	
		return historicReportService.getHistoricReportByIdHistoricReport(idHr).getListAlternation();
	}
	
	public List<Alteration> saveAlterationDefectsInspectionReport(DefectsInspectionReport oldDefectsInspectionReport, DefectsInspectionReport defectsInspectionReport, HistoricReport historicReport){

		List<Alteration> listAlternation = new ArrayList<>();
		try {
			for (Field oldField : oldDefectsInspectionReport.getClass().getDeclaredFields()) {
				oldField.setAccessible(true);
				
				for (Field newField : defectsInspectionReport.getClass().getDeclaredFields()) {
					newField.setAccessible(true);
					
					if (oldField.getName().equals(newField.getName()) && newField.getName() != "modifiedDate") {
						log.info(oldField.getName());
						if ( oldField.get(oldDefectsInspectionReport) != null && newField.get(defectsInspectionReport) != null  && !oldField.get(oldDefectsInspectionReport).equals( newField.get(defectsInspectionReport)) &&  !"locked".equals(newField.getName()) ) {
							Alteration alteration = new Alteration();
							alteration.setField(newField.getName());
							alteration.setFieldOld(String.valueOf(oldField.get(oldDefectsInspectionReport)));
							alteration.setFieldNew(String.valueOf(newField.get(defectsInspectionReport)));
							alteration.setImage(false);
							alteration.setHash(null);
							alteration.setImageChange(0);
							alteration.setLocalDateTime(LocalDateTime.now());
							alteration.setOldPicByte(null);
							alteration.setNewPicByte(null);
							alteration.setHistoricReport(historicReport);
							listAlternation.add(alteration);
							break;
						}
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}	
		return listAlternation;
	}
	
	public void deleteAlteration(int id) {
		alterationRepository.deleteById(id);
	}	
}
