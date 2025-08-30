package com.noor.store.service;

import com.noor.store.dto.MiscExpenseDTO;
import com.noor.store.exception.ResourceNotFoundException;
import com.noor.store.mapper.MiscExpenseMapper;
import com.noor.store.model.MiscExpense;
import com.noor.store.repository.MiscExpenseRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class MiscExpenseService {

    private final MiscExpenseRepository miscExpenseRepository;
    private final MiscExpenseMapper mapper;

    public MiscExpenseDTO.Response create(MiscExpenseDTO.Request req) {
        MiscExpense e = mapper.toEntity(req);
        MiscExpense saved = miscExpenseRepository.save(e);
        return mapper.toResponse(saved);
    }

    public MiscExpenseDTO.Response getById(Long id) {
        return mapper.toResponse(miscExpenseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id)));
    }

    public Page<MiscExpenseDTO.Response> list(Pageable pageable) {
        return miscExpenseRepository.findAll(pageable).map(mapper::toResponse);
    }

    public void delete(Long id) {
        MiscExpense e = miscExpenseRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Expense not found: " + id));
        miscExpenseRepository.delete(e);
    }

    public List<MiscExpenseDTO.Response> findByDateRange(LocalDate start, LocalDate end) {
        return miscExpenseRepository.findByDateRange(start, end).stream().map(mapper::toResponse).toList();
    }
}
