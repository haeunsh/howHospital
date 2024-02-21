package kr.or.iei.hospital.controller;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import kr.or.iei.FileUtils;
import kr.or.iei.hospital.model.dto.BusinessAuth;
import kr.or.iei.hospital.model.dto.BusinessAuthFile;
import kr.or.iei.hospital.model.service.DoctorService;
import kr.or.iei.member.model.dto.Member;
import kr.or.iei.member.model.service.MemberService;
import kr.or.iei.reservation.model.dto.H_Reservation;
import kr.or.iei.reservation.model.dto.ReservationDetail;
import kr.or.iei.reservation.model.service.ReservationDetailService;
import kr.or.iei.reservation.model.service.ReservationService;
import kr.or.iei.hospital.model.service.HospitalService;

@Controller
@RequestMapping(value="/hospital")
public class HospitalController {
	@Autowired
	private HospitalService hospitalService;
	@Autowired
	private ReservationService reservationService;
	@Autowired
	private MemberService memberService;
	@Autowired
	private DoctorService doctorService;
	@Autowired
	private ReservationDetailService reservationDetailService;
	
	@Value("${file.root}")
	private String root; // 자바 전체에서 쓸수있는 변수, application.properties에 선언되어있는 값을 문자열로 가져옴.

	@Autowired
	private FileUtils fileUtils;
	
	@GetMapping(value="/myHospitalMain")
	public String myHospitalMain() {
		return "hospital/myHospitalMain";
	}
	
	
	@PostMapping(value="/businessAuthEnroll")
	public String businessAuthEnroll(BusinessAuth ba, MultipartFile[] upfile, HttpSession session, Model model) {
		Member member = (Member)session.getAttribute("member");
		List<BusinessAuthFile> fileList = new ArrayList<BusinessAuthFile>();
		if (!upfile[0].isEmpty()) {
			String savepath = root + "/notice/";
			for (MultipartFile file : upfile) {
				// 업로드한 파일명을 추출
				String filename = file.getOriginalFilename();
				String filepath = fileUtils.upload(savepath, file);
				BusinessAuthFile businessAuthFile = new BusinessAuthFile();
				businessAuthFile.setFilename(filename);
				businessAuthFile.setFilepath(filepath);
				fileList.add(businessAuthFile);
			}
		}
		// ba : businessAuthNo, memberNo
		// fileList : (BusinessAuthFile) x 첨부파일갯수(2개)
		// 총 3차례 insert

		int result = hospitalService.insertBusinessAuth(ba, fileList);
		
		
		// insert 성공은 Notice 테이블 결과(1) + 파일 테이블 결과(파일갯수)
		if (result == (fileList.size() + 1)) { // notice 테이블 1 포함
			model.addAttribute("title", "성공");
			model.addAttribute("msg", "공지사항 작성에 성공했습니다.");
			model.addAttribute("icon", "success");
		} else {
			model.addAttribute("title", "실패");
			model.addAttribute("msg", "공지사항 작성에 실패했습니다.");
			model.addAttribute("icon", "error");
		}
		model.addAttribute("loc", "/");
		return "hospital/msg";
	}
		

	
	
	@GetMapping(value="/businessAuth")
	public String businessAuth(Model model, HttpSession session) {
		
		return "hospital/businessAuth";
	}
	
	
	@GetMapping("/myHospitalReservation")
	public String myHospitalReservation(int reqPage, Model model) {
		//병원 예약 조회해오기
		List list = reservationService.selectReservation(reqPage);
		model.addAttribute("reservation", list);
		return "hospital/myHospitalReservationList";
	}

	@ResponseBody
	@GetMapping("/changeReservationType")
	public int changeReservationType(int selectValue, int reservationNo) {
		//병원 예약 업데이트
		int result = reservationService.updateReservation(selectValue, reservationNo);
		if(result > 0) {
			return 1;
		}else {
			return 0;
		}
	}

	
}



