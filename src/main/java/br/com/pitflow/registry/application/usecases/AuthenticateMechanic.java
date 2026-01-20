package br.com.pitflow.registry.application.usecases;

import br.com.pitflow.registry.application.dto.LoginDto;
import br.com.pitflow.registry.application.dto.AuthenticationResult;

public interface AuthenticateMechanic {
    AuthenticationResult execute(LoginDto dto);
}
