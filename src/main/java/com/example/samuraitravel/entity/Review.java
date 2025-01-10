package com.example.samuraitravel.entity;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "reviews")
@Data
public class Review {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "house_id", referencedColumnName = "id")
    private House house;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "review_text")
    private String review_text;

    @Column(name = "image_name")
    private String imageName;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
    
    // プロパティをMapとして取得
    public Map<String, Object> getProperties() {
        Map<String, Object> properties = new HashMap<>();
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true); // privateフィールドにアクセス
            try {
                properties.put(field.getName(), field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return properties;
    }

    public int getPropertyCount() {
        return this.getClass().getDeclaredFields().length;
    }
    
    
    public String getReview_text() {
        return review_text;
    }
    
    
    public Integer getUser_id() {
        return user.getId();
    }
    
    /*
    @Override
    public String toString() {  
	    return "Review{id='" + id + 
	    		",' user_id='" + user.getId() + 
	    		"', house_id='" + house.getId() +
	    		"', rating='" + rating + 
	    		"', review_text='" + review_text + 
	    		"', image_name='" + imageName + 
	    		"', created_at='" + createdAt + 
	    		"', updated_at='" + updatedAt + 
	    		"'}"; // 必要なフィールドのみを表示
    }*/
}
