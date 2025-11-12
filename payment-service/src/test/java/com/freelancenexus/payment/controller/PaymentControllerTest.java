package com.freelancenexus.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freelancenexus.payment.dto.*;
import com.freelancenexus.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    private PaymentRequestDTO paymentRequest;

    @BeforeEach
    void setUp() {
        paymentRequest = PaymentRequestDTO.builder()
                .projectId(1L).payerId(2L).payeeId(3L)
                .amount(new BigDecimal("100")).currency("INR")
                .paymentMethod("UPI").upiId("test@upi").build();
    }

    @Test
    void shouldInitiatePayment() throws Exception {
        PaymentResponseDTO response = PaymentResponseDTO.builder().transactionId("TXN-1").build();
        when(paymentService.initiatePayment(any())).thenReturn(response);

        mockMvc.perform(post("/api/payments/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("TXN-1"));
    }

    @Test
    void shouldGetPaymentById() throws Exception {
        PaymentResponseDTO response = PaymentResponseDTO.builder().id(1L).build();
        when(paymentService.getPaymentById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldGetPaymentsByProject() throws Exception {
        PaymentResponseDTO dto = PaymentResponseDTO.builder().id(1L).build();
        when(paymentService.getPaymentsByProject(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/payments/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void shouldRefundPayment() throws Exception {
        PaymentResponseDTO dto = PaymentResponseDTO.builder().id(1L).status(null).build();
        when(paymentService.refundPayment(1L, "reason")).thenReturn(dto);

        mockMvc.perform(post("/api/payments/1/refund")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("reason", "reason"))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnHealthCheck() throws Exception {
        mockMvc.perform(get("/api/payments/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("Payment Service"));
    }
}
