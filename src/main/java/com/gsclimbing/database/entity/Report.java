package com.gsclimbing.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gsclimbing.dto.ReportDto;
import com.gsclimbing.x.database.entity.ReportHistory;
import com.gsclimbing.x.dto.MobileReportDTO;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
public class Report {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer reportId;
	private String uuid;
	private LocalDateTime createDate;
	private LocalDateTime modifiedDate;
	private String locked;
	private String permission2Edit;
	private String site;
	private String wtgNumber;
	private String wtgType;
	private String yearConstruction;
	private Integer typeReport;
	private Integer projectoId;
	private Integer turbinaId;
	private String insertImagesChk;
	private String additionalField1Label;
	private String additionalField1Text;
	private String additionalField2Label;
	private String additionalField2Text;
	private String additionalField3Label;
	private String additionalField3Text;
	private String additionalField4Label;
	private String additionalField4Text;
	private String additionalField5Label;
	private String additionalField5Text;
	private String additionalField6Label;
	private String additionalField6Text;
	private String additionalField7Label;
	private String additionalField7Text;







	/**
	 * Estado do relatório
	 */
	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private ReportStatus status = ReportStatus.DRAFT;

	/**
	 * Erros de validação em formato JSON
	 * Exemplo: [{"field":"site","message":"Campo obrigatório"}]
	 */
	@Column(name = "validation_errors", columnDefinition = "TEXT")
	private String validationErrors;

	/**
	 * Idioma do relatório (EN ou ES)
	 */
	@Column(name = "language", length = 2)
	private String language = "EN";

	/**
	 * Se o relatório foi criado offline
	 */
	@Column(name = "offline_created")
	private Boolean offlineCreated = false;

	/**
	 * Estado de sincronização
	 */
	@Column(name = "sync_status", length = 50)
	@Enumerated(EnumType.STRING)
	private SyncStatus syncStatus = SyncStatus.SYNCED;

	/**
	 * Data e hora de submissão
	 */
	@Column(name = "submitted_at")
	private LocalDateTime submittedAt;

	/**
	 * Username de quem submeteu
	 */
	@Column(name = "submitted_by", length = 255)
	private String submittedBy;

	/**
	 * Se foi pedido desbloqueio
	 */
	@Column(name = "unlock_requested")
	private Boolean unlockRequested = false;

	/**
	 * Data do pedido de desbloqueio
	 */
	@Column(name = "unlock_requested_at")
	private LocalDateTime unlockRequestedAt;

	/**
	 * Username de quem desbloqueou (admin)
	 */
	@Column(name = "unlocked_by", length = 255)
	private String unlockedBy;

	/**
	 * Data de desbloqueio
	 */
	@Column(name = "unlocked_at")
	private LocalDateTime unlockedAt;

	// ========== RELACIONAMENTOS ==========

	/**
	 * Histórico de alterações
	 */
	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<ReportHistory> historyList = new ArrayList<>();

	// ========== ENUMS ==========

	/**
	 * Estados possíveis de um relatório
	 */
	public enum ReportStatus {
		DRAFT,           // Rascunho (pode editar livremente)
		SUBMITTED,       // Submetido (pode editar com permissão)
		LOCKED,          // Bloqueado após submissão
		APPROVED,        // Aprovado pelo admin
		REJECTED         // Rejeitado pelo admin
	}

	/**
	 * Estados de sincronização
	 */
	public enum SyncStatus {
		PENDING,         // Pendente de sincronização
		SYNCED,          // Sincronizado
		FAILED           // Falhou a sincronização
	}












	@Column(name = "report_type", nullable = false)
	private Integer reportType; // 0-7 conforme REPORT_NAMES

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	// Relacionamento com ficheiros (fotos, PDFs, etc)
	@OneToMany(mappedBy = "report", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<FileData> listaFileData = new ArrayList<>();





	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "turbineId", nullable = true)
	@JsonIgnore
	private Turbine turbine;

	@OneToMany(mappedBy = "report", cascade = { CascadeType.ALL })
	@JsonIgnore
	private List<HistoricReport> listHistoric = new ArrayList<>();

