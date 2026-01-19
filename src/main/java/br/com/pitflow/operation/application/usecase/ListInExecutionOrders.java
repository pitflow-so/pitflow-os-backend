package br.com.pitflow.operation.application.usecase;

import br.com.pitflow.operation.domain.ServiceOrder;

import java.util.List;

public interface ListInExecutionOrders {
    List<ServiceOrder> execute();
}
