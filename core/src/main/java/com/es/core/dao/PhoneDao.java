package com.es.core.dao;

import com.es.core.model.phone.Phone;

import java.util.List;
import java.util.Optional;

public interface PhoneDao {
    Optional<Phone> get(Long key);
    void save(Phone phone);
    List<Phone> findAll(long offset, long limit);
    List<Phone> findAll(String searchQuery, String sortField, String sortOrder, long offset, long limit);
    List<Phone> findAll(List<Long> phoneIds);
    boolean exists(Long id);
    long getTotalNumber();
    long getTotalNumber(String searchQuery);
}