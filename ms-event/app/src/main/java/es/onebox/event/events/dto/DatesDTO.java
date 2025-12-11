/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.events.dto;

import java.io.Serializable;

/**
 * @author rcarrillo
 */
public class DatesDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private DateDTO start;
    private DateDTO end;

    public DateDTO getStart() {
        return start;
    }

    public void setStart(DateDTO start) {
        this.start = start;
    }

    public DateDTO getEnd() {
        return end;
    }

    public void setEnd(DateDTO end) {
        this.end = end;
    }

}
