package br.com.pitflow.registry.application;

import br.com.pitflow.registry.application.dto.CreateMechanicDto;
import br.com.pitflow.registry.application.usecases.CreateMechanic;
import br.com.pitflow.registry.domain.Mechanic;
import br.com.pitflow.registry.domain.repository.MechanicRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CreateMechanicImp implements CreateMechanic {

    private final MechanicRepository repository;
    private final PasswordEncoder passwordEncoder;

    public CreateMechanicImp(MechanicRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mechanic execute(CreateMechanicDto dto) {
        if (repository.findByUsername(dto.username()).isPresent()) {
            throw new RuntimeException("Mechanic with username " + dto.username() + " already exists");
        }

        String encodedPassword = passwordEncoder.encode(dto.password());
        var mechanic = new Mechanic(dto.name(), dto.username(), encodedPassword);
        repository.save(mechanic);

        return mechanic;
    }
}