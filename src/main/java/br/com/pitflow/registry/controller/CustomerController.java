package br.com.pitflow.registry.controller;

import br.com.pitflow.registry.controller.dto.CreateCustomerCommand;
import br.com.pitflow.registry.controller.dto.UpdateCustomerCommand;
import br.com.pitflow.registry.core.usecase.customer.inputPort.CreateCustomer;
import br.com.pitflow.registry.core.usecase.customer.inputPort.DeleteCustomer;
import br.com.pitflow.registry.core.usecase.customer.inputPort.FindCustomerByDocument;
import br.com.pitflow.registry.core.usecase.customer.inputPort.FindCustomerById;
import br.com.pitflow.registry.core.usecase.customer.inputPort.ListCustomers;
import br.com.pitflow.registry.core.usecase.customer.inputPort.UpdateCustomer;
import br.com.pitflow.registry.infrastructure.web.dto.CreateCustomerRequest;
import br.com.pitflow.registry.infrastructure.web.dto.UpdateCustomerRequest;
import br.com.pitflow.registry.presenter.CustomerPresenter;
import br.com.pitflow.registry.presenter.dto.CustomerResponse;

import java.util.List;
import java.util.UUID;

public class CustomerController {
    private final CreateCustomer createCustomer;
    private final UpdateCustomer updateCustomer;
    private final DeleteCustomer deleteCustomer;
    private final FindCustomerById findCustomerById;
    private final FindCustomerByDocument findCustomerByDocument;
    private final ListCustomers listCustomers;

    public CustomerController(
            CreateCustomer createCustomer,
            UpdateCustomer updateCustomer,
            DeleteCustomer deleteCustomer,
            FindCustomerById findCustomerById,
            FindCustomerByDocument findCustomerByDocument,
            ListCustomers listCustomers) {
        this.createCustomer = createCustomer;
        this.updateCustomer = updateCustomer;
        this.deleteCustomer = deleteCustomer;
        this.findCustomerById = findCustomerById;
        this.findCustomerByDocument = findCustomerByDocument;
        this.listCustomers = listCustomers;
    }

    public CustomerResponse create(CreateCustomerRequest request) {
        var command = new CreateCustomerCommand(request.name(), request.document(), request.phone(), request.email());
        var customer = createCustomer.execute(command);
        return CustomerPresenter.toResponse(customer);
    }

    public CustomerResponse update(UUID id, UpdateCustomerRequest request) {
        var command = new UpdateCustomerCommand(request.name(), request.document(), request.phone(), request.email());
        updateCustomer.execute(id, command);
        var updated = findCustomerById.execute(id);
        return CustomerPresenter.toResponse(updated);
    }

    public void delete(UUID id) {
        deleteCustomer.execute(id);
    }

    public CustomerResponse getById(UUID id) {
        var customer = findCustomerById.execute(id);
        return CustomerPresenter.toResponse(customer);
    }

    public CustomerResponse getByDocument(String document) {
        var customer = findCustomerByDocument.execute(document);
        return CustomerPresenter.toResponse(customer);
    }

    public List<CustomerResponse> listAll() {
        return listCustomers.execute()
                .stream()
                .map(CustomerPresenter::toResponse)
                .toList();
    }
}