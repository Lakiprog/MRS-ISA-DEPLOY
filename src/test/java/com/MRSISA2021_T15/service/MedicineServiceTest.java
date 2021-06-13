package com.MRSISA2021_T15.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.MRSISA2021_T15.model.Medicine;
import com.MRSISA2021_T15.model.MedicineForm;
import com.MRSISA2021_T15.model.MedicineType;
import com.MRSISA2021_T15.repository.MedicineRepository;

@SpringBootTest
public class MedicineServiceTest {
	
	@Mock
	private MedicineRepository medicineRepository;
	
	@Mock
	private Medicine medicineMock;
	
	@Test
	public void addMedicine() {
		Medicine medicine = new Medicine();
		medicine.setAdditionalComments("");
		medicine.setComposition("");
		medicine.setForm(MedicineForm.CAPSULE);
		medicine.setManufacturer("");
		medicine.setMedicineType(MedicineType.ANALGESIC);
		medicine.setName("");
		medicine.setPoints(9);
		medicine.setSubstituteMedicineIds(null);
		medicine.setMedicineCode("s123");
		medicine.setPrescription(false);
		
		when(medicineRepository.findByMedicineCode(medicine.getMedicineCode())).thenReturn(medicineMock);
		
		assertEquals(medicine.getMedicineCode(), medicineMock.getMedicineCode());
		
	}
}