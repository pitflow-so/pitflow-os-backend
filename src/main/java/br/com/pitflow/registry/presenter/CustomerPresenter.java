package br.com.pitflow.registry.presenter;

import br.com.pitflow.registry.core.entity.Customer;
import br.com.pitflow.registry.presenter.dto.CustomerResponse;

public class CustomerPresenter {
    private CustomerPresenter() {}

    public static CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getDocument().value(),
                customer.getPhone(),
                customer.getEmail().value()
        );
    }
}
