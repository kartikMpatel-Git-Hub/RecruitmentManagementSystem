package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.repositories;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.DegreeModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DegreeRepository extends JpaRepository<DegreeModel,Integer> {
    DegreeModel findByDegree(String Degree);

    @Query("select d.degree,count(d) from DegreeModel d group by d.degree order by count(d) desc")
    Page<Object[]> findTopSkills(Pageable pageable);

    long count();
}
