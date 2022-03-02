package com.gsclimbing.dto;

public enum ReportsEnum{

	DIR("Defect Inspection Report"),
    ET("Examination transformer"),
    MMSSC("Measurements of MV Switchgear and Stator Cabinet"),
    M6KV("Medidas 6Kv"),
    M690V400V("Medidas 690V400V"),
    OCIR("Onboard crane Inspection Report"),
    PRRE("Performance Report Repair Elevator"),
    SIR("Statutory Inspection Report");

    public final String label;

    private ReportsEnum(String label) {
        this.label = label;
    }
    
    public static ReportsEnum valueOfLabel(String label) {
        for (ReportsEnum e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
