package br.com.damasceno.mspedido.application.controller;

import br.com.damasceno.mspedido.application.dto.PedidoRequestDTO;
import br.com.damasceno.mspedido.application.dto.PedidoResponseDTO;
import br.com.damasceno.mspedido.application.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos API", description = "Endpoints para gerenciamento de pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(summary = "Cria um novo pedido", responses = {
            @ApiResponse(responseCode = "201", description = "Pedido criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida (verificar campos)")
    })
    @PostMapping
    public ResponseEntity<PedidoResponseDTO> criarPedido(
            @Valid @RequestBody PedidoRequestDTO requestDTO) {

        PedidoResponseDTO response = pedidoService.criarPedido(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
