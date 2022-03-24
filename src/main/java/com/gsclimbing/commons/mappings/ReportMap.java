package com.gsclimbing.commons.mappings;

import java.util.HashMap;

public class ReportMap {

	public static HashMap<String, String> mapeamento(){
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
		return mapa;
	}

}
