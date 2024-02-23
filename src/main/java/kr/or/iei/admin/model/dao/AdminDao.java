package kr.or.iei.admin.model.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import kr.or.iei.admin.model.dto.AdminBusinessAuthRowMapper;
import kr.or.iei.admin.model.dto.MemberReport;
import kr.or.iei.admin.model.dto.MemberReportRowMapper;
import kr.or.iei.admin.model.dto.Notice;
import kr.or.iei.admin.model.dto.NoticeListData;
import kr.or.iei.admin.model.dto.NoticeRowMapper;

@Repository
public class AdminDao {
	@Autowired
	private JdbcTemplate jdbc;
	
	@Autowired
	private NoticeRowMapper noticeRowMapper;
	
	@Autowired
	private MemberReportRowMapper memberReportRowMapper;
	
	@Autowired
	private AdminBusinessAuthRowMapper abaRowMapper;

	public List selectAllNotice(int start, int end) {
		String query = "SELECT  * FROM (SELECT ROWNUM AS RNUM, N.* FROM (SELECT NOTICE_NO, MEMBER_TBL.MEMBER_NO, NOTICE_TITLE, NOTICE_CONTENT, READ_COUNT, REQ_DATE, MEMBER_NAME FROM MEMBER_TBL RIGHT OUTER JOIN NOTICE_TBL ON MEMBER_TBL.MEMBER_NO = NOTICE_TBL.MEMBER_NO ORDER BY NOTICE_NO DESC)N) WHERE RNUM BETWEEN ? AND ?";
		Object[] params = {start, end};
		List list = jdbc.query(query, noticeRowMapper, params);
		return list;
	}

	public int selectAllBoardCount() {
		String query = "select count(*) from notice_tbl";
		int totalCount = jdbc.queryForObject(query, Integer.class);
		return totalCount;
	}

	public List selectSearchTitle(int start, int end, String keyword) {
		String query = "SELECT  * FROM (SELECT ROWNUM AS RNUM, N.* FROM (SELECT NOTICE_NO, MEMBER_TBL.MEMBER_NO, NOTICE_TITLE, NOTICE_CONTENT, READ_COUNT, REQ_DATE, MEMBER_NAME FROM MEMBER_TBL RIGHT OUTER JOIN NOTICE_TBL ON MEMBER_TBL.MEMBER_NO = NOTICE_TBL.MEMBER_NO where NOTICE_TITLE like '%'||?||'%' ORDER BY NOTICE_NO DESC)N) WHERE RNUM BETWEEN ? AND ?";
		Object[] params = {keyword, start, end};
		List list = jdbc.query(query, noticeRowMapper, params);
		return list;
	}

	public List selectSearchWriter(int start, int end, String keyword) {
		String query = "SELECT  * FROM (SELECT ROWNUM AS RNUM, N.* FROM (SELECT NOTICE_NO, MEMBER_TBL.MEMBER_NO, NOTICE_TITLE, NOTICE_CONTENT, READ_COUNT, REQ_DATE, MEMBER_NAME FROM MEMBER_TBL RIGHT OUTER JOIN NOTICE_TBL ON MEMBER_TBL.MEMBER_NO = NOTICE_TBL.MEMBER_NO where MEMBER_NAME like '%'||?||'%' ORDER BY NOTICE_NO DESC)N) WHERE RNUM BETWEEN ? AND ?";
		Object[] params = {keyword, start, end};
		List list = jdbc.query(query, noticeRowMapper, params);
		return list;
	}

	public int titleTotalCount(String keyword) {
		String query = "SELECT COUNT(*) FROM NOTICE_TBL WHERE NOTICE_TITLE LIKE '%'||?||'%'";
		Object[] params = {keyword};
		int totalCount = jdbc.queryForObject(query, Integer.class,params);
		return totalCount;
	}

	public int writerTotalCount(String keyword) {
		String query = "SELECT COUNT(*) FROM (SELECT NOTICE_NO, MEMBER_TBL.MEMBER_NO, NOTICE_TITLE, NOTICE_CONTENT, READ_COUNT, REQ_DATE, MEMBER_NAME FROM MEMBER_TBL RIGHT OUTER JOIN NOTICE_TBL ON MEMBER_TBL.MEMBER_NO = NOTICE_TBL.MEMBER_NO) WHERE MEMBER_NAME LIKE '%'||?||'%'";
		Object[] params = {keyword};
		int totalCount = jdbc.queryForObject(query, Integer.class,params);
		return totalCount;
	}

	public int insertNotice(Notice n) {
		String query = "insert into notice_tbl values (notice_seq.nextval,?,?,?,0,to_char(sysdate,'yyyy-mm-dd'))";
		Object[] params = {n.getMemberNo(), n.getNoticeTitle(),n.getNoticeContent()};
		int result = jdbc.update(query,params);
		return result;
	}

