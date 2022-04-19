package com.example.restaurant.controllers;

import com.example.restaurant.dto.BookingDTO;
import com.example.restaurant.model.Booking;
import com.example.restaurant.repositories.BookingRepository;
import com.example.restaurant.services.BookingService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import net.bytebuddy.matcher.ElementMatchers;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

@SpringBootTest
public class BookingControllerTest {

    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    BookingService bookingService;

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    ObjectMapper mapper;

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor user = user("user").password(passwordEncoder().encode("password"));
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    LocalDateTime localDateTime = LocalDateTime.parse("2030-04-12 13:30:00", df);


    @BeforeEach
    public void setup() {

        bookingRepository.deleteAll();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);


    }


    @Test
    public void noAuthentication() throws  Exception {
        final ResultActions result =
                mockMvc.perform(
                        get("/bookingService/bookings")
                                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isUnauthorized());
    }

    @Test
    public void getAllBookings() throws Exception {

        for(int i = 1; i<=3; i++) {
            bookingService.bookTable(new Booking(localDateTime,i));
        }

        mockMvc.perform(
                get("/bookingService/bookings").with(user)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(result -> System.out.println("ASD " + result.getResponse().getContentAsString()))
        .andExpect(status().isOk())
        .andExpect(content()
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].tables[0].numSeats", is(2)))
        .andExpect(jsonPath("$[1].tables[0].numSeats", is(2)))
        .andExpect(jsonPath("$[2].tables[0].numSeats", is(4)));
    }

    @Test
    public void bookTable() throws Exception {
        String TOKEN_ATTR_NAME = "org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN";
        HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
        CsrfToken csrfToken = httpSessionCsrfTokenRepository.generateToken(new MockHttpServletRequest());

        BookingDTO bookingDto = new BookingDTO();
        bookingDto.setTime(localDateTime);
        bookingDto.setNumOfPeople(2);

        String body  = this.mapper.writeValueAsString(bookingDto);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/bookingService/bookings/addBooking")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .sessionAttr(TOKEN_ATTR_NAME, csrfToken)
                .param(csrfToken.getParameterName(), csrfToken.getToken())
                .content(body);

        mockMvc.perform(mockRequest.with(user))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()));

    }

}
