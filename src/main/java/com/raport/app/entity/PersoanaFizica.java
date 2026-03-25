package com.raport.app.entity;


import com.raport.app.entity.enums.PersoanaFizicaUserType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "persoana_fizica")
public class PersoanaFizica {

    @Id
    private Integer userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone_number", length = 30)
    private String phoneNumber;

    @Column(name = "karma_points", nullable = false)
    private Integer karmaPoints = 0;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "user_type", nullable = false, columnDefinition = "persoana_fizica_user_type_enum")
    private PersoanaFizicaUserType userType;

    public Integer getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Integer getKarmaPoints() {
        return karmaPoints;
    }

    public void setKarmaPoints(Integer karmaPoints) {
        this.karmaPoints = karmaPoints;
    }

    public PersoanaFizicaUserType getUserType() {
        return userType;
    }

    public void setUserType(PersoanaFizicaUserType userType) {
        this.userType = userType;
    }
}