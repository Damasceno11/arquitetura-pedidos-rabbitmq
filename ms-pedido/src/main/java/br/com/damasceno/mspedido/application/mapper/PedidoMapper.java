package br.com.damasceno.mspedido.application.mapper;

import br.com.damasceno.mspedido.application.dto.ItemResponseDTO;
import br.com.damasceno.mspedido.application.dto.PedidoRequestDTO;
import br.com.damasceno.mspedido.application.dto.ItemRequestDTO;
import br.com.damasceno.mspedido.application.dto.PedidoResponseDTO;
import br.com.damasceno.mspedido.domain.entity.Pedido;
import br.com.damasceno.mspedido.domain.entity.PedidoItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pedido", ignore = true)
    PedidoItem toEntity(ItemRequestDTO itemDTO);

    ItemResponseDTO toDTO(PedidoItem item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataCriacao", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "itens", ignore = true)
    Pedido toEntity(PedidoRequestDTO requestDTO);

    // Mapeamento principal: Entidade -> Response
    PedidoResponseDTO toDTO(Pedido pedido);
}
