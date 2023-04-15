package com.gsclimbing.database.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.OnboardCraneInspectionReport;
import com.gsclimbing.database.repository.OnboardCraneInspectionReportRepository;
import com.gsclimbing.ftp.FTPDownloadFiles;

@Service
public class OnboardCraneInspectionReportService {

	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private FileService fileService;

	@Autowired
	private OnboardCraneInspectionReportService onboardCraneInspectionReportService;

	@Autowired
	private OnboardCraneInspectionReportRepository onboardCraneInspectionReportRepository;

	public OnboardCraneInspectionReport createOnboardCraneInspectionReport(OnboardCraneInspectionReport onboardCraneInspectionReport) {
		return onboardCraneInspectionReportRepository.save(onboardCraneInspectionReport);
	}

	public OnboardCraneInspectionReport readOnboardCraneInspectionReport(Integer id) {
		return onboardCraneInspectionReportRepository.findById(id).orElse(null);
	}

	public List<OnboardCraneInspectionReport> readOnboardCraneInspectionReportByTurbineId(String turbineId) {
		return onboardCraneInspectionReportRepository.findByTurbineId(turbineId);
	}

	public List<OnboardCraneInspectionReport> readAllOnboardCraneInspectionReport() {
		List<OnboardCraneInspectionReport> reports = new ArrayList<OnboardCraneInspectionReport>();
		onboardCraneInspectionReportRepository.findAll().forEach(reports::add);
		return reports;
	}

	public OnboardCraneInspectionReport updateOnboardCraneInspectionReport(OnboardCraneInspectionReport onboardCraneInspectionReport) {
		return onboardCraneInspectionReportRepository.save(onboardCraneInspectionReport);
	}

	public List<OnboardCraneInspectionReport> searchOnboardCraneInspectionReport(String site, String wtgNumber, String wtgType, String yearConstruction) {
		return onboardCraneInspectionReportRepository.findBySiteAndWtgNumberAndWtgTypeAndYearConstruction(site, wtgNumber, wtgType, yearConstruction);
	}

	public void deleteOnboardCraneInspectionReport(Integer id) {
		deleteHistoricAndFileData(id);
		onboardCraneInspectionReportRepository.deleteById(id);
	}

	public void deleteOnboardCraneInspectionReportByTurbineId(String turbineId) {
		List<OnboardCraneInspectionReport> lista = onboardCraneInspectionReportRepository.findByTurbineId(turbineId);
		lista.stream().forEach(report -> deleteOnboardCraneInspectionReport(report.getReportId()));
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
		OnboardCraneInspectionReport onboardCraneInspectionReport = onboardCraneInspectionReportService.readOnboardCraneInspectionReport(id);
		if (onboardCraneInspectionReport != null) {
			String uuid = onboardCraneInspectionReport.getUuid();
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

	public List<String> chkIfAllFieldsNull(OnboardCraneInspectionReport onboardCraneInspectionReport) {
		List<String> lista = new ArrayList<String>();
		try {
			for (Field field : onboardCraneInspectionReport.getClass().getSuperclass().getDeclaredFields()) {
				field.setAccessible(true);
				Object object = field.get(onboardCraneInspectionReport);
				if (object == null || ObjectUtils.isEmpty(object.toString())) {
					lista.add(field.getName());
				}
			}
			for (Field field : onboardCraneInspectionReport.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object object = field.get(onboardCraneInspectionReport);
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
