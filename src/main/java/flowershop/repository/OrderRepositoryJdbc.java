package flowershop.repository;

import flowershop.exception.DatabaseConnectionException;
import flowershop.model.BouquetOrder;
import flowershop.model.OrderStatus;
import flowershop.util.DatabaseManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepositoryJdbc implements OrderRepository {

    @Override
    public BouquetOrder save(BouquetOrder order) {
        String sql = "INSERT INTO orders " +
                "(customer_id, bouquet_id, quantity, total_price, status, order_date, delivery_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            fillStatement(ps, order);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    order.setId(keys.getLong(1));
                }
            }
            return order;

        } catch (SQLException e) {
            throw new DatabaseConnectionException("Ошибка при сохранении заказа", e);
        }
    }

    @Override
    public Optional<BouquetOrder> findById(Long id) {
        String sql = "SELECT id, customer_id, bouquet_id, quantity, total_price, status, " +
                "order_date, delivery_date FROM orders WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseConnectionException("Ошибка при поиске заказа по ID", e);
        }
    }

    @Override
    public List<BouquetOrder> findAll() {
        String sql = "SELECT id, customer_id, bouquet_id, quantity, total_price, status, " +
                "order_date, delivery_date FROM orders ORDER BY id";
        List<BouquetOrder> result = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new DatabaseConnectionException("Ошибка при получении списка заказов", e);
        }
    }

    @Override
    public BouquetOrder update(BouquetOrder order) {
        String sql = "UPDATE orders SET customer_id = ?, bouquet_id = ?, quantity = ?, " +
                "total_price = ?, status = ?, order_date = ?, delivery_date = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            fillStatement(ps, order);
            ps.setLong(8, order.getId());
            ps.executeUpdate();

            return order;

        } catch (SQLException e) {
            throw new DatabaseConnectionException("Ошибка при обновлении заказа", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM orders WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseConnectionException("Ошибка при удалении заказа", e);
        }
    }

    @Override
    public List<BouquetOrder> findByBouquetId(Long bouquetId) {
        String sql = "SELECT id, customer_id, bouquet_id, quantity, total_price, status, " +
                "order_date, delivery_date FROM orders WHERE bouquet_id = ?";
        List<BouquetOrder> result = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, bouquetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
            return result;

        } catch (SQLException e) {
            throw new DatabaseConnectionException("Ошибка при поиске заказов по букету", e);
        }
    }

    @Override
    public List<BouquetOrder> findByCustomerId(Long customerId) {
        String sql = "SELECT id, customer_id, bouquet_id, quantity, total_price, status, " +
                "order_date, delivery_date FROM orders WHERE customer_id = ?";
        List<BouquetOrder> result = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
            return result;

        } catch (SQLException e) {
            throw new DatabaseConnectionException("Ошибка при поиске заказов по клиенту", e);
        }
    }

    private void fillStatement(PreparedStatement ps, BouquetOrder order) throws SQLException {
        ps.setLong(1, order.getCustomerId());
        ps.setLong(2, order.getBouquetId());
        ps.setInt(3, order.getQuantity());
        ps.setBigDecimal(4, order.getTotalPrice());
        ps.setString(5, order.getStatus().name());
        ps.setDate(6, Date.valueOf(order.getOrderDate()));
        ps.setDate(7, order.getDeliveryDate() != null ? Date.valueOf(order.getDeliveryDate()) : null);
    }

    private BouquetOrder mapRow(ResultSet rs) throws SQLException {
        Date deliveryDate = rs.getDate("delivery_date");
        return new BouquetOrder(
                rs.getLong("id"),
                rs.getLong("customer_id"),
                rs.getLong("bouquet_id"),
                rs.getInt("quantity"),
                rs.getBigDecimal("total_price"),
                OrderStatus.valueOf(rs.getString("status")),
                rs.getDate("order_date").toLocalDate(),
                deliveryDate != null ? deliveryDate.toLocalDate() : null
        );
    }
}
