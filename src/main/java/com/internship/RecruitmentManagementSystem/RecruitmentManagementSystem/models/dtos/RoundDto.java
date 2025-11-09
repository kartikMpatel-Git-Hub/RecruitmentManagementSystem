package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.dtos;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundResult;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.enums.RoundType;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.ApplicationModel;
import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model.RoundStatusModel;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Deprecated
public class RoundDto {

    private Integer roundId;

//    private Integer applicationId;

    private RoundType roundType;

    private RoundResult roundResult;

    private LocalDate roundDate;

    private LocalTime roundTime;

    private Integer roundSequence;

    private RoundStatusDto roundStatus;

}
