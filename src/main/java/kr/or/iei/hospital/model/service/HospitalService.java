package kr.or.iei.hospital.model.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.iei.hospital.model.dao.HospitalDao;
import kr.or.iei.hospital.model.dto.BusinessAuth;
import kr.or.iei.hospital.model.dto.BusinessAuthFile;
import kr.or.iei.hospital.model.dto.Doctor;
import kr.or.iei.hospital.model.dto.Hospital;
import kr.or.iei.hospital.model.dto.Subject;
import kr.or.iei.hospital.model.dto.Time;
import kr.or.iei.reservation.model.dao.ReservationDao;

@Service
public class HospitalService {

	@Autowired
	private HospitalDao hospitalDao;

	@Autowired
	private ReservationDao reservationDao;
	
	public List searchHospital(String keyword) {
		List hospitalList = hospitalDao.searchHospital(keyword);
		if(!hospitalList.isEmpty()) {
			for(Object o : hospitalList) {
				Hospital h = (Hospital) o;
				List subjectList = hospitalDao.searchSubjectNameList(h.getHospitalNo());
				List keywordList = hospitalDao.searchKeywordList(h.getHospitalNo());
				h.setSubjectList(subjectList);
				h.setKeywordList(keywordList);
			}
		}
		return hospitalList;
	}

	
	public Hospital searchHospitalDetail(int hospitalNo) {
		Hospital h = hospitalDao.searchHospitalDetail(hospitalNo);
		if(h != null) {
			List subjectList = hospitalDao.searchSubjectNameList(hospitalNo);
			List keywordList = hospitalDao.searchKeywordList(hospitalNo);
			Time time = hospitalDao.searchHospitalTime(hospitalNo);
			List doctorList = hospitalDao.searchSubjectDoctorList(hospitalNo);
			h.setSubjectList(subjectList);
			h.setKeywordList(keywordList);
			h.setTime(time);
			h.setDoctorList(doctorList);
		}
		return h;
	}

	@Transactional
	public int insertBusinessAuth(BusinessAuth ba, List<BusinessAuthFile> fileList) {
		// 1. BusinessAuth 테이블 insert
		// file은 insert안할 것이므로 ba만 보내면 됨
		int result = hospitalDao.insertBusinessAuth(ba); //DTO에 FileList 추가하여 통으로 전달
		if (result > 0) {
			//  insert 한  BusinessAuth 테이블의 데이터의 businessauth_no가 필요 -> 외래키 사용
			int businessAuthNo = hospitalDao.selectBusinessAuthNo();
			
			// 2. BusinessAuth_file테이블에 insert
			for (BusinessAuthFile businessAuthFile : fileList) {
				businessAuthFile.setBusinessAuthNo(businessAuthNo);
				result += hospitalDao.insertBusinessAuthFile(businessAuthFile);
			}
			result += hospitalDao.updateMemberStatus(ba.getMemberNo()); 
		}
		return result;
	}

	@Transactional
	public int insertHospitalEnroll(Hospital hospital, Time time, List<Doctor> doctorList, List<Subject> subjectList) {
		//1. hospital 테이블 insert
		int result = hospitalDao.insertHospitalEnroll(hospital);
		// 방금 insert 한  테이블의 데이터의 _no가 필요 
		int hospitalNo = hospitalDao.selectHospitalNo();
		
		
		
		//2. subject 테이블 insert
		for(int i = 0; i < subjectList.size(); i++) {
			  result += hospitalDao.insertSubject(subjectList.get(i));
			  int subjectNo = hospitalDao.selectSubjectNo();
		//3. doctor 테이블 insert (*사진파일명 포함)
			  result += hospitalDao.insertDoctor(hospitalNo, doctorList.get(i), subjectNo);
	      }
		
		//4. time 테이블 insert
		result += hospitalDao.insertHospitalTime(hospitalNo, time);
		
	return result;
	}
	
	
	public List selectReviewList(int hospitalNo, int sortValue, int start, int amount) {
		int end = start+amount-1;
		if(sortValue == 1) {
			List reviewList = hospitalDao.selectReviewList(hospitalNo, sortValue, start, end);			
			return reviewList;
		}else {
			List reviewList = hospitalDao.selectReviewList2(hospitalNo, sortValue, start, end);
			return reviewList;
		}
	}

	public Hospital selectHospitalInfo(int hospitalNo) {
		Hospital h = new Hospital();
		List subjectList = hospitalDao.searchSubjectList(hospitalNo);
		Time time = hospitalDao.searchHospitalTime(hospitalNo);
		List doctorList = hospitalDao.searchSubjectDoctorList(hospitalNo);
		h.setSubjectList(subjectList);
		h.setTime(time);
		h.setDoctorList(doctorList);
		return h;
	}
	
	public Time searchHospitalTime(int hospitalNo) {
		Time time = hospitalDao.searchHospitalTime(hospitalNo);
		return time;
	}

