package com.igot.cb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginationRequestDto {
    private int offset;
    private int limit;
    private Boolean isActive;
}
