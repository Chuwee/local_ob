package es.flc.ie.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Model class to store the code and message of current REST request
 *
 * @author VASS
 */
@XmlRootElement(name = "status")
@JsonInclude(Include.NON_NULL)
public class Status implements Serializable {

    private static final long serialVersionUID = 1L;
    private int code;
    private String message;

    /**
     * Default Constructor
     */
    public Status() {
        code = 0;
        message = "";
    }

    /**
     * Method Constructor
     *
     * @param code
     * @param message
     */
    public Status(int code, String message) {
        this.code = code;
        this.message = message;
    }


    /**
     * Method to get Code
     *
     * @return
     */
    public int getCode() {
        return code;
    }

    /**
     * Method to set Code
     *
     * @param code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * Method to get Message
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Method to set Message
     *
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
