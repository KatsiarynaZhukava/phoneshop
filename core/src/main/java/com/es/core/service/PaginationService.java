package com.es.core.service;

public interface PaginationService {
    long getTotalPagesNumber(long limit, String searchQuery);
    long getOffset(long limit, long currentPage);
}
