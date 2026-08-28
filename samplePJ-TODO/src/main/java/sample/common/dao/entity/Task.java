package sample.common.dao.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Task {

	private Long id;
	private String username;
	private String title;
	private String content;
	private LocalDate startDate;
	private LocalDate endDate;
	private String status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	// id:ID
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	// username:ユーザー名
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	// title:タイトル
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	// content:内容
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	// startDate:開始日
	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	// endDate:終了日
	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	// status:ステータス値
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	// createdAt:作成日
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	// updatedAt:更新日
	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}
