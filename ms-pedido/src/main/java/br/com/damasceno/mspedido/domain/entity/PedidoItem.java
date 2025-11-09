package br.com.damasceno.mspedido.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_pedido_itens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pedido_item_seq")
    @SequenceGenerator(
            name = "pedido_item_seq",
            sequenceName = "pedido_item_seq",
            allocationSize = 50)
    private Long id;

    private String produtoId;
    private String descricao;

    @Column(nullable = false)
    private Integer quantidade;

    private BigDecimal precoUnitario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id",  nullable = false)
    private Pedido pedido;

    public BigDecimal getSubTotal(){
        if (precoUnitario == null || quantidade == null){
            return BigDecimal.ZERO;
        }
        return precoUnitario.multiply(new BigDecimal(quantidade));
    }
}