	public Notice searchNoticeDetail(int noticeNo) {
		String query = "SELECT NOTICE_NO, MEMBER_TBL.MEMBER_NO, NOTICE_TITLE, NOTICE_CONTENT, READ_COUNT, REQ_DATE, MEMBER_NAME FROM MEMBER_TBL RIGHT OUTER JOIN NOTICE_TBL ON MEMBER_TBL.MEMBER_NO = NOTICE_TBL.MEMBER_NO where notice_no=?";
		Object[] params = {noticeNo};
		Notice n = jdbc.queryForObject(query, noticeRowMapper, params);
		return n;
	}

	public int deleteNotice(int noticeNo) {
		String query = "delete from notice_tbl where notice_no =?";
		Object[] params = {noticeNo};
		int result = jdbc.update(query,params);
		return result;
	}

	public List selectAllMemberReport(int start, int end) {
		String query = "SELECT  * FROM (SELECT ROWNUM AS RNUM, N.* FROM (SELECT report_NO, MEMBER_TBL.MEMBER_NO, board_no, report_title, report_content, report_status, MEMBER_NAME FROM MEMBER_TBL RIGHT OUTER JOIN Member_Report_TBL ON MEMBER_TBL.MEMBER_NO = Member_Report_TBL.MEMBER_NO ORDER BY Report_NO DESC)N) WHERE RNUM BETWEEN ? AND ?";
		Object[] params = {start, end};
		List list = jdbc.query(query, memberReportRowMapper, params);
		return list;
	}

	public int selectAllMemberReportCount() {
		String query = "select count(*) from Member_report_tbl";
		int totalCount = jdbc.queryForObject(query, Integer.class);
		return totalCount;
	}

	public int deleteChk(int reportNo) {
		String query = "delete from member_report_tbl where report_no = ?";
		Object[] params = {reportNo};
		int result = jdbc.update(query,params);
		return result;
	}

	public MemberReport searchReportDetail(int reportNo) {
		String query = "SELECT report_NO, MEMBER_TBL.MEMBER_NO, board_no, report_title, report_content, report_status, MEMBER_NAME FROM MEMBER_TBL RIGHT OUTER JOIN Member_Report_TBL ON MEMBER_TBL.MEMBER_NO = Member_Report_TBL.MEMBER_NO WHERE report_no= ?";
		Object[] params = {reportNo};
		MemberReport mr = jdbc.queryForObject(query, memberReportRowMapper,params);
		return mr;
	}

	public int deleteReport(int reportNo) {
		String query = "delete from member_report_tbl where report_no=?";
		Object[] params = {reportNo};
		int result = jdbc.update(query,params);
		return result;
	}

	public int confirmReport(int reportNo) {
		String query = "update member_report_tbl set report_status = 1 where report_no=?";
		Object[] params = {reportNo};
		int result = jdbc.update(query,params);
		return result;
	}

	public List selectAllBusinessAuth(int start, int end) {
		String query = "SELECT * FROM (SELECT ROWNUM AS RNUM, N.* FROM(SELECT MEMBER_NAME, J_TBL.BUSINESSAUTH_NO, MEMBER_EMAIL, MEMBER_PHONE, FILENAME, FILEPATH, REPRESENTATIVE_NO, REG_DATE FROM BUSINESSAUTH_FILE_TBL RIGHT OUTER JOIN (SELECT * FROM MEMBER_TBL RIGHT OUTER JOIN BUSINESSAUTH_TBL ON MEMBER_TBL.MEMBER_NO = BUSINESSAUTH_TBL.MEMBER_NO WHERE MEMBER_STATUS = 4) J_TBL ON J_TBL.BUSINESSAUTH_NO = BUSINESSAUTH_FILE_TBL.BUSINESSAUTH_NO ORDER BY BUSINESSAUTH_NO DESC)N)WHERE RNUM BETWEEN ? AND ?";
		Object[] params = {start,end};
		List list = jdbc.query(query, abaRowMapper, params);
		return list;
	}

	public int selectAllBusinessAuthCount() {
		String query = "SELECT count(*) FROM (BUSINESSAUTH_FILE_TBL RIGHT OUTER JOIN (SELECT * FROM MEMBER_TBL RIGHT OUTER JOIN BUSINESSAUTH_TBL ON MEMBER_TBL.MEMBER_NO = BUSINESSAUTH_TBL.MEMBER_NO WHERE MEMBER_STATUS = 4) J_TBL ON J_TBL.BUSINESSAUTH_NO = BUSINESSAUTH_FILE_TBL.BUSINESSAUTH_NO)";
		int totalCount = jdbc.queryForObject(query, Integer.class);
		return totalCount;
	}
}
