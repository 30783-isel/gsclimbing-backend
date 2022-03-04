package com.gsclimbing.database.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.ExaminationTransformer;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.repository.ExaminationTransformerRepository;
import com.gsclimbing.ftp.FTPDownloadFiles;

@Service
public class ExaminationTransformerService {

	@Autowired
	private FileService fileService;

	@Autowired
	private ExaminationTransformerService examinationTransformerService;


	@Autowired
	private ExaminationTransformerRepository examinationTransformerRepository;

	public ExaminationTransformer createExaminationTransformer(ExaminationTransformer examinationTransformer) {
		return examinationTransformerRepository.save(examinationTransformer);
	}

	public ExaminationTransformer readExaminationTransformer(Integer id) {
		return examinationTransformerRepository.findById(id).orElse(null);
	}

	public List<ExaminationTransformer> readAllExaminationTransformer() {
		List<ExaminationTransformer> reports = new ArrayList<ExaminationTransformer>();
		examinationTransformerRepository.findAll().forEach(reports::add);
		return reports;
	}

	public List<ExaminationTransformer> readExaminationTransformerByTurbineId(String turbineId) {
		return examinationTransformerRepository.findByTurbineId(turbineId);
	}

	public ExaminationTransformer updateExaminationTransformer(ExaminationTransformer examinationTransformer) {
		return examinationTransformerRepository.save(examinationTransformer);
	}

	public List<ExaminationTransformer> searchExaminationTransformer(String site, String wtgNumber, String wtgType) {
		return examinationTransformerRepository.findBySiteAndWtgNumberAndWtgType(site, wtgNumber, wtgType);
	}

	public void deleteExaminationTransformer(Integer id) {
		deleteHistoricAndFileData(id);
		examinationTransformerRepository.deleteById(id);
	}

	public void deleteExaminationTransformerByTurbineId(String turbineId) {
		List<ExaminationTransformer> lista = examinationTransformerRepository.findByTurbineId(turbineId);
		lista.stream().forEach(report -> deleteExaminationTransformer(report.getReportId()));
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
		ExaminationTransformer examinationTransformer = examinationTransformerService.readExaminationTransformer(id);
		if (examinationTransformer != null) {
			String uuid = examinationTransformer.getUuid();
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
