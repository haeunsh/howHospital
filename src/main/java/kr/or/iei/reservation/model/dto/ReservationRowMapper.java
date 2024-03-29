package kr.or.iei.reservation.model.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class ReservationRowMapper implements RowMapper<Reservation>{

	@Override
	public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
		Reservation r = new Reservation();
		r.setHospitalNo(rs.getInt("hospital_no"));
		r.setMemberNo(rs.getInt("member_no"));
		r.setRegReservation(rs.getString("reg_reservation"));
		r.setReservationNo(rs.getInt("reservation_no"));
		r.setReservationStatus(rs.getInt("reservation_status"));
		r.setReservationType(rs.getInt("reservation_type"));
		r.setReservationTime(rs.getString("reservation_time"));
		return r;
	}

}
