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
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.Alteration;
import com.gsclimbing.database.entity.Medidas6Kv;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.HistoricReport;
import com.gsclimbing.database.entity.Medidas6Kv;
import com.gsclimbing.database.entity.Medidas6Kv;
import com.gsclimbing.database.repository.Medidas6KvRepository;
import com.gsclimbing.database.repository.Medidas6KvRepository;
import com.gsclimbing.database.repository.Medidas6KvRepository;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.ftp.FTPDownloadFiles;

@Service
public class Medidas6KvService {

	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private FileService fileService;

	@Autowired
	private Medidas6KvService medidas6KvService;

	@Autowired
	private HistoricReportService historicReportService;

	@Autowired
	private AlterationService alterationService;

	@Autowired
	private UserRepository userService;

	@Autowired
	private Medidas6KvRepository medidas6KvRepository;

	public Medidas6Kv createMedidas6Kv(Medidas6Kv medidas6Kv) {
		return medidas6KvRepository.save(medidas6Kv);
	}

	public Medidas6Kv readMedidas6Kv(Integer id) {
		return medidas6KvRepository.findById(id).orElse(null);
	}

	public List<Medidas6Kv> readMedidas6KvByTurbineId(String turbineId) {
		return medidas6KvRepository.findByTurbineId(turbineId);
	}

	public List<Medidas6Kv> readAllMedidas6Kv() {
		List<Medidas6Kv> reports = new ArrayList<Medidas6Kv>();
		medidas6KvRepository.findAll().forEach(reports::add);
		return reports;
	}

	public Medidas6Kv updateMedidas6Kv(Medidas6Kv medidas6Kv) {
		return medidas6KvRepository.save(medidas6Kv);
	}

	public List<Medidas6Kv> searchMedidas6Kv(String site, String wtgNumber, String wtgType, String yearConstruction) {
		return medidas6KvRepository.findBySiteAndWtgNumberAndWtgTypeAndYearConstruction(site, wtgNumber, wtgType, yearConstruction);
	}

	public void deleteMedidas6Kv(Integer id) {
		deleteHistoricAndFileData(id);
		medidas6KvRepository.deleteById(id);
	}

	public void deleteMedidas6KvByTurbineId(String turbineId) {
		List<Medidas6Kv> lista = medidas6KvRepository.findByTurbineId(turbineId);
		lista.stream().forEach(report -> deleteMedidas6Kv(report.getReportId()));
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
		Medidas6Kv medidas6Kv = medidas6KvService.readMedidas6Kv(id);
		if (medidas6Kv != null) {
			String uuid = medidas6Kv.getUuid();
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

	public List<String> chkIfAllFieldsNull(Medidas6Kv medidas6Kv) {
		List<String> lista = new ArrayList<String>();
		try {
			for (Field field : medidas6Kv.getClass().getSuperclass().getDeclaredFields()) {
				field.setAccessible(true);
				Object object = field.get(medidas6Kv);
				if (object == null || ObjectUtils.isEmpty(object.toString())) {
					lista.add(field.getName());
				}
			}
			for (Field field : medidas6Kv.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object object = field.get(medidas6Kv);
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
