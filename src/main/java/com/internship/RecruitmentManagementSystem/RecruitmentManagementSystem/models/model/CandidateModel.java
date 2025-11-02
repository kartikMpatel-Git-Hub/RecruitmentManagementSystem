package com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.models.model;

import com.internship.RecruitmentManagementSystem.RecruitmentManagementSystem.config.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_candidate",indexes = {
        @Index(name = "idx_user_id",columnList = "user_id"),
        @Index(name = "idx_created_at", columnList = "createdAt"),
        @Index(name = "idx_updated_at", columnList = "updatedAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CandidateModel extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false,length = 10)
    private Integer candidateId;

    @Column(nullable = true,length = 30)
    @Size(min = 3,max = 30)
    private String candidateFirstName;

    @Column(nullable = true,length = 30)
    @Size(min = 3,max = 30)
    private String candidateMiddleName;

    @Column(nullable = true,length = 30)
    @Size(min = 3,max = 30)
    private String candidateLastName;

    @Column(unique = true, nullable = true,length = 12)
    @Size(min = 10,max = 12)
    private String candidatePhoneNumber;

    @Column(nullable = true,length = 6)
    @Size(min = 4,max = 6)
    private String candidateGender;

    @Column(nullable = true)
    private LocalDate candidateDateOfBirth;

    @Column(nullable = true,length = 100)
    @Size(min = 3,max = 100)
    private String candidateAddress;

    @Column(nullable = true,length = 30)
    @Size(min = 3,max = 30)
    private String candidateCity;

    @Column(nullable = true,length = 30)
    @Size(min = 3,max = 30)
    private String candidateState;

    @Column(nullable = true,length = 30)
    @Size(min = 3,max = 30)
    private String candidateCountry;

    @Column(nullable = true,length = 10)
    @Size(min = 5,max = 10)
    private String candidateZipCode;

    @Column(nullable = true,length = 500)
    private String candidateResumeUrl;

    @Column(nullable = true)
    private Integer candidateTotalExperienceInYears;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserModel user;

//    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
//    @JoinTable(
//            name = "tbl_candidate_skills",
//            joinColumns = @JoinColumn(name = "candidate_id",referencedColumnName = "candidateId"),
//            inverseJoinColumns = @JoinColumn(name = "skill_id",referencedColumnName = "skillId")
//    )
//    private List<SkillModel> skills = new ArrayList<>();
}
