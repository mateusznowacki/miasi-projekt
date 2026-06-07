package pl.edu.pwr.MiASI.document.domain;

import pl.edu.pwr.MiASI.shared.domain.AggregateRoot;

@AggregateRoot
public class Document {
    private DocumentId id;
    private FileReference fileReference;
    private FileType fileType;
    private Metadata metadata;
    private OcrStatus ocrStatus;

    public Document(DocumentId id, FileReference fileReference, FileType fileType) {
        this.id = id;
        this.fileReference = fileReference;
        this.fileType = fileType;
        this.ocrStatus = OcrStatus.PENDING;
    }

    public static Document upload(FileReference referencja, FileType typ) {
        return new Document(DocumentId.generate(), referencja, typ);
    }

    public void enrichMetadata(Metadata metadata) {
        this.metadata = metadata;
        this.ocrStatus = OcrStatus.COMPLETED;
    }

    public DocumentId getId() { return id; }
    public FileReference getReferencjaPliku() { return fileReference; }
    public FileType getTypPliku() { return fileType; }
    public Metadata getMetadane() { return metadata; }
    public OcrStatus getStatusOcr() { return ocrStatus; }
}
