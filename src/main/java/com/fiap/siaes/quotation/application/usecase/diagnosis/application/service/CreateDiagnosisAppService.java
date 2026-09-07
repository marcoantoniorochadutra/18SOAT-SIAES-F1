package com.fiap.siaes.quotation.application.usecase.diagnosis.application.service;

import com.fiap.siaes.customer.domain.model.CustomerId;
import com.fiap.siaes.customer.domain.repository.CustomerRepository;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.application.usecase.CreateDiagnosisUseCase;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.model.Diagnosis;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.model.DiagnosisId;
import com.fiap.siaes.diagnosis.application.usecase.diagnosis.domain.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateDiagnosisAppService implements CreateDiagnosisUseCase {

    private final DiagnosisRepository diagnosisRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public DiagnosisId execute(CreateDiagnosisCommand command) {
        CustomerId ownerId = new CustomerId(command.ownerId());
        this.customerRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + ownerId));

        if (this.diagnosisRepository.existsByPlate(command.plate())) {
            throw new IllegalArgumentException("Já existe um veículo cadastrado com a placa: " + command.plate());
        }

        Diagnosis diagnosis = Diagnosis.register(ownerId, command.plate(), command.brand(), command.model(), command.year());
        return this.diagnosisRepository.save(diagnosis).getId();
    }
}
