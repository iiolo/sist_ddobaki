package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.service.BoardService;
import com.example.demo.service.PlaceService;
import com.example.demo.service.UserInfoService;
import com.example.demo.vo.Board;

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

	@GetMapping("/firstListBoard")
	public void listBoard(Model model) {
		model.addAttribute("list", bs.findAll());
	}
	
	//카테고리 하나 눌렀을때 그 페이지로
	@GetMapping("/listBoard/{board_num}")
	public ModelAndView goCategory(@PathVariable int board_num, Model model) {
		
		ModelAndView mav = new ModelAndView("listNamegy");
		if(board_num==3) {
			mav=new ModelAndView("listReview");
			}
		model.addAttribute("boardCategory", bs.goCategory(board_num));
		model.addAttribute("board_num", (Integer)board_num);
		//System.out.println("리스트보드에서 상태유지할 board_num"+board_num);
		return mav;
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
	@PostMapping("/insertBoard/insertBoardOK/{board_num}")
	public String insertBoardOK(@PathVariable int board_num, Board b) {
		b.setBoard_num(board_num);
		b.setPost_num(bs.getNextPostNum());
		System.out.println(b.getPlace());
		bs.insert(b);
		return "redirect:/firstListBoard";
	}

	//상세보기 눌렀을때 board_num도 함께 가도록 + 조회수 증가
	@GetMapping("/detailPost/{board_num}/{post_num}")
	public ModelAndView detailPost(@PathVariable int board_num,@PathVariable int post_num, Model model) {
		//System.out.println("detailPost의 board_num:"+board_num);
		//System.out.println("detailPost의 post_num:"+post_num);
		ModelAndView mav = new ModelAndView("detailPost");
		model.addAttribute("b",bs.detailPost(board_num, post_num));
		model.addAttribute(bs.plusPostHit(post_num));
		return mav;
	}
  
    @GetMapping("/updateBoard/{post_num}")
    public String updateBoardForm(Model model, @PathVariable int post_num) {
    	Board b = bs.findById(post_num);
    	model.addAttribute("b", b);
    	return "updateBoard";
    }
	    
    @PostMapping("/updateBoard/updateBoardOK/{post_num}")
    public String updateBoardOK(@PathVariable int post_num, Board b) {
    	System.out.println("수정ok컨트롤러옴");
    	bs.updateBoard(b);
    	return "redirect:/firstListBoard";
	   }
    
    @GetMapping(value="/deleteBoard/{post_num}")
    public String delete(@PathVariable("post_num") int post_num, Model model) {
        bs.deleteBoard(post_num);
        return "redirect:/firstListBoard";
    }
}