package com.gsclimbing.x.database.entity;

import com.gsclimbing.database.entity.Report;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidade para rastrear o histórico de alterações em relatórios
 * Permite saber quem criou, editou e quando
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "report_history")
public class ReportHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;

    @Column(name = "changed_by", nullable = false, length = 255)
    private String changedBy; // Username do utilizador que fez a alteração

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "action", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private HistoryAction action;

    @Column(name = "field_name", length = 255)
    private String fieldName; // Nome do campo alterado (null para CREATE)

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue; // Valor anterior (null para CREATE)

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue; // Novo valor

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // Descrição adicional da alteração

    /**
     * Tipos de ações de histórico
     */
    public enum HistoryAction {
        CREATE,          // Relatório criado
        UPDATE,          // Relatório atualizado
        SUBMIT,          // Relatório submetido
        LOCK,            // Relatório bloqueado (após submit)
        REQUEST_UNLOCK,  // Pedido de desbloqueio
        UNLOCK,          // Relatório desbloqueado pelo admin
        APPROVE,         // Relatório aprovado
        REJECT,          // Relatório rejeitado
        DELETE           // Relatório eliminado
    }

    @PrePersist
    protected void onCreate() {
        if (changedAt == null) {
            changedAt = LocalDateTime.now();
        }
    }
}
