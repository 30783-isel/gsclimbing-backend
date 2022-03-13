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

	public List<Alteration> saveAlterationReport(Report oldReport, Report report, HistoricReport historicReport) {
		List<Alteration> listAlternation = new ArrayList<>();
		try {
			if (report instanceof StatutoryInspectionReport) {
				for (Field oldField : oldReport.getClass().getDeclaredFields()) {
					oldField.setAccessible(true);
					for (Field newField : report.getClass().getDeclaredFields()) {
						newField.setAccessible(true);
						if (oldField.getName().equals(newField.getName()) && newField.getName() != "modifiedDate") {
							if (oldField.get(oldReport) != null && newField.get(report) != null && !oldField.get(oldReport).equals(newField.get(report)) && !"locked".equals(newField.getName()) && oldField.get(oldReport) instanceof StatutoryInspectionReportInt) {
								System.out.println(oldField.getName());
								System.out.println(newField.getName());
								for (Field oldStatutoryInspectionReportField : oldField.get(oldReport).getClass().getDeclaredFields()) {
									oldStatutoryInspectionReportField.setAccessible(true);
									for (Field newStaturoryInspectionReportField : newField.get(report).getClass().getDeclaredFields()) {
										newStaturoryInspectionReportField.setAccessible(true);
										if (oldStatutoryInspectionReportField.getName().equals(newStaturoryInspectionReportField.getName()) && newStaturoryInspectionReportField.getName() != "modifiedDate") {
											System.out.println(oldStatutoryInspectionReportField.getName());
											System.out.println(newStaturoryInspectionReportField.getName());
											if (oldStatutoryInspectionReportField.get(oldField.get(oldReport)) != null && newStaturoryInspectionReportField.get(newField.get(report)) != null && !oldStatutoryInspectionReportField.get(oldField.get(oldReport)).equals(newStaturoryInspectionReportField.get(newField.get(report))) && !"locked".equals(newStaturoryInspectionReportField.getName()) && oldStatutoryInspectionReportField.get(oldField.get(oldReport)) instanceof StatutoryInspectionReportInt) {
												System.out.println(oldStatutoryInspectionReportField.getName());
												System.out.println(newStaturoryInspectionReportField.getName());
												Alteration alteration = new Alteration();
												alteration.setField(newStaturoryInspectionReportField.getName());
												alteration.setFieldOld(String.valueOf(oldStatutoryInspectionReportField.get(oldField.get(oldReport))));
												alteration.setFieldNew(String.valueOf(newStaturoryInspectionReportField.get(newField.get(report))));
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
			}

			for (Field oldField : oldReport.getClass().getSuperclass().getDeclaredFields()) {
				oldField.setAccessible(true);
				for (Field newField : report.getClass().getSuperclass().getDeclaredFields()) {
					newField.setAccessible(true);
					if (oldField.getName().equals(newField.getName()) && newField.getName() != "modifiedDate") {
						System.out.println(oldField.getName());
						System.out.println(newField.getName());
						if (oldField.get(oldReport) != null && newField.get(report) != null && !oldField.get(oldReport).equals(newField.get(report)) && !"locked".equals(newField.getName())) {
							Alteration alteration = new Alteration();
							alteration.setField(newField.getName());
							alteration.setFieldOld(String.valueOf(oldField.get(oldReport)));
							alteration.setFieldNew(String.valueOf(newField.get(report)));
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
				for (Field newField : report.getClass().getDeclaredFields()) {
					newField.setAccessible(true);
					if (oldField.getName().equals(newField.getName())) {
						System.out.println(oldField.getName());
						System.out.println(newField.getName());
						if (oldField.get(oldReport) != null && newField.get(report) != null && !oldField.get(oldReport).equals(newField.get(report)) && !"locked".equals(newField.getName()) && !(oldField.get(oldReport) instanceof StatutoryInspectionReportInt)) {
							Alteration alteration = new Alteration();
							alteration.setField(newField.getName());
							alteration.setFieldOld(String.valueOf(oldField.get(oldReport)));
							alteration.setFieldNew(String.valueOf(newField.get(report)));
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
