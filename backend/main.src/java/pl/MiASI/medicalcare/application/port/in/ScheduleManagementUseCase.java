package pl.MiASI.medicalcare.application.port.in;

import pl.MiASI.medicalcare.application.domain.model.SlotId;
import pl.MiASI.medicalcare.application.domain.model.TimeRange;
import pl.MiASI.shared.application.domain.model.DoctorId;

import java.util.List;

public interface ScheduleManagementUseCase {
    void addTimeSlots(DoctorId doctorId, List<AddSlotCommand> commands);

    void freeSlots(DoctorId doctorId, List<SlotId> slotIds);

    void updateSlot(DoctorId doctorId, SlotId slotId, TimeRange newTimeRange, String newOffice);

    void removeSlot(DoctorId doctorId, SlotId slotId);
}