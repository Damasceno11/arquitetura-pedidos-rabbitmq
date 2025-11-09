package br.com.damasceno.mspedido.application.service;

import br.com.damasceno.mspedido.application.dto.PedidoRequestDTO;
import br.com.damasceno.mspedido.application.dto.PedidoResponseDTO;
import br.com.damasceno.mspedido.application.mapper.PedidoMapper;
import br.com.damasceno.mspedido.domain.entity.Pedido;
import br.com.damasceno.mspedido.domain.entity.PedidoItem;
import br.com.damasceno.mspedido.domain.repository.PedidoRepository;
import br.com.damasceno.mspedido.infrastructure.messaging.event.PedidoCriadoEvent;
import br.com.damasceno.mspedido.infrastructure.messaging.event.PedidoProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PedidoMapper pedidoMapper;
    private final PedidoProducer pedidoProducer;

    @Transactional
    public PedidoResponseDTO criarPedido(PedidoRequestDTO pedidoRequestDTO){
        log.info("Iniciando processo de criacao de pedido para cliente: {}", pedidoRequestDTO);

        Pedido pedido = pedidoMapper.toEntity(pedidoRequestDTO);
        pedidoRequestDTO.itens().forEach(itemDTO -> {
            PedidoItem item = pedidoMapper.toEntity(itemDTO);
            pedido.adicionarItem(item);
        });

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        log.info("Pedido salvo com sucesso. ID: {}", pedidoSalvo.getId());

        String emailClienteSimulado = "pedropaulodamsceno@gmail.com";

        PedidoCriadoEvent event = new PedidoCriadoEvent(
                pedido.getId(),
                pedido.getClienteId(),
                emailClienteSimulado,
                pedidoSalvo.getTotal()
        );

        pedidoProducer.enviarEventoPedidoCriado(event);

        log.info("Criação do pedido {} finalizado com sucesso", pedidoSalvo.getId());
        return pedidoMapper.toDTO(pedidoSalvo);
    }
}
