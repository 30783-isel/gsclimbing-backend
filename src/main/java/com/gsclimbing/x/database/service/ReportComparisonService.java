package com.gsclimbing.x.database.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gsclimbing.database.entity.FileData;
import com.gsclimbing.database.service.FileService;
import com.gsclimbing.x.util.FieldChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Serviço para comparar relatórios e identificar alterações
 * Faz diff de JSONs campo a campo
 */
@Service
public class ReportComparisonService {

    private static final Logger logger = LoggerFactory.getLogger(ReportComparisonService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ✅ NOVO: Injetar FileService para obter hash das fotos
    @Autowired
    private FileService fileService;

    /**
     * Compara dois JSONs de reportData e retorna lista de campos alterados
     *
     * @param oldJson JSON antigo
     * @param newJson JSON novo
     * @return Lista de alterações detectadas
     */
    public List<FieldChange> compareReportData(String oldJson, String newJson) {
        List<FieldChange> changes = new ArrayList<>();

        try {
            JsonNode oldNode = objectMapper.readTree(oldJson);
            JsonNode newNode = objectMapper.readTree(newJson);

            // Obter todos os nomes de campos do novo JSON
            Iterator<Map.Entry<String, JsonNode>> fields = newNode.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String fieldName = field.getKey();
                JsonNode newValue = field.getValue();

                // Obter valor antigo (se existir)
                JsonNode oldValue = oldNode.has(fieldName) ? oldNode.get(fieldName) : null;

                // Comparar valores
                String oldValueStr = nodeToString(oldValue);
                String newValueStr = nodeToString(newValue);

                if (!oldValueStr.equals(newValueStr)) {
                    FieldChange change = new FieldChange(fieldName, oldValueStr, newValueStr);
                    changes.add(change);
                    logger.info("Campo alterado: {} | '{}' -> '{}'", fieldName, oldValueStr, newValueStr);
                }
            }

            // Verificar se há campos que foram removidos (existiam no antigo mas não no novo)
            Iterator<Map.Entry<String, JsonNode>> oldFields = oldNode.fields();
            while (oldFields.hasNext()) {
                Map.Entry<String, JsonNode> field = oldFields.next();
                String fieldName = field.getKey();

                if (!newNode.has(fieldName)) {
                    String oldValueStr = nodeToString(field.getValue());
                    FieldChange change = new FieldChange(fieldName, oldValueStr, "");
                    changes.add(change);
                    logger.info("Campo removido: {} | '{}'", fieldName, oldValueStr);
                }
            }

        } catch (Exception e) {
            logger.error("Erro ao comparar JSONs: {}", e.getMessage(), e);
        }

        return changes;
    }

    /**
     * Converte JsonNode para String de forma segura
     * Trata objetos nested e arrays
     */
    private String nodeToString(JsonNode node) {
        if (node == null || node.isNull()) {
            return "";
        }

        if (node.isObject() || node.isArray()) {
            // Para objetos ou arrays, retornar como JSON string
            try {
                return objectMapper.writeValueAsString(node);
            } catch (Exception e) {
                return node.toString();
            }
        }

        return node.asText();
    }


    /**
     * Compara listas de IDs de fotos antigas vs novas
     * ✅ CORREÇÃO COMPLETA: Ignora ordem, detecta adições/remoções e guarda hash
     *
     * @param oldPhotoIds Lista de IDs de fotos antigas
     * @param newPhotoIds Lista de IDs de fotos novas
     * @return Lista de alterações com hash incluído no valor
     */
    public List<FieldChange> comparePhotos(List<Long> oldPhotoIds, List<Long> newPhotoIds) {
        List<FieldChange> changes = new ArrayList<>();

        // ✅ Converter para Sets para ignorar ordem e facilitar comparação
        Set<Long> oldSet = new HashSet<>(oldPhotoIds);
        Set<Long> newSet = new HashSet<>(newPhotoIds);

        // ✅ Encontrar fotos REMOVIDAS (estavam nas antigas mas não estão nas novas)
        Set<Long> removedPhotos = new HashSet<>(oldSet);
        removedPhotos.removeAll(newSet);

        // ✅ Encontrar fotos ADICIONADAS (estão nas novas mas não estavam nas antigas)
        Set<Long> addedPhotos = new HashSet<>(newSet);
        addedPhotos.removeAll(oldSet);

        // ✅ NOVO: Registar remoções COM HASH
        for (Long photoId : removedPhotos) {
            String photoValue = buildPhotoValue(photoId);

            changes.add(new FieldChange(
                    "photo_removed",
                    photoValue,  // ✅ "Foto ID: 123|abc123hash"
                    ""
            ));
            logger.info("📸 Foto removida: {} ({})", photoId, photoValue);
        }

        // ✅ NOVO: Registar adições COM HASH
        for (Long photoId : addedPhotos) {
            String photoValue = buildPhotoValue(photoId);

            changes.add(new FieldChange(
                    "photo_added",
                    "",
                    photoValue  // ✅ "Foto ID: 123|abc123hash"
            ));
            logger.info("📸 Foto adicionada: {} ({})", photoId, photoValue);
        }

        return changes;
    }

    /**
     * ✅ NOVO: Constrói o valor da foto no formato "Foto ID: 123|hash"
     *
     * @param photoId ID da foto
     * @return String no formato "Foto ID: 123|hash" ou "Foto ID: 123|unknown" se não encontrar
     */
    private String buildPhotoValue(Long photoId) {
        try {
            // Buscar FileData para obter o hash
            Optional<FileData> fileDataOpt = fileService.readFile(photoId.intValue());

            if (fileDataOpt.isPresent()) {
                FileData fileData = fileDataOpt.get();
                String hash = fileData.getHash();

                if (hash != null && !hash.isEmpty()) {
                    // ✅ Formato esperado pelo Frontend: "Foto ID: 123|abc123hash"
                    return "Foto ID: " + photoId + "|" + hash;
                }
            }

            logger.warn("⚠️ Hash não encontrado para foto ID {}, usando 'unknown'", photoId);
            return "Foto ID: " + photoId + "|unknown";

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar hash da foto {}: {}", photoId, e.getMessage());
            return "Foto ID: " + photoId + "|error";
        }
    }

    /**
     * Traduz nomes técnicos de campos para nomes legíveis
     */
    public String getReadableFieldName(String fieldName) {
        switch (fieldName) {
            case "site":
                return "Site/Lugar";
            case "wtgNumber":
                return "Número WTG";
            case "wtgType":
                return "Tipo WTG";
            case "yearConstruction":
                return "Ano de Construção";
            case "dateInspection":
                return "Data de Inspeção";
            case "inspectedBy":
                return "Inspecionado por";
            case "observations":
                return "Observações";
            case "photo_added":
                return "Foto adicionada";
            case "photo_removed":
                return "Foto removida";
            default:
                // Para campos adicionais (additionalField1, etc)
                if (fieldName.startsWith("additionalField")) {
                    return "Campo Adicional";
                }
                return fieldName;
        }
    }
}