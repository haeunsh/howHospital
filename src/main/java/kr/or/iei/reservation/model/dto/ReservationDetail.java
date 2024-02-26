package kr.or.iei.reservation.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReservationDetail {
	private int reservationDetailNo;
	private int reservationNo;
	private int doctorNo;
	private int subjectNo;
	private String symptom;
	private String memberName;
	private int childNo;
	private int reservation_status;
	private int reservation_type;
	private String reservation_time;
	private String filename;
	private String filepath;
}
