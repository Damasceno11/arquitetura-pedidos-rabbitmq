package br.com.damasceno.ms_pedido.domain.repository;

import br.com.damasceno.ms_pedido.domain.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {}
