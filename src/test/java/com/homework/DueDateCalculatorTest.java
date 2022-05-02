package com.homework;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.homework.DueDateCalculator;

public class DueDateCalculatorTest {

	private DueDateCalculator dueDateCalculator;

	@BeforeEach
	public void init() {
		dueDateCalculator = new DueDateCalculator();
	}

	@Test
	public void testCalculateDueDate_negativeTurnAroundTime() {
		assertThrows(IllegalArgumentException.class,
				() -> dueDateCalculator.calculateDueDate(LocalDateTime.of(2022, 4, 29, 11, 0), -1),
				"Negative turnaroundtime");
	}

	@Test
	public void testCalculateDueDate_submitDateTimeIsNull() {
		assertThrows(IllegalArgumentException.class, () -> dueDateCalculator.calculateDueDate(null, 0),
				"SubmitDateTime is not working hour");
	}

	@ParameterizedTest
	@MethodSource("notWorkingHours")
	public void testCalculateDueDate_submitDateTimeIsNotWorkingHour(LocalDateTime submitDateTime) {
		assertThrows(IllegalArgumentException.class, () -> dueDateCalculator.calculateDueDate(submitDateTime, 0),
				"SubmitDateTime is not working hour");
	}

	private static Stream<LocalDateTime> notWorkingHours() {
		return Stream.of(LocalDateTime.of(2022, 4, 30, 10, 0), // Saturday 10:00
				LocalDateTime.of(2022, 5, 1, 10, 0), // Sunday 10:00
				LocalDateTime.of(2022, 5, 2, 8, 59), // Monday 8:59
				LocalDateTime.of(2022, 5, 3, 17, 1)); // Tuesday 17:01
	}

	@ParameterizedTest
	@MethodSource("workingHours")
	public void testCalculateDueDate_submitTimeAtStartOfWork_zeroTurnaroundTime(LocalDateTime submitDateTime) {
		LocalDateTime dueDate = dueDateCalculator.calculateDueDate(submitDateTime, 0);
		assertThat(dueDate, equalTo(submitDateTime));
	}

	private static Stream<LocalDateTime> workingHours() {
		return Stream.of(LocalDateTime.of(2022, 5, 2, 9, 0), // Monday 9:00
				LocalDateTime.of(2022, 5, 3, 10, 0), // Tuesday 10:00
				LocalDateTime.of(2022, 5, 4, 11, 0), // Wednesday 11:00
				LocalDateTime.of(2022, 5, 5, 16, 0), // Thursday 16:00
				LocalDateTime.of(2022, 5, 6, 17, 0)); // Friday 17:00
	}

	@Test
	public void testCalculateDueDate_dueDateIsOnSameDay() {
		var submitDateTime = LocalDateTime.of(2022, 5, 2, 9, 0); // Monday 9:00
		var dueDate = dueDateCalculator.calculateDueDate(submitDateTime, 1);
		var expectedDueDate = LocalDateTime.of(2022, 5, 2, 10, 0); // Monday 10:00
		assertThat(dueDate, equalTo(expectedDueDate));
	}
	
	@Test
	public void testCalculateDueDate_dueDateIsOnSameDayEdgeCase() {
		var submitDateTime = LocalDateTime.of(2022, 5, 2, 9, 0); // Monday 9:00
		var dueDate = dueDateCalculator.calculateDueDate(submitDateTime, 8);
		var expectedDueDate = LocalDateTime.of(2022, 5, 2, 17, 0); // Monday 17:00
		assertThat(dueDate, equalTo(expectedDueDate));
	}

	@Test
	public void testCalculateDueDate_dueDateIsOnSameDay_notWholeHour() {
		var submitDateTime = LocalDateTime.of(2022, 5, 2, 9, 13, 22, 164); // Monday 9:13:22:164
		var dueDate = dueDateCalculator.calculateDueDate(submitDateTime, 4);
		var expectedDueDate = LocalDateTime.of(2022, 5, 2, 13, 13, 22, 164); // Monday 13:13:22:164
		assertThat(dueDate, equalTo(expectedDueDate));
	}

