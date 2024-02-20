package kr.or.iei.hospital.model.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import kr.or.iei.hospital.model.dto.HospitalRowMapper;
import kr.or.iei.hospital.model.dto.HospitalSearchRowMapper;
import kr.or.iei.hospital.model.dto.KeywordRowMapper;
import kr.or.iei.hospital.model.dto.SubjectRowMapper;

@Repository
public class HospitalDao {
	
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	private HospitalRowMapper hospitalRowMapper;
	
	@Autowired
	private SubjectRowMapper subjectRowMapper;
	
	@Autowired
	private KeywordRowMapper keywordRowMapper;
	
	@Autowired
	private HospitalSearchRowMapper hospitalSearchRowMapper;

	public List searchHospital(String keyword) {	
		String query = "select hospital_no, hospital_name, hospital_address, lat, lng,\r\n" + 
				"(select distinct\r\n" + 
				"    case when to_char(sysdate,'dy') in ('월','화','수','목','금')\r\n" + 
				"    then\r\n" + 
				"        case\r\n" + 
				"            when to_char(sysdate,'hh24:mm') between (select substr(day_hour,1,instr(day_hour,'~',1,1)-1) opening_time from time_tbl where hospital_no=h.hospital_no) and (select substr(day_hour,instr(day_hour,'~',1,1)+1) closing_time from time_tbl where hospital_no=h.hospital_no)\r\n" + 
				"            then '진료중'\r\n" + 
				"            else '진료종료'\r\n" + 
				"            end\r\n" + 
				"    when to_char(sysdate,'dy') in ('토','일')\r\n" + 
				"    then\r\n" + 
				"        case\r\n" + 
				"            when to_char(sysdate,'hh24:mm') between (select substr(weekend_hour,1,instr(weekend_hour,'~',1,1)-1) opening_time from time_tbl where hospital_no=h.hospital_no) and (select substr(weekend_hour,instr(weekend_hour,'~',1,1)+1) closing_time from time_tbl where hospital_no=h.hospital_no)\r\n" + 
				"            then '진료중'\r\n" + 
				"            else '진료종료'\r\n" + 
				"            end\r\n" + 
				"    else '진료종료' \r\n" + 
				"    end open_status\r\n" + 
				"from time_tbl) open_status\r\n" + 
				"from hospital_tbl h\r\n" + 
				"where hospital_no in (select hospital_no from hospital_tbl where hospital_name||hospital_address like '%'||?||'%')\r\n" + 
				"or hospital_no in (select hospital_no from doctor_tbl where subject_no in (select subject_no from subject_tbl where subject_name like '%'||?||'%'))\r\n" + 
				"or hospital_no in (select hospital_no from doctor_tbl where subject_no in (select subject_no from keyword_tbl where keyword like '%'||?||'%'))";
		Object[] params = {keyword,keyword,keyword};
		List hospitalList = jdbc.query(query, hospitalSearchRowMapper, params);
		return hospitalList;
	}

	public List searchSubjectHospital(int hospitalNo) {
		String query = "select * from subject_tbl where subject_no in (select subject_no from doctor_tbl where hospital_no=?)";
		Object[] params = {hospitalNo};
		List list = jdbc.query(query, subjectRowMapper, params);
		return list;
	}

	public List searchKeywordHospital(int hospitalNo) {
		String query = "select * from keyword_tbl where subject_no in(select subject_no from doctor_tbl where hospital_no=?)";
		Object[] params = {hospitalNo};
		List list = jdbc.query(query, keywordRowMapper, params);
		return list;
	}
	
}