package kr.or.iei.hospital.model.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import kr.or.iei.hospital.model.dto.BusinessAuth;
import kr.or.iei.hospital.model.dto.BusinessAuthFile;
import kr.or.iei.hospital.model.dto.Doctor;
import kr.or.iei.admin.model.dto.ReviewMemberNameRowMapper;
import kr.or.iei.admin.model.dto.ReviewRowMapper;
import kr.or.iei.hospital.model.dto.DoctorRowMapper;
import kr.or.iei.hospital.model.dto.Hospital;
import kr.or.iei.hospital.model.dto.HospitalDetailRowMapper;
import kr.or.iei.hospital.model.dto.HospitalRowMapper;
import kr.or.iei.hospital.model.dto.HospitalSearchRowMapper;
import kr.or.iei.hospital.model.dto.KeywordRowMapper;
import kr.or.iei.hospital.model.dto.Subject;
import kr.or.iei.hospital.model.dto.SubjectDoctorRowMapper;
import kr.or.iei.hospital.model.dto.SubjectRowMapper;
import kr.or.iei.hospital.model.dto.Time;
import kr.or.iei.hospital.model.dto.TimeRowMapper;



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
	@Autowired
	private HospitalDetailRowMapper hospitalDetailRowMapper;
	@Autowired
	private TimeRowMapper timeRowMapper;
	@Autowired
	private DoctorRowMapper doctorRowMapper;
	@Autowired
	private SubjectDoctorRowMapper subjectDoctorRowMapper;
	@Autowired
	private ReviewRowMapper reviewRowMapper;
	@Autowired
	private ReviewMemberNameRowMapper reviewMemberNameRowMapper;

	public List searchHospital(String keyword) {	
		String query = "select hospital_no, hospital_name, hospital_tel, hospital_postcode, hospital_addr_main, hospital_addr_sub, lat, lng,\r\n" + 
				"(select distinct\r\n" + 
				"    case\r\n" + 
				"    when to_char(sysdate,'d') in (select holiday from time_tbl where hospital_no=h.hospital_no)\r\n" + 
				"    then '진료종료'\r\n" + 
				"    when to_char(sysdate,'dy') in ('월','화','수','목','금')"+ 
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
				"where hospital_no in (select hospital_no from hospital_tbl where hospital_name||hospital_addr_main||hospital_addr_sub like '%'||?||'%')\r\n" + 
				"or hospital_no in (select hospital_no from doctor_tbl where subject_no in (select subject_no from subject_tbl where subject_name like '%'||?||'%'))\r\n" + 
				"or hospital_no in (select hospital_no from doctor_tbl where subject_no in (select subject_no from keyword_tbl where keyword like '%'||?||'%'))";
		Object[] params = {keyword,keyword,keyword};
		List hospitalList = jdbc.query(query, hospitalSearchRowMapper, params);
		return hospitalList;
	}

	public List searchSubjectList(int hospitalNo) {
		String query = "select * from subject_tbl where subject_no in (select subject_no from doctor_tbl where hospital_no=?)";
		Object[] params = {hospitalNo};
		List list = jdbc.query(query, subjectRowMapper, params);
		return list;
	}

	public List searchKeywordList(int hospitalNo) {
		String query = "select * from keyword_tbl where subject_no in(select subject_no from doctor_tbl where hospital_no=?)";
		Object[] params = {hospitalNo};
		List list = jdbc.query(query, keywordRowMapper, params);
		return list;
	}

	public Hospital searchHospitalDetail(int hospitalNo) {
		String query = "select hospital_no, hospital_name, hospital_intro, hospital_tel, hospital_postcode, hospital_addr_main, hospital_addr_sub, cost_one, cost_two, hospital_picture,\r\n" + 
				"nvl((select count(*) from review_tbl where reservation_no in(select reservation_no from reservation_tbl where hospital_no=h.hospital_no)),0) review_count,\r\n" +
				"nvl((select avg(review_rating) from review_tbl where reservation_no in(select reservation_no from reservation_tbl where hospital_no=h.hospital_no)),0) rating_avg,\r\n" + 
				"(select distinct\r\n" + 
				"    case\r\n" + 
				"    when to_char(sysdate,'d') in (select holiday from time_tbl where hospital_no=h.hospital_no)\r\n" + 
				"    then '진료종료'\r\n" + 
				"    when to_char(sysdate,'dy') in ('월','화','수','목','금')\r\n" + 
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
				"where hospital_no=?";
		Object[] params = {hospitalNo};
		List list = jdbc.query(query, hospitalDetailRowMapper, params);
		if(list.isEmpty()) {
			return null;
		}else {
			return (Hospital)list.get(0);			
		}
	}

	public Time searchHospitalTime(int hospitalNo) {
		String query = "select * from time_tbl where hospital_no=?";
		Object[] params = {hospitalNo};
		List list = jdbc.query(query, timeRowMapper, params);
		if(list.isEmpty()) {
			return null;
		}else {
			return (Time)list.get(0);
		}
	}

	public List searchSubjectDoctorList(int hospitalNo) {
		String query = "select doctor_no, hospital_no, subject_no, (select subject_name from subject_tbl where subject_no = d.subject_no) subject_name, doctor_picture, doctor_name, doctor_education, doctor_experience from doctor_tbl d where hospital_no=? order by 1";
		Object[] params = {hospitalNo};
		List list = jdbc.query(query, subjectDoctorRowMapper, params);
		return list;
	}

	public List searchReviewMemberNameList(int hospitalNo) {
		String query = "select review_no, reservation_no, member_no, (select member_name from member_tbl where member_no=r.member_no) member_name, review_title, review_content, review_date, review_img from review_tbl r where reservation_no in(select reservation_no from reservation_tbl where hospital_no=?)";
		Object[] params = {hospitalNo};
		List list = jdbc.query(query, reviewMemberNameRowMapper, params);
		return list;
	}
	
	public int selectBusinessAuthNo() {
		String query = "select max(businessauth_no) from businessauth_tbl";
		int businessAuthNo = jdbc.queryForObject(query, Integer.class);
		return businessAuthNo;
	}

	public int insertBusinessAuthFile(BusinessAuthFile businessAuthFile) {
		String query = "insert into businessauth_file_tbl values(businessauth_file_seq.nextval,?,?,?)";
		Object[] params = {businessAuthFile.getBusinessAuthNo(), businessAuthFile.getFilename(), businessAuthFile.getFilepath()};
		int result = jdbc.update(query, params);
		return result;
	}

	//사업자등록
	public int insertBusinessAuth(BusinessAuth ba) {
		String query = "insert into businessauth_tbl values(businessauth_seq.nextval, ?,?, to_char(sysdate,'yyyy-mm-dd'))";
		Object[] params = {ba.getMemberNo(), ba.getRepresentativeNo()};
		int result = jdbc.update(query, params);
		return result;
	}

	public int updateMemberStatus(int memberNo) {
		String query = "update member_tbl set member_status = 4 where member_no=?";
		Object[] params = {memberNo};
		int result = jdbc.update(query, params);
		return result;
	}

	public int insertHospitalEnroll(Hospital hospital) {
		String query = "INSERT INTO HOSPITAL_TBL VALUES(HOSPITAL_SEQ.nextval, ?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ?, ?)";
		Object[] params = {hospital.getMemberNo(), hospital.getHospitalName(), hospital.getHospitalIntro(), hospital.getLat(), hospital.getLng(), hospital.getHospitalTel(), hospital.getCostOne(), hospital.getCostTwo(), hospital.getHospitalPicture(), hospital.getHospitalPostcode(), hospital.getHospitalAddrMain(), hospital.getHospitalAddrSub()};
		int reuslt = jdbc.update(query, params);
		return reuslt;
	}


	public int insertHospitalTime(int hospitalNo, Time time) {
		String query = "INSERT INTO time_tbl VALUES(?, ?, ?, ?, ?)";
		Object[] params = {hospitalNo, time.getDayHour(), time.getWeekendHour(), time.getLunchHour(), time.getHoliday()};
		int reuslt = jdbc.update(query, params);
		return reuslt;
	}

	public int insertSubject(Subject subject) {
		String query = "insert into subject_tbl values(subject_seq.nextval, ?)";
		Object[] params = {subject.getSubjectName()};
		int result = jdbc.update(query, params);
		return result;
	}


	
	
	public List selectReviewList(int hospitalNo, int sortValue, int start, int end) {
		String query = "select * from (select rownum rnum, r.* from (select review_no, (select replace(member_name,substr(member_name,2,1),'*') from member_tbl where member_no=r.member_no) member_name, review_title, review_content, review_rating, review_date, review_img from review_tbl r where reservation_no in (select reservation_no from reservation_tbl where hospital_no=?) order by review_no desc)r) where rnum between ? and ?";
		Object[] params = {hospitalNo, start, end};
		List reviewList = jdbc.query(query, reviewRowMapper, params);
		return reviewList;
	}

	
	public List selectReviewList2(int hospitalNo, int sortValue, int start, int end) {
		String query = "select * from (select rownum rnum, r.* from (select review_no, (select replace(member_name,substr(member_name,2,1),'*') from member_tbl where member_no=r.member_no) member_name, review_title, review_content, review_rating, review_date, review_img from review_tbl r where reservation_no in (select reservation_no from reservation_tbl where hospital_no=?) order by review_rating desc, review_no desc)r) where rnum between ? and ?";
		Object[] params = {hospitalNo, start, end};
		List reviewList = jdbc.query(query, reviewRowMapper, params);
		return reviewList;
	}

	public int selectHospitalNo() {
		String query = "select max(hospital_no) from hospital_tbl";
		int hospitalNo = jdbc.queryForObject(query, Integer.class);
		return hospitalNo;
	}

	public int selectSubjectNo() {
		String query = "select max(subject_no) from subject_tbl";
		int subjectNo = jdbc.queryForObject(query, Integer.class);
		return subjectNo;
	}

	public int insertDoctor(int hospitalNo, Doctor doctor, int subjectNo) {
		String query = "insert into doctor_tbl values(doctor_seq.nextval, ?, ?, ?, ?, ?, ?)";
		Object[] params = {hospitalNo, subjectNo, doctor.getDoctorPicture(), doctor.getDoctorName(), doctor.getDoctorEducation(), doctor.getDoctorExperience()};
		int result = jdbc.update(query, params);
		return result;
	}

	public Hospital selectHospital(int memberNo) {
		String query = "select * from hospital_tbl where hospital_tbl.member_no = ?";
		Object[] params = {memberNo};
		List list = jdbc.query(query, hospitalRowMapper, params);
		return (Hospital)list.get(0);
	}

	public Hospital findHospitalInfo(int hospitalNo) {
		String query = "select * from hospital_tbl where hospital_no = ?";
		Object[] params = {hospitalNo};
		List list = jdbc.query(query, hospitalRowMapper, params);		
		return (Hospital)list.get(0);
	}

	//병원 테이블 Update
	public int updateHospital(Hospital hospital) {
		String query = "update hospital_tbl set hospital_intro=?, lat=?, lng=?, hospital_tel=?, cost_one=?, cost_two=?, hospital_postcode=?, hospital_addr_main=?, hospital_addr_sub=? where hospital_no = ?"; 
		Object[] params = {hospital.getHospitalIntro(), hospital.getLat(), hospital.getLng(), hospital.getHospitalTel(), hospital.getCostOne(), hospital.getCostTwo(), hospital.getHospitalPostcode(), hospital.getHospitalAddrMain(), hospital.getHospitalAddrSub(), hospital.getHospitalNo()}; 
		int result = jdbc.update(query, params);
		return result;
	}


	//시간 테이블 Update
	public int updateHospitalTime(int hospitalNo, Time time) {
		String query = "update time_tbl set day_hour=?, weekend_hour=?, lunch_hour=?, holiday=? where hospital_no=?";
		Object[] params = {time.getDayHour(), time.getWeekendHour(), time.getLunchHour(), time.getHoliday(), hospitalNo};
		int result = jdbc.update(query, params);
		return result;
	}




	public int updateDoctor(Doctor doctor) {
		System.out.println("다오:" + doctor);
        String query = "UPDATE doctor_tbl SET doctor_name=?, doctor_education=?, doctor_experience=? WHERE doctor_no=?";
        Object[] params = {doctor.getDoctorName(), doctor.getDoctorEducation(), doctor.getDoctorExperience(), doctor.getDoctorNo()};
        int result = jdbc.update(query, params);
	    return result;
	  
	}

	public int updateSubject(Doctor doctor) {
		String query = "UPDATE subject_tbl SET subject_name = ? WHERE subject_no =?";
        Object[] params = {doctor.getSubjectName(), doctor.getSubjectNo()};
        int result = jdbc.update(query, params);
	    return result;
	}

	public int updateDoctorPicture(Doctor doctor) {
		String query = "UPDATE doctor_tbl SET doctor_picture = ? WHERE doctor_no =?";
		System.out.println("다오 사진:" + doctor.getDoctorPicture());
        Object[] params = {doctor.getDoctorPicture(), doctor.getDoctorNo()};
        int result = jdbc.update(query, params);
	    return result;
	}




	

	
}
