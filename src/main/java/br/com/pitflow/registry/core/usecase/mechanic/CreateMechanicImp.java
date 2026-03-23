package br.com.pitflow.registry.core.usecase.mechanic;

import br.com.pitflow.registry.controller.dto.CreateMechanicCommand;
import br.com.pitflow.registry.core.entity.Mechanic;
import br.com.pitflow.registry.core.gateway.MechanicGateway;
import br.com.pitflow.registry.core.usecase.mechanic.inputPort.CreateMechanic;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CreateMechanicImp implements CreateMechanic {

    private final MechanicGateway repository;
    private final PasswordEncoder passwordEncoder;

    public CreateMechanicImp(MechanicGateway repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mechanic execute(CreateMechanicCommand dto) {
        if (repository.findByUsername(dto.username()).isPresent()) {
            throw new RuntimeException("Mechanic with username " + dto.username() + " already exists");
        }

        String encodedPassword = passwordEncoder.encode(dto.password());
        var mechanic = new Mechanic(dto.name(), dto.username(), encodedPassword);
        repository.save(mechanic);

        return mechanic;
    }
}