package com.sparta.ezpzuser.common.util;

public final class RepositoryUtil {

    public static Long getTotal(Long totalCount) {
        return (totalCount == null) ? 0L : totalCount;
    }

}
