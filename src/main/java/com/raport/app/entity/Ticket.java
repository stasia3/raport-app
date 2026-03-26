package com.raport.app.entity;

import com.raport.app.entity.enums.TicketStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ticket_number", nullable = false, unique = true, length = 100)
    private String ticketNumber;

    @OneToOne
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private Post post;

    @Column(name = "problem_tag", nullable = false, length = 100)
    private String problemTag;

    @ManyToOne
    @JoinColumn(name = "assigned_institution_user_id")
    private User assignedInstitutionUser;

    @ManyToOne
    @JoinColumn(name = "dispatcher_user_id")
    private User dispatcherUser;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false, columnDefinition = "ticket_enum")
    private TicketStatus status = TicketStatus.Sesizat;

    @Column(name = "ranking_score", nullable = false)
    private Integer rankingScore = 0;

    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;

    @Column(name = "is_red_flag", nullable = false)
    private Boolean isRedFlag = false;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getProblemTag() {
        return problemTag;
    }

    public void setProblemTag(String problemTag) {
        this.problemTag = problemTag;
    }

    public User getAssignedInstitutionUser() {
        return assignedInstitutionUser;
    }

    public void setAssignedInstitutionUser(User assignedInstitutionUser) {
        this.assignedInstitutionUser = assignedInstitutionUser;
    }

    public User getDispatcherUser() {
        return dispatcherUser;
    }

    public void setDispatcherUser(User dispatcherUser) {
        this.dispatcherUser = dispatcherUser;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Integer getRankingScore() {
        return rankingScore;
    }

    public void setRankingScore(Integer rankingScore) {
        this.rankingScore = rankingScore;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Boolean getIsRedFlag() {
        return isRedFlag;
    }

    public void setIsRedFlag(Boolean redFlag) {
        isRedFlag = redFlag;
    }
}
