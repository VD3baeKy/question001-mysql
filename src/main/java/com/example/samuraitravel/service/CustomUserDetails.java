package com.example.samuraitravel.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.samuraitravel.entity.User;

public class CustomUserDetails implements UserDetails {
    private User user; // Userエンティティ

    public CustomUserDetails(User user) {
        this.user = user;
    }

    public Integer getUserId() {
        return user.getId(); // IDを返すメソッド
    }

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public String getPassword() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public String getUsername() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

    // 他のUserDetailsメソッドの実装

}
