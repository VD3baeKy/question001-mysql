package com.example.samuraitravel.entity;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "name")
    private String name;
        
    @Column(name = "furigana")
    private String furigana;    
        
    @Column(name = "postal_code")
    private String postalCode;
        
    @Column(name = "address")
    private String address;
        
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "email")
    private String email;
        
    @Column(name = "password")
    private String password;    
    
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;   
    
    @Column(name = "enabled")
    private Boolean enabled;
    
    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;
    
    @Column(name = "updated_at", insertable = false, updatable = false)
    private Timestamp updatedAt;
    
    //リレーション追加
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews; // Userに関連するレビューのリスト
    
    /*
    @Override
    public String toString() {
        return "User{id=" + id + 
        		", name='" + name +
        		", furigana='" + furigana +
        		", postal_code='" + postalCode +
        		", address='" + address +
        		", phone_number='" + phoneNumber +
        		", email='" + email +
        		", password='" + password +
        		", enabled='" + enabled +
        		", created_at='" + createdAt +
        		", updated_at='" + updatedAt +
        		"'}"; // 必要なフィールドのみを表示
    }*/
    
}
