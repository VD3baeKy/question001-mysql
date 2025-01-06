package com.example.samuraitravel.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.entity.Review;
import com.example.samuraitravel.entity.User;

import jakarta.transaction.Transactional;

public interface ReviewsRepository extends JpaRepository<Review, Integer> {
	public Page<Review> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    public Page<Review> findByHouseOrderByCreatedAtDesc(House house, Pageable pageable);
    public Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
    public Page<Review> findByUserAndHouseOrderByCreatedAtDesc(User user, House house, Pageable pageable);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.id = :reviewId")
    void deleteByReviewId(Integer reviewId);
}