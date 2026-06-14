package pl.MiASI.staff.infrastructure.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pl.MiASI.iam.infrastructure.out.security.JwtTokenProvider;
import pl.MiASI.staff.application.port.in.StaffUseCase;
import pl.MiASI.staff.application.domain.model.StaffMember;
import pl.MiASI.staff.application.domain.model.StaffRole;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = DoctorController.class)
@AutoConfigureMockMvc(addFilters = false)
class DoctorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StaffUseCase staffUseCase;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @WithMockUser(roles = "PATIENT")
    void shouldListAllDoctors() throws Exception {
        StaffMember doc1 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "John", "Doe", "john@example.com", "Cardiology", "1234567", null, null, null);
        StaffMember doc2 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "Jane", "Smith", "jane@example.com", "Neurology", "7654321", null, null, null);

        when(staffUseCase.getStaffByRole(StaffRole.DOCTOR)).thenReturn(List.of(doc1, doc2));

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void shouldListDoctorsBySpecialization() throws Exception {
        StaffMember doc1 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "John", "Doe", "john@example.com", "Cardiology", "1234567", null, null, null);
        StaffMember doc2 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "Jane", "Smith", "jane@example.com", "Neurology", "7654321", null, null, null);

        when(staffUseCase.getStaffByRole(StaffRole.DOCTOR)).thenReturn(List.of(doc1, doc2));

        // The controller filters by specialization in memory
        mockMvc.perform(get("/api/doctors").param("specialization", "Cardiology"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void shouldListDoctorsByName() throws Exception {
        StaffMember doc1 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "John", "Doe", "john@example.com", "Cardiology", "1234567", null, null, null);
        StaffMember doc2 = StaffMember.create(UUID.randomUUID(), StaffRole.DOCTOR, "Jane", "Smith", "jane@example.com", "Neurology", "7654321", null, null, null);

        when(staffUseCase.getStaffByRole(StaffRole.DOCTOR)).thenReturn(List.of(doc1, doc2));

        // The controller filters by name in memory (first name or last name contains)
        mockMvc.perform(get("/api/doctors").param("name", "smIth"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].firstName").value("Jane"));
    }
}
