package pl.MiASI.medicalcare.application.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TimeRangeUnitTest {

    @Test
    @DisplayName("Should create TimeRange when start time is before end time")
    void constructorWhenValidDatesShouldCreateObject() {
        // given
        LocalDateTime start = LocalDateTime.of(2023, 10, 10, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 10, 11, 0);

        // when
        TimeRange timeRange = new TimeRange(start, end);

        // then
        assertThat(timeRange.startTime()).isEqualTo(start);
        assertThat(timeRange.endTime()).isEqualTo(end);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when start time is null")
    void constructorWhenStartTimeNullShouldThrowException() {
        // given
        LocalDateTime end = LocalDateTime.of(2023, 10, 10, 11, 0);

        // when & then
        assertThatThrownBy(() -> new TimeRange(null, end))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start time and end time cannot be null");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when end time is null")
    void constructorWhenEndTimeNullShouldThrowException() {
        // given
        LocalDateTime start = LocalDateTime.of(2023, 10, 10, 10, 0);

        // when & then
        assertThatThrownBy(() -> new TimeRange(start, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start time and end time cannot be null");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when start time is after end time")
    void constructorWhenStartTimeAfterEndTimeShouldThrowException() {
        // given
        LocalDateTime start = LocalDateTime.of(2023, 10, 10, 11, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 10, 10, 0);

        // when & then
        assertThatThrownBy(() -> new TimeRange(start, end))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start time must be before end time");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when start time is equal to end time")
    void constructorWhenStartTimeEqualsEndTimeShouldThrowException() {
        // given
        LocalDateTime start = LocalDateTime.of(2023, 10, 10, 10, 0);
        LocalDateTime end = LocalDateTime.of(2023, 10, 10, 10, 0);

        // when & then
        assertThatThrownBy(() -> new TimeRange(start, end))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Start time must be before end time");
    }

    @Test
    @DisplayName("Should return true when time ranges overlap")
    void overlapsWithWhenRangesOverlapShouldReturnTrue() {
        // given
        TimeRange range1 = new TimeRange(
                LocalDateTime.of(2023, 10, 10, 10, 0),
                LocalDateTime.of(2023, 10, 10, 12, 0)
        );
        TimeRange range2 = new TimeRange(
                LocalDateTime.of(2023, 10, 10, 11, 0),
                LocalDateTime.of(2023, 10, 10, 13, 0)
        );

        // when
        boolean overlaps = range1.overlapsWith(range2);

        // then
        assertThat(overlaps).isTrue();
    }

    @Test
    @DisplayName("Should return true when time ranges partially overlap from before")
    void overlapsWithWhenRangesOverlapFromBeforeShouldReturnTrue() {
        // given
        TimeRange range1 = new TimeRange(
                LocalDateTime.of(2023, 10, 10, 10, 0),
                LocalDateTime.of(2023, 10, 10, 12, 0)
        );
        TimeRange range2 = new TimeRange(
                LocalDateTime.of(2023, 10, 10, 9, 0),
                LocalDateTime.of(2023, 10, 10, 11, 0)
        );

        // when
        boolean overlaps = range1.overlapsWith(range2);

        // then
        assertThat(overlaps).isTrue();
    }

    @Test
    @DisplayName("Should return true when one time range is inside another")
    void overlapsWithWhenRangeIsInsideShouldReturnTrue() {
        // given
        TimeRange range1 = new TimeRange(
                LocalDateTime.of(2023, 10, 10, 10, 0),
                LocalDateTime.of(2023, 10, 10, 12, 0)
        );
        TimeRange range2 = new TimeRange(
                LocalDateTime.of(2023, 10, 10, 10, 30),
                LocalDateTime.of(2023, 10, 10, 11, 30)
        );

        // when
        boolean overlaps1 = range1.overlapsWith(range2);
        boolean overlaps2 = range2.overlapsWith(range1);

        // then
        assertThat(overlaps1).isTrue();
        assertThat(overlaps2).isTrue();
    }

    @Test
    @DisplayName("Should return false when time ranges do not overlap")
    void overlapsWithWhenRangesDoNotOverlapShouldReturnFalse() {
        // given
        TimeRange range1 = new TimeRange(
                LocalDateTime.of(2023, 10, 10, 10, 0),
                LocalDateTime.of(2023, 10, 10, 11, 0)
        );
        TimeRange range2 = new TimeRange(
                LocalDateTime.of(2023, 10, 10, 12, 0),
                LocalDateTime.of(2023, 10, 10, 13, 0)
        );

        // when
        boolean overlaps = range1.overlapsWith(range2);

        // then
        assertThat(overlaps).isFalse();
    }

    @Test
    @DisplayName("Should return false when time ranges are adjacent (end equals start)")
    void overlapsWithWhenRangesAreAdjacentShouldReturnFalse() {
        // given
        TimeRange range1 = new TimeRange(
                LocalDateTime.of(2023, 10, 10, 10, 0),
                LocalDateTime.of(2023, 10, 10, 11, 0)
        );
        TimeRange range2 = new TimeRange(
                LocalDateTime.of(2023, 10, 10, 11, 0),
                LocalDateTime.of(2023, 10, 10, 12, 0)
        );

        // when
        boolean overlaps = range1.overlapsWith(range2);

        // then
        assertThat(overlaps).isFalse();
    }
}
