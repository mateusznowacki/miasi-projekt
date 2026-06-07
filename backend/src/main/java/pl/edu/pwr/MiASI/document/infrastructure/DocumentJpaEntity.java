package pl.edu.pwr.MiASI.document.infrastructure;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "documents", schema = "documents")
public class DocumentJpaEntity {
    @Id
    private UUID id;
    private String fileReference;
    private String fileType;
    private String ocrStatus;
    private String extractedText;
    private String issueDate;

    protected DocumentJpaEntity() {}

    public DocumentJpaEntity(UUID id, String fileReference, String fileType, String ocrStatus, String extractedText, String issueDate) {
        this.id = id;
        this.fileReference = fileReference;
        this.fileType = fileType;
        this.ocrStatus = ocrStatus;
        this.extractedText = extractedText;
        this.issueDate = issueDate;
    }

    public UUID getId() { return id; }
    public String getReferencjaPliku() { return fileReference; }
    public String getTypPliku() { return fileType; }
    public String getStatusOcr() { return ocrStatus; }
    public String getWyekstrahowanyTekst() { return extractedText; }
    public String getDataWystawienia() { return issueDate; }
}
