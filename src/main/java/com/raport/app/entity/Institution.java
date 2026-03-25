package com.raport.app.entity;

import com.raport.app.entity.enums.InstitutionServiceType;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "institution")
public class Institution {

    @Id
    private Integer userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "official_name", nullable = false, length = 255)
    private String officialName;

    @Column(name = "fiscal_code", nullable = false, unique = true, length = 50)
    private String fiscalCode;

    @Column(name = "legal_address", nullable = false, length = 255)
    private String legalAddress;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "service_type", nullable = false, columnDefinition = "institution_service_type_enum")
    private InstitutionServiceType serviceType;

    public Integer getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }

    public String getFiscalCode() {
        return fiscalCode;
    }

    public void setFiscalCode(String fiscalCode) {
        this.fiscalCode = fiscalCode;
    }

    public String getLegalAddress() {
        return legalAddress;
    }

    public void setLegalAddress(String legalAddress) {
        this.legalAddress = legalAddress;
    }

    public InstitutionServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(InstitutionServiceType serviceType) {
        this.serviceType = serviceType;
    }
}