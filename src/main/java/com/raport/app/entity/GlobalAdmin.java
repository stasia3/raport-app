package com.raport.app.entity;

import com.raport.app.entity.enums.GlobalAdminClearance;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "global_admin")
public class GlobalAdmin {

    @Id
    private Integer userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "admin_handle", nullable = false, length = 100)
    private String adminHandle;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "security_clearance", nullable = false, columnDefinition = "global_admin_clearance_enum")
    private GlobalAdminClearance securityClearance;

    @Column(name = "master_key_hash", nullable = false, length = 255)
    private String masterKeyHash;

    @Column(name = "can_manage_tags", nullable = false)
    private Boolean canManageTags = false;

    @Column(name = "can_manage_users", nullable = false)
    private Boolean canManageUsers = false;

    @Column(name = "last_security_audit")
    private LocalDateTime lastSecurityAudit;

    public Integer getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAdminHandle() {
        return adminHandle;
    }

    public void setAdminHandle(String adminHandle) {
        this.adminHandle = adminHandle;
    }

    public GlobalAdminClearance getSecurityClearance() {
        return securityClearance;
    }

    public void setSecurityClearance(GlobalAdminClearance securityClearance) {
        this.securityClearance = securityClearance;
    }

    public String getMasterKeyHash() {
        return masterKeyHash;
    }

    public void setMasterKeyHash(String masterKeyHash) {
        this.masterKeyHash = masterKeyHash;
    }

    public Boolean getCanManageTags() {
        return canManageTags;
    }

    public void setCanManageTags(Boolean canManageTags) {
        this.canManageTags = canManageTags;
    }

    public Boolean getCanManageUsers() {
        return canManageUsers;
    }

    public void setCanManageUsers(Boolean canManageUsers) {
        this.canManageUsers = canManageUsers;
    }

    public LocalDateTime getLastSecurityAudit() {
        return lastSecurityAudit;
    }

    public void setLastSecurityAudit(LocalDateTime lastSecurityAudit) {
        this.lastSecurityAudit = lastSecurityAudit;
    }
}