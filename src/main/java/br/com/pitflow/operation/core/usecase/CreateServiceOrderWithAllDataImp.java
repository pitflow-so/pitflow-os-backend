package br.com.pitflow.operation.core.usecase;

import br.com.pitflow.common.core.gateway.TransactionGateway;
import br.com.pitflow.operation.controller.dto.AddOrderItemCommand;
import br.com.pitflow.operation.controller.dto.CreateServiceOrderAllDataCommand;
import br.com.pitflow.operation.controller.dto.CreateServiceOrderCommand;
import br.com.pitflow.operation.core.entity.ServiceOrder;
import br.com.pitflow.operation.core.usecase.inputPort.AddOrderItem;
import br.com.pitflow.operation.core.usecase.inputPort.CreateServiceOrder;
import br.com.pitflow.operation.core.usecase.inputPort.CreateServiceOrderWithAllData;
import br.com.pitflow.operation.core.usecase.inputPort.GetServiceOrderById;

public class CreateServiceOrderWithAllDataImp implements CreateServiceOrderWithAllData {
    private final CreateServiceOrder createServiceOrder;
    private final AddOrderItem addOrderItem;
    private final GetServiceOrderById getServiceOrderById;
    private final TransactionGateway transactionGateway;

    public CreateServiceOrderWithAllDataImp(
            CreateServiceOrder createServiceOrder,
            AddOrderItem addOrderItem,
            GetServiceOrderById getServiceOrderById,
            TransactionGateway transactionGateway
    ) {
        this.createServiceOrder = createServiceOrder;
        this.addOrderItem = addOrderItem;
        this.getServiceOrderById = getServiceOrderById;
        this.transactionGateway = transactionGateway;
    }

    @Override
    public ServiceOrder execute(CreateServiceOrderAllDataCommand command) {
        return transactionGateway.execute(() -> {
            var os = createServiceOrder.execute(
                    new CreateServiceOrderCommand(
                            command.customerId(),
                            command.vehicleId(),
                            command.orderDescription()
                    )
            );
            if(command.orderItems().isEmpty())
                throw new IllegalArgumentException("Order must have at least one item");

            for (var item: command.orderItems()) {
                addOrderItem.execute(
                        new AddOrderItemCommand(
                                os.getId(),
                                item.catalogId(),
                                item.quantity(),
                                item.type()
                        )
                );
            }

            return getServiceOrderById.execute(os.getId());
        });

    }
}
