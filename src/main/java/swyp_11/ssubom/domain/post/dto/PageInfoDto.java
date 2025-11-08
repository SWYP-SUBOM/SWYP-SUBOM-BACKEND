package swyp_11.ssubom.domain.post.dto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PageInfoDto {
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    public boolean isLast;
}
