package com.upgrade.campsitereservations.component;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidatorTest {

    Validator validator = new Validator();

    @Nested
    @DisplayName("Given a isAvailabilityRangeProvided COMMAND")
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
    @DisplayName("Given a IsAvailabilityRequestValid COMMAND")
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

}