package com.gsclimbing.database.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.StatutoryInspectionReport;
import com.gsclimbing.database.repository.StatutoryInspectionReportRepository;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.ftp.FTPDownloadFiles;

@Service
public class StatutoryInspectionReportReportService {

	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private FileService fileService;

	@Autowired
	private StatutoryInspectionReportReportService statutoryInspectionReportService;

	@Autowired
	private HistoricReportService historicReportService;

	@Autowired
	private AlterationService alterationService;

	@Autowired
	private UserRepository userService;

	@Autowired
	private StatutoryInspectionReportRepository statutoryInspectionReportRepository;

	public StatutoryInspectionReport createStatutoryInspectionReport(StatutoryInspectionReport statutoryInspectionReport) {
		return statutoryInspectionReportRepository.save(statutoryInspectionReport);
	}

	public StatutoryInspectionReport readStatutoryInspectionReport(Integer id) {
		return statutoryInspectionReportRepository.findById(id).orElse(null);
	}

	public List<StatutoryInspectionReport> readStatutoryInspectionReportByTurbineId(String turbineId) {
		return statutoryInspectionReportRepository.findByTurbineId(turbineId);
	}

	public List<StatutoryInspectionReport> readAllStatutoryInspectionReport() {
		List<StatutoryInspectionReport> reports = new ArrayList<StatutoryInspectionReport>();
		statutoryInspectionReportRepository.findAll().forEach(reports::add);
		return reports;
	}

	public StatutoryInspectionReport updateStatutoryInspectionReport(StatutoryInspectionReport statutoryInspectionReport) {
		return statutoryInspectionReportRepository.save(statutoryInspectionReport);
	}

	public List<StatutoryInspectionReport> searchStatutoryInspectionReport(String site, String wtgNumber, String wtgType, String yearConstruction) {
		return statutoryInspectionReportRepository.findBySiteAndWtgNumberAndWtgTypeAndYearConstruction(site, wtgNumber, wtgType, yearConstruction);
	}

	public void deleteStatutoryInspectionReport(Integer id) {
		deleteHistoricAndFileData(id);
		statutoryInspectionReportRepository.deleteById(id);
	}

	public void deleteStatutoryInspectionReportByTurbineId(String turbineId) {
		List<StatutoryInspectionReport> lista = statutoryInspectionReportRepository.findByTurbineId(turbineId);
		lista.stream().forEach(report -> deleteStatutoryInspectionReport(report.getReportId()));
	}

	public String getCurrentLoggedUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = null;
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else {
			username = principal.toString();
		}
		return username;
	}

	private void deleteHistoricAndFileData(Integer id) {
		StatutoryInspectionReport statutoryInspectionReport = statutoryInspectionReportService.readStatutoryInspectionReport(id);
		if (statutoryInspectionReport != null) {
			String uuid = statutoryInspectionReport.getUuid();
			List<FileData> listFileData = fileService.readFile(uuid);

			listFileData.stream().forEach(fileData -> {
				fileService.deleteFile(fileData.getFileId());
				FTPDownloadFiles.deleteFile2FTPServer(fileData.getHash());
				for (int i = 0; i <= 5; i++) {
					String path = "/oldImages/" + i + "/" + fileData.getHash();
					FTPDownloadFiles.deleteFile2FTPServer(path);
				}

			});
		}
	}

	public List<String> chkIfAllFieldsNull(StatutoryInspectionReport statutoryInspectionReport) {
		List<String> lista = new ArrayList<String>();
		try {
			for (Field field : statutoryInspectionReport.getClass().getSuperclass().getDeclaredFields()) {
				field.setAccessible(true);
				Object object = field.get(statutoryInspectionReport);
				if (object == null || ObjectUtils.isEmpty(object.toString())) {
					lista.add(field.getName());
				}
			}
			for (Field field : statutoryInspectionReport.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object object = field.get(statutoryInspectionReport);
				if (object instanceof ArrayList<?>) {
					if (((ArrayList) object).size() == 0) {
						lista.add(field.getName());
					}
				} else {
					if (object == null || ObjectUtils.isEmpty(object.toString())) {
						lista.add(field.getName());
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return lista;
	}

}
