package kr.or.iei.reservation.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.iei.reservation.model.dao.ReservationDetailDao;
import kr.or.iei.reservation.model.dto.H_Reservation;
import kr.or.iei.reservation.model.dto.ReservationDetail;

@Service
public class ReservationDetailService {
	@Autowired
	private ReservationDetailDao reservationDetailDao;

	public String selectDoctor() {
		String doctorName = reservationDetailDao.selectDoctor();
		return doctorName;
	}

	public ReservationDetail selectOneReservation(H_Reservation hr) {
		ReservationDetail rd = reservationDetailDao.selectOneReservation(hr);
		return rd;
	}
}
