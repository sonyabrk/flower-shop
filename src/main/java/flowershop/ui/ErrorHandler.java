package flowershop.ui;

import flowershop.exception.BusinessException;
import flowershop.exception.DatabaseConnectionException;
import flowershop.exception.EntityNotFoundException;
import flowershop.exception.OperationCancelledException;
import flowershop.exception.ValidationException;

public final class ErrorHandler {

    private ErrorHandler() {
    }

    /** Выполняет действие; любую ошибку превращает в сообщение, программа продолжает работу. */
    public static void run(Runnable action) {
        try {
            action.run();
        } catch (OperationCancelledException e) {
            System.out.println(e.getMessage());
        } catch (ValidationException e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
        } catch (BusinessException e) {
            System.out.println("Нарушено бизнес-правило: " + e.getMessage());
        } catch (EntityNotFoundException e) {
            System.out.println("Не найдено: " + e.getMessage());
        } catch (DatabaseConnectionException e) {
            System.out.println("Ошибка базы данных: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Непредвиденная ошибка: " + e.getMessage());
        }
    }
}