package com.upgrade.campsitereservations.component;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidatorTest {

    Validator validator = new Validator();

    @Nested
    @DisplayName("Given an IsDatesFormatValid COMMAND")
    class IsDatesFormatValid {

        @Test
        @DisplayName("Given a valid from and to dates, then return true")
        void testIsDatesFormatValid_1() {
            assertTrue(validator.isDatesFormatValid("2022-06-05", "2022-06-20"));
        }

        @Test
        @DisplayName("Given a valid from date and to not valid, then return false")
        void testIsDatesFormatValid_2() {
            assertFalse(validator.isDatesFormatValid("2022-06-05", "20-06-2022"));
        }

        @Test
        @DisplayName("Given an invalid from date and to valid, then return false")
        void testIsDatesFormatValid_3() {
            assertFalse(validator.isDatesFormatValid("05-06-2022", "2022-06-20"));
        }

        @Test
        @DisplayName("Given an invalid from and to dates, then return false")
        void testIsDatesFormatValid_4() {
            assertFalse(validator.isDatesFormatValid("05-06-2022", "20-06-2022"));
        }
    }

    @Nested
    @DisplayName("Given an isAvailabilityRangeProvided COMMAND")
    class IsAvailabilityRangeProvided {

        @Test
        @DisplayName("Given a valid from and to dates, then return true")
        void testHappyPath() {
            assertTrue(validator.isAvailabilityRangeProvided("2022-06-05", "2022-06-20"));
        }

        @Test
        @DisplayName("Given an empty from, then return false")
        void testFromIsEmpty() {
            assertFalse(validator.isAvailabilityRangeProvided("", "2022-06-20"));
        }

        @Test
        @DisplayName("Given from null, then return false")
        void testFromIsNull() {
            assertFalse(validator.isAvailabilityRangeProvided(null, "2022-06-20"));
        }

        @Test
        @DisplayName("Given an empty to, then return false")
        void testToIsEmpty() {
            assertFalse(validator.isAvailabilityRangeProvided("2022-06-05", ""));
        }

        @Test
        @DisplayName("Given to null, then return false")
        void testToIsNull() {
            assertFalse(validator.isAvailabilityRangeProvided("2022-06-05", null));
        }
    }

    @Nested
    @DisplayName("Given a onlyFromDateIsProvided COMMAND")
    class OnlyFromDateIsProvided {

        @Test
        @DisplayName("Given a valid from date and an empty to date, then return true")
        void testHappyPath() {
            assertTrue(validator.onlyFromDateIsProvided("2022-06-05", ""));
        }

        @Test
        @DisplayName("Given a valid from date and to date = null, then return true")
        void testCaseFromValidAndToNull() {
            assertTrue(validator.onlyFromDateIsProvided("2022-06-05", null));
        }

        @Test
        @DisplayName("Given a valid from date and to valid, then return false")
        void testCaseFromValidAndToValid() {
            assertFalse(validator.onlyFromDateIsProvided("2022-06-05", "2022-06-20"));
        }

        @Test
        @DisplayName("Given a from date null, then return false")
        void testCaseFromIsNull() {
            assertFalse(validator.onlyFromDateIsProvided(null, "2022-06-20"));
        }
    }

    @Nested
    @DisplayName("Given a onlyToDateIsProvided COMMAND")
    class OnlyToDateIsProvided {

        @Test
        @DisplayName("Given a valid to date and an empty from date, then return true")
        void testHappyPath() {
            assertTrue(validator.onlyToDateIsProvided("", "2022-06-05"));
        }

        @Test
        @DisplayName("Given a valid to date and from date = null, then return true")
        void testCaseToValidAndFromNull() {
            assertTrue(validator.onlyToDateIsProvided(null, "2022-06-05"));
        }

        @Test
        @DisplayName("Given a valid from date and to valid, then return false")
        void testCaseFromValidAndToValid() {
            assertFalse(validator.onlyToDateIsProvided("2022-06-05", "2022-06-20"));
        }

        @Test
        @DisplayName("Given a to date null, then return false")
        void testCaseFromIsNull() {
            assertFalse(validator.onlyToDateIsProvided(null, null));
        }
    }

    @Nested
    @DisplayName("Given an IsAvailabilityRequestValid COMMAND")
    class IsAvailabilityRequestValid {

        @Test
        @DisplayName("Given a valid from and to dates, then return true")
        void testHappyPath() {
            assertTrue(validator.isAvailabilityRequestValid("2022-06-05", "2022-06-20"));
        }

        @Test
        @DisplayName("Given a from dates that's is after the to dates, then return false")
        void testErrorCase() {
            assertFalse(validator.isAvailabilityRequestValid("2022-06-20", "2022-06-05"));
        }

    }

    @Nested
    @DisplayName("Given an IsReservationStartDateValid COMMAND")
    class IsReservationStartDateValid {

        @Test
        @DisplayName("Given a valid reservation Start date, then return true")
        void testIsReservationStartDateValid_1() {
            LocalDate today = LocalDate.now();
            LocalDate start = today.plusDays(1);
            assertTrue(validator.isReservationStartDateValid(start.toString(), 1, 30));

            LocalDate start1 = today.plusDays(20);
            assertTrue(validator.isReservationStartDateValid(start1.toString(), 1, 30));

            LocalDate start2 = today.plusDays(30);
            assertTrue(validator.isReservationStartDateValid(start2.toString(), 1, 30));
        }

        @Test
        @DisplayName("Given an invalid reservation Start date, then return false")
        void testIsReservationStartDateValid_2() {
            LocalDate today = LocalDate.now();
            assertFalse(validator.isReservationStartDateValid(today.toString(), 1, 30));

            LocalDate start1 = today.plusDays(35);
            assertFalse(validator.isReservationStartDateValid(start1.toString(), 1, 30));
        }
    }

    @Nested
    @DisplayName("Given an IsReservationLessThan4Days COMMAND")
    class  IsReservationLessThan4Days {

        @Test
        @DisplayName("Given a valid from and to dates, then return true")
        void testIsReservationLessThan4Days_1() {
            LocalDate today = LocalDate.now();
            LocalDate from = today.plusDays(1);
            LocalDate to = today.plusDays(4);
            assertTrue(validator.isReservationLessThan4Days(from.toString(), to.toString(), 3));

            LocalDate to1 = today.plusDays(2);
            assertTrue(validator.isReservationLessThan4Days(from.toString(), to1.toString(), 3));
        }

        @Test
        @DisplayName("Given an inValid from and to dates, then return false")
        void testIsReservationLessThan4Days_2() {
            LocalDate today = LocalDate.now();
            LocalDate from = today.plusDays(1);
            LocalDate to = today.plusDays(6);
            assertFalse(validator.isReservationLessThan4Days(from.toString(), to.toString(), 3));
        }
    }

}