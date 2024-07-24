package com.sparta.ezpzuser.common.util;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.sparta.ezpzuser.common.exception.CustomException;
import com.sparta.ezpzuser.common.exception.ErrorType;

public class PageUtil {
	public static final int DEFAULT_PAGE_SIZE = 10;
	
	// 페이지 번호, 페이지 사이즈, 정렬 방식을 이용한 Pageable 객체 생성
	public static Pageable createPageable(Integer page, Integer size, String sortBy, Sort.Direction direction) {
		validatePageNumber(page);
		Sort sort = Sort.by(direction, sortBy);
		return PageRequest.of(page - 1, size, sort);
	}
	
	// 페이지 번호, 정렬 방식을 이용한 Pageable 객체 생성
	public static Pageable createPageable(Integer page, String sortBy, Sort.Direction direction) {
		validatePageNumber(page);
		Sort sort = Sort.by(direction, sortBy);
		return PageRequest.of(page - 1, DEFAULT_PAGE_SIZE, sort);
	}
	
	// 페이지 번호를 이용한 Pageable 객체 생성
	public static Pageable createPageable(Integer page) {
		validatePageNumber(page);
		return PageRequest.of(page - 1, DEFAULT_PAGE_SIZE, Sort.by("createdAt").descending());
	}
	
	/**
	 * 페이지 번호 유효성 검사
	 *
	 * @param page 페이지 번호
	 */
	private static void validatePageNumber(Integer page) {
		if (page < 1) {
			throw new CustomException(ErrorType.INVALID_PAGE);
		}
	}
	
	/**
	 * 페이지 유효성 검사
	 *
	 * @param page 페이지 번호
	 * @param pageList 페이지 리스트
	 */
	public static void validatePage(Integer page, Page<?> pageList) {
		if (pageList.getTotalElements() == 0) {
			throw new CustomException(ErrorType.NOT_FOUND_PAGE);
		}
		
		if (page > pageList.getTotalPages() || page < 1) {
			throw new CustomException(ErrorType.INVALID_PAGE);
		}
	}
}
