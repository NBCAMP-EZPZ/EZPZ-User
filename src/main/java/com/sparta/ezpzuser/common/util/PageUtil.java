package com.sparta.ezpzuser.common.util;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

@Getter
@Builder
public class PageUtil {

    private int page;
    private int size;
    private String sortBy;
    private String firstStatus;
    private String secondStatus;
    private String thirdStatus;

    public Pageable toPageable() {
        if (Objects.isNull(sortBy)) {
            return PageRequest.of(page - 1, size);
        } else {
            return PageRequest.of(page - 1, size, Sort.by(sortBy).descending());
        }
    }

    public Pageable toPageable(String sortBy) {
        return PageRequest.of(page - 1, size, Sort.by(sortBy).descending());
    }
}
