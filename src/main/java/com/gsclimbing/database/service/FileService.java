package com.gsclimbing.database.service;

import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.repository.FileRepository;
import com.gsclimbing.x.database.service.ReportComparisonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileService {

	private static final Logger logger = LoggerFactory.getLogger(ReportComparisonService.class);
	@Autowired
	private FileRepository fileRepository;

	// Método existente - MANTER como está
	public void createFile(FileData file) {
		fileRepository.save(file);
	}

	public Optional<FileData> readFile(Integer id) {
		return fileRepository.findById(id);
	}

	public List<FileData> readAllFile() {
		List<FileData> files = new ArrayList<FileData>();
		fileRepository.findAll().forEach(files::add);
		return files;
	}

	// ✅ ALTERAR: Soft delete em vez de hard delete
	public void deleteFile(Integer id) {
		Optional<FileData> fileData = fileRepository.findById(id);

		if (fileData.isPresent()) {
			FileData file = fileData.get();
			file.markAsDeleted();  // ✅ Marcar como deletada
			fileRepository.save(file);  // ✅ Guardar alteração

			logger.info("🗑️ Soft delete: Foto {} marcada como removida", id);
		}

		// ✅ NÃO apagar fisicamente!
		// fileRepository.deleteById(id);  ← REMOVER ESTA LINHA
	}

	// ✅ NOVO: Buscar fotos por UUID (APENAS ATIVAS)
	public List<FileData> readActiveFilesByUuid(String uuid) {
		return fileRepository.findByUuid(uuid).stream()
				.filter(FileData::isActive)
				.collect(Collectors.toList());
	}

	// ✅ NOVO: Buscar TODAS as fotos (incluindo removidas) - para histórico
	public List<FileData> readAllFilesByUuid(String uuid) {
		return fileRepository.findByUuid(uuid);
	}

	public List<FileData> readFile(String uuid) {
		return fileRepository.findByUuid(uuid);
	}

	public FileData readFileByHash(String hash) {
		return fileRepository.findByHash(hash);
	}
}


