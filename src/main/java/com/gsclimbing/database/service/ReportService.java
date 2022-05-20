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
import com.gsclimbing.database.entity.Report;
import com.gsclimbing.database.entity.StatutoryInspectionReportInt;
import com.gsclimbing.database.repository.ReportRepository;
import com.gsclimbing.ftp.FTPDownloadFiles;

@Service
public class ReportService {

	Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private FileService fileService;

	@Autowired
	private ReportRepository reportRepository;

	public Report createDefectsInspectionReport(Report report) {
		return reportRepository.save(report);
	}

	public Report readReport(Integer id) {
		return reportRepository.findById(id).orElse(null);
	}

	public List<Report> readReportByTurbineId(String turbineId) {
		return reportRepository.findByTurbineId(turbineId);
	}

	public List<Report> readAllReport() {
		List<Report> reports = new ArrayList<>();
		reportRepository.findAll().forEach(reports::add);
		return reports;
	}

	public Report updateReport(Report report) {
		return reportRepository.save(report);
	}

	public List<Report> searchReport(String site, String wtgNumber, String wtgType, String yearConstruction) {
		return reportRepository.findBySiteAndWtgNumberAndWtgTypeAndYearConstruction(site, wtgNumber, wtgType, yearConstruction);
	}

	public void deleteReport(Integer id) {
		deleteHistoricAndFileData(id);
		reportRepository.deleteById(id);
	}

	public void deleteReportByTurbineId(String turbineId) {
		List<Report> lista = reportRepository.findByTurbineId(turbineId);
		lista.stream().forEach(report -> this.deleteReport(report.getReportId()));
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
		Report report = this.readReport(id);
		if (report != null) {
			String uuid = report.getUuid();
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

	public List<String> chkIfAllFieldsNull(Report report) {
		List<String> lista = new ArrayList<String>();
		try {
			for (Field field : report.getClass().getSuperclass().getDeclaredFields()) {
				field.setAccessible(true);
				Object object = field.get(report);
				System.out.println(field.getName() + " - " + object);
				if (object == null || ObjectUtils.isEmpty(object.toString())) {
					String fieldName = report.mapeamento().get(field.getName());
					if (fieldName != null) {
						lista.add(report.mapeamento().get(field.getName()));
					}
				}
//				Method method = report.getClass().getSuperclass().getDeclaredMethod("mapeamento", null);
//				method.toString();
//				report.getClass().getSuperclass().getMethod("mapeamento", new Class[] {}).getReturnType();
//				Object objMapa = report.getClass().getSuperclass().getMethod("mapeamento", new Class[] {}).invoke(report.getClass().getSuperclass().newInstance(), new Object[] {});
//				HashMap<String, String> mapa = (HashMap<String, String>) objMapa;
//				fieldName = mapa.get(field.getName());
//				if (fieldName != null) {
//					lista.add(fieldName);
//				}	
			}

			List<String> listaChkTrue = new ArrayList<String>();
			for (Field field : report.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object object = field.get(report);
				String fieldName = field.getName();
				System.out.println(fieldName + " - " + object);
				if (("true".equals(object.toString()) && !ObjectUtils.isEmpty(object.toString())) && fieldName.contains("Chkn")) {
					if (fieldName != null) {
						listaChkTrue.add(fieldName.substring(0, fieldName.length() - 4));
					}
				}

				if (object instanceof StatutoryInspectionReportInt) {
					for (Field statutoryInspectionReportField : object.getClass().getDeclaredFields()) {
						statutoryInspectionReportField.setAccessible(true);
						Object statutoryInspectionReportObject = statutoryInspectionReportField.get(object);
						String statutoryInspectionReportFieldName = statutoryInspectionReportField.getName();
						System.out.println(statutoryInspectionReportFieldName + " - " + statutoryInspectionReportObject);
						if (("true".equals(statutoryInspectionReportObject.toString()) && !ObjectUtils.isEmpty(statutoryInspectionReportObject.toString())) && statutoryInspectionReportFieldName.contains("Chkn")) {
							if (statutoryInspectionReportFieldName != null) {
								listaChkTrue.add(statutoryInspectionReportFieldName.substring(0, statutoryInspectionReportFieldName.length() - 4));
							}
						}
					}
				}
			}

			for (Field field : report.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				Object object = field.get(report);
				if (object instanceof ArrayList<?>) {
					if (((ArrayList) object).size() == 0) {
						lista.add(field.getName());
					}
				} else {

					if (object instanceof StatutoryInspectionReportInt) {
						for (Field statutoryInspectionReportField : object.getClass().getDeclaredFields()) {
							statutoryInspectionReportField.setAccessible(true);
							Object statutoryInspectionReportObject = statutoryInspectionReportField.get(object);
							String statutoryInspectionReportFieldName = statutoryInspectionReportField.getName();
							System.out.println(statutoryInspectionReportFieldName + " - " + statutoryInspectionReportObject);
							String str = listaChkTrue.stream().filter(chkc -> (chkc + "Txt").equals(statutoryInspectionReportField.getName())).findAny().orElse(null);

							if (str != null) {
								System.out.println(statutoryInspectionReportField.getName() + " - " + statutoryInspectionReportObject);
								if ((statutoryInspectionReportObject == null || ObjectUtils.isEmpty(statutoryInspectionReportObject.toString()))) {
									String fieldName = report.mapeamento().get(statutoryInspectionReportField.getName());
									if (fieldName != null) {
										lista.add(fieldName);
									}
								}
							} else {
								if ((statutoryInspectionReportObject == null || ObjectUtils.isEmpty(statutoryInspectionReportObject.toString()))) {
									String fieldName = report.mapeamento().get(statutoryInspectionReportField.getName());
									if (fieldName != null) {
										lista.add(fieldName);
									}
								}
							}

						}
					} else {

						String str = listaChkTrue.stream().filter(chkc -> (chkc + "Txt").equals(field.getName())).findAny().orElse(null);
						if (str != null) {
							System.out.println(field.getName() + " - " + object);
//							if ((object != null || ObjectUtils.isEmpty(object.toString()))) {
//								String fieldName = report.mapeamento().get(field.getName());
//								if (fieldName != null) {
//									lista.add(fieldName);
//								}
//							}
						} else {
							if ((object == null || ObjectUtils.isEmpty(object.toString()))) {
								String fieldName = report.mapeamento().get(field.getName());
								if (fieldName != null) {
									lista.add(fieldName);
								}
							}
						}

					}

				}
			}
		} catch (NullPointerException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return lista;
	}
}
