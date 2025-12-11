package es.onebox.mgmt.sessions.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class TaxesDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private IdNameDTO ticket;

    private List<IdNameDTO> ticketTaxes;

    private IdNameDTO charges;

    private List<IdNameDTO> chargesTaxes;

    @Valid
    private TaxesDataDTO data;

    public IdNameDTO getTicket() {
        return ticket;
    }

    public void setTicket(IdNameDTO ticket) {
        this.ticket = ticket;
    }

    public IdNameDTO getCharges() {
        return charges;
    }

    public void setCharges(IdNameDTO charges) {
        this.charges = charges;
    }

    public List<IdNameDTO> getTicketTaxes() { return ticketTaxes; }

    public void setTicketTaxes(List<IdNameDTO> ticketTaxes) { this.ticketTaxes = ticketTaxes; }

    public List<IdNameDTO> getChargesTaxes() { return chargesTaxes; }

    public void setChargesTaxes(List<IdNameDTO> chargesTaxes) { this.chargesTaxes = chargesTaxes; }

    public TaxesDataDTO getData() {
        return data;
    }

    public void setData(TaxesDataDTO data) {
        this.data = data;
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
