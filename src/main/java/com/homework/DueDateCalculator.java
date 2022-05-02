package com.homework;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class DueDateCalculator {

	private static final LocalTime START_OF_WORK = LocalTime.of(9, 0, 0, 0);
	private static final LocalTime END_OF_WORK = LocalTime.of(17, 0, 0, 0);
	private static final long WORKING_NANOS_PER_DAY = START_OF_WORK.until(END_OF_WORK, ChronoUnit.NANOS);

	public LocalDateTime calculateDueDate(LocalDateTime submitDateTime, long turnaroundTime) {
		if(turnaroundTime < 0) {
			throw new IllegalArgumentException("Negative turnroundTime");
		}
		// A problem can only be reported during working hours.
		if(submitDateTime == null || isWeekend(submitDateTime) || isNotWorkingHour(submitDateTime)) {
			throw new IllegalArgumentException("SubmitDateTime is not working hour");
		}

		LocalTime submitTime = LocalTime.from(submitDateTime);
		LocalDate submitDate = LocalDate.from(submitDateTime);

		long turnaroundTimeInNanos = TimeUnit.HOURS.toNanos(turnaroundTime);
		long nanosTillEndOfWork = submitTime.until(END_OF_WORK, ChronoUnit.NANOS);
		long nanosForFollowingDays = turnaroundTimeInNanos - nanosTillEndOfWork;
		boolean dueOnSameDay = nanosForFollowingDays <= 0;
		if(dueOnSameDay) {
			return submitDateTime.plusNanos(turnaroundTimeInNanos);
		}
		long requiredWorkDays = nanosForFollowingDays / WORKING_NANOS_PER_DAY;
		LocalDate dueDate = getDueDate(submitDate, requiredWorkDays);
		long remainingNanosForDueDate = nanosForFollowingDays % WORKING_NANOS_PER_DAY;
		return LocalDateTime.of(dueDate, START_OF_WORK).plusNanos(remainingNanosForDueDate);
	}

	private boolean isWeekend(LocalDateTime dateTime) {
		return isWeekend(LocalDate.from(dateTime));
	}

	private boolean isWeekend(LocalDate date) {
		switch(date.getDayOfWeek()) {
			case SATURDAY:
			case SUNDAY:
				return true;
			default:
				return false;
		}
	}

	private boolean isNotWorkingHour(LocalDateTime submitDateTime) {
		LocalTime submitTime = LocalTime.from(submitDateTime);
		return submitTime.isBefore(START_OF_WORK) || submitTime.isAfter(END_OF_WORK);
	}

	private LocalDate getDueDate(LocalDate baseDate, long requiredWorkingDays) {
		LocalDate dueDate = getNextWorkDayDate(baseDate);
		for(int i = 0; i < requiredWorkingDays; i++) {
			dueDate = getNextWorkDayDate(dueDate);
		}
		return dueDate;
	}

	private LocalDate getNextWorkDayDate(LocalDate baseDate) {
		LocalDate nextDay = getNextDay(baseDate);
		while(isWeekend(nextDay)) {
			nextDay = getNextDay(nextDay);
		}
		return nextDay;
	}

	private LocalDate getNextDay(LocalDate baseDate) {
		return baseDate.plusDays(1);
	}

}