package me.cocoblue.springrestdocssample.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PaginatedResponseDto<T> {
    @Builder
    public PaginatedResponseDto(List<T> data, Long nextCursor, boolean hasNext) {
        this.data = data;
        this.nextCursor = nextCursor;
        this.hasNext = hasNext;
    }

    private List<T> data;
    private Long nextCursor;
    private boolean hasNext;

}
