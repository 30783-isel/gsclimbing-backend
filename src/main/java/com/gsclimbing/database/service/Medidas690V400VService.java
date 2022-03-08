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

import com.gsclimbing.database.entity.Alteration;
import com.gsclimbing.database.entity.Medidas690V400V;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.entity.HistoricReport;
import com.gsclimbing.database.entity.Medidas690V400V;
import com.gsclimbing.database.repository.Medidas690V400VRepository;
import com.gsclimbing.database.repository.Medidas690V400VRepository;
import com.gsclimbing.database.repository.UserRepository;
import com.gsclimbing.ftp.FTPDownloadFiles;

@Service
public class Medidas690V400VService {

	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private FileService fileService;

	@Autowired
	private Medidas690V400VService medidas690V400VService;

	@Autowired
	private HistoricReportService historicReportService;

	@Autowired
	private AlterationService alterationService;

	@Autowired
	private UserRepository userService;

	@Autowired
	private Medidas690V400VRepository medidas690V400VRepository;

	public Medidas690V400V createMedidas690V400V(Medidas690V400V medidas690V400V) {
		return medidas690V400VRepository.save(medidas690V400V);
	}

	public Medidas690V400V readMedidas690V400V(Integer id) {
		return medidas690V400VRepository.findById(id).orElse(null);
	}

	public List<Medidas690V400V> readMedidas690V400VByTurbineId(String turbineId) {
		return medidas690V400VRepository.findByTurbineId(turbineId);
	}

	public List<Medidas690V400V> readAllMedidas690V400V() {
		List<Medidas690V400V> reports = new ArrayList<Medidas690V400V>();
		medidas690V400VRepository.findAll().forEach(reports::add);
		return reports;
	}

	public Medidas690V400V updateMedidas690V400V(Medidas690V400V medidas690V400V) {
		return medidas690V400VRepository.save(medidas690V400V);
	}

	public List<Medidas690V400V> searchMedidas690V400V(String site, String wtgNumber, String wtgType, String yearConstruction) {
		return medidas690V400VRepository.findBySiteAndWtgNumberAndWtgType(site, wtgNumber, wtgType, yearConstruction);
	}

	public void deleteMedidas690V400V(Integer id) {
		deleteHistoricAndFileData(id);
		medidas690V400VRepository.deleteById(id);
	}

	public void deleteMedidas690V400VByTurbineId(String turbineId) {
		List<Medidas690V400V> lista = medidas690V400VRepository.findByTurbineId(turbineId);
		lista.stream().forEach(report -> deleteMedidas690V400V(report.getReportId()));
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
		Medidas690V400V medidas690V400V = medidas690V400VService.readMedidas690V400V(id);
		if (medidas690V400V != null) {
			String uuid = medidas690V400V.getUuid();
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

	public List<String> chkIfAllFieldsNull(Medidas690V400V medidas690V400V) {
		List<String> lista = new ArrayList<String>();
		try {
			for (Field field : medidas690V400V.getClass().getSuperclass().getDeclaredFields()) {
				field.setAccessible(true);
				Object object = field.get(medidas690V400V);
				if (object == null || ObjectUtils.isEmpty(object.toString())) {
					lista.add(field.getName());
				}
			}
			for (Field field : medidas690V400V.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object object = field.get(medidas690V400V);
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