	@Test
	public void testCalculateDueDate_dueDateIsOnNextDay() {
		var submitDateTime = LocalDateTime.of(2022, 5, 3, 16, 0); // Tuesday 16:00
		var dueDate = dueDateCalculator.calculateDueDate(submitDateTime, 2);
		var expectedDueDate = LocalDateTime.of(2022, 5, 4, 10, 0); // Wednesday 10:00
		assertThat(dueDate, equalTo(expectedDueDate));
	}

	@Test
	public void testCalculateDueDate_dueDateIsOnNextDay_notWholeHour() {
		var submitDateTime = LocalDateTime.of(2022, 5, 3, 16, 13, 55, 142); // Tuesday 16:13:55:142
		var dueDate = dueDateCalculator.calculateDueDate(submitDateTime, 2);
		var expectedDueDate = LocalDateTime.of(2022, 5, 4, 10, 13, 55, 142); // Wednesday 10:13:55:142
		assertThat(dueDate, equalTo(expectedDueDate));
	}
	
	@Test
	public void testCalculateDueDate_dueDateIsTwoDaysFromSubmit() {
		var submitDateTime = LocalDateTime.of(2022, 5, 3, 14, 12, 49, 12345); // Tuesday 14:12:49:12345
		var dueDate = dueDateCalculator.calculateDueDate(submitDateTime, 16);
		var expectedDueDate = LocalDateTime.of(2022, 5, 5, 14, 12, 49, 12345); // Thursday 14:12:49:12345
		assertThat(dueDate, equalTo(expectedDueDate));
	}
	
	@Test
	public void testCalculateDueDate_dueDateIsTwoAndAHalfDaysFromSubmit() {
		var submitDateTime = LocalDateTime.of(2022, 5, 3, 14, 12, 49, 12345); // Tuesday 14:12:49:12345
		var dueDate = dueDateCalculator.calculateDueDate(submitDateTime, 20);
		var expectedDueDate = LocalDateTime.of(2022, 5, 6, 10, 12, 49, 12345); // Friday 10:12:49:12345
		assertThat(dueDate, equalTo(expectedDueDate));
	}
	
	@Test
	public void testCalculateDueDate_dueDateIsThreeDaysFromSubmit() {
		var submitDateTime = LocalDateTime.of(2022, 5, 3, 14, 12, 49, 12345); // Tuesday 14:12:49:12345
		var dueDate = dueDateCalculator.calculateDueDate(submitDateTime, 24);
		var expectedDueDate = LocalDateTime.of(2022, 5, 6, 14, 12, 49, 12345); // Friday 14:12:49:12345
		assertThat(dueDate, equalTo(expectedDueDate));
	}
	
	@Test
	public void testCalculateDueDate_dueDateIsFourDaysFromSubmit() {
		var submitDateTime = LocalDateTime.of(2022, 5, 3, 14, 12, 49, 12345); // Tuesday 14:12:49:12345
		var dueDate = dueDateCalculator.calculateDueDate(submitDateTime, 32);
		var expectedDueDate = LocalDateTime.of(2022, 5, 9, 14, 12, 49, 12345); // Monday 14:12:49:12345
		assertThat(dueDate, equalTo(expectedDueDate));
	}
	
	@Test
	public void testCalculateDueDate_dueDateIsFourAndAHalfDaysFromSubmit() {
		var submitDateTime = LocalDateTime.of(2022, 5, 3, 14, 12, 49, 12345); // Tuesday 14:12:49:12345
		var dueDate = dueDateCalculator.calculateDueDate(submitDateTime, 36);
		var expectedDueDate = LocalDateTime.of(2022, 5, 10, 10, 12, 49, 12345); // Tuesday 10:12:49:12345
		assertThat(dueDate, equalTo(expectedDueDate));
	}
	
	@Test
	public void testCalculateDueDate_submittedLastMinuteFriday() {
		var submitDateTime = LocalDateTime.of(2022, 5, 6, 17, 0); // Friday 17:00
		var dueDate = dueDateCalculator.calculateDueDate(submitDateTime, 5);
		var expectedDueDate = LocalDateTime.of(2022, 5, 9, 14, 0); // Monday 14:00
		assertThat(dueDate, equalTo(expectedDueDate));
	}

}
