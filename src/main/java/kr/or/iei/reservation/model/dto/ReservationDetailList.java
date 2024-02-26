package kr.or.iei.reservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReservationDetailList {
	private int reservationNo;
	private int reservationStatus;
	private int reservationType;
	private String reservationTime;
	private String filename;
	private String filepath;
	private String memberName;
	private String symptom;
	private int prescriptionStatus;
}
