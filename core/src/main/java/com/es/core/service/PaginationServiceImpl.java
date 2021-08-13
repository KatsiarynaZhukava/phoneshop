package com.es.core.service;

import com.es.core.dao.PhoneDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PaginationServiceImpl implements PaginationService {
    @Resource
    private PhoneDao phoneDao;

    @Override
    public long getTotalPagesNumber( long limit, final String searchQuery ) {
        if (limit <= 0) throw new IllegalArgumentException("Limit must be > 0");
        long totalPhonesNumber = phoneDao.getTotalNumber(searchQuery);
        long totalPagesNumber = totalPhonesNumber / limit + (totalPhonesNumber % limit > 0 ? 1 : 0);
        return totalPagesNumber > 0 ? totalPagesNumber : 1;
    }

    @Override
    public long getOffset( long limit, long currentPage ) {
        if (limit <= 0) throw new IllegalArgumentException("Limit must be > 0");
        if (currentPage <= 0) throw new IllegalArgumentException("Current page number must be > 0");
        return limit * (currentPage - 1);
    }
}
