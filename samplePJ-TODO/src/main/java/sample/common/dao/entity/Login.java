package sample.common.dao.entity;

import java.time.LocalDateTime;

public class Login {
	  private Long id; 
	  private String username; 
	  private String password;
	  private LocalDateTime createdAt;
	  private LocalDateTime updatedAt;
	
	// Getter データの取得メソッド
	public long getId() {
		return id;
	}
	public String getUsername() {
		return username;
	}
	public String getPass() {
		return password;
	}
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	
	// Setter データの設定メソッド
	public void setId(Long id) {
		this.id = id;
	}
	public void setUsername(String username) {
		this.username = username;
	}	
	public void setPass(String password) {
		this.password = password;
	}	
}
