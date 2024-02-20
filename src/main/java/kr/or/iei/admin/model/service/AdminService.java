package kr.or.iei.admin.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.or.iei.admin.model.dao.AdminDao;
import kr.or.iei.admin.model.dto.NoticeListData;

@Service
public class AdminService {
	@Autowired
	private AdminDao adminDao;
	
	
	public NoticeListData selectAllNotice(int reqPage) {
		
		//페이지당 게시슬 갯수 10개? 좀 더 많이 보여줄까.....
		int numPerPage = 10;
		
		int end = reqPage*numPerPage;
		int start = end - numPerPage + 1;
		List list = adminDao.selectAllNotice(start,end);
		int totalCount = adminDao.selectAllBoardCount();
		
		
		int totalPage = 0;
		if(totalCount%numPerPage == 0) {
			totalPage = totalCount/numPerPage;
		}else {
			totalPage = totalCount/numPerPage+1;			
		}
		
		
		int pageNaviSize = 10;
		
		
		int pageNo =((reqPage -1)/pageNaviSize)*pageNaviSize + 1;
		
		String pageNavi = "<ul class='pagination box'>";
		if(pageNo !=1) {
			pageNavi += "<li>";
			pageNavi += "<a class='page-item' href='/admin/noticeList?reqPage="+ (pageNo-1) +"'>";
			pageNavi += "<span class='material-icons'>chevron_left</span>";
			pageNavi += "</a></li>";
		}
		
		for(int i = 0;i<pageNaviSize;i++) {
			if(pageNo == reqPage) {
				pageNavi += "<li>";
				pageNavi += "<a class='active' href='/admin/noticeList?reqPage="+ (pageNo) +"'>";
				pageNavi += pageNo;
				pageNavi += "</a></li>";
			}else {				
				pageNavi += "<li>";
				pageNavi += "<a href='/admin/noticeList?reqPage="+ (pageNo) +"'>";
				pageNavi += pageNo;
				pageNavi += "</a></li>";
			}
			pageNo++;
			
			if(pageNo > totalPage) {
				break;
			}
		}
		
		
		if(pageNo <= totalPage) {
			pageNavi += "<li>";
			pageNavi += "<a class='page-item' href='/admin/noticeList?reqPage="+ (pageNo) +"'>";
			pageNavi += "<span class='material-icons'>chevron_right</span>";
			pageNavi += "</a></li>";
		}
		
		pageNavi += "</ul>";
		
		NoticeListData nld = new NoticeListData(list, pageNavi);
		return nld;
	}

}