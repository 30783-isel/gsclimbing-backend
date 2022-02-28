package com.gsclimbing.database.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gsclimbing.database.entity.Alteration;
import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.HistoricReport;
import com.gsclimbing.database.repository.DefectsInspectionReportRepository;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.ftp.FTPDownloadFiles;

@Service
public class DefectsInspectionReportService {

	Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private FileService fileService;

	@Autowired
	private DefectsInspectionReportService defectsInspectionReportService;

	@Autowired
	private HistoricReportService historicReportService;

	@Autowired
	private AlterationService alterationService;

	@Autowired
	private UserRepository userService;

	@Autowired
	private DefectsInspectionReportRepository defectsInspectionReportRepository;

	public void createDefectsInspectionReport(DefectsInspectionReport defectsInspectionReport) {
		defectsInspectionReportRepository.save(defectsInspectionReport);
	}

	public DefectsInspectionReport readDefectsInspectionReport(Integer id) {
		return defectsInspectionReportRepository.findById(id).orElse(null);
	}

	public List<DefectsInspectionReport> readDefectsInspectionReportByTurbineId(String turbineId) {
		return defectsInspectionReportRepository.findByTurbineId(turbineId);
	}

	public List<DefectsInspectionReport> readAllDefectsInspectionReport() {
		List<DefectsInspectionReport> reports = new ArrayList<DefectsInspectionReport>();
		defectsInspectionReportRepository.findAll().forEach(reports::add);
		return reports;
	}

	public void updateDefectsInspectionReport(DefectsInspectionReport defectsInspectionReport) {
		defectsInspectionReportRepository.save(defectsInspectionReport);
	}

	public List<DefectsInspectionReport> searchDefectsInspectionReport(String site, String wtgNumber, String wtgType, String yearConstruction) {
		return defectsInspectionReportRepository.findBySiteAndWtgNumberAndWtgTypeAndYearConstruction(site, wtgNumber, wtgType, yearConstruction);
	}

	public void deleteDefectsInspectionReport(Integer id) {
		deleteHistoricAndFileData(id);
		defectsInspectionReportRepository.deleteById(id);
	}

	public void deleteDefectsInspectionReportByTurbineId(String turbineId) {
		List<DefectsInspectionReport> lista = defectsInspectionReportRepository.findByTurbineId(turbineId);
		lista.stream().forEach(report -> deleteDefectsInspectionReport(report.getReportId()));
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
		DefectsInspectionReport defectsInspectionReport = defectsInspectionReportService.readDefectsInspectionReport(id);
		if (defectsInspectionReport != null) {
			String uuid = defectsInspectionReport.getUuid();
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

	public List<String> chkIfAllFieldsNull(DefectsInspectionReport defectsInspectionReport) {
		List<String> lista = new ArrayList<String>();
		try {
			for (Field field : defectsInspectionReport.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object object = field.get(defectsInspectionReport);
				if(object instanceof ArrayList<?>) {
					if(((ArrayList) object).size() == 0) {
						lista.add(field.getName());
					}
				}else {
					if(object == null || StringUtils.isEmpty(object.toString())) {
						System.out.println(field.getName());
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
