package com.example.samuraitravel.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;
import com.example.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.repository.ReviewsRepository;
import com.example.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.service.ReviewsService;

import jakarta.transaction.Transactional;


@ControllerAdvice
@Controller
@RequestMapping("/houses")
public class HouseController {
    private final HouseRepository houseRepository;        
    private final UserRepository userRepository; // UserRepositoryの追加
    private final ReviewsRepository reviewsRepository; // ReviewsRepositoryの追加
    
    public HouseController(HouseRepository houseRepository, UserRepository userRepository, ReviewsRepository reviewsRepository) {
        this.houseRepository = houseRepository; // HouseRepositoryの初期化
        this.userRepository = userRepository; // UserRepositoryの初期化
        this.reviewsRepository = reviewsRepository; // ReviewsRepositoryの初期化
    }     
  
    @GetMapping
    public String index(@RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "area", required = false) String area,
                        @RequestParam(name = "price", required = false) Integer price,  
                        @RequestParam(name = "order", required = false) String order,
                        @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
                        Model model) 
    {

        Page<House> housePage;

        if (keyword != null && !keyword.isEmpty()) {
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
            } else {
                housePage = houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
            }            
        } else if (area != null && !area.isEmpty()) {            
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
            } else {
                housePage = houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
            }            
        } else if (price != null) {            
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
            } else {
                housePage = houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
            }            
        } else {            
            if (order != null && order.equals("priceAsc")) {
                housePage = houseRepository.findAllByOrderByPriceAsc(pageable);
            } else {
                housePage = houseRepository.findAllByOrderByCreatedAtDesc(pageable);
            }
        }

        model.addAttribute("housePage", housePage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("area", area);
        model.addAttribute("price", price); 
        model.addAttribute("order", order);

        return "houses/index";
    }
    
     @GetMapping("/{id}")
     public String show(
    		 @PathVariable(name = "id") Integer id,
    		 Model model,
    		 //Principal principal,
    		 @AuthenticationPrincipal UserDetailsImpl userDetails)
     {
    	 //System.out.println("Show method is called.");  // 追加
    	 //System.out.println("Received ID: " + id);  // 追加
    	 
    	 if(userDetails!=null) {
    		 System.out.println(userDetails.getUser_Id()); // 現在ログインしているユーザーのuserId
    		 System.out.println(userDetails.getPassword()); //現在ログインしているユーザーのハッシュ化されたpassword
    		 System.out.println(userDetails.getUsername()); //現在ログインしているユーザーのmailアドレス
    	 }else {
    		 System.out.println(" ##### NOT LOGIN #####");
    	 }
    	 
    	 //HouseService houseService = new HouseService(houseRepository);
    	 //UserService userService = new UserService(userRepository, null, null);
    	 ReviewsService reviewsService = new ReviewsService(reviewsRepository);
    	 
    	 //houseService.clearCache();
    	 //userService.clearCache();
    	 reviewsService.refreshReviews();
    	 
         House house = houseRepository.getReferenceById(id);
         
         User userLogined = null;
         if(userDetails!=null) {
        	 userLogined = userRepository.getReferenceById(userDetails.getUser_Id());// 現在のログインーザーのIDからユーザーデータを取得
         }
         User user = userRepository.getReferenceById(id);// 現在の宿泊施設IDからユーザーデータを取得
         //Page<Review> review = reviewsRepository.findByUserAndHouseOrderByCreatedAtDesc(user, house, null);
         
         if(userDetails!=null) {
        	 System.out.println("Current User ID: " + userDetails.getUser_Id());
        	 model.addAttribute("currentUserId", userDetails.getUser_Id());
         }
         
         // Houseに関連するレビューを取得
         List<Review> reviews = house.getReviews(); // reviewsはHouseエンティティのプロパティ
     
         List<User> userAll = userRepository.findAll(); // 有効なユーザーのリストを取得
         
         System.out.println(userAll.get(2).getEnabled());
         
         // 現在のユーザーのレビューを取得
         boolean hasWrittenReview = userDetails.getUser_Id() != null && 
             reviews.stream().anyMatch(r -> r.getUser_id().equals(userDetails.getUser_Id())); // ユーザーのレビューがあるか確認
         System.out.println("hasWrittenReview= " + hasWrittenReview);
         
         model.addAttribute("house", house);         
         model.addAttribute("reservationInputForm", new ReservationInputForm());
         
         model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());
         
         model.addAttribute("reviews",reviews); // 取得した現在の宿泊施設のレビューをモデルに追加
         model.addAttribute("user", user);     // 取得した現在の宿泊施設にレビューを投稿している全てのユーザーデータをモデルに追加
         //model.addAttribute("revuser", revuser);     // 取得したレビューユーザーをモデルに追加
         
         if(userDetails!=null) {
        	 model.addAttribute("currentUserId", userDetails.getUser_Id()); // 現在のユーザーIDをモデルに追加         
        	 model.addAttribute("hasWrittenReview", hasWrittenReview); // ユーザーのレビューがあるかどうかをモデルに追加
        	 if(userLogined!=null)model.addAttribute("userLogined", userLogined);
         }
         
         model.addAttribute("userAll", userAll);
         
         return "houses/show";
     }
    
     //レビューの削除実行
     //@PostMapping("/{id}/review/{reviewId}/delete")
     @Transactional
     @GetMapping("/{id}/review/{reviewId}/delete")
     public String delete(
    		 @PathVariable(name = "id") Integer id,
    		 @PathVariable(name = "reviewId") Integer reviewId,
    		 RedirectAttributes redirectAttributes)
     {
     //public String delete( @ModelAttribute RequestParam form,	ModelAndView mav){
     //@PostMapping("/{reviewId}/delete")
     //public String delete(@PathVariable(name = "reviewId") Integer reviewId, RedirectAttributes redirectAttributes) {
    	 System.out.println("delwork");
    	 System.out.println("houseId = " + id + " ,   reviewId=" + reviewId);
    	 /*
         reviewsRepository.deleteById(reviewId);
         redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
         */
    	 try {
    	        //reviewsRepository.deleteById(reviewId);
    		    reviewsRepository.deleteByReviewId(reviewId);
    	        redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");
    	 } catch (EmptyResultDataAccessException e) {
    	        // エンティティが見つからない場合の処理
    	        redirectAttributes.addFlashAttribute("successMessage", "レビューが見つかりません。削除に失敗しました。");
    	 } catch (Exception e) {
    	        // その他のエラー処理
    	        redirectAttributes.addFlashAttribute("successMessage", "削除中にエラーが発生しました。" + e.getMessage());
    	 }
         //return "redirect:/houses/{id}";
         return "redirect:/houses/" + id;
     }
     
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
	@ResponseBody
	public Map<String, Object> handleError() {
		Map<String, Object> errorMap = new HashMap<String, Object>();
		errorMap.put("message", "許可されていないメソッド");
		errorMap.put("status", HttpStatus.METHOD_NOT_ALLOWED);
		return errorMap;
	}

}

