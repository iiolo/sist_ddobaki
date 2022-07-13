package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.service.BoardService;
import com.example.demo.service.PlaceService;
import com.example.demo.service.ReplyService;
import com.example.demo.service.UserInfoService;
import com.example.demo.vo.Board;
import com.example.demo.vo.Reply;

import lombok.Setter;

@Controller
@Setter
public class BoardController {
	
	@Autowired
	private BoardService bs;
	
	@Autowired
	private UserInfoService uis;
	
	@Autowired
	private PlaceService ps;
	
	@Autowired
	private ReplyService rps;

	//리스트
	@GetMapping("/firstListBoard")						//sort 와 direction 넣으면 오류남!
	public void listBoard(Model model) {		
		List<Board> list = bs.findAll();
		model.addAttribute("list", list);
	}
	
	@RequestMapping("/getAllList")
	@ResponseBody
	public List<Board> listBoardAjax(Model model) {
		List<Board> list = bs.findAll();
		model.addAttribute("list", bs.findAll());
		return list;
	}
	
	//카테고리 하나 눌렀을때 그 페이지로 //여기서 상태유지한것 /*<![CDATA[*/ 이걸로 스트립트 단에서 그냥 쓰기
	@GetMapping("/listBoard/{board_num}")
	public ModelAndView goCategory(@PathVariable int board_num, Model model) {
		
		ModelAndView mav = new ModelAndView("listNamegy");
		if(board_num==3) {
			mav=new ModelAndView("listReview");
			}
		List<Board> boardCategory = bs.goCategory(board_num);
		model.addAttribute("boardCategory", boardCategory);
		model.addAttribute("listLength", boardCategory.size());	//totalrecord될것임		
		model.addAttribute("board_num", (Integer)board_num);	
		return mav;
	}
	
	//ajax
	@RequestMapping("/findByPostTitle")
	@ResponseBody
	public List<Board> findByPostTitle(@RequestParam("board_num")int board_num, @RequestParam("post_keyword")String post_keyword, Model model) {	
		//ModelAndView mav = new ModelAndView("listReviewByTitle");
		List<Board> reviewList = bs.findByPostTitle(board_num, post_keyword);
		model.addAttribute("findByPostTitle", bs.findByPostTitle(board_num, post_keyword));	
		System.out.println("사용자가 검색한 제목:"+post_keyword);
		return reviewList;
	}

	@RequestMapping(value = "/insertBoard/{board_num}", method = RequestMethod.GET)
	public ModelAndView insertBoardForm(@PathVariable int board_num, Model model) {
		ModelAndView mav=new ModelAndView("insertBoard");
		if(board_num==3) {
			//System.out.println("리뷰면 컨트롤러가 여기까지 오나요?");
			mav.setViewName("insertReview");
		}
		//System.out.println("폼컨트롤러 왔다");
		//System.out.println(board_num);
		model.addAttribute("board_num", board_num);
		model.addAttribute("user_list", uis.findAll());
		return mav;
	}
	
	//insertBoard.html에서 다 쓰면 여기로 와서 insert가 되는 것임 (폼태그 방식이 post니까)
	@PostMapping("/insertBoardOK/{board_num}")
	public String insertBoardOK(@PathVariable int board_num, Board b) {
		b.setBoard_num(board_num);
		b.setPost_num(bs.getNextPostNum());
		System.out.println(b.getPlace());
		if(board_num==3) {
			System.out.println("장소번호:"+b.getPlace().getPlace_num());
			bs.insertReview(b,b.getPlace().getPlace_num());	
		}else {
			bs.insertBoard(b);
		}
		return "redirect:/listBoard/"+board_num;
	}
	
	//상세보기 눌렀을때 board_num도 함께 가도록 + 조회수 증가
	@GetMapping("detailPost/{board_num}/{post_num}")
	public ModelAndView detailPost(@PathVariable int board_num,@PathVariable int post_num, Model model) {
		//System.out.println("detailPost의 board_num:"+board_num);
		//System.out.println("detailPost의 post_num:"+post_num);
		ModelAndView mav = new ModelAndView("detailPost");
		model.addAttribute("b",bs.detailPost(board_num, post_num));
		model.addAttribute(bs.plusPostHit(post_num));
		model.addAttribute("post_num", post_num);
		model.addAttribute("user_list", uis.findAll());
		model.addAttribute("reply_list", rps.findByPostNum(post_num));
		return mav;
	}
  
    @GetMapping("/updateBoard/{post_num}")
    public String updateBoardForm(Model model, @PathVariable int post_num) {
    	Board b = bs.findById(post_num);
    	model.addAttribute("b", b);
    	return "updateBoard";
    }
	    
    @PostMapping("/updateBoardOK/{post_num}")
    public String updateBoardOK(@PathVariable int post_num, Board b) {
    	System.out.println("수정ok컨트롤러옴");
    	bs.updateBoard(b);
    	//협의 후 수정
    	return "redirect:/firstListBoard";
	}
    
    @GetMapping(value="/deleteBoard/{post_num}")
    public String delete(@PathVariable("post_num") int post_num, Model model) {
        bs.deleteBoard(post_num);
        //협의 후 수정
        return "redirect:/firstListBoard";
    }
    
}