package com.gsclimbing.database.service;

import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.MeasurementsMwSwitchgear;
import com.gsclimbing.database.repository.MeasurementsMwSwitchgearRepository;
import com.gsclimbing.ftp.FTPDownloadFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MeasurementsMwSwitchgearService {

	@Autowired
	private FileService fileService;

	@Autowired
	private MeasurementsMwSwitchgearService measurementsMwSwitchgearService;


	@Autowired
	private MeasurementsMwSwitchgearRepository measurementsMwSwitchgearRepository;

	public MeasurementsMwSwitchgear createMeasurementsMwSwitchgear(MeasurementsMwSwitchgear measurementsMwSwitchgear) {
		return measurementsMwSwitchgearRepository.save(measurementsMwSwitchgear);
	}

	public MeasurementsMwSwitchgear readMeasurementsMwSwitchgear(Integer id) {
		return measurementsMwSwitchgearRepository.findById(id).orElse(null);
	}

	public List<MeasurementsMwSwitchgear> readAllMeasurementsMwSwitchgear() {
		List<MeasurementsMwSwitchgear> reports = new ArrayList<MeasurementsMwSwitchgear>();
		measurementsMwSwitchgearRepository.findAll().forEach(reports::add);
		return reports;
	}

	public List<MeasurementsMwSwitchgear> readMeasurementsMwSwitchgearByTurbineId(String turbineId) {
		return measurementsMwSwitchgearRepository.findByTurbineId(turbineId);
	}

	public MeasurementsMwSwitchgear updateMeasurementsMwSwitchgear(MeasurementsMwSwitchgear measurementsMwSwitchgear) {
		return measurementsMwSwitchgearRepository.save(measurementsMwSwitchgear);
	}

	public List<MeasurementsMwSwitchgear> searchMeasurementsMwSwitchgear(String site, String wtgNumber, String wtgType, String yearConstruction) {
		return measurementsMwSwitchgearRepository.findBySiteAndWtgNumberWtgTypeAndYearConstruction(site, wtgNumber, wtgType, yearConstruction);
	}

	public void deleteMeasurementsMwSwitchgear(Integer id) {
		deleteHistoricAndFileData(id);
		measurementsMwSwitchgearRepository.deleteById(id);
	}

	public void deleteMeasurementsMwSwitchgearByTurbineId(String turbineId) {
		List<MeasurementsMwSwitchgear> lista = measurementsMwSwitchgearRepository.findByTurbineId(turbineId);
		lista.stream().forEach(report -> deleteMeasurementsMwSwitchgear(report.getReportId()));
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

	private void deleteHistoricAndFileData(int id) {
		MeasurementsMwSwitchgear measurementsMwSwitchgear = measurementsMwSwitchgearService.readMeasurementsMwSwitchgear(id);
		if (measurementsMwSwitchgear != null) {
			String uuid = measurementsMwSwitchgear.getUuid();
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

}
