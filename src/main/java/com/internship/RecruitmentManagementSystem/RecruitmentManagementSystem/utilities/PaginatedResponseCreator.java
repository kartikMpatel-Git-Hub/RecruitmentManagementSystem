package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.utilities;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.payloads.responses.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PaginatedResponseCreator {

    private static final Logger logger = LoggerFactory.getLogger(PaginatedResponseCreator.class);

    private final Mapper mapper;

    public <D,T> PaginatedResponse<D> getPaginatedResponse(Page<T> pageResponse, Class<D> dtoClass) {
        logger.debug("Creating paginated response for dtoClass={}, pageNumber={}, pageSize={}",
                dtoClass.getSimpleName(), pageResponse.getNumber(), pageResponse.getSize());

        PaginatedResponse<D> response = new PaginatedResponse<>();
        response.setData(pageResponse.stream().map(val -> mapper.toDto(val,dtoClass)).toList());
        response.setCurrentPage(pageResponse.getNumber());
        response.setLast(pageResponse.isLast());
        response.setPageSize(pageResponse.getSize());
        response.setTotalItems(pageResponse.getTotalElements());
        response.setTotalPages(pageResponse.getTotalPages());

        logger.debug("Paginated response created: totalItems={}, totalPages={}",
                response.getTotalItems(), response.getTotalPages());

        return response;
    }

    public Pageable getPageable(Integer page, Integer size, String sortBy, String sortDir) {
        logger.debug("Creating pageable with page={}, size={}, sortBy={}, sortDir={}",
                page, size, sortBy, sortDir);

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        logger.debug("Pageable created successfully");
        return pageable;
    }
}
