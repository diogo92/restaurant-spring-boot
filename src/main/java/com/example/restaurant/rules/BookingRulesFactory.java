package com.example.restaurant.rules;

import com.example.restaurant.exceptions.service.BookingServiceException;
import com.example.restaurant.model.Booking;
import lombok.Data;
import org.jeasy.rules.annotation.Action;
import org.jeasy.rules.annotation.Condition;
import org.jeasy.rules.annotation.Fact;
import org.jeasy.rules.annotation.Rule;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.springframework.stereotype.Component;


@Data
@Component
public class BookingRulesFactory {

    private Facts facts;
    private Rules rules;
    private DefaultRulesEngine rulesEngine;

    public BookingRulesFactory() {
        facts = new Facts();
        rules = new Rules();
        rulesEngine = new DefaultRulesEngine();
        rules.register(new MinAmountOfPeopleRule());
        rules.register(new MaxAmountOfPeopleRule());
    }

    public void fireRules() {
        rulesEngine.fire(rules, facts);
        for (org.jeasy.rules.api.Rule rule: rules) {
            if(!rule.evaluate(facts)) {
                throw new BookingServiceException("Rule " + rule.getClass().getName() + "failed. Reason: " + rule.getDescription());
            }
        }
    }

    public void setFact(String factName, Object factValue) {
        facts.put(factName, factValue);
    }

    @Rule(name = "Minimum amount of people", description = "The minimum amount of people for a booking should be 1")
    public static class MinAmountOfPeopleRule {

        @Condition
        public boolean hasMinimumAmountOfPeople(@Fact("bookingRequest") Booking bookingRequest) {
            return bookingRequest.getNumOfPeople() >= 1;
        }

        @Action
        public void action() {}
    }

    @Rule(name = "Maximum amount of people", description = "The maximum amount of people for a booking should be 8")
    public static class MaxAmountOfPeopleRule {

        @Condition
        public boolean hasMaximumAmountOfPeople(@Fact("bookingRequest") Booking bookingRequest) {
            return bookingRequest.getNumOfPeople() <= 8;
        }

        @Action
        public void action() {}
    }
}
