package com.census.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

@Entity
@Table(
    name = "person",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_person_id_number", columnNames = {"id_number"})
    }
)
public class Person extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "ID number is required")
    @Size(max = 20, message = "ID number must not exceed 20 characters")
    @Column(name = "id_number", nullable = false, unique = true, length = 20)
    private String idNumber;

    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Gender is required")
    @Column(nullable = false, length = 20)
    private String gender;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @Column(nullable = false)
    private LocalDate dob;

    @NotBlank(message = "Relationship to head of household is required")
    @Column(nullable = false, length = 50)
    private String relation;

    @NotBlank(message = "Marital status is required")
    @Column(name = "marital_status", nullable = false, length = 50)
    private String maritalStatus;

    @NotBlank(message = "Education level is required")
    @Column(nullable = false, length = 100)
    private String education;

    @NotBlank(message = "Occupation is required")
    @Column(nullable = false, length = 100)
    private String occupation;

    @Column(nullable = false, length = 50)
    private String nationality = "Indian";

    @NotNull(message = "Household is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "household_id", nullable = false)
    private Household household;

    public Person() {
        this.nationality = "Indian";
    }

    public Person(String idNumber, String name, String gender, LocalDate dob, String relation,
                  String maritalStatus, String education, String occupation, String nationality, Household household) {
        this.idNumber = idNumber;
        this.name = name;
        this.gender = gender;
        this.dob = dob;
        this.relation = relation;
        this.maritalStatus = maritalStatus;
        this.education = education;
        this.occupation = occupation;
        this.nationality = (nationality != null && !nationality.trim().isEmpty()) ? nationality : "Indian";
        this.household = household;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Household getHousehold() {
        return household;
    }

    public void setHousehold(Household household) {
        this.household = household;
    }

    @Transient
    public int getAge() {
        if (dob == null) return 0;
        return Period.between(dob, LocalDate.now()).getYears();
    }

    @Transient
    public boolean isLiterate() {
        if (education == null) return false;
        return !"Illiterate".equalsIgnoreCase(education.trim());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person person)) return false;
        return Objects.equals(id, person.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
