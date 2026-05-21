package br.com.pitflow.registry.presenter;

import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.presenter.dto.CustomerResponse;
import br.com.pitflow.registry.presenter.dto.VehicleResponse;

import java.util.ArrayList;
import java.util.List;

public class CustomerPresenter {
    private CustomerPresenter() {}

    public static CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getDocument().value(),
                customer.getPhone(),
                customer.getEmail().value(),
                customer.geStatus()
        );
    }
}
