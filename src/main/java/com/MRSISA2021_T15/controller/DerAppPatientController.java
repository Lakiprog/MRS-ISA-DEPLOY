package com.MRSISA2021_T15.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.MRSISA2021_T15.dto.AppointmentDermatologistDTO;
import com.MRSISA2021_T15.model.AppointmentDermatologist;
import com.MRSISA2021_T15.model.Patient;
import com.MRSISA2021_T15.model.Pharmacy;
import com.MRSISA2021_T15.service.AppointmentService;
import com.MRSISA2021_T15.service.PharmacySearchService;
import com.google.gson.GsonBuilder;

@RestController
@RequestMapping("/make_dermatologist_appointment")
public class DerAppPatientController {
	@Autowired
	PharmacySearchService service;
	
	@Autowired
	private AppointmentService AppService;
	
	private String cancel = "You can't cancel your appointment under 24h before it's beginning!";
	
	@GetMapping(value = "/getAllPharamacies", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_PATIENT')")
	public List<Pharmacy>getAll(){
		return service.findAll();
	}
	
	@GetMapping(value = "/getAllFreeDerApp", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_PATIENT')")
	public List<AppointmentDermatologist>getAllFreeDerApp(){
		return AppService.findAllFreeDermatologicalApp();
	}
	
	@PostMapping(value = "/send", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_PATIENT')")
	public ResponseEntity<String> send(@RequestBody AppointmentDermatologistDTO appointmentDto){
		Patient p = (Patient) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var appointment = new AppointmentDermatologist();
		appointment.setDiscount(appointmentDto.getDiscount());
		appointment.setStart(appointmentDto.getStart());
		appointment.setEnd(appointmentDto.getEnd());
		appointment.setPatient(p);
		appointment.setDermatologist(appointmentDto.getDermatologist());
		appointment.setPrice(appointmentDto.getPrice());
		AppService.saveDerApp(appointment);
		
		var gson = new GsonBuilder().create();
		return new ResponseEntity<>(gson.toJson("You scheduled an appointment with dermatologist."), HttpStatus.OK);
	}
	
	
	@GetMapping(value = "/getPatientDerApp", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_PATIENT')")
	public List<AppointmentDermatologist>getPatientDerApp(){
		Patient p = (Patient) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return AppService.findAllDerAppWithPatientId(p.getId());
	}
	
	
	@GetMapping(value = "/getPastPatientDerApp", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_PATIENT')")
	public List<AppointmentDermatologist>getPastPatientDerApp(){
		Patient p = (Patient) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return AppService.findAllPastDerAppWithPatientId(p.getId());
	}
	
	
	
	@PutMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('ROLE_PATIENT')")
	public ResponseEntity<String> delete(@RequestBody AppointmentDermatologistDTO appointmentDto){
		
		var message = "";
		var now = LocalDateTime.now();
		if(now.getYear() == appointmentDto.getStart().getYear()) {
			if(now.getMonthValue() == appointmentDto.getStart().getMonthValue()) {
				if(now.getDayOfMonth() == appointmentDto.getStart().getDayOfMonth()) {
					message = cancel;
				}else if(now.getDayOfMonth() + 1 == appointmentDto.getStart().getDayOfMonth()) { //ako je otkazujem dan prije
					//provjeri sate i minute onda
					if(now.getHour() > appointmentDto.getStart().getHour()) {
						message = cancel;
					}else if(now.getHour() == appointmentDto.getStart().getHour()) {
						//ovdje provjeri minute
						if(now.getMinute() >  appointmentDto.getStart().getMinute()) { //moze tacno 24 od pocetka da otkaze
							message = cancel;
						}
					}
				}
			}
		}
		
		if(message.equals("")) {
			AppService.deleteDermatologicalApp(appointmentDto);
		}
		
		var gson = new GsonBuilder().create();
		if (message.equals("")) {
			return new ResponseEntity<>(gson.toJson("You canceled your appointment with dermatologist!"), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(gson.toJson(message), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}	
}
