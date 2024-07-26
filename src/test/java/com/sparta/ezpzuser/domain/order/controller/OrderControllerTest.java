package com.sparta.ezpzuser.domain.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.ezpzuser.common.security.UserDetailsImpl;
import com.sparta.ezpzuser.domain.order.dto.OrderRequestDto;
import com.sparta.ezpzuser.domain.order.dto.OrderRequestDto.CartIdRequest;
import com.sparta.ezpzuser.domain.order.dto.OrderResponseDto;
import com.sparta.ezpzuser.domain.order.service.OrderService;
import com.sparta.ezpzuser.domain.user.dto.SignupRequestDto;
import com.sparta.ezpzuser.domain.user.entity.User;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@WebMvcTest(OrderController.class)
@TestPropertySource(locations = "classpath:/properties/env.properties")
class OrderControllerTest {

    @MockBean
    private OrderService orderService;

    private MockMvc mockMvc;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new OrderController(orderService)).build();

        user = User.of(
                new SignupRequestDto(
                        "testuser",
                        "Test@12345678",
                        "Test Name",
                        "testuser@example.com",
                        "01012345678"
                ),
                "encodedPassword"
        );
    }


    @Test
    @WithMockUser
    @DisplayName("Test 1 / 주문하기")
    public void createOrder() throws Exception {
        OrderRequestDto.CartIdRequest cartIdRequest = new CartIdRequest(1L);
        OrderRequestDto requestDto = new OrderRequestDto(Arrays.asList(cartIdRequest));

        UserDetailsImpl userDetails = new UserDetailsImpl(user);

        OrderResponseDto orderResponseDto = new OrderResponseDto();
        when(orderService.createOrder(any(OrderRequestDto.class), any())).thenReturn(
                orderResponseDto);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestDto = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestDto)
                        .with(SecurityMockMvcRequestPostProcessors.user(userDetails)))
                .andExpect(status().isOk());
    }

}