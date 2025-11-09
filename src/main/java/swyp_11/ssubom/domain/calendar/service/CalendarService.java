package swyp_11.ssubom.domain.calendar.service;

import swyp_11.ssubom.domain.calendar.dto.CalendarResponse;
import swyp_11.ssubom.domain.user.entity.User;

public interface CalendarService {
    CalendarResponse getCalendar(User user, int year, int month);
}
