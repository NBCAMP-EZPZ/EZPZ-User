package com.sparta.ezpzuser.common.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;

public class PageUtil {
	public static final int DEFAULT_PAGE_SIZE = 10;
	
	public static Pageable createPageable(Integer page, Sort sort) {
		if (page < 1) {
			throw new CustomException(ErrorType.INVALID_PAGE);
		}
		
		return PageRequest.of(page - 1, DEFAULT_PAGE_SIZE, sort);
	}
	
	public static Pageable createPageable(Integer page) {
		if (page < 1) {
			throw new CustomException(ErrorType.INVALID_PAGE);
		}
		
		return PageRequest.of(page - 1, DEFAULT_PAGE_SIZE, Sort.by(Sort.Order.asc("id")));
	}
	
	
	public static void checkValidatePage(Integer page, Page<?> pageList) {
		if (pageList.getTotalElements() == 0) {
			throw new CustomException(ErrorType.NOT_FOUND_PAGE);
		}
		
		if (page > pageList.getTotalPages() || page < 1) {
			throw new CustomException(ErrorType.INVALID_PAGE);
		}
	}
}
