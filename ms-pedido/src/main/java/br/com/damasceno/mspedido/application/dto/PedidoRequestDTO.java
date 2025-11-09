package br.com.damasceno.mspedido.application.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoRequestDTO(
        @NotNull(message = "ID do cliente não pode ser nulo")
        Long clienteId,

        @NotEmpty(message = "O pedido deve conter pelo menos um item")
        List<ItemRequestDTO> itens) {}
