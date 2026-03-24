package br.com.pitflow.registry.core.usecase.mechanic;

import br.com.pitflow.registry.controller.dto.CreateMechanicCommand;
import br.com.pitflow.registry.core.entity.Mechanic;
import br.com.pitflow.registry.core.gateway.MechanicGateway;
import br.com.pitflow.registry.core.gateway.PasswordEncoderGateway;
import br.com.pitflow.registry.core.usecase.mechanic.inputPort.CreateMechanic;

public class CreateMechanicImp implements CreateMechanic {

    private final MechanicGateway mechanicGateway;
    private final PasswordEncoderGateway passwordEncoder;

    public CreateMechanicImp(MechanicGateway mechanicGateway, PasswordEncoderGateway passwordEncoder) {
        this.mechanicGateway = mechanicGateway;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mechanic execute(CreateMechanicCommand command) {
        if (mechanicGateway.findByUsername(command.username()).isPresent()) {
            throw new RuntimeException("Mechanic with username " + command.username() + " already exists");
        }

        String encodedPassword = passwordEncoder.encode(command.password());
        var mechanic = new Mechanic(command.name(), command.username(), encodedPassword);
        mechanicGateway.save(mechanic);

        return mechanic;
    }
}