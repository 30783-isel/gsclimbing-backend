package com.gsclimbing.controller;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gsclimbing.database.entity.DefectsInspectionReport;
import com.gsclimbing.database.service.DefectsInspectionReportService;

@CrossOrigin(origins = "*", methods = { RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@RestController
@RequestMapping(path = "/api/reports")
public class ReportsController {

	@Autowired
	private DefectsInspectionReportService defectsInspectionReportService;
	
	@RequestMapping("/turbine-report/{turbineId}")
	public List<DefectsInspectionReport> readDefectsInspectionReportByTurbine(@PathVariable String turbineId ) {
		List<DefectsInspectionReport> reports = defectsInspectionReportService.readDefectsInspectionReportByTurbineId( turbineId );
		List<DefectsInspectionReport> sortedList = reports.stream().sorted(Comparator.comparing(DefectsInspectionReport::getCreateDate).reversed()).collect(Collectors.toList());
		return sortedList;
	}
	
}
