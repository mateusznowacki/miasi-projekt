package pl.edu.pwr.MiASI.document.infrastructure.primary;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwr.MiASI.document.application.DocumentHandlingUseCase;
import pl.edu.pwr.MiASI.document.domain.DocumentId;
import pl.edu.pwr.MiASI.document.domain.FileReference;
import pl.edu.pwr.MiASI.document.domain.FileType;
import pl.edu.pwr.MiASI.document.domain.Metadata;

import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentHandlingUseCase obslugaDokumentowUseCase;

    public DocumentController(DocumentHandlingUseCase obslugaDokumentowUseCase) {
        this.obslugaDokumentowUseCase = obslugaDokumentowUseCase;
    }

    @PostMapping("/upload")
    public ResponseEntity<Void> uploadDocument(@RequestBody PrzeslijDokumentRequest request) {
        obslugaDokumentowUseCase.uploadDocument(
            new FileReference(request.url()),
            FileType.valueOf(request.fileType())
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{dokumentId}/webhook-ocr")
    public ResponseEntity<Void> ocrWebhook(@PathVariable UUID dokumentId, @RequestBody OcrResultRequest request) {
        obslugaDokumentowUseCase.enrichAfterOcr(
            new DocumentId(dokumentId),
            new Metadata(request.extractedText(), request.issueDate())
        );
        return ResponseEntity.ok().build();
    }
}

record PrzeslijDokumentRequest(String url, String fileType) {}
record OcrResultRequest(String extractedText, String issueDate) {}
