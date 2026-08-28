package sample.common.dto;

import java.time.LocalDate;

public class TaskRequest {

    private String title;
    private String content;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;

    // getter/setter
    // タイトル:title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // 内容:content
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // ステータス:status
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // 開始日:startDate
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    // 終了日:endDate
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

}
