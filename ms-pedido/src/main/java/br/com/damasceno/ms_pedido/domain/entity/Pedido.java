package br.com.damasceno.ms_pedido.domain.entity;

import br.com.damasceno.ms_pedido.domain.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tb_pedidos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pedido_seq")
    @SequenceGenerator(
            name = "pedido_seq",
            sequenceName = "pedido_seq",
            allocationSize = 50)
    private Long id;

    private Long clienteId;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusPedido status;

    private BigDecimal total;

    @OneToMany(
            mappedBy = "pedido", cascade = CascadeType.ALL,  orphanRemoval = true, fetch = FetchType.LAZY
    )
    private Set<PedidoItem> itens = new HashSet<>();

    public void adicionarItem(PedidoItem item){
        itens.add(item);
        item.setPedido(this);
    }

    public  void removerItem(PedidoItem item){
        itens.remove(item);
        item.setPedido(null);
    }

    public void calcularTotal(){
        this.total = itens.stream()
                .map(PedidoItem::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @PrePersist
    protected void onCreate(){
        dataCriacao = LocalDateTime.now();
        status = StatusPedido.CRIADO;
        calcularTotal();
    }

}
