package kr.or.iei.member.model.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.or.iei.member.model.dao.MemberDao;
import kr.or.iei.member.model.dto.Member;

@Service
public class MemberService {
	
	@Autowired
	private MemberDao memberDao;

	public Member signIn(String memberEmail, String memberPassword) {
		
		
		
		Member member = memberDao.signIn(memberEmail,memberPassword);
		
		
		
		return member;
	}

	@Transactional
	public int signUp(Member member) {
		
		System.out.println(member);
		
		if(member.getMemberType()==2) {
			
			int memberStatus = 2;
			
			member.setMemberStatus(memberStatus);
		}else if(member.getMemberType()==1) {
			
			int memberStatus = 1 ;
			
			member.setMemberStatus(memberStatus);
			
		}
		
		
		int result = memberDao.signUp(member);
		
		
		return result;
		
	}
	
	//이메일 중복체크
	public int checkEmail(String memberEmail) {
		
		int cnt = memberDao.checkEmail(memberEmail);
		
		return cnt;
		
	}

	
	//회탈
	@Transactional
	public int confirmDelete(String memberPassword, String memberEmail) {
		int cnt = memberDao.confirmDelete(memberPassword, memberEmail);
		
		return cnt;
	}


	

	public int checkPassword(String memberPassword, String memberEmail) {
		int cnt = memberDao.checkPassword(memberPassword,memberEmail);
		
		return cnt;
	}
	//회원정보 수정
	@Transactional
	public int updateInfo(Member m) {
		
		int result = memberDao.updateInfo(m);
		
		return result;
	}
	
	
	//내 자녀 추가
	@Transactional
	public int childAdd(Integer memberNo, String childName, String childRrn) {
		
		int result = memberDao.childAdd(memberNo,childName,childRrn);
		
		return result;

	}
		//내 자녀 보기
	public List selectMyChildInfo(int memberNo) {
		List child = memberDao.selectMyChildInfo(memberNo);
		return child;

	}}
