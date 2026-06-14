package pl.MiASI.medicalcare.infrastructure.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.MiASI.iam.infrastructure.out.security.JwtTokenProvider;
import pl.MiASI.medicalcare.application.port.in.ScheduleQueryUseCase;
import pl.MiASI.medicalcare.application.domain.model.Schedule;
import pl.MiASI.medicalcare.application.domain.model.ScheduleId;
import pl.MiASI.medicalcare.application.domain.model.Slot;
import pl.MiASI.medicalcare.application.domain.model.SlotId;
import pl.MiASI.medicalcare.application.domain.model.SlotStatus;
import pl.MiASI.medicalcare.application.domain.model.TimeRange;
import pl.MiASI.shared.application.domain.model.DoctorId;
import pl.MiASI.staff.application.port.in.StaffUseCase;
import pl.MiASI.staff.application.domain.model.StaffMember;
import pl.MiASI.staff.application.domain.model.StaffRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AvailableSlotController.class)
@AutoConfigureMockMvc(addFilters = false)
class AvailableSlotControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StaffUseCase staffUseCase;

    @MockitoBean
    private ScheduleQueryUseCase scheduleQueryUseCase;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = "PATIENT")
    void shouldGetAvailableSlots() throws Exception {
        UUID doctorId = UUID.randomUUID();
        StaffMember doctor = StaffMember.create(doctorId, StaffRole.DOCTOR, "John", "Doe", "doc@example.com", "Cardiology", "123", "Dept", null, null);

        TimeRange range = new TimeRange(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1));
        Slot availableSlot = new Slot(new SlotId(UUID.randomUUID()), range, "Room 1", SlotStatus.AVAILABLE);
        Schedule schedule = new Schedule(new ScheduleId(UUID.randomUUID()), new DoctorId(doctorId), List.of(availableSlot));

        when(staffUseCase.getStaffByRole(StaffRole.DOCTOR)).thenReturn(List.of(doctor));
        when(scheduleQueryUseCase.getAllSchedules()).thenReturn(List.of(schedule));

        mockMvc.perform(get("/api/schedules/available")
                .param("specialization", "Cardiology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].doctorFirstName").value("John"))
                .andExpect(jsonPath("$[0].specialization").value("Cardiology"));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void shouldReturnEmptyListWhenNoSlotsAvailable() throws Exception {
        when(staffUseCase.getStaffByRole(StaffRole.DOCTOR)).thenReturn(List.of());
        when(scheduleQueryUseCase.getAllSchedules()).thenReturn(List.of());

        mockMvc.perform(get("/api/schedules/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
