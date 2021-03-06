package com.MRSISA2021_T15.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.MRSISA2021_T15.dto.AbsenceDTO;
import com.MRSISA2021_T15.model.Absence;
import com.MRSISA2021_T15.service.AbsenceService;
import com.google.gson.GsonBuilder;

@RestController
@RequestMapping("/absence")
public class AbsenceController {

	@Autowired
	private AbsenceService service;
	
	@PostMapping(path="/pharmacist",  consumes=MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_PHARMACIST')")
	public @ResponseBody ResponseEntity<String> createAbsencePharmacist(@RequestBody AbsenceDTO absenceDto) throws InterruptedException {
		var absence = new Absence();
		absence.setStart(absenceDto.getStart());
		absence.setEnd(absenceDto.getEnd());
		absence.setDescription(absenceDto.getDescription());
		String message = service.createAbsencePharmacist(absence);
		var gson = new GsonBuilder().create();
		if (message.equals("")) {
			return new ResponseEntity<>(gson.toJson("Absence succesfully created. An admin will review your request."), HttpStatus.OK);
		}
		return new ResponseEntity<>(gson.toJson(message), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@PostMapping(path="/dermatologist",  consumes=MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_DERMATOLOGIST')")
	public @ResponseBody ResponseEntity<String> createAbsenceDermatologist(@RequestBody AbsenceDTO absenceDto) throws InterruptedException {
		var absence = new Absence();
		absence.setStart(absenceDto.getStart());
		absence.setEnd(absenceDto.getEnd());
		absence.setDescription(absenceDto.getDescription());
		String message = service.createAbsenceDermatologist(absence);
		var gson = new GsonBuilder().create();
		if (message.equals("")) {
			return new ResponseEntity<>(gson.toJson("Absence succesfully created. An admin will review your request."), HttpStatus.OK);
		}
		return new ResponseEntity<>(gson.toJson(message), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