	public Hospital selectHospital(int memberNo) {
		Hospital hospital = new Hospital();
		//1. 병원변호 찾기
		hospital = hospitalDao.selectHospital(memberNo);
		System.out.println("서비스 병원자료:" + hospital);

		//2. 병원번호로 병원/의사/시간/진료과목 조회
		List subjectList = hospitalDao.searchSubjectList(hospital.getHospitalNo());

		Time time = hospitalDao.searchHospitalTime(hospital.getHospitalNo());
		System.out.println("서비스 시간: "+ time);
		List doctorList = hospitalDao.searchSubjectDoctorList(hospital.getHospitalNo());
		System.out.println("의사 리스트: "+ doctorList);
		hospital.setSubjectList(subjectList);
		hospital.setTime(time);
		hospital.setDoctorList(doctorList);
		System.out.println("서비스:" + hospital);
		return hospital;

	}


	public Hospital findHospitalInfo(int hospitalNo) {
		Hospital hospital = hospitalDao.findHospitalInfo(hospitalNo);
		String[] tel = hospital.getHospitalTel().split("-",2);
		String telFirst = tel[0];
		String telLast = tel[1];
		hospital.setTelFirst(telFirst);
		hospital.setTelLast(telLast);
		List subjectList = hospitalDao.searchSubjectList(hospitalNo);
		Time time = hospitalDao.searchHospitalTime(hospitalNo);
		String[] dayTime = time.getDayHour().split("~");
		String dayStartTime = dayTime[0];
		String dayEndTime = dayTime[1];
		String[] weekendTime = time.getWeekendHour().split("~");
		String weekendStartTime = weekendTime[0];
		String weekendEndTime = weekendTime[1];
		String[] lunchTime = time.getLunchHour().split("~");
		String lunchStartTime = lunchTime[0];
		String lunchEndTime = lunchTime[1];
		time.setDayStartTime(dayStartTime);
		time.setDayEndTime(dayEndTime);
		time.setWeekendStartTime(weekendStartTime);
		time.setWeekendEndTime(weekendEndTime);
		time.setLunchStartTime(lunchStartTime);
		time.setLunchEndTime(lunchEndTime);
		hospital.setTime(time);
		
		
		List doctorList = hospitalDao.searchSubjectDoctorList(hospitalNo);
		hospital.setSubjectList(subjectList);
		hospital.setDoctorList(doctorList);
		return hospital;
	}

	@Transactional
	public int updateHospital(Hospital hospital, Time time) {

		//병원 테이블 Update
		int result = hospitalDao.updateHospital(hospital);

		//시간 테이블 Update
		result += hospitalDao.updateHospitalTime(hospital.getHospitalNo(), time);

		return result;
	}


	@Transactional
	public int updateDoctorSubject(List<Doctor> doctorList) {
		//진료과목수정: 변경된 진료과목명(닥터vo) + 변경된 진료과목번호(닥터vo)
		int result = 0;
		for(int i=0; i<doctorList.size(); i++) {
			result = hospitalDao.updateDoctor(doctorList.get(i));
			result += hospitalDao.updateSubject(doctorList.get(i));
		}		
		
		return result;
	}

	@Transactional
	public int updateDoctorPicture(List<Doctor> doctorPictureList) {
		int result = 0;
		for(int i=0; i<doctorPictureList.size(); i++) {
			System.out.println(doctorPictureList.get(i));
				result += hospitalDao.updateDoctorPicture(doctorPictureList.get(i));
		}
		return result;
	}


	public int myReviewTotalCount(int memberNo) {
		int myResTotalCount = hospitalDao.myReviewTotalCount(memberNo);
		return myResTotalCount;
	}


	public List selectMyHospitalReview(int memberNo, int start, int amount) {
		int end = start+amount-1;
		List myHistoryList = hospitalDao.selectMyHospitalReview(memberNo, start, end);
		return myHistoryList;
	}


	
	public int hospitalMemberReport(String goodByeReason, int memberNo, int hospitalNo, int reviewNo) {
		int hospitalMemberReport = hospitalDao.hospitalMemberReport(goodByeReason, memberNo, hospitalNo, reviewNo);
		return hospitalMemberReport ;
	}


	public int checkReport(int reviewNo) {
		int checkRepo = hospitalDao.checkReport(reviewNo);
		return checkRepo;
	}





	public List selectMyReviewHistory(int memberNo) {
	    System.out.println(memberNo);
	    List myReviewList = hospitalDao.selectMyReviewHistory(memberNo);
		System.out.println(myReviewList);
	    return myReviewList;
	    
	}


	public List selectReservationInfo(int memberNo) {
		List reservationInfo = hospitalDao.selectReservationInfo(memberNo);
		System.out.println(reservationInfo);
		return reservationInfo;
	}


	
	public int selectMyResCount(int memberNo, int hospitalNo) {
		int myResCount = hospitalDao.selectMyResCount(memberNo, hospitalNo);
		return myResCount;
	}

	public int selectMyReviewCount(int memberNo, int hospitalNo) {
		int myReviewCount = hospitalDao.selectMyReviewCount(memberNo, hospitalNo);
		return myReviewCount;
	}


	public List searchReservation(int hospitalNo, String selectedDate, int subjectNo) {
		List timeResInfo = hospitalDao.searchReservation(hospitalNo, selectedDate, subjectNo);
		return timeResInfo;
	}
	
}

