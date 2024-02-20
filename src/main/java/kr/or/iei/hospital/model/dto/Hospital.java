package kr.or.iei.hospital.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Hospital {
	private int hospitalNo;
	private int memberNo;
	private String hospitalName;
	private String hospitalIntro;
	private String hospitalAddress;
	private String lat;
	private String lng;
	private String hospitalTel;
	private String costOne;
	private String costTwo;
	private String openStatus;
	private List subjectList;
	private List keywordList;
}