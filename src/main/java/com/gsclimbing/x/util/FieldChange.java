package com.gsclimbing.x.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa uma alteração num campo do relatório
 * Usado para comparar valores antigos vs novos
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldChange {
    private String fieldName;
    private String oldValue;
    private String newValue;
    
    /**
     * Verifica se o campo foi realmente alterado
     */
    public boolean hasChanged() {
        if (oldValue == null && newValue == null) {
            return false;
        }
        if (oldValue == null || newValue == null) {
            return true;
        }
        return !oldValue.equals(newValue);
    }
    
    /**
     * Retorna descrição legível da alteração
     */
    public String getChangeDescription() {
        if (oldValue == null || oldValue.isEmpty()) {
            return "Campo adicionado: " + fieldName;
        }
        if (newValue == null || newValue.isEmpty()) {
            return "Campo removido: " + fieldName;
        }
        return "Campo alterado: " + fieldName;
    }
}
