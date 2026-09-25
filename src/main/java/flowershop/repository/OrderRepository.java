package flowershop.repository;

import flowershop.model.BouquetOrder;

import java.util.List;

public interface OrderRepository extends Repository<BouquetOrder, Long> {

    List<BouquetOrder> findByBouquetId(Long bouquetId);

    List<BouquetOrder> findByCustomerId(Long customerId);
}
