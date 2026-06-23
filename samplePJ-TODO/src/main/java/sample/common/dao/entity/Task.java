package sample.common.dao.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

public class Task {
	private Long id;
	private String username;
	private String title;
	private String content;
	private String name;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime createdAt;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDateTime updatedAt;

	// Getter データの取得メソッド
	public long getId() {
		return id;
	}
	public String getUsername() {
		// user名
		return username;
	}
	public String getTitle() {
		return title;
	}
	public String getContent() {
		return content;
	}
	public String getName() {
		// 登録者名
		return name;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
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
	public void setTitle(String title) {
		this.title= title;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
}
