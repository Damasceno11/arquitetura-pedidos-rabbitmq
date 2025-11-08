package br.com.damasceno.ms_pedido.domain.repository;

import br.com.damasceno.ms_pedido.domain.entity.Pedido;
import br.com.damasceno.ms_pedido.domain.entity.PedidoItem;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PedidoRepositoryTest {

    private final TestEntityManager entityManager;

    private final PedidoRepository pedidoRepository;

    @Test
    @DisplayName("Deve salvar um Pedido com Itens em cascata")
    void deveSalvarPedidoComItensEmCascata() {
        Pedido pedido = new Pedido();
        pedido.setClienteId(123L);

        PedidoItem item1 = new PedidoItem();
        item1.setProdutoId("PROD-A");
        item1.setDescricao("Produto A");
        item1.setQuantidade(2);
        item1.setPrecoUnitario(new BigDecimal("10.00"));

        pedido.adicionarItem(item1);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        assertNotNull(pedidoSalvo.getId(), "O ID do Pedido não deve ser nulo após salvar");
        assertNotNull(pedidoSalvo.getDataCriacao(), "A data de criação deve ser definida pelo @PrePersist");
        assertEquals(1, pedidoSalvo.getItens().size(), "O pedido deve ter 1 item");
        assertNotNull(item1.getId(), "O ID do PedidoItem deve ser preenchido pelo cascade");

        Optional<Pedido> pedidoDoBanco = pedidoRepository.findById(pedidoSalvo.getId());
        assertTrue(pedidoDoBanco.isPresent(), "Deve encontrar o pedido no banco");
        assertEquals(1, pedidoDoBanco.get().getItens().size(), "O pedido buscado do banco deve ter 1 item");
    }

    @Test
    @DisplayName("Deve calcular o total do pedido via @PrePersist")
    void deveCalcularTotalPedidoAntesDeSalvar() {
        Pedido pedido = new Pedido();
        pedido.setClienteId(456L);

        PedidoItem item1 = new PedidoItem();
        item1.setProdutoId("PROD-B");
        item1.setQuantidade(3);
        item1.setPrecoUnitario(new BigDecimal("5.00")); // Total: 15.00
        pedido.adicionarItem(item1);

        PedidoItem item2 = new PedidoItem();
        item2.setProdutoId("PROD-C");
        item2.setQuantidade(1);
        item2.setPrecoUnitario(new BigDecimal("25.50")); // Total: 25.50
        pedido.adicionarItem(item2);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        entityManager.flush();
        entityManager.clear();

        Pedido pedidoDoBanco = pedidoRepository.findById(pedidoSalvo.getId()).orElseThrow();

        assertThat(pedidoDoBanco.getTotal()).isEqualByComparingTo(new BigDecimal("40.50"));
    }
}
