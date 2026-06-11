package pl.MiASI.medicalcare.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.MiASI.iam.adapter.out.security.JwtTokenProvider;
import pl.MiASI.medicalcare.application.port.in.AddSlotCommand;
import pl.MiASI.medicalcare.application.port.in.ScheduleManagementUseCase;
import pl.MiASI.medicalcare.application.port.in.ScheduleQueryUseCase;
import pl.MiASI.medicalcare.domain.model.Schedule;
import pl.MiASI.medicalcare.domain.model.ScheduleId;
import pl.MiASI.medicalcare.domain.model.Slot;
import pl.MiASI.medicalcare.domain.model.SlotId;
import pl.MiASI.medicalcare.domain.model.SlotStatus;
import pl.MiASI.medicalcare.domain.model.TimeRange;
import pl.MiASI.shared.domain.model.DoctorId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ScheduleController.class)
@AutoConfigureMockMvc(addFilters = false)
class ScheduleControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    @MockitoBean
    private ScheduleManagementUseCase scheduleManagementUseCase;

    @MockitoBean
    private ScheduleQueryUseCase scheduleQueryUseCase;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldAddTimeSlots() throws Exception {
        UUID doctorId = UUID.randomUUID();
        AddSlotCommand cmd = new AddSlotCommand(new TimeRange(LocalDateTime.now(), LocalDateTime.now().plusHours(1)), "Room A");
        AddSlotsRequest request = new AddSlotsRequest(List.of(cmd));

        mockMvc.perform(post("/api/schedules/{doctorId}/slots", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(scheduleManagementUseCase, times(1)).addTimeSlots(eq(new DoctorId(doctorId)), any());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturnBadRequestWhenAddTimeSlotsThrowsException() throws Exception {
        UUID doctorId = UUID.randomUUID();
        AddSlotCommand cmd = new AddSlotCommand(new TimeRange(LocalDateTime.now(), LocalDateTime.now().plusHours(1)), "Room A");
        AddSlotsRequest request = new AddSlotsRequest(List.of(cmd));

        doThrow(new IllegalArgumentException("Invalid slots")).when(scheduleManagementUseCase).addTimeSlots(any(), any());

        mockMvc.perform(post("/api/schedules/{doctorId}/slots", doctorId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid slots"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldUpdateSlot() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();
        TimeRange range = new TimeRange(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        UpdateSlotRequest request = new UpdateSlotRequest(range, "Room B");

        mockMvc.perform(put("/api/schedules/{doctorId}/slots/{slotId}", doctorId, slotId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(scheduleManagementUseCase, times(1)).updateSlot(eq(new DoctorId(doctorId)), eq(new SlotId(slotId)), any(), eq("Room B"));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturnBadRequestWhenUpdateSlotThrowsException() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();
        TimeRange range = new TimeRange(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        UpdateSlotRequest request = new UpdateSlotRequest(range, "Room B");

        doThrow(new IllegalArgumentException("Slot error")).when(scheduleManagementUseCase).updateSlot(any(), any(), any(), any());

        mockMvc.perform(put("/api/schedules/{doctorId}/slots/{slotId}", doctorId, slotId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldRemoveSlot() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();

        mockMvc.perform(delete("/api/schedules/{doctorId}/slots/{slotId}", doctorId, slotId))
                .andExpect(status().isOk());

        verify(scheduleManagementUseCase, times(1)).removeSlot(eq(new DoctorId(doctorId)), eq(new SlotId(slotId)));
    }

    @Test
    @WithMockUser(roles = "DOCTOR")
    void shouldReturnBadRequestWhenRemoveSlotThrowsException() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID slotId = UUID.randomUUID();

        doThrow(new IllegalArgumentException("Slot error")).when(scheduleManagementUseCase).removeSlot(any(), any());

        mockMvc.perform(delete("/api/schedules/{doctorId}/slots/{slotId}", doctorId, slotId))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void shouldGetScheduleByDoctor() throws Exception {
        UUID doctorId = UUID.randomUUID();
        UUID scheduleId = UUID.randomUUID();
        TimeRange range = new TimeRange(LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        Slot slot = new Slot(new SlotId(UUID.randomUUID()), range, "Room A", SlotStatus.AVAILABLE);
        Schedule schedule = new Schedule(new ScheduleId(scheduleId), new DoctorId(doctorId), List.of(slot));

        when(scheduleQueryUseCase.getScheduleByDoctor(new DoctorId(doctorId))).thenReturn(Optional.of(schedule));

        mockMvc.perform(get("/api/schedules/doctor/{doctorId}", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorId").value(doctorId.toString()))
                .andExpect(jsonPath("$.slots.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void shouldReturnNotFoundWhenScheduleMissing() throws Exception {
        UUID doctorId = UUID.randomUUID();

        when(scheduleQueryUseCase.getScheduleByDoctor(new DoctorId(doctorId))).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/schedules/doctor/{doctorId}", doctorId))
                .andExpect(status().isNotFound());
    }
}
