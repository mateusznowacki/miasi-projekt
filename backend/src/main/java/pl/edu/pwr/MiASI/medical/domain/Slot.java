package pl.edu.pwr.MiASI.medical.domain;

public class Slot {
    private SlotId id;
    private TimeRange okres;
    private SlotStatus stan;

    public Slot(SlotId id, TimeRange okres, SlotStatus stan) {
        this.id = id;
        this.okres = okres;
        this.stan = stan;
    }

    public void book() {
        if (this.stan == SlotStatus.OCCUPIED) {
            throw new IllegalStateException("Slot jest już zajęty");
        }
        this.stan = SlotStatus.OCCUPIED;
    }

    public void release() {
        this.stan = SlotStatus.FREE;
    }

    public SlotId getId() { return id; }
    public TimeRange getOkres() { return okres; }
    public SlotStatus getStan() { return stan; }
}
