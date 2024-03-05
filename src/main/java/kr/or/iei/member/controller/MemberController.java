package kr.or.iei.member.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import kr.or.iei.EmailSender;
import kr.or.iei.FileUtils;
import kr.or.iei.admin.model.dto.Review;
import kr.or.iei.hospital.model.dto.Hospital;
import kr.or.iei.member.model.dto.Child;
import kr.or.iei.member.model.dto.Member;
import kr.or.iei.member.model.service.MemberService;

import kr.or.iei.reservation.model.dto.Reservation;

import kr.or.iei.reservation.model.dto.ReservationDetail;
import kr.or.iei.reservation.model.service.ReservationService;

import lombok.Getter;


@Controller
@RequestMapping(value="/member")
public class MemberController {

	@Autowired
	private MemberService memberService;
	
	@Autowired
	private EmailSender emailSender;
	
	@Autowired
	private ReservationService reservationService;
	
	@Autowired
	private FileUtils fileUtils;
	
	@Value("${file.root}")
	private String root;
	
	//회원가입 페이지
	@GetMapping(value="/signUpFrm")
	public String signUpFrm() {
	
		
		return "member/signUpFrm";
		
	}
	
	//로그인 페이지
	@GetMapping(value="/signInFrm")
	public String signInFrm() {
		
		
		return "member/signInFrm";
		
	}
	
	//로그인 실패 페이지
	@GetMapping(value="/signInFail")
	public String signInFail() {
		
		return "member/signInFail";
	}
	
	//로그인 	
	@PostMapping(value="/signIn")
	public String signIn(String memberEmail, String memberPassword, HttpSession session, Model model) {
		
		
		Member member = memberService.signIn(memberEmail,memberPassword);
		
		if(member == null) {
			
			return "member/signInFail";
		}else {
			
			if(member.getMemberStatus()==2) {
				
				
				session.setAttribute("member", member);
				
				
				return "hospital/businessAuth";
				
			}
			
			session.setAttribute("memberNo", member.getMemberNo());
			session.setAttribute("member", member);
			
			return "redirect:/";
			
		}
		
	}
	
	//이메일 인증
	@ResponseBody
	@PostMapping(value="/sendCode")
	public String sendCode(String memberEmail) {
		
		String authCode = emailSender.sendCode(memberEmail);
		return authCode;
		
	}
		

	
	//회원가입 실패 페이지
	@GetMapping(value="/failSignUp")
	public String failSignUp() {
		
		return "member/failSignUp";
		
		
	}
	
	
	//회원가입
	@PostMapping(value="/signUp")
	public String signUp(Member member, String postcode, String address, String detailAddress) {
		
		String memberAddress = postcode+" "+address+" "+detailAddress;
		
		
		member.setMemberAddress(memberAddress);
		
		
		int	result = memberService.signUp(member);
			
		
				
		
		if(result > 0 ) {
			
			return "redirect:/member/signInFrm";
			
		}else {
			
			return "member/failSignUp";
		}
	}
	
	//로그아웃
	@GetMapping(value="/logout")
	public String logout(HttpSession session) {
		
		session.invalidate();
		
				
		
		return "redirect:/";
	}
	
	//마이페이지
	@GetMapping(value="/myInfo")
	public String myPage() {
		
		return "member/myInfo";
		
		
	}
	
	//회원탈퇴 화면
	@GetMapping(value="/memberDelete")
	public String delete(@SessionAttribute(required = false) Member member) {
		
		
		
		return "member/memberDelete";
	}
	
	
	
	//회원탈퇴 확인
	@GetMapping(value="/confirmDelete")
	public String confirmDelete() {
		
		return "member/confirmDelete";
		
		
	}
	
	//이메일 중복 체크
	@ResponseBody
	@PostMapping(value="/emailChk")
	public int emailChk(@RequestParam("memberEmail") String memberEmail ) {
		
		int cnt = memberService.checkEmail(memberEmail);
		
		return cnt;
		
		
	}
	
	
	//비밀번호 중복 체크
	@ResponseBody
	@PostMapping(value="passwordChk")
	public int passwordChk(@RequestParam("memberPassword") String memberPassword, @SessionAttribute(required = false) Member member) {
		
		String memberEmail = member.getMemberEmail();
		
		int cnt = memberService.checkPassword(memberPassword, memberEmail);
		
		return cnt;
		
	}
	
	
	
	//서비스 이용 약관
	@GetMapping(value="/agreeService")
	public String agreeService() {
		
		
		return "/member/agreeService";
		
	}
	
	
	//위치 이용 약관
	@GetMapping(value="/agreeLocation")
	public String agreeLocation() {
		
		return "/member/agreeLocation";
		
	}
	
	//나이 이용 약관
	@GetMapping(value="/agreeAge")
	public String agreeAge() {
		
		return "/member/agreeAge";
	}
	
	
	//회원탈퇴 실패
	@GetMapping(value="/failDeleteMember")
	public String failDeleteMember() {
		
		return "/member/failDeleteMember";
		
	}
	
	
	//회탈
	@ResponseBody
	@PostMapping(value="/delete")
	public int confirmDelete(@SessionAttribute(required = false) Member member, Member m,HttpSession session) {
		
		String memberEmail = member.getMemberEmail();
		String memberPassword = member.getMemberPassword();
		
		
		int cnt = memberService.confirmDelete(memberEmail,memberPassword);
		
		if(session != null) {
			session.invalidate();
			
		}
			
			return cnt;
	
	}
	
	
	//이메일 찾기
	@GetMapping(value="/findEmail")
	public String findEmail() {
		
		return "/member/findEmail";
		
	}
	
