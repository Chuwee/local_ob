package es.onebox.event.catalog.elasticsearch.dto;

import es.onebox.event.events.dto.conditions.ProfessionalClientConditions;

import java.util.List;

public class ChannelAgency {

    private Long id;
    private List<Long> quotas;
    private Boolean allQuotas;
    private ProfessionalClientConditions conditions;

    public ChannelAgency() {
    }

    public ChannelAgency(Long id, List<Long> quotas, ProfessionalClientConditions conditions) {
        this.id = id;
        this.quotas = quotas;
        this.conditions = conditions;
    }

    public ChannelAgency(Long id, ProfessionalClientConditions conditions) {
        this.id = id;
        this.allQuotas = Boolean.TRUE;
        this.conditions = conditions;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAllQuotas() {
        return allQuotas;
    }

    public List<Long> getQuotas() {
        return quotas;
    }

    public ProfessionalClientConditions getConditions() {
        return conditions;
    }

    public void setConditions(ProfessionalClientConditions conditions) {
        this.conditions = conditions;
    }

    public void setQuotas(List<Long> quotas) {
        this.quotas = quotas;
    }
}
