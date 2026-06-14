package pl.MiASI.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import pl.MiASI.iam.application.port.in.AuthUseCase;
import pl.MiASI.iam.application.domain.model.Account;
import pl.MiASI.iam.application.domain.model.AccountId;
import pl.MiASI.iam.application.port.out.AccountRepository;
import pl.MiASI.medicalcare.application.port.in.AddSlotCommand;
import pl.MiASI.medicalcare.application.port.in.ScheduleManagementUseCase;
import pl.MiASI.medicalcare.application.port.in.ScheduleQueryUseCase;
import pl.MiASI.medicalcare.application.port.in.VisitManagementUseCase;
import pl.MiASI.medicalcare.application.port.in.VisitQueryUseCase;
import pl.MiASI.medicalcare.application.domain.model.ConsultationType;
import pl.MiASI.medicalcare.application.domain.model.Schedule;
import pl.MiASI.medicalcare.application.domain.model.Slot;
import pl.MiASI.medicalcare.application.domain.model.SlotStatus;
import pl.MiASI.medicalcare.application.domain.model.Visit;
import pl.MiASI.medicalcare.application.domain.model.VisitId;
import pl.MiASI.medicalcare.application.domain.model.VisitStatus;
import pl.MiASI.patient.application.port.in.PatientUseCase;
import pl.MiASI.patient.application.domain.model.Patient;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.shared.application.domain.model.PatientId;
import pl.MiASI.staff.application.port.in.CreateStaffCommand;
import pl.MiASI.staff.application.port.in.StaffUseCase;
import pl.MiASI.staff.application.domain.model.StaffRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DomainIntegrationTest {

    @Autowired
    private StaffUseCase staffUseCase;

    @Autowired
    private PatientUseCase patientUseCase;

    @Autowired
    private AuthUseCase authUseCase;

    @Autowired
    private ScheduleManagementUseCase scheduleManagementUseCase;

    @Autowired
    private ScheduleQueryUseCase scheduleQueryUseCase;

    @Autowired
    private VisitManagementUseCase visitManagementUseCase;

    @Autowired
    private VisitQueryUseCase visitQueryUseCase;

    @Autowired
    private AccountRepository accountRepository;

    private UUID doctorId;
    private PatientId patientId;
    private UUID slotId;

    @BeforeEach
    void setUp() {
        // 1. Stworzenie lekarza (automatycznie tworzy konto w IAM)
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        CreateStaffCommand createDoc = new CreateStaffCommand(
                StaffRole.DOCTOR, "Jan", "Kowalski", "jan.kowalski" + uniqueSuffix + "@example.com",
                "Kardiolog", "1234567" + uniqueSuffix, "Oddział Kardiologii", null, "8:00-16:00"
        );
        doctorId = staffUseCase.createStaff(createDoc);

        // 2. Dodanie harmonogramu dla lekarza
        LocalDateTime start = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime end = start.plusMinutes(30);
        AddSlotCommand addSlot = new AddSlotCommand(new pl.MiASI.medicalcare.application.domain.model.TimeRange(start, end), "Gabinet 101");
        scheduleManagementUseCase.addTimeSlots(new DoctorId(doctorId), List.of(addSlot));

        // Pobierz id dodanego slotu
        Schedule schedule = scheduleQueryUseCase.getScheduleByDoctor(new DoctorId(doctorId)).orElseThrow();
        slotId = schedule.slots().get(0).getSlotId().value();

        // 3. Stworzenie pacjenta (automatycznie tworzy konto w IAM)
        patientId = patientUseCase.registerPatient(
                "Adam", "Nowak", "90" + uniqueSuffix.substring(0, 6) + "123", "123123123", "adam.nowak" + uniqueSuffix + "@example.com", "zaq1@WSX"
        );
    }

    @Test
    void shouldCreateIamAccountWhenRegisteringPatient() {
        // Sprawdzamy czy w IAM utworzono konto
        Optional<Account> account = accountRepository.findById(new AccountId(patientId.value()));
        assertTrue(account.isPresent());
        assertTrue(account.get().getEmail().startsWith("adam.nowak"));
        assertEquals(pl.MiASI.iam.application.domain.model.Role.PATIENT, account.get().getRole());
    }

    @Test
    void shouldCreateIamAccountWhenCreatingStaff() {
        // Sprawdzamy czy w IAM utworzono konto
        Optional<Account> account = accountRepository.findById(new AccountId(doctorId));
        assertTrue(account.isPresent());
        assertTrue(account.get().getEmail().startsWith("jan.kowalski"));
        assertEquals(pl.MiASI.iam.application.domain.model.Role.DOCTOR, account.get().getRole());
    }

    @Test
    void shouldReserveVisitAndChangeSlotStatus() {
        // Given slot is available
        Schedule scheduleBefore = scheduleQueryUseCase.getScheduleByDoctor(new DoctorId(doctorId)).orElseThrow();
        Slot slotBefore = scheduleBefore.slots().stream().filter(s -> s.getSlotId().value().equals(slotId)).findFirst().orElseThrow();
        assertEquals(SlotStatus.AVAILABLE, slotBefore.getStatus());

        // When
        VisitId visitId = visitManagementUseCase.reserveVisit(
                patientId,
                new DoctorId(doctorId),
                ConsultationType.GENERAL,
                List.of(new pl.MiASI.medicalcare.application.domain.model.SlotId(slotId))
        );

        // Then
        assertNotNull(visitId);
        Schedule scheduleAfter = scheduleQueryUseCase.getScheduleByDoctor(new DoctorId(doctorId)).orElseThrow();
        Slot slotAfter = scheduleAfter.slots().stream().filter(s -> s.getSlotId().value().equals(slotId)).findFirst().orElseThrow();
        assertEquals(SlotStatus.BOOKED, slotAfter.getStatus());
        
        List<Visit> visits = visitQueryUseCase.getVisitsByPatient(patientId);
        assertEquals(1, visits.size());
        assertEquals(VisitStatus.RESERVED, visits.get(0).getStatus());
    }

    @Test
    void shouldCancelVisitAndFreeSlot() {
        // Given reserved visit
        VisitId visitId = visitManagementUseCase.reserveVisit(
                patientId,
                new DoctorId(doctorId),
                ConsultationType.GENERAL,
                List.of(new pl.MiASI.medicalcare.application.domain.model.SlotId(slotId))
        );
        Schedule scheduleAfterReserve = scheduleQueryUseCase.getScheduleByDoctor(new DoctorId(doctorId)).orElseThrow();
        assertEquals(SlotStatus.BOOKED, scheduleAfterReserve.slots().get(0).getStatus());

        // When
        visitManagementUseCase.cancelVisit(visitId);

        // Then slot should be free due to VisitEventListener handling VisitCanceledEvent
        Schedule scheduleAfterCancel = scheduleQueryUseCase.getScheduleByDoctor(new DoctorId(doctorId)).orElseThrow();
        Slot slotAfterCancel = scheduleAfterCancel.slots().stream().filter(s -> s.getSlotId().value().equals(slotId)).findFirst().orElseThrow();
        assertEquals(SlotStatus.AVAILABLE, slotAfterCancel.getStatus());
        
        List<Visit> visits = visitQueryUseCase.getVisitsByPatient(patientId);
        assertEquals(VisitStatus.CANCELED, visits.get(0).getStatus());
    }

    @Test
    void shouldCompleteVisitWhenMedicalRecordIsAdded() {
        // Given reserved visit
        VisitId visitId = visitManagementUseCase.reserveVisit(
                patientId,
                new DoctorId(doctorId),
                ConsultationType.GENERAL,
                List.of(new pl.MiASI.medicalcare.application.domain.model.SlotId(slotId))
        );

        // When
        patientUseCase.addMedicalRecord(
                patientId,
                visitId.value(),
                new DoctorId(doctorId),
                "I10 - Nadciśnienie",
                "Bóle głowy",
                "Paracetamol",
                "Kontrola za miesiąc",
                "Brak badań"
        );

        // Then visit status should be COMPLETED due to VisitEventListener handling RecordCreatedEvent
        List<Visit> visits = visitQueryUseCase.getVisitsByPatient(patientId);
        assertEquals(1, visits.size());
        assertEquals(VisitStatus.COMPLETED, visits.get(0).getStatus());
        
        Optional<Patient> patientOpt = patientUseCase.getPatientProfile(patientId);
        assertTrue(patientOpt.isPresent());
        assertEquals(1, patientOpt.get().getMedicalRecords().size());
        assertEquals(visitId.value(), patientOpt.get().getMedicalRecords().get(0).getVisitId());
    }
}