	public void addImgOnListImages(FileData fileData) {
		this.listaFileData.add(fileData);
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public ReportDto mapper() {
		return ReportDto.builder().reportId(reportId).uuid(uuid).createDate(createDate).modifiedDate(modifiedDate).locked(locked).permission2Edit(permission2Edit).site(site).wtgNumber(wtgNumber).wtgType(wtgType)
				.yearConstruction(yearConstruction).typeReport(typeReport).projectoId(projectoId).turbinaId(turbinaId).insertImagesChk(insertImagesChk).additionalField1Label(additionalField1Label)
				.additionalField1Text(additionalField1Text).additionalField2Label(additionalField2Label).additionalField2Text(additionalField2Text).additionalField3Label(additionalField3Label)
				.additionalField3Text(additionalField3Text).additionalField4Label(additionalField4Label).additionalField4Text(additionalField4Text).additionalField5Label(additionalField5Label)
				.additionalField5Text(additionalField5Text).additionalField6Label(additionalField6Label).additionalField6Text(additionalField6Text).additionalField7Label(additionalField7Label)
				.additionalField7Text(additionalField7Text).build();
	}

	public MobileReportDTO.ReportResponseDTO toMobileResponseDTO() {
		return MobileReportDTO.ReportResponseDTO.builder()
				.id(reportId != null ? reportId.longValue() : null)
				.turbineId(turbinaId != null ? turbinaId.longValue() : null)
				.turbineName(turbine != null ? turbine.getName() : null)
				.reportType(typeReport)

				// Campos novos
				.status(status.name())
				.language(language)
				.offlineCreated(offlineCreated)
				.submittedAt(submittedAt)
				.submittedBy(submittedBy)
				.unlockRequested(unlockRequested)
				.unlockRequestedAt(unlockRequestedAt)

				// Criado e modificado
				.createdAt(createDate)
				.updatedAt(modifiedDate)

				// Dados completos do relatório (JSON)
				.reportData(null) // TODO: meter JSON final do relatório

				// Ficheiros / fotos
				.files(listaFileData != null ?
						listaFileData.stream()
								.map(f -> MobileReportDTO.FileDataDTO.builder()
										.id(f.getFileId() != null ? f.getFileId().longValue() : null)
										.filename(f.getName())
										.url(f.getUrl())
										.mimeType(f.getMimeType())
										.size(f.getSize())
										.uploadedAt(f.getModifiedDate())
										.build()
								)
								.collect(Collectors.toList())
						: null
				)

				// Locked e permissões
				.isLocked("Y".equalsIgnoreCase(locked))
				.canEdit("Y".equalsIgnoreCase(permission2Edit))

				// Erros de validação — JSON → lista
				// TODO: converter validationErrors (String JSON) para List<ValidationErrorDTO>
				.validationErrors(null)

				.build();
	}


	public HashMap<String, String> mapeamento() {
		HashMap<String, String> mapa = new HashMap<String, String>();
		mapa.put("reportId", "Id");
		mapa.put("uuid", "UUID");
		mapa.put("createDate", "Creation Date");
		mapa.put("modifiedDate", "Modified Date");
		mapa.put("locked", "Locked");
		mapa.put("permission2Edit", "Permission to Edit");
		mapa.put("site", "Site");
		mapa.put("wtgNumber", "WTG Number");
		mapa.put("wtgType", "WTG Type");
		mapa.put("yearConstruction", "Year of Construction");
		mapa.put("typeReport", "Type of Report");
		mapa.put("projectoId", "Project Id");
		mapa.put("turbinaId", "Turbine Id");

		mapa.put("photoOne", "Photo one");
		mapa.put("photoTwo", "Photo two");
		mapa.put("photoThree", "Photo three");
		mapa.put("photoFour", "Photo four");
		mapa.put("photoFive", "Photo five");
		mapa.put("photoSix", "Photo six");
		mapa.put("photoSeven", "Photo seven");
		mapa.put("photoEight", "Photo eight");
		mapa.put("photoNine", "Photo nine");
		mapa.put("photoTen", "Photo ten");

		mapa.put("additionalField1Label", "Additional Field Label 1");
		mapa.put("additionalField2Label", "Additional Field Label 2");
		mapa.put("additionalField3Label", "Additional Field Label 3");
		mapa.put("additionalField4Label", "Additional Field Label 4");
		mapa.put("additionalField5Label", "Additional Field Label 5");
		mapa.put("additionalField6Label", "Additional Field Label 6");
		mapa.put("additionalField7Label", "Additional Field Label 7");

		mapa.put("additionalField1Text", "Additional Field Value 1");
		mapa.put("additionalField2Text", "Additional Field Value 2");
		mapa.put("additionalField3Text", "Additional Field Value 3");
		mapa.put("additionalField4Text", "Additional Field Value 4");
		mapa.put("additionalField5Text", "Additional Field Value 5");
		mapa.put("additionalField6Text", "Additional Field Value 6");
		mapa.put("additionalField7Text", "Additional Field Value 7");

		return mapa;
	}


	// ========== LIFECYCLE CALLBACKS ==========

	@PrePersist
	protected void onCreate() {
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	// ========== MÉTODOS AUXILIARES ==========

	/**
	 * Verifica se o relatório pode ser editado
	 */
	public boolean canEdit() {
		return status == ReportStatus.DRAFT ||
				(status == ReportStatus.SUBMITTED && !unlockRequested);
	}

	/**
	 * Verifica se o relatório está bloqueado
	 */
	public boolean isLocked() {
		return status == ReportStatus.LOCKED ||
				status == ReportStatus.APPROVED;
	}

	/**
	 * Submete o relatório
	 */
	public void submit(String username) {
		this.status = ReportStatus.LOCKED;
		this.submittedAt = LocalDateTime.now();
		this.submittedBy = username;
	}

	/**
	 * Pede desbloqueio
	 */
	public void requestUnlock() {
		this.unlockRequested = true;
		this.unlockRequestedAt = LocalDateTime.now();
	}

	/**
	 * Desbloqueia o relatório
	 */
	public void unlock(String adminUsername) {
		this.status = ReportStatus.SUBMITTED;
		this.unlockRequested = false;
		this.unlockedBy = adminUsername;
		this.unlockedAt = LocalDateTime.now();
	}

}
