package com.gsclimbing.database.service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.commons.mappings.ReportMap;
import com.gsclimbing.database.entity.Alteration;
import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.HistoricReport;
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.StatutoryInspectionReport;
import com.gsclimbing.database.entity.StatutoryInspectionReportInt;
import com.gsclimbing.database.repository.AlterationRepository;

@Service
public class AlterationService {

	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private AlterationRepository alterationRepository;

	@Autowired
	private HistoricReportService historicReportService;

	public List<Alteration> getListAlterationsByIdHistoricReport(int idHr) {
		return historicReportService.getHistoricReportByIdHistoricReport(idHr).getListAlternation();
	}

	public List<Alteration> saveAlterationReport(Report oldReport, Report newReport, HistoricReport historicReport) {
		List<Alteration> listAlternation = new ArrayList<>();
		try {
			if (newReport instanceof StatutoryInspectionReport) {
				for (Field oldField : oldReport.getClass().getDeclaredFields()) {
					oldField.setAccessible(true);
					for (Field newField : newReport.getClass().getDeclaredFields()) {
						newField.setAccessible(true);
						if (oldField.getName().equals(newField.getName()) && (oldField.get(oldReport) instanceof StatutoryInspectionReportInt)  && (newField.get(newReport) instanceof StatutoryInspectionReportInt)) {
							for (Field oldStatutoryInspectionReportField : oldField.get(oldReport).getClass().getDeclaredFields()) {
								oldStatutoryInspectionReportField.setAccessible(true);
								for (Field newStaturoryInspectionReportField : newField.get(newReport).getClass().getDeclaredFields()) {
									newStaturoryInspectionReportField.setAccessible(true);
									if (oldStatutoryInspectionReportField.getName().equals(newStaturoryInspectionReportField.getName()) && !(oldStatutoryInspectionReportField.get(oldField.get(oldReport)) instanceof StatutoryInspectionReport) && !(newStaturoryInspectionReportField.get(newField.get(newReport)) instanceof StatutoryInspectionReport)) {
										if (oldStatutoryInspectionReportField.get(oldField.get(oldReport)) != null && newStaturoryInspectionReportField.get(newField.get(newReport)) != null && !oldStatutoryInspectionReportField.get(oldField.get(oldReport)).equals(newStaturoryInspectionReportField.get(newField.get(newReport)))) {											
											Alteration alteration = new Alteration();
											alteration.setField(newStaturoryInspectionReportField.getName());
											alteration.setFieldOld(String.valueOf(oldStatutoryInspectionReportField.get(oldField.get(oldReport))));
											alteration.setFieldNew(String.valueOf(newStaturoryInspectionReportField.get(newField.get(newReport))));
											alteration.setImage(false);
											alteration.setHash(null);
											alteration.setImageChange(0);
											alteration.setLocalDateTime(LocalDateTime.now());
											alteration.setHistoricReport(historicReport);
											listAlternation.add(alteration);
											break;
										}
									}
								}
							}
						}
					}
				}
			}

			for (Field oldField : oldReport.getClass().getSuperclass().getDeclaredFields()) {
				oldField.setAccessible(true);
				for (Field newField : newReport.getClass().getSuperclass().getDeclaredFields()) {
					newField.setAccessible(true);
					if (oldField.getName().equals(newField.getName()) && newField.getName() != "modifiedDate") {
						System.out.println(oldField.getName());
						System.out.println(newField.getName());
						if (oldField.get(oldReport) != null && newField.get(newReport) != null && !oldField.get(oldReport).equals(newField.get(newReport)) && !"locked".equals(newField.getName())) {
							Alteration alteration = new Alteration();
							alteration.setField( ReportMap.mapeamento().get(newField.getName()) );
							alteration.setFieldOld(String.valueOf(oldField.get(oldReport)));
							alteration.setFieldNew(String.valueOf(newField.get(newReport)));
							alteration.setImage(false);
							alteration.setHash(null);
							alteration.setImageChange(0);
							alteration.setLocalDateTime(LocalDateTime.now());
							alteration.setHistoricReport(historicReport);
							listAlternation.add(alteration);
							break;
						}
					}
				}
			}
			for (Field oldField : oldReport.getClass().getDeclaredFields()) {
				oldField.setAccessible(true);
				for (Field newField : newReport.getClass().getDeclaredFields()) {
					newField.setAccessible(true);
					if (oldField.getName().equals(newField.getName())) {
						System.out.println(oldField.getName());
						System.out.println(newField.getName());
						if (oldField.get(oldReport) != null && newField.get(newReport) != null && !oldField.get(oldReport).equals(newField.get(newReport)) && !"locked".equals(newField.getName()) && !(oldField.get(oldReport) instanceof StatutoryInspectionReportInt)) {
							Alteration alteration = new Alteration();
							alteration.setField(newField.getName());
							alteration.setFieldOld(String.valueOf(oldField.get(oldReport)));
							alteration.setFieldNew(String.valueOf(newField.get(newReport)));
							alteration.setImage(false);
							alteration.setHash(null);
							alteration.setImageChange(0);
							alteration.setLocalDateTime(LocalDateTime.now());
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
