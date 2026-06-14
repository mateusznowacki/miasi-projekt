package pl.MiASI.bootstrap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pl.MiASI.iam.application.domain.model.AccountId;
import pl.MiASI.iam.application.domain.model.Role;
import pl.MiASI.iam.application.port.in.AuthUseCase;
import pl.MiASI.iam.application.port.out.AccountRepository;
import pl.MiASI.medicalcare.application.domain.model.*;
import pl.MiASI.medicalcare.application.port.in.AddSlotCommand;
import pl.MiASI.medicalcare.application.port.in.ScheduleManagementUseCase;
import pl.MiASI.medicalcare.application.port.in.ScheduleQueryUseCase;
import pl.MiASI.medicalcare.application.port.in.VisitManagementUseCase;
import pl.MiASI.patient.application.port.in.PatientUseCase;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.shared.application.domain.model.PatientId;
import pl.MiASI.staff.application.domain.model.StaffRole;
import pl.MiASI.staff.application.port.in.CreateStaffCommand;
import pl.MiASI.staff.application.port.in.StaffUseCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevDataSeeder implements CommandLineRunner {

    private static final String DEMO_PASSWORD = "haslo123";
    private static final String STAFF_PASSWORD = "password";

    private final AuthUseCase authUseCase;
    private final AccountRepository accountRepository;
    private final StaffUseCase staffUseCase;
    private final PatientUseCase patientUseCase;
    private final ScheduleManagementUseCase scheduleManagementUseCase;
    private final ScheduleQueryUseCase scheduleQueryUseCase;
    private final VisitManagementUseCase visitManagementUseCase;

    @Override
    @Transactional
    public void run(String... args) {
        if (accountRepository.findByEmail("admin@medflow.pl").isPresent()) {
            log.info("Dev seed data already present — skipping.");
            return;
        }

        seedAdmin();
        UUID receptionistId = seedReceptionist();
        UUID doctorKowalskiId = seedDoctorKowalski();
        UUID doctorNowakId = seedDoctorNowak();
        PatientId patientJanId = seedPatientJan();
        PatientId patientMariaId = seedPatientMaria();

        List<UUID> doctorKowalskiSlots = seedDoctorKowalskiSchedule(doctorKowalskiId);
        List<UUID> doctorNowakSlots = seedDoctorNowakSchedule(doctorNowakId);

        VisitId completedVisitId = visitManagementUseCase.reserveVisit(
                patientJanId,
                new DoctorId(doctorKowalskiId),
                ConsultationType.GENERAL,
                List.of(new SlotId(doctorKowalskiSlots.get(0)))
        );
        patientUseCase.addMedicalRecord(
                patientJanId,
                completedVisitId.value(),
                new DoctorId(doctorKowalskiId),
                "I10 - Nadciśnienie tętnicze",
                "Bóle głowy, zawroty",
                "Ramipril 5 mg 1x dziennie",
                "Kontrola za miesiąc",
                "Ciśnienie 145/95 mmHg"
        );

        visitManagementUseCase.reserveVisit(
                patientJanId,
                new DoctorId(doctorKowalskiId),
                ConsultationType.GENERAL,
                List.of(new SlotId(doctorKowalskiSlots.get(2)))
        );
        visitManagementUseCase.reserveVisit(
                patientMariaId,
                new DoctorId(doctorKowalskiId),
                ConsultationType.SPECIALIST,
                List.of(new SlotId(doctorKowalskiSlots.get(1)))
        );
        visitManagementUseCase.reserveVisit(
                patientMariaId,
                new DoctorId(doctorNowakId),
                ConsultationType.FOLLOW_UP,
                List.of(new SlotId(doctorNowakSlots.get(0)))
        );

        log.info("""
                
                === Dev seed data created ===
                Admin:           admin@medflow.pl / {}
                Recepcja:        rejestracja@medflow.pl / {}
                Lekarz (kard.):  lekarz@medflow.pl / {}
                Lekarz (int.):   maria.nowak@medflow.pl / {}
                Pacjent:         pacjent@medflow.pl / {}
                Pacjent:         anna.wisniewska@medflow.pl / {}
                Receptionist ID: {}
                Doctors: {}, {}
                Patients seeded with upcoming and completed visits.
                """,
                DEMO_PASSWORD,
                STAFF_PASSWORD,
                STAFF_PASSWORD,
                STAFF_PASSWORD,
                DEMO_PASSWORD,
                DEMO_PASSWORD,
                receptionistId,
                doctorKowalskiId,
                doctorNowakId
        );
    }

    private void seedAdmin() {
        AccountId adminId = authUseCase.registerUser("admin@medflow.pl", DEMO_PASSWORD, Role.ADMIN);
        authUseCase.activateAccount(adminId.value().toString());
    }

    private UUID seedReceptionist() {
        return staffUseCase.createStaff(new CreateStaffCommand(
                StaffRole.ADMIN_STAFF,
                "Katarzyna",
                "Zielińska",
                "rejestracja@medflow.pl",
                null,
                null,
                null,
                "Rejestratorka medyczna",
                "8:00-16:00"
        ));
    }

    private UUID seedDoctorKowalski() {
        return staffUseCase.createStaff(new CreateStaffCommand(
                StaffRole.DOCTOR,
                "Jan",
                "Kowalski",
                "lekarz@medflow.pl",
                "Kardiologia",
                "1234567",
                "Oddział Kardiologii",
                null,
                "8:00-16:00"
        ));
    }

    private UUID seedDoctorNowak() {
        return staffUseCase.createStaff(new CreateStaffCommand(
                StaffRole.DOCTOR,
                "Maria",
                "Nowak",
                "maria.nowak@medflow.pl",
                "Interna",
                "2345678",
                "Oddział Interny",
                null,
                "9:00-17:00"
        ));
    }

    private PatientId seedPatientJan() {
        PatientId patientId = patientUseCase.registerPatient(
                "Jan",
                "Kowalski",
                "90010112345",
                "600 100 200",
                "pacjent@medflow.pl",
                DEMO_PASSWORD
        );
        authUseCase.activateAccount(patientId.value().toString());
        return patientId;
    }

    private PatientId seedPatientMaria() {
        PatientId patientId = patientUseCase.registerPatient(
                "Anna",
                "Wiśniewska",
                "85052254321",
                "601 222 333",
                "anna.wisniewska@medflow.pl",
                DEMO_PASSWORD
        );
        authUseCase.activateAccount(patientId.value().toString());
        return patientId;
    }

    private List<UUID> seedDoctorKowalskiSchedule(UUID doctorId) {
        LocalDateTime yesterday10 = dayAt(-1, 10, 0);
        LocalDateTime tomorrow9 = dayAt(1, 9, 0);
        LocalDateTime tomorrow10 = dayAt(1, 10, 0);
        LocalDateTime tomorrow14 = dayAt(1, 14, 0);

        return addSlots(doctorId, List.of(
                slot(yesterday10, yesterday10.plusMinutes(30), "Gabinet 4"),
                slot(tomorrow9, tomorrow9.plusMinutes(30), "Gabinet 4"),
                slot(tomorrow10, tomorrow10.plusMinutes(30), "Gabinet 4"),
                slot(tomorrow14, tomorrow14.plusMinutes(30), "Gabinet 4")
        ));
    }

    private List<UUID> seedDoctorNowakSchedule(UUID doctorId) {
        LocalDateTime tomorrow11 = dayAt(1, 11, 0);
        LocalDateTime tomorrow15 = dayAt(1, 15, 0);
        LocalDateTime dayAfter12 = dayAt(2, 12, 0);

        return addSlots(doctorId, List.of(
                slot(tomorrow11, tomorrow11.plusMinutes(30), "Gabinet 12"),
                slot(tomorrow15, tomorrow15.plusMinutes(30), "Gabinet 12"),
                slot(dayAfter12, dayAfter12.plusMinutes(30), "Gabinet 12")
        ));
    }

    private List<UUID> addSlots(UUID doctorId, List<AddSlotCommand> commands) {
        DoctorId docId = new DoctorId(doctorId);
        scheduleManagementUseCase.addTimeSlots(docId, commands);
        Schedule schedule = scheduleQueryUseCase.getScheduleByDoctor(docId).orElseThrow();
        return schedule.slots().stream()
                .map(slot -> slot.getSlotId().value())
                .toList();
    }

    private static AddSlotCommand slot(LocalDateTime start, LocalDateTime end, String office) {
        return new AddSlotCommand(new TimeRange(start, end), office);
    }

    private static LocalDateTime dayAt(int daysFromNow, int hour, int minute) {
        return LocalDateTime.now()
                .plusDays(daysFromNow)
                .withHour(hour)
                .withMinute(minute)
                .withSecond(0)
                .withNano(0);
    }
}
