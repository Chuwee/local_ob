package es.onebox.internal.automaticsales.filemanagement.dto;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@CouchDocument
public class AutomaticSaleFileData implements Serializable {

    @Serial
    private static final long serialVersionUID = -5752766005578918846L;
    @Id
    private String documentId;
    private String filename;
    private Long sessionId;
    private List<FileInfo> fileInfo;

    public AutomaticSaleFileData() {
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public List<FileInfo> getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(List<FileInfo> fileInfo) {
        this.fileInfo = fileInfo;
    }
}
