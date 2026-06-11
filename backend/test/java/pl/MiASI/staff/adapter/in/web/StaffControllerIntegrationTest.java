package pl.MiASI.staff.adapter.in.web;

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
import pl.MiASI.staff.application.port.in.CreateStaffCommand;
import pl.MiASI.staff.application.port.in.StaffUseCase;
import pl.MiASI.staff.application.port.in.UpdateStaffCommand;
import pl.MiASI.staff.domain.model.StaffMember;
import pl.MiASI.staff.domain.model.StaffRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = StaffController.class)
@AutoConfigureMockMvc(addFilters = false)
class StaffControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private StaffUseCase staffUseCase;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateStaff() throws Exception {
        CreateStaffCommand command = new CreateStaffCommand(
                StaffRole.DOCTOR, "John", "Doe", "john@example.com", "Cardiology",
                "1234567", "Dept1", null, "Mon-Fri"
        );
        UUID expectedId = UUID.randomUUID();

        when(staffUseCase.createStaff(any(CreateStaffCommand.class))).thenReturn(expectedId);

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.staffId").value(expectedId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenCreateStaffWithInvalidData() throws Exception {
        CreateStaffCommand command = new CreateStaffCommand(
                null, "", "", "invalid-email", "Cardiology",
                "123", "Dept1", null, "Mon-Fri"
        );

        mockMvc.perform(post("/api/staff")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateStaff() throws Exception {
        UUID staffId = UUID.randomUUID();
        UpdateStaffCommand command = new UpdateStaffCommand(
                "Jane", "Doe", "jane@example.com", true, "Neurology",
                "7654321", "Dept2", null, "Tue-Thu"
        );

        mockMvc.perform(put("/api/staff/{id}", staffId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk());

        verify(staffUseCase, times(1)).updateStaff(eq(staffId), any(UpdateStaffCommand.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenUpdateStaffWithInvalidData() throws Exception {
        UUID staffId = UUID.randomUUID();
        UpdateStaffCommand command = new UpdateStaffCommand(
                "", "", "invalid-email", true, "Neurology",
                "123", "Dept2", null, "Tue-Thu"
        );

        mockMvc.perform(put("/api/staff/{id}", staffId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetStaffById() throws Exception {
        UUID staffId = UUID.randomUUID();
        StaffMember mockStaff = StaffMember.create(staffId, StaffRole.DOCTOR, "John", "Doe", "john@example.com", "Cardiology", "1234567", "Dept", null, "Mon");

        when(staffUseCase.getStaffById(staffId)).thenReturn(Optional.of(mockStaff));

        mockMvc.perform(get("/api/staff/{id}", staffId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(staffId.toString()))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenStaffMissing() throws Exception {
        UUID staffId = UUID.randomUUID();

        when(staffUseCase.getStaffById(staffId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/staff/{id}", staffId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeactivateStaff() throws Exception {
        UUID staffId = UUID.randomUUID();

        mockMvc.perform(post("/api/staff/{id}/deactivate", staffId))
                .andExpect(status().isOk());

        verify(staffUseCase, times(1)).deactivateStaff(staffId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteStaff() throws Exception {
        UUID staffId = UUID.randomUUID();

        mockMvc.perform(delete("/api/staff/{id}", staffId))
                .andExpect(status().isNoContent());

        verify(staffUseCase, times(1)).deleteStaff(staffId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldListAllStaff() throws Exception {
        StaffMember mockStaff = StaffMember.create(UUID.randomUUID(), StaffRole.ADMIN_STAFF, "Admin", "User", "admin@example.com", null, null, null, "Manager", null);

        when(staffUseCase.getAllStaff()).thenReturn(List.of(mockStaff));

        mockMvc.perform(get("/api/staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Admin"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldListStaffByRole() throws Exception {
        StaffMember mockStaff = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "Doc", "Tor", "doc@example.com", "Cardiology", "1234567", null, null, null);

        when(staffUseCase.getStaffByRole(StaffRole.DOCTOR)).thenReturn(List.of(mockStaff));

        mockMvc.perform(get("/api/staff").param("role", "DOCTOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Doc"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldSearchStaff() throws Exception {
        StaffMember mockStaff = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "Doc", "Tor", "doc@example.com", "Cardiology", "1234567", null, null, null);

        when(staffUseCase.searchStaff("Doc", null, null, null, true)).thenReturn(List.of(mockStaff));

        mockMvc.perform(get("/api/staff/search")
                .param("firstName", "Doc")
                .param("active", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstName").value("Doc"));
    }
}
