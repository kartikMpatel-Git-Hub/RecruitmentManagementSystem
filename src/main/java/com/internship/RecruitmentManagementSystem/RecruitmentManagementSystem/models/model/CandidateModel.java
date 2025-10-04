package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_candidate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidateModel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false,length = 10)
    private Integer candidateId;

    @Column(nullable = false,length = 30)
    @NotEmpty(message = "Candidate First Name Can't Be Empty !")
    @Size(min = 3,max = 30)
    private String candidateFirstName;

    @Column(nullable = false,length = 30)
    @NotEmpty(message = "Candidate Middle Name Can't Be Empty !")
    @Size(min = 3,max = 30)
    private String candidateMiddleName;

    @Column(nullable = false,length = 30)
    @NotEmpty(message = "Candidate Last Name Can't Be Empty !")
    @Size(min = 3,max = 30)
    private String candidateLastName;

    @Column(unique = true, nullable = false,length = 12)
    @NotEmpty(message = "Candidate Phone Number Can't Be Empty !")
    @Size(min = 10,max = 12)
    private String candidatePhoneNumber;

    @Column(nullable = false,length = 6)
    @NotEmpty(message = "Candidate Gender Can't Be Empty !")
    @Size(min = 4,max = 6)
    private String candidateGender;

    @Column(nullable = false)
    @NotNull(message = "Candidate Gender Can't Be Empty !")
    private LocalDate candidateDateOfBirth;

    @Column(nullable = false,length = 100)
    @NotEmpty(message = "Candidate Address Can't Be Empty !")
    @Size(min = 3,max = 100)
    private String candidateAddress;

    @Column(nullable = false,length = 30)
    @NotEmpty(message = "Candidate City Can't Be Empty !")
    @Size(min = 3,max = 30)
    private String candidateCity;

    @Column(nullable = false,length = 30)
    @NotEmpty(message = "Candidate State Can't Be Empty !")
    @Size(min = 3,max = 30)
    private String candidateState;

    @Column(nullable = false,length = 30)
    @NotEmpty(message = "Candidate Country Can't Be Empty !")
    @Size(min = 3,max = 30)
    private String candidateCountry;

    @Column(nullable = false,length = 10)
    @NotEmpty(message = "Candidate Zip Code Can't Be Empty !")
    @Size(min = 5,max = 10)
    private String candidateZipCode;

    @Column(nullable = false,length = 500)
    private String candidateResumeUrl;

    @Column(nullable = false)
    private Integer candidateTotalExperienceInYears;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserModel user;
}
