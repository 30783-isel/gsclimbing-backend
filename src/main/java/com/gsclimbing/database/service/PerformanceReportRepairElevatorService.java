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
import com.gsclimbing.database.entity.PerformanceReportRepairElevator;
import com.gsclimbing.database.repository.PerformanceReportRepairElevatorRepository;
import com.gsclimbing.ftp.FTPDownloadFiles;

@Service
public class PerformanceReportRepairElevatorService {

	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private FileService fileService;

	@Autowired
	private PerformanceReportRepairElevatorService performanceReportRepairElevatorService;

	@Autowired
	private PerformanceReportRepairElevatorRepository performanceReportRepairElevatorRepository;

	public PerformanceReportRepairElevator createPerformanceReportRepairElevator(PerformanceReportRepairElevator performanceReportRepairElevator) {
		return performanceReportRepairElevatorRepository.save(performanceReportRepairElevator);
	}

	public PerformanceReportRepairElevator readPerformanceReportRepairElevator(Integer id) {
		return performanceReportRepairElevatorRepository.findById(id).orElse(null);
	}

	public List<PerformanceReportRepairElevator> readPerformanceReportRepairElevatorByTurbineId(String turbineId) {
		return performanceReportRepairElevatorRepository.findByTurbineId(turbineId);
	}

	public List<PerformanceReportRepairElevator> readAllPerformanceReportRepairElevator() {
		List<PerformanceReportRepairElevator> reports = new ArrayList<PerformanceReportRepairElevator>();
		performanceReportRepairElevatorRepository.findAll().forEach(reports::add);
		return reports;
	}

	public PerformanceReportRepairElevator updatePerformanceReportRepairElevator(PerformanceReportRepairElevator performanceReportRepairElevator) {
		return performanceReportRepairElevatorRepository.save(performanceReportRepairElevator);
	}

	public List<PerformanceReportRepairElevator> searchPerformanceReportRepairElevator(String site, String wtgNumber, String wtgType, String yearConstruction) {
		return performanceReportRepairElevatorRepository.findBySiteAndWtgNumberAndWtgTypeAndYearConstruction(site, wtgNumber, wtgType, yearConstruction);
	}

	public void deletePerformanceReportRepairElevator(Integer id) {
		deleteHistoricAndFileData(id);
		performanceReportRepairElevatorRepository.deleteById(id);
	}

	public void deletePerformanceReportRepairElevatorByTurbineId(String turbineId) {
		List<PerformanceReportRepairElevator> lista = performanceReportRepairElevatorRepository.findByTurbineId(turbineId);
		lista.stream().forEach(report -> deletePerformanceReportRepairElevator(report.getReportId()));
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
		PerformanceReportRepairElevator performanceReportRepairElevator = performanceReportRepairElevatorService.readPerformanceReportRepairElevator(id);
		if (performanceReportRepairElevator != null) {
			String uuid = performanceReportRepairElevator.getUuid();
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

	public List<String> chkIfAllFieldsNull(PerformanceReportRepairElevator performanceReportRepairElevator) {
		List<String> lista = new ArrayList<String>();
		try {
			for (Field field : performanceReportRepairElevator.getClass().getSuperclass().getDeclaredFields()) {
				field.setAccessible(true);
				Object object = field.get(performanceReportRepairElevator);
				if (object == null || ObjectUtils.isEmpty(object.toString())) {
					lista.add(field.getName());
				}
			}
			for (Field field : performanceReportRepairElevator.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object object = field.get(performanceReportRepairElevator);
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