	//비밀번호 찾기
	@GetMapping(value="/findPassword")
	public String findPassword() {
		
		
		
		return "/member/findPassword";
	}
	
	
	//회원 정보 수정 frm
	@PostMapping(value="/updateMyInfo")
	public String updateMyInfo() {
		
		
		
		return "/member/updateMyInfo";
	}
	
	
	//회원 정보 수정
	@PostMapping(value="/updateMember")
	public String updateMember(HttpSession session, Member m) {
		
		int memberNo = (int)session.getAttribute("memberNo");
		
		int result = memberService.updateInfo(memberNo,m);
		
		if(result > 0) {
			 Member member = (Member) session.getAttribute("member");
		        
		        
		        member.setMemberAddress(m.getMemberAddress());
		        member.setMemberPhone(m.getMemberPhone());
		        member.setMemberPassword(m.getMemberPassword());
		        
		        
		        session.setAttribute("member", member);
		        
		        return "/member/myInfo";
		        
		}else {
			
			return "redirect:/";
		}
		
	}
	
	
	//내 자녀 보기
	@GetMapping(value="/myChildren")
	public String myChildren(Child Child, Model model, HttpSession session) {
		
		int memberNo = (int)session.getAttribute("memberNo");
		
		List child = memberService.selectMyChildInfo(memberNo);
		
		
		System.out.println(child);
		
		
		model.addAttribute("child", child);
		
		return "/member/myChildren";
		
	}
	
	
	//내 자녀 추가
	@PostMapping(value="/myChildAdd")
	public String myChildAdd(HttpSession session, @RequestParam("childName") String childName, @RequestParam("childRrn") String childRrn) {
		
		Integer memberNo =(Integer)session.getAttribute("memberNo");
		
		int result = memberService.childAdd(memberNo,childName,childRrn);
		
		System.out.println(memberNo);
		
		if(result > 0) {
			
			
			return "/member/myChildAddFrm";
		}else {
			
			return "/member/failAdd";
		}
		
		
		
		
	}
	
	
	// 내 자녀 추가 frm
	@GetMapping(value="/myChildAddFrm")
	public String myChildAddFrm() {
		
		
		
		return "/member/myChildAddFrm";
	}
	
	
	
	
	//내 진료내역 보기
	@GetMapping(value="/myMedicalHistory")
	public String myMedicalHistory(@SessionAttribute(required=false) Member member, Model model) {
		int memberNo = member.getMemberNo();
		int totalCount = reservationService.myResTotalCount(memberNo);
		model.addAttribute("totalCount", totalCount);
		return "member/myMedicalHistory";
	}
	


	
	//내 자녀 삭제
	@ResponseBody
	@PostMapping(value="/deleteChild")
	public int deleteChild(int childNo,HttpSession session) {
		
		int memberNo = (int)session.getAttribute("memberNo");
		
		int cnt = memberService.deleteChild(childNo,memberNo);
		
		System.out.println(childNo);
		
		return cnt;
		
		
	}
	
	
	
	//나의 리뷰 보기
	@GetMapping(value="/myReview")
	public String myReview(HttpSession session, Review review ,Model model) {
		
		
		
		int memberNo = (int)session.getAttribute("memberNo");
		
		List list = memberService.reviewList(memberNo);
		
		
		model.addAttribute("list",list);
		
		System.out.println(list);
		return "/member/myReview";
		
		
	}
	
	
	//나의 리뷰 작성(예약 번호 가져오기)
	@GetMapping(value="myReviewFrm")
	public String myReviewFrm(int reservationNo, Model model, HttpSession session) {
		
		
		int memberNo = (int)session.getAttribute("memberNo");
		
		int hospitalNo = memberService.hospitalNo(reservationNo); 
		
		
		
		model.addAttribute("memberNo",memberNo);
		model.addAttribute("hospitalNo",hospitalNo);
		model.addAttribute("reservationNo",reservationNo);
		
		
		return "/member/myReviewFrm";
		
		
	}
	
	// 나의 리뷰 작성 전송
	@PostMapping(value="/submitReview")
	public String submitReview(Review review,MultipartFile imageFile,Model model,HttpSession session) {
		
		
		System.out.println(review);
		
		
		// 파일 저장 경로 설정
	    String savepath = root + "/reservation/review/";

	    
	    
	    String filepath = fileUtils.hyokyungUpLoad(savepath, imageFile);
	    
	    if(filepath != null) {
	    	
	    	review.setReviewImg(filepath);
	    
	    }else {
	    	review.setReviewImg(null);
	    	
	    }
	    
	    



	    int result = memberService.submit(review);


	    
	    return "/member/myReviewFrm";
	}

	
	
	
	@ResponseBody
	@PostMapping(value="/reviewDel")
	public int reviewDel(int reviewNo, int memberNo ) {
		
		
		
		
		
		int result = memberService.reviewDel(memberNo, reviewNo);
		
		System.out.println(memberNo);
		System.out.println(reviewNo);
		
		return result;
		
		
		
	}
	
	@GetMapping(value="/reportHospitalFrm")
	public String reportHospitalFrm(int reservationNo, Model model) {
		model.addAttribute("reservationNo", reservationNo);
		return "member/reportHospitalFrm";
	}
}







