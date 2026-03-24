package br.com.pitflow.common.infrastructure.transaction;

import br.com.pitflow.common.core.gateway.TransactionGateway;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Supplier;

public class SpringTransactionAdapter implements TransactionGateway {

    @Override
    @Transactional
    public <T> T execute(Supplier<T> operation) {
        return operation.get();
    }

    @Override
    @Transactional
    public void execute(Runnable operation) {
        operation.run();
    }
}
