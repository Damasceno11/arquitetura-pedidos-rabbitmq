package br.com.damasceno.mspedido.domain.repository;

import br.com.damasceno.mspedido.domain.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {}
