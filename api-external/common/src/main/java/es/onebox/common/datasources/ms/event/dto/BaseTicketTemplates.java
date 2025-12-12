package es.onebox.common.datasources.ms.event.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public abstract class BaseTicketTemplates implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    protected String individualTicketPassbookTemplateCode;

    protected Long groupTicketPdfTemplateId;
    protected Long groupTicketPrinterTemplateId;
    protected String groupTicketPassbookTemplateCode;

    protected Long individualInvitationPdfTemplateId;
    protected Long individualInvitationPrinterTemplateId;
    protected String individualInvitationPassbookTemplateCode;

    protected Long groupInvitationPdfTemplateId;
    protected Long groupInvitationPrinterTemplateId;
    protected String groupInvitationPassbookTemplateCode;

    protected String sessionPackPassbookTemplateCode;

    public String getIndividualTicketPassbookTemplateCode() {
        return individualTicketPassbookTemplateCode;
    }

    public void setIndividualTicketPassbookTemplateCode(String individualTicketPassbookTemplateCode) {
        this.individualTicketPassbookTemplateCode = individualTicketPassbookTemplateCode;
    }

    public Long getGroupTicketPdfTemplateId() {
        return groupTicketPdfTemplateId;
    }

    public void setGroupTicketPdfTemplateId(Long groupTicketPdfTemplateId) {
        this.groupTicketPdfTemplateId = groupTicketPdfTemplateId;
    }

    public Long getGroupTicketPrinterTemplateId() {
        return groupTicketPrinterTemplateId;
    }

    public void setGroupTicketPrinterTemplateId(Long groupTicketPrinterTemplateId) {
        this.groupTicketPrinterTemplateId = groupTicketPrinterTemplateId;
    }

    public String getGroupTicketPassbookTemplateCode() {
        return groupTicketPassbookTemplateCode;
    }

    public void setGroupTicketPassbookTemplateCode(String groupTicketPassbookTemplateCode) {
        this.groupTicketPassbookTemplateCode = groupTicketPassbookTemplateCode;
    }

    public Long getIndividualInvitationPdfTemplateId() {
        return individualInvitationPdfTemplateId;
    }

    public void setIndividualInvitationPdfTemplateId(Long individualInvitationPdfTemplateId) {
        this.individualInvitationPdfTemplateId = individualInvitationPdfTemplateId;
    }

    public Long getIndividualInvitationPrinterTemplateId() {
        return individualInvitationPrinterTemplateId;
    }

    public void setIndividualInvitationPrinterTemplateId(Long individualInvitationPrinterTemplateId) {
        this.individualInvitationPrinterTemplateId = individualInvitationPrinterTemplateId;
    }

    public String getIndividualInvitationPassbookTemplateCode() {
        return individualInvitationPassbookTemplateCode;
    }

    public void setIndividualInvitationPassbookTemplateCode(String individualInvitationPassbookTemplateCode) {
        this.individualInvitationPassbookTemplateCode = individualInvitationPassbookTemplateCode;
    }

    public Long getGroupInvitationPdfTemplateId() {
        return groupInvitationPdfTemplateId;
    }

    public void setGroupInvitationPdfTemplateId(Long groupInvitationPdfTemplateId) {
        this.groupInvitationPdfTemplateId = groupInvitationPdfTemplateId;
    }

    public Long getGroupInvitationPrinterTemplateId() {
        return groupInvitationPrinterTemplateId;
    }

    public void setGroupInvitationPrinterTemplateId(Long groupInvitationPrinterTemplateId) {
        this.groupInvitationPrinterTemplateId = groupInvitationPrinterTemplateId;
    }

    public String getGroupInvitationPassbookTemplateCode() {
        return groupInvitationPassbookTemplateCode;
    }

    public void setGroupInvitationPassbookTemplateCode(String groupInvitationPassbookTemplateCode) {
        this.groupInvitationPassbookTemplateCode = groupInvitationPassbookTemplateCode;
    }

    public String getSessionPackPassbookTemplateCode() {
        return sessionPackPassbookTemplateCode;
    }

    public void setSessionPackPassbookTemplateCode(String sessionPackPassbookTemplateCode) {
        this.sessionPackPassbookTemplateCode = sessionPackPassbookTemplateCode;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
