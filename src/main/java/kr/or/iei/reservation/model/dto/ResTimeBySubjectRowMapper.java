package kr.or.iei.reservation.model.dto;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class ResTimeBySubjectRowMapper implements RowMapper<ResTimeBySubject>{

	@Override
	public ResTimeBySubject mapRow(ResultSet rs, int rowNum) throws SQLException {
		ResTimeBySubject t = new ResTimeBySubject();
		t.setTime(rs.getString("time"));
		return t;
	}
	
}
