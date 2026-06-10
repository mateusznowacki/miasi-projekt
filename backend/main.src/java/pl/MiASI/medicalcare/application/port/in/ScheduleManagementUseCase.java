package pl.MiASI.medicalcare.application.port.in;

import pl.MiASI.shared.domain.model.DoctorId;
import pl.MiASI.medicalcare.domain.model.SlotId;
import pl.MiASI.medicalcare.domain.model.TimeRange;
import java.util.List;

public interface ScheduleManagementUseCase {
    void addTimeSlots(DoctorId doctorId, List<AddSlotCommand> commands);
    void freeSlots(DoctorId doctorId, List<SlotId> slotIds);
    void updateSlot(DoctorId doctorId, SlotId slotId, TimeRange newTimeRange, String newOffice);
}